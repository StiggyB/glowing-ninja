package de.haw.tt1;

import java.io.*;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;

public class NCImpl implements NotifyCallback {

    @Override
    public void retrieved(ID target) {
        boolean hit = Battleship.getInstance().gotHit(target);
        Network.getInstance().getChord().broadcast(target, hit);
        System.out.printf(
                "[retreived] Enemy attacks at %s\n and %s\n", target,
                hit ? "hits target." : "misses target."); // fancy!

        if (Battleship.getInstance().getShipsLeft() <= 0) {
            InputStream in;
            try {
                in = new FileInputStream(
                        new File(
                                "C:\\Users\\Benjamin\\git\\glowing-ninja\\TT1_Battleships\\DEATH.WAV"));
                AudioStream as;
                as = new AudioStream(in);
                AudioPlayer.player.start(as);
                Battleship.getInstance().hasTurn(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[retreived] All our ships sunk!");
        } else {
            Battleship.getInstance().hasTurn(true);
        }
    }

    @Override
    public void broadcast(ID source, ID target, Boolean hit) {
        // TODO Auto-generated method stub
        // here we get the info who attacked whom and if it was a hit, right?
        Battleship.getInstance().logAttack(source, target, hit);
        System.out.printf(
                "[broadcast] %s \n was attacked at %s \n and %s\n",
                source, target, hit ? "got hit." : "missed.");
    }

}
