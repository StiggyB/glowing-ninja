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
    private boolean[]         map           = null;
    private ID                predecessorID = Network
                                                    .getInstance()
                                                    .getPredecessorID();
    private ID                myID          = Network.getInstance()
                                                    .getChordID();
    private int               shipsLeft;
    private volatile boolean  alive         = true;
    private boolean           turn          = false;
    private List<Enemy>       enemies       = new ArrayList<>();
    private BigInteger        intervalSize  = null;
    private final BigInteger  maxVal        = (new BigInteger("2")
                                                    .pow(160))
                                                    .subtract(new BigInteger(
                                                            "1"));
    private static Battleship game          = new Battleship();

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
     * Calculates the intervals in the DHT. TODO Wrap around might work. Look at
     * .addPowerOfTwo
     */
    private int checkInterval(ID id) {
        BigInteger upperIntervalBorder = null;
        BigInteger lowerIntervalBorder = predecessorID.toBigInteger();
        calcIntervalSize();

        for (int i = 0; i < nIntervals; i++) {
            upperIntervalBorder = lowerIntervalBorder.add(
                    intervalSize).add(maxVal).mod(maxVal);

            if (id.isInInterval(ID.valueOf(lowerIntervalBorder), ID
                    .valueOf(upperIntervalBorder))) {
                System.out
                        .println("[battleship/checkInterval] Interval: "
                                + i);
                return i;
            }
            lowerIntervalBorder = upperIntervalBorder;
        }

        return -1;
    }

    private void calcIntervalSize() {
        BigInteger myIDBigInt = myID.toBigInteger();
        BigInteger predecessorIDBigInt = predecessorID.toBigInteger();
        BigInteger range = myIDBigInt.subtract(predecessorIDBigInt);

        range = range.add(maxVal).mod(maxVal);

        intervalSize = range.divide(BigInteger.valueOf(nIntervals));
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
        return this.turn = new ID(tmp).isInInterval(predecessorID,
                myID);
    }

    private void fileEnemys() {
        List<Node> fingerTable = Network.getInstance().getChord()
                .getFingerTable();

        enemies.add(new Enemy(fingerTable.get(0).getNodeID(), myID,
                nIntervals, nShips));
        enemies.add(new Enemy(predecessorID, fingerTable.get(0)
                .getNodeID(), nIntervals, nShips));

        for (int i = 1; i < fingerTable.size(); i++) {
            ID enemyID = fingerTable.get(i).getNodeID();
            ID preEnemyID = fingerTable.get(i - 1).getNodeID();

            if (myID.isInInterval(preEnemyID, enemyID)) {
                preEnemyID = myID;
            }

            Enemy newEnemy = new Enemy(enemyID, preEnemyID,
                    nIntervals, nShips);

            for (Enemy e : enemies) {
                if (e.inRange(newEnemy.getId())) {
                    newEnemy = e.setNewPredecessor(newEnemy.getId());
                }
            }
            enemies.add(newEnemy);
        }

        enemies.add(new Enemy(myID, predecessorID, nIntervals, nShips));

    }

    /**
     * Logs attacks received via broadcast
     * 
     * @param source
     *            the enemy
     * @param target
     *            the target id
     * @param hit
     *            got hit?
     */
    public void logAttack(ID source, ID target, Boolean hit) {
        Enemy attackedEnemy = null;
        for (Enemy e : enemies) {
            if (e.getId().equals(source)) {
                attackedEnemy = e;
                break;
            }
        }

        if (attackedEnemy == null) {
            for (Enemy e : enemies) {
                if (e.inRange(source)) {
                    attackedEnemy = e.setNewPredecessor(source);
                    enemies.add(attackedEnemy);
                    break;
                }
            }
        }
        
        for (Enemy e : enemies) {
            for (Enemy y : enemies) {
                if (!e.equals(y) && e.inRange(y.getId())) {
                    y = e.setNewPredecessor(y.getId());
                }
            }
        }

        attackedEnemy.gotAttackedAt(target, hit);
        if (hit) {
            System.out.println("[battleship/logAttack] "
                    + attackedEnemy + " got hit");
        }
    }

    class Shooter implements Runnable {

        ID target;

        public Shooter(ID target) {
            this.target = target;
        }

        @Override
        public void run() {
            Network.getInstance().shoot(target);
        }

    }

    /**
     * Attacks given enemy
     * 
     * @param enemy
     *            the enemys number
     * @throws EnemyNotFoundException
     */
    public void attackEnemy(Enemy enemy)
            throws EnemyNotFoundException {

        ID target = null;
        for (Enemy e : enemies) {
            if (e.equals(enemy)) {
                for (int j = 0; j < e.getAttackedIntervals().length; j++) {
                    if (!e.getAttackedIntervals()[j]) {
                        target = e.getIdInInterval(j);
                        break;
                    }
                }
            }
        }
        if (target == null) {
            throw new EnemyNotFoundException("No such enemy");
        } else {
            System.out
                    .println("[battleship/attackEnemy] shooting at: "
                            + target);
        }

        this.turn = false;
        Shooter shooter = new Shooter(target);
        Thread t = new Thread(shooter);
        t.start();
    }

    // --GETTER AND SETTER--//

    /**
     * Should return an ID in an Interval of the enemy
     * 
     * @return target ID
     * @throws EnemyNotFoundException
     */
    public void attackBestTarget() throws EnemyNotFoundException {
        ID target = null;
        int shipsLeft = Integer.MAX_VALUE;

        for (Enemy e : enemies) {
            if (!e.getId().equals(myID)
                    && nShips - e.getNumberOfHits() < shipsLeft
                    && nShips - e.getNumberOfHits() != 2) {
                shipsLeft = nShips - e.getNumberOfHits();
                target = e.getId();
            }
        }

        if (target == null) {
            for (Enemy e : enemies) {
                if (!e.getId().equals(myID) && !e.isDefeated()) {
                    target = e.getId();
                    break;
                }
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).getId().equals(target))
                if (enemies.get(i).getId().equals(myID)) {
                    System.out
                            .println("[battleship/attackBestTarget] friendly fire!!!1elf");
                    try {
                        attackEnemy(enemies.get(i + 1));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        attackEnemy(enemies.get(i - 1));
                    }
                } else {
                    attackEnemy(enemies.get(i));
                }

        }

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

    public void hasTurn(boolean turn) {
        this.turn = turn;
    }

    public boolean getTurn() {
        return turn;
    }

    /**
     * @return the enemys
     */
    protected List<Enemy> getEnemys() {
        return enemies;
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
        StringBuilder listOfEnemys = new StringBuilder();
        int i = 0;
        for (Enemy e : enemies) {
            listOfEnemys.append(e.getId() + " - " + i++ + "\n");
        }
        System.out.println(listOfEnemys);
    }

}

class EnemyNotFoundException extends Throwable {

    /**
     * 
     */
    private static final long serialVersionUID = -8437441282556546464L;

    public EnemyNotFoundException() {
        super();
    }

    public EnemyNotFoundException(String s) {
        super(s);
    }

}
