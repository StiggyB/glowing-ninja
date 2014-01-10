package de.haw.tt1;

import java.io.*;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;

/**
 * NCImpl class, implementation of the NotifyCallback interface with the
 * retrieved and broadcast method.
 */
public class NCImpl implements NotifyCallback {

    @Override
    public void retrieved(ID target) {
        boolean hit = Battleship.getInstance().gotHit(target);
        Network.getInstance().getChord().broadcast(target, hit);
        System.out.printf(
                "[retreived]\t Enemy attacks at %s\n \t and %s\n",
                target, hit ? "hits target." : "misses target."); // fancy!

        if (Battleship.getInstance().getShipsLeft() <= 0) {
            InputStream in;
            try {
                in = new FileInputStream(
                        new File(
                                "C:\\Users\\Benjamin\\git\\glowing-ninja\\TT1_Battleships\\DEATH.WAV"));
                AudioStream as;
                as = new AudioStream(in);
                AudioPlayer.player.start(as);
                Battleship.getInstance().setTurn(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[retreived] All our ships sunk!");
        } else {
            Battleship.getInstance().setTurn(true);
        }
        System.err.println("We have "
                + Battleship.getInstance().getShipsLeft()
                + " ships left...");
    }

    @Override
    public void broadcast(ID source, ID target, Boolean hit) {
        Battleship.getInstance().logAttack(source, target, hit);
        System.out
                .printf("[broadcast] \t %s \n \t was attacked at %s \n \t and %s\n",
                        source, target, hit ? "got hit." : "missed.");
    }

}
