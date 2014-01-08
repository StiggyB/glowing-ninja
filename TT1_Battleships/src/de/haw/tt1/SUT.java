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
        boolean hoster = false;

        if (cmd.equals("-c")) {
            chord.create();
            hoster = true;
        } else if (cmd.equals("-j")) {
            String host = args[2];
            int bport = Integer.parseInt(args[3]);
            chord.join(host, bport);
        }

        System.out.println("Chord ID: " + chord.getChordID());

        Scanner in = new Scanner(System.in);

        System.out.println("Standard Setup 10/5, see code...");
        // System.out.println("Enter number of intervals: ");
        int intervals = 10;// in.nextInt();
        // System.out.println("Enter number of ships: ");
        int ships = 5;// in.nextInt();

        if (hoster) {
            System.out.println("Waiting for players...");
            boolean playersReady = false;
            System.out
                    .println("Press Enter if all Players are ready.");
            while (chord.getChord().getFingerTable().isEmpty()
                    && !playersReady) {
                if (in.nextLine().equals(""))
                    playersReady = true;
            }
        } else {
            System.out.println("Waiting...");
            in.nextLine();
        }

        System.out
                .println("Predecessor: " + chord.getPredecessorID());

        Battleship.getInstance().setIntervalsAndShips(intervals,
                ships);

        System.out
                .println("They ain't gonna sink this battleship, no way!");
        System.out.println("Game is on!");

        if (Battleship.getInstance().hasStartID()) {
            System.out.println("WE START!");
            Battleship.getInstance().hasTurn(true);
        }

        // chord.getChord().broadcast(chord.getChordID(), true);
        printCommands();

        ID target;
        while (!cmd.equals("q")) {
            cmd = in.nextLine();
            if (cmd.equals("h")) {
                printCommands();
            } else if (cmd.equals("s")
                    && Battleship.getInstance().isAlive()) {
                do {
                    target = chord.getRandomID();
                } while (target.isInInterval(
                        chord.getPredecessorID(), chord.getChordID()));
                chord.shoot(target);
            } else if (cmd.equals("map")) {
                Battleship.getInstance().showShips();
                Battleship.getInstance().printFingerTable();
            } else if (cmd.equals("e")) {
                Battleship.getInstance().printEnemys();
            } else if (cmd.equals("a")) {
                System.out
                        .println("Enter number of enemy or a String for best enemy");
                cmd = in.nextLine();
                try {
                    if (cmd.matches("^[0-9]+$"))
                        Battleship.getInstance().attackEnemy(
                                Integer.parseInt(cmd));
                    else {
                        Battleship.getInstance().attackEnemy();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (EnemyNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        // this will be the main game loop
        // while (Battleship.getInstance().isAlive()) {
        // if (Battleship.getInstance().getTurn()) {
        // do {
        // target = chord.getRandomID();
        // } while (target.isInInterval(
        // chord.getPredecessorID(), chord.getChordID()));
        // chord.shoot(target);
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        // }

        // stop input
        in.close();
        chord.leave();
    }

    private static void printCommands() {
        System.out.println("-- Help --");
        System.out.println("h - show help");
        System.out.println("q - quit game");
        System.out.println("s - shoot randomly");
        System.out
                .println("map - print location of our ships and fingetable");
        System.out.println("e - print enemys");
        System.out.println("a - attack enemy");
    }

}
