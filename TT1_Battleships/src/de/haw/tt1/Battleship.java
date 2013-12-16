package de.haw.tt1;

import java.math.BigInteger;

import de.uniba.wiai.lspi.chord.data.ID;

public class Battleship {

    private int               I;
    private int               S;
    private boolean[]         map         = null;
    private ID                predecessor = Network.getInstance()
                                                  .getPredecessorID();
    private ID                myID        = Network.getInstance()
                                                  .getChordID();
    private int               shipsLeft;
    private volatile boolean  alive       = true;

    private static Battleship game        = new Battleship();

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

    public void setIntervalsAndShips(int i, int s) {
        if (!(s < i && s > 0))
            throw new IllegalArgumentException("s<i && s>0");
        this.I = i;
        this.S = s;
        shipsLeft = S;
        arrangeShips();
    }

    /**
     * Place ships randomly in the intervals, see checkInterval() for real
     * intervals
     */
    private void arrangeShips() {
        map = new boolean[I];
        int nos = S;
        while (nos > 0) {
            int idx = (int) (Math.random() * I);
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

        System.out.println("My ID: " + myIDInt + " - " + lowerIntervalBorder + " :predecessor ID");
        BigInteger range = myIDInt.subtract(lowerIntervalBorder);
        System.out.println("Range: " + range);
        BigInteger oneStep = range.divide(BigInteger.valueOf(I));
        System.out.println("Step: " + oneStep);

        System.out.println("for - loop:");
        for (int i = 0; i < I - 1; i++) {
            upperIntervalBorder = lowerIntervalBorder.add(oneStep);
            System.out.println("upperIntervalBorder: " + upperIntervalBorder);
            if (id.isInInterval(ID.valueOf(lowerIntervalBorder), ID
                    .valueOf(upperIntervalBorder))) {
                System.out.println("Interval: " + i);
                return i;
            }
            lowerIntervalBorder = upperIntervalBorder;
        }

        if (id.isInInterval(ID.valueOf(lowerIntervalBorder), myID))
            return I - 1;

        return -1;
    }

    /**
     * Check if we got hit.
     * 
     * @param id
     * @return
     */
    public boolean gotHit(ID id) {
        int interval = checkInterval(id);
        if (interval != -1 && map[interval]) {
            map[interval] = false; // ship sunk
            shipsLeft--;
            alive = shipsLeft == 0 ? false : true;
            return true;
        }
        return false;
    }

    public boolean hasStartID() {
        byte[] tmp = new byte[myID.getLength() / 8];
        for (int i = 0; i < myID.getLength() / 8; i++) {
            tmp[i] = (byte) 0xFF;
        }
        return new ID(tmp).isInInterval(predecessor, myID);
    }

    public void logAttack(ID source, ID target, Boolean hit) {

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

}
