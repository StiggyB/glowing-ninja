package de.haw.tt1;

import de.uniba.wiai.lspi.chord.data.ID;

public class Battleship {

    private int         I;
    private int         S;
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
    private Battleship() {}
    
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
    private void checkInterval(ID id) {
//        for (int i = 0; i < I - 1; i++) {
//        }
        //TODO: Somehow calculate equidistant intervals for IDs
        // like:
        // if id isInInterval(LowerIntervalBorder, UpperIntervalBorder)
        //      return true;
        if (id.isInInterval(predecessor, myID)) {
            System.out.println("could have hitted");
        }
    }

    /**
     * Check if we got hit. Always returns false atm
     * @param id
     * @return
     */
    public boolean gotHit(ID id) {
        checkInterval(id);
        return false;
    }

}
