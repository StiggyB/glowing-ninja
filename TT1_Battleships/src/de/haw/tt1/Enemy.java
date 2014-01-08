package de.haw.tt1;

import java.math.BigInteger;
import java.util.Arrays;

import de.uniba.wiai.lspi.chord.data.ID;

public class Enemy {

    private ID               enemyID;
    private ID               predecessorID;
    private int              hits;
    private boolean[]        map;
    private BigInteger       intervalSize;
    private int              nIntervals;
    private boolean          isDefeated;
    private int              nShips;
    private boolean[]        attackedIntervals;
    private final BigInteger maxVal = (new BigInteger("2").pow(160))
                                            .subtract(new BigInteger(
                                                    "1"));

    /**
     * Enemy Class, to keep track of our enemy's data an ship losses. Takes
     * Enemys Id an it's predecessor ID as parameters
     * 
     * @param enemyID
     * @param predecessorID
     */
    public Enemy(ID enemyID, ID predecessorID, int nIntervals,
            int nShips) {
        this.enemyID = enemyID;
        this.predecessorID = predecessorID;
        this.nIntervals = nIntervals;
        this.isDefeated = false;
        this.nShips = nShips;
        calcIntervalSize();
        attackedIntervals = new boolean[nIntervals];
    }

    /**
     * Files where an enemy got attacked (id/interval) and if a ship got hit
     * 
     * @param id
     *            the enemy was attacked at
     * @param hit
     *            true if enemy ship was hit, false if not
     */
    public void gotAttackedAt(ID id, boolean hit) {
        if (hit && !attackedIntervals[calculateInterval(id)])
            hits++;
        
        attackedIntervals[calculateInterval(id)] = true;
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

        for (int i = 0; i < nIntervals; i++) {
            upperIntervalBorder = lowerIntervalBorder.add(
                    intervalSize).add(maxVal).mod(maxVal);

            if (id.isInInterval(ID.valueOf(lowerIntervalBorder), ID
                    .valueOf(upperIntervalBorder))) {
                System.out.println("[enemy] attacked interval: " + i);
                return i;
            }
            lowerIntervalBorder = upperIntervalBorder;
        }

        System.err
                .println("[enemy] Interval not found, that should not happen");

        return -1;
    }

    private void calcIntervalSize() {
        System.out.println("[enemy/calcIntervalSize] id length="
                + enemyID.getLength());

        BigInteger enemyIDBigInt = enemyID.toBigInteger();
        BigInteger predecessorIDBigInt = predecessorID.toBigInteger();
        System.out.println("[enemy/calcIntervalSize] PredecessorID="
                + predecessorID);
        BigInteger range = enemyIDBigInt
                .subtract(predecessorIDBigInt);

        System.out.println("[enemy/calcIntervalSize] range=" + range);
        range = range.add(maxVal).mod(maxVal);
        System.out.println("[enemy/calcIntervalSize] rangeWithPower="
                + range);

        intervalSize = range.divide(BigInteger.valueOf(nIntervals));

        System.out.println("[enemy/calcIntervalSize] IntervalSize="
                + intervalSize + " of Enemy " + enemyID);
    }

    /**
     * Get ID in given intervall i
     * 
     * @param i interval
     * @return ID in interval i
     */
    public ID getIdInInterval(int i) {

        BigInteger predecessorIDBigInt = predecessorID.toBigInteger();
        BigInteger iBigInt = BigInteger.valueOf(i);

        BigInteger intervalBigInt = (intervalSize.multiply(iBigInt)).add(BigInteger.ONE);

        return ID.valueOf(predecessorIDBigInt.add(intervalBigInt)
                .add(maxVal).mod(maxVal));
    }

    public boolean equals(Enemy e) {
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
        System.out.println("New Predecessor set: " + newPredecessor
                + ", old: " + oldPredecessor);
        predecessorID = newPredecessor;
        calcIntervalSize();
        return new Enemy(newPredecessor, oldPredecessor, nIntervals,
                nShips);
    }

    /**
     * Returns the amount of hits this enemy retreived Not attack if hits -
     * nShips == 2!
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

    /**
     * @return the attackedIntervals
     */
    protected boolean[] getAttackedIntervals() {
        return attackedIntervals;
    }

    /**
     * @param attackedIntervals
     *            the attackedIntervals to set
     */
    protected void setAttackedIntervals(boolean[] attackedIntervals) {
        this.attackedIntervals = attackedIntervals;
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

    public boolean checkDefeated() {
        if (nShips > this.hits) {
            return false;
        }
        return true;
    }

    public void setDefeated(boolean isDefeated) {
        this.isDefeated = isDefeated;
    }

    public boolean checkAndSetDefeated() {
        boolean defeated = checkDefeated();
        setDefeated(defeated);
        return defeated;
    }

}
