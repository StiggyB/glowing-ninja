package de.haw.tt1;

import java.util.Scanner;

import de.uniba.wiai.lspi.chord.data.ID;

public class SUT {

    private static Network chord;

    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("Battleship game warm up...");
        
        String cmd = args[0];
        int port = Integer.parseInt(args[1]);
        Network.getInstance().setURLPort(port);
        chord = Network.getInstance();

        if (cmd.equals("-c")) {
            chord.create();
        } else if (cmd.equals("-j")) {
            String host = args[2];
            int bport = Integer.parseInt(args[3]);
            chord.join(host, bport);
        } else if (cmd.equals("-l")) {
            chord.leave();
        } else if (cmd.equals("-h")) {
            
        }

        System.out.println("Chord ID:" + chord.getChordID());

        Scanner in = new Scanner(System.in);
        
        System.out.println("Enter number of intervals: ");
        int i = in.nextInt();
        System.out.println("Enter number of ships: ");
        int s = in.nextInt();
        
        Battleship.getInstance().setIntervalsAndShips(i, s);
        
        System.out.println("They ain't gonna sink this battleship, no way!");
        System.out.println("Game is on!");
        
        ID target;
        while (!cmd.equals("q")) {

            cmd = in.nextLine();
            if (cmd.equals("s")) {
                do {
                    target = chord.getRandomID();
                    System.out.println("Predecessor: " + chord.getPredecessorID().toString());
                } while (target.isInInterval(chord.getPredecessorID(), chord.getChordID()));
                chord.shoot(target);
            }

        }
        // stop input
        in.close();
        chord.leave();
    }

}
