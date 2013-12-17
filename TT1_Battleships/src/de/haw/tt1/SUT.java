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
        } else if (cmd.equals("-h")) {
            // help
        }

        System.out.println("Chord ID: " + chord.getChordID());

        Scanner in = new Scanner(System.in);

        System.out.println("Standard Setup 10/5, see code...");
        // System.out.println("Enter number of intervals: ");
        int intervals = 10;// in.nextInt();
        // System.out.println("Enter number of ships: ");
        int ships = 5;// in.nextInt();

        System.out.println("Bleiebige Taste zum Starten drücken.");
        in.next();

        System.out
                .println("Predecessor: " + chord.getPredecessorID());

        Battleship.getInstance().setIntervalsAndShips(intervals, ships);

        System.out
                .println("They ain't gonna sink this battleship, no way!");
        System.out.println("Game is on!");

        if (Battleship.getInstance().hasStartID()) {
            System.out.println("WE START!");
        }

        chord.getChord().broadcast(chord.getChordID(), true);

        ID target;
        while (!cmd.equals("q")) {
            cmd = in.nextLine();
            if (cmd.equals("s")) {
                do {
                    target = chord.getRandomID();
                } while (target.isInInterval(
                        chord.getPredecessorID(), chord.getChordID()));
                chord.shoot(target);
            } else if (cmd.equals("map")) {
                Battleship.getInstance().showShips();
                Battleship.getInstance().printFingerTable();
            }
        }

        // this will be the main game loop
//        while (Battleship.getInstance().isAlive()) {
//            if (Battleship.getInstance().getTurn()) {
//                do {
//                    target = chord.getRandomID();
//                } while (target.isInInterval(
//                        chord.getPredecessorID(), chord.getChordID()));
//                chord.shoot(target);
//            }
//        }

        // stop input
        in.close();
        chord.leave();
    }

}
