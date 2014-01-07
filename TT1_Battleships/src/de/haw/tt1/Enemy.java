package de.haw.tt1;

import java.math.BigInteger;
import java.util.Arrays;

import de.uniba.wiai.lspi.chord.data.ID;

public class Enemy {

    private ID         enemyID;
    private ID         predecessorID;
    private int        hits;
    private boolean[]  map;
    private BigInteger intervalSize;
    private int        nIntervals;
    private boolean	   isDefeated;
    private int 	   nShips;

    /**
     * Enemy Class, to keep track of our enemy's data an ship losses. Takes
     * Enemys Id an it's predecessor ID as parameters
     * 
     * @param enemyID
     * @param predecessorID
     */
    public Enemy(ID enemyID, ID predecessorID, int nIntervals, int nShips) {
        this.enemyID = enemyID;
        this.predecessorID = predecessorID;
        this.nIntervals = nIntervals;
        this.isDefeated = false;
        this.nShips = nShips;
        calcIntervalSize();
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
        
        this.checkAndSetDefeated();
    }

    /**
     * Calculates the interval where this id is in and returns it Wrap around
     * should work with .addPowerOfTwo(160)
     * 
     * @param id
     *            whose interval is searched
     * @return the interval
     */
    private int calculateInterval(ID id) {
        BigInteger upperIntervalBorder = null;
        BigInteger lowerIntervalBorder = predecessorID.toBigInteger();

        for (int i = 0; i < nIntervals - 1; i++) {
            upperIntervalBorder = lowerIntervalBorder
                    .add(intervalSize);
            if (ID.valueOf(
                    id.addPowerOfTwo(enemyID.getLength() - 1)
                            .toBigInteger().add(new BigInteger("-1")))
                    .isInInterval(ID.valueOf(lowerIntervalBorder),
                            ID.valueOf(upperIntervalBorder))) {
                System.out.println("[enemy] Interval: " + i);
                return i;
            }
            lowerIntervalBorder = upperIntervalBorder;
        }
        System.out
                .println("[enemy] Interval not found, that should not happen");
        return -1;
    }

    private void calcIntervalSize() {
        BigInteger range = enemyID.addPowerOfTwo(
                enemyID.getLength() - 1).toBigInteger().add(
                new BigInteger("-1")).subtract(
                predecessorID.toBigInteger());
        intervalSize = range.divide(BigInteger.valueOf(nIntervals));
    }

    public boolean contains(Enemy e) {
        if (!(e instanceof Enemy))
            return false;
        return this.enemyID.equals(e.getId());
    }

    public boolean inRange(ID player) {
        return player.isInInterval(predecessorID, enemyID);
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
        ID oldPredecessor = predecessorID;
        predecessorID = newPredecessor;
        return new Enemy(newPredecessor, oldPredecessor, nIntervals,nShips);
    }

    /**
     * Returns the number of ships left for this enemy, do not attack if ships
     * == 2!
     * 
     * @return number of ships left
     */
    public int getNumberOfHits() {
        return hits;
    }

    /**
     * 
     * @return enemyID
     */
    public ID getId() {
        return enemyID;
    }

    /**
     * 
     * @return predecessorID
     */
    public ID getPredecesorID() {
        return predecessorID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Enemy [id=" + enemyID + ",\n predecessor="
                + predecessorID + ",\n " + ", map="
                + Arrays.toString(map) + "]\n\n";
    }
    
    

    public boolean isDefeated() {
		return isDefeated;
	}

    public boolean checkDefeated(){
    	if (nShips > this.hits){
    		return false;
    	}
    	return true;
    }
    
	public void setDefeated(boolean isDefeated) {
		this.isDefeated = isDefeated;
	}
	
	public boolean checkAndSetDefeated(){
		boolean defeated = checkDefeated();
    	setDefeated(defeated);
    	return defeated;
	}

}
