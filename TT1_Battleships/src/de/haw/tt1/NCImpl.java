package de.haw.tt1;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;

public class NCImpl implements NotifyCallback {

    
    @Override
    public void retrieved(ID target) {
        boolean hit = Battleship.getInstance().gotHit(target);
        Network.getInstance().getChord().broadcast(target, hit);
        System.out.println("retrieved - " + target.toString()
                + " has been attacked");
    }

    @Override
    public void broadcast(ID source, ID target, Boolean hit) {
        // TODO Auto-generated method stub
        // here we get the info who attacked whom and if it was a hit, right?
        System.out.println("broadcast - from: " + source.toString()
                + "\nto: " + target.toString() + "\n hit?: " + hit);
    }

}
