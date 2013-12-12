package de.haw.tt1;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;

public class NCImpl implements NotifyCallback {

    @Override
    public void retrieved(ID target) {
        // TODO Auto-generated method stub
        System.out.println("retrieved - " + target.toString() + " has been attacked");
    }

    @Override
    public void broadcast(ID source, ID target, Boolean hit) {
        // TODO Auto-generated method stub
        System.out.println("broadcast - from: " + source.toString() + "\nto: " + target.toString() + "\n hit?: " + hit);
    }

}
