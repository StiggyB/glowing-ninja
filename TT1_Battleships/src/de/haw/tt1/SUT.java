package de.haw.tt1;

import java.util.Scanner;

import de.uniba.wiai.lspi.chord.data.ID;

/**
 * Main Class to start or join a game
 * 
 * @param args
 */
public class SUT {

    private static Network chord;

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
        int intervals = 100;// in.nextInt();
        // System.out.println("Enter number of ships: ");
        int ships = 10;// in.nextInt();

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
            Battleship.getInstance().setTurn(true);
        }

        printCommands();

        ID target;
        // q also leads to the main game loop
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
                                Battleship.getInstance().getEnemys()
                                        .get(Integer.parseInt(cmd)));
                    else {
                        Battleship.getInstance().attackBestTarget();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (EnemyNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        // this is the main game loop
        while (Battleship.getInstance().isAlive()) {
            if (Battleship.getInstance().hasTurn()) {
                try {
                    Battleship.getInstance().attackBestTarget();
                } catch (EnemyNotFoundException e1) {
                    e1.printStackTrace();
                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // loosing loop
        while (!cmd.equals("exit")) {
            System.out.println(":'-(");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // stop input & chord
        in.close();
        chord.leave();
    }

    private static void printCommands() {
        System.out.println("-- Help --");
        System.out.println("h - show help");
        System.out.println("q - quit game and start automatism");
        System.out.println("s - shoot randomly");
        System.out
                .println("map - print location of our ships and fingetable");
        System.out.println("e - print enemys");
        System.out.println("a - attack enemy");
    }

}
