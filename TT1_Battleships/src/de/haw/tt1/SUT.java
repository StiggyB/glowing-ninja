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
            //help
        }

        System.out.println("Chord ID: " + chord.getChordID().toBigInteger());

        Scanner in = new Scanner(System.in);
        
        System.out.println("Standard Setup 10/5, see code...");
//        System.out.println("Enter number of intervals: ");
        int i = 10;//in.nextInt();
//        System.out.println("Enter number of ships: ");
        int s = 5;//in.nextInt();
        
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Predecessor: " + chord.getPredecessorID().toBigInteger());
        
        Battleship.getInstance().setIntervalsAndShips(i, s);
        
        
        System.out.println("They ain't gonna sink this battleship, no way!");
        System.out.println("Game is on!");
        
        if (Battleship.getInstance().hasStartID()) {
            System.out.println("WE START!");
        }
        
        ID target;
        while (!cmd.equals("q")) {

            cmd = in.nextLine();
            if (cmd.equals("s")) {
                do {
                    target = chord.getRandomID();
                } while (target.isInInterval(chord.getPredecessorID(), chord.getChordID()));
                chord.shoot(target);
            }

        }
        
        //this will be the main game loop
//        while (Battleship.getInstance().isAlive()) {
//            
//        }
        
        // stop input
        in.close();
        chord.leave();
    }

}
