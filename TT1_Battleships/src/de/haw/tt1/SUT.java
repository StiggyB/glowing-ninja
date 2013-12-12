package de.haw.tt1;

import java.util.Scanner;

import de.uniba.wiai.lspi.chord.data.ID;

public class SUT {

    /**
     * @param args
     */
    public static void main(String[] args) {

        String cmd = args[0];
        int port = Integer.parseInt(args[1]);

        Network chord = new Network(port);

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
        ID target;
        while (!cmd.equals("q")) {

            cmd = in.nextLine();
            int i = 0;
            if (cmd.equals("s")) {
                do {
                    target = chord.getRandomID();
                    System.out.println(i++);
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
