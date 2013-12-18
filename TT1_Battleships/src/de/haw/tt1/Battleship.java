package de.haw.tt1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;

public class Battleship {

    private int               nIntervals;
    private int               nShips;
    private boolean[]         map         = null;
    private ID                predecessor = Network.getInstance()
                                                  .getPredecessorID();
    private ID                myID        = Network.getInstance()
                                                  .getChordID();
    private int               shipsLeft;
    private volatile boolean  alive       = true;
    private boolean           turn        = false;
    private List<Enemy>       enemys      = new ArrayList<>();

    private static Battleship game        = new Battleship();

    // --CONSTRUCTORS--//
    /**
     * Battleship, the main game and strategy class.
     * 
     * @param i
     * @param s
     */
    private Battleship() {
    }

    public static Battleship getInstance() {
        return game;
    }

    // --METHODS--//
    /**
     * Place Ships in intervals
     * 
     * @param intervals
     * @param ships
     */
    public void setIntervalsAndShips(int intervals, int ships) {
        if (!(ships < intervals && ships > 0))
            throw new IllegalArgumentException("s<i && s>0");
        this.nIntervals = intervals;
        this.nShips = ships;
        shipsLeft = nShips;
        arrangeShips();
        fileEnemys();
    }

    /**
     * Place ships randomly in the intervals, see checkInterval() for real
     * intervals
     */
    private void arrangeShips() {
        map = new boolean[nIntervals];
        int nos = nShips;
        while (nos > 0) {
            int idx = (int) (Math.random() * nIntervals);
            if (!map[idx]) {
                map[idx] = true;
                nos--;
            }
        }
    }

    /**
     * Calculates the intervals in the DHT. TODO Wrap around does not work.
     */
    private int checkInterval(ID id) {
        BigInteger upperIntervalBorder = null;
        BigInteger lowerIntervalBorder = predecessor.toBigInteger();
        BigInteger myIDInt = myID.toBigInteger();
        BigInteger range;
        if (myIDInt.compareTo(lowerIntervalBorder) < 0) {
            range = lowerIntervalBorder.add(BigInteger.valueOf(-1))
                    .subtract(myIDInt);
            System.out
                    .println("[battleship] MyID was samller: "
                            + myIDInt + " - Calculated range value: "
                            + range);
        }
        range = myIDInt.subtract(lowerIntervalBorder);
        BigInteger oneStep = range.divide(BigInteger
                .valueOf(nIntervals));

        for (int i = 0; i < nIntervals - 1; i++) {
            upperIntervalBorder = lowerIntervalBorder.add(oneStep);
            System.out.println("upperIntervalBorder: "
                    + upperIntervalBorder);
            if (id.isInInterval(ID.valueOf(lowerIntervalBorder), ID
                    .valueOf(upperIntervalBorder))) {
                System.out.println("Interval: " + i);
                return i;
            }
            lowerIntervalBorder = upperIntervalBorder;
        }
        if (id.isInInterval(ID.valueOf(lowerIntervalBorder), myID)) {
            System.out.println("Interval: " + (nIntervals - 1));
            return nIntervals - 1;
        }
        return -1;
    }

    /**
     * Check if we got hit.
     * 
     * @param id
     * @return
     */
    public boolean gotHit(ID id) {
        boolean hit = false;
        int interval = checkInterval(id);
        if (interval != -1 && map[interval]) {
            map[interval] = false; // ship sunk
            shipsLeft--;
            alive = shipsLeft <= 0 ? false : true; // are we down?
            hit = true;
        }
        return hit;
    }

    /**
     * Check if we starting ID
     * 
     * @return
     */
    public boolean hasStartID() {
        byte[] tmp = new byte[myID.getLength() / 8];
        for (int i = 0; i < myID.getLength() / 8; i++) {
            tmp[i] = (byte) 0xFF;
        }
        return this.turn = new ID(tmp)
                .isInInterval(predecessor, myID);
    }

    private void fileEnemys() {
        List<Node> fingerTable = Network.getInstance().getChord()
                .getFingerTable();
        
        enemys.add(new Enemy(fingerTable.get(0).getNodeID(), myID,
                nShips, nIntervals));

        for (int i = 1; i < fingerTable.size(); i++) {
            enemys.add(new Enemy(fingerTable.get(i).getNodeID(),
                    fingerTable.get(i - 1).getNodeID(), nShips,
                    nIntervals));
        }
    }

    /**
     * TODO
     * 
     * @param source
     * @param target
     * @param hit
     */
    public void logAttack(ID source, ID target, Boolean hit) {
        Enemy attackedEnemy = null;
        for (Enemy e : enemys) {
            if (e.getId().equals(source)) {
                attackedEnemy = e;
                break;
            }
        }
        
        if (attackedEnemy == null) {
            for (Enemy e : enemys) {
                if (e.inRange(source)) {
                    attackedEnemy = e.setNewPredecessor(source);
                    enemys.add(attackedEnemy);
                    break;
                }
            }
        }
        
        attackedEnemy.gotAttackedAt(target, hit);
        if (hit) {
            System.out.println(attackedEnemy + " got hit");
        }
    }

    /**
     * TODO
     * 
     * @param enemy
     * @param interval
     */
    public void attack(ID enemy, int interval) {
        ID target = getIdInInterval(enemy, interval);
        Network.getInstance().shoot(target);
    }

    // --GETTER AND SETTER--//
    /**
     * TODO
     * 
     * @param enemy
     * @param interval
     * @return
     */
    private ID getIdInInterval(ID enemy, int interval) {
        return null;
    }

    /**
     * @return the shipsLeft
     */
    protected int getShipsLeft() {
        return shipsLeft;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public boolean getTurn() {
        return turn;
    }

    // --MISC--//
    /**
     * TODO fancier
     */
    public void showShips() {
        System.out.println(Arrays.toString(map));
    }

    /**
     * TODO USE FINGERTABLE FOR SHOOTING
     */
    public void printFingerTable() {
        System.out.println(Network.getInstance().getChord()
                .printFingerTable());
    }

    public void printEnemys() {
        System.out.println(enemys.toString());
    }

}
