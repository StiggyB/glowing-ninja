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
     * Should calculate the intervals in our DHT space
     */
    private int checkInterval(ID id) {
        BigInteger upperIntervalBorder = null;
        BigInteger lowerIntervalBorder = predecessor.toBigInteger();
        BigInteger myIDInt = myID.toBigInteger();

        BigInteger range = myIDInt.subtract(lowerIntervalBorder);
        BigInteger oneStep = range.divide(BigInteger.valueOf(I));

        
        for (int i = 0; i < I - 1; i++) {
            upperIntervalBorder = lowerIntervalBorder.add(oneStep);
            if (id.isInInterval(ID.valueOf(lowerIntervalBorder), ID
                    .valueOf(upperIntervalBorder))) {
                return i;
            }
            lowerIntervalBorder = upperIntervalBorder;
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
        int interval = checkInterval(id); 
        if (interval != -1 && map[interval]) {
            map[interval] = false; // ship sunk
            return true;
        }
        return false;
    }

}
