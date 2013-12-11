package de.haw.tt1;

public class SUT {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        String cmd = args[0];
        String host = args[1];
        int port = Integer.parseInt(args[2]);
        
        Network chord = new Network(port);

        if (cmd.equals("-c")) {
            chord.create();
        } else if (cmd.equals("-j")) {
            chord.join(host, port);
        } else if (cmd.equals("-l")) {
            chord.leave();
        } else if (cmd.equals("-s")) {
            chord.shoot();
        }
        
//        chord.create();
        System.out.println("Chord ID:" + chord.getChordID());
    }

}
