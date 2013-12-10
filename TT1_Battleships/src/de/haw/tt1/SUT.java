package de.haw.tt1;

public class SUT {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        Network chord = new Network(8080);
        chord.create();
        System.out.println("Chord ID:" + chord.getChordID());
    }

}
