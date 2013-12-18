package de.haw.tt1;

import java.util.Arrays;

import de.uniba.wiai.lspi.chord.data.ID;

public class Enemy {

    private ID        id;
    private ID        predecessor;
    private int       hits;
    private int       ships;
    private final int intervals;
    private boolean[] map;

    /**
     * Enemy Class, to keep track of our enemy's data an ship losses
     * 
     * @param id
     * @param predecessor
     * @param ships
     * @param intervals
     */
    public Enemy(ID id, ID predecessor, int ships, int intervals) {
        this.id = id;
        this.predecessor = predecessor;
        this.ships = ships;
        this.intervals = intervals;
    }

    /**
     * Files where an enemy got attacked (id) and if a ship got hit
     * 
     * @param id
     *            the enemy was attacked at
     * @param hit
     *            true if enemy ship was hit, false if not
     */
    public void gotAttackedAt(ID id, boolean hit) {
        int interval = calculateInterval(id);
        if (hit)
            hits++;
    }

    /**
     * Calculates the interval where this id is in and returns it
     * 
     * @param id
     *            whose interval is searched
     * @return the interval
     */
    private int calculateInterval(ID id) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean contains(Enemy e) {
        if (!(e instanceof Enemy))
            return false;
        return this.id.equals(e.getId());
    }

    public boolean inRange(ID player) {
        return player.isInInterval(predecessor, id);
    }

    /**
     * Set new Predecessor to this enemy
     * 
     * @param newPredecessor
     *            for this enemy
     * @return the enemy between the old predecessor of this enemy and this
     *         enemy
     */
    public Enemy setNewPredecessor(ID newPredecessor) {
        ID oldPredecessor = predecessor;
        predecessor = newPredecessor;
        return new Enemy(newPredecessor, oldPredecessor, ships,
                intervals);
    }

    /**
     * Returns the number of ships left for this enemy, do not attack if ships
     * == 2!
     * 
     * @return number of ships left
     */
    public int getNumberOfShipsLeft() {
        return ships - hits;
    }

    /**
     * @return the id
     */
    public ID getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Enemy [id=" + id + ",\n predecessor=" + predecessor
                + ",\n ships=" + ships + ", map="
                + Arrays.toString(map) + "]\n\n";
    }

}
