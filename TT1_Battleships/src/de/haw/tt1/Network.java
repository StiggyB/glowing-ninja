package de.haw.tt1;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Random;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * Sets up the chord-network
 * 
 * @author Benjamin Burchard
 * 
 */
public class Network {

    private String host;
    private int    port;
    private String protocol;
    private NCImpl nc = new NCImpl();
    private Chord  chord;
    private URL    chordURL;
    private ID     chordID;

    public Network(int port) {
        PropertiesLoader.loadPropertyFile();

        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        this.port = port;

        this.chordURL = getChordURL(host, this.port);
        this.chord = new ChordImpl();
        this.chord.setURL(chordURL);
        this.chord.setID(getRandomID()); 
        this.chord.setCallback(nc);
        this.chordID = chord.getID();
    }

    private ID getRandomID() {
        byte[] bytes = new byte[8];
        new Random().nextBytes(bytes);
        return new ID(bytes);
    }

    public void create() {
        try {
            chord.create(chordURL);
        } catch (ServiceException e) {
            throw new RuntimeException("Could not create DHT!", e);
        }
    }

    public void join(String host, int port) {
        URL bootstrapURL = getChordURL(host, port);
        try {
            chord.join(chordURL, bootstrapURL);
            System.out.println("Joined chord network: "
                    + bootstrapURL.toString());
        } catch (ServiceException e) {
            throw new RuntimeException("Could not join DHT!", e);
        }
    }

    public void leaveChord() {
        try {
            chord.leave();
            System.out.println("Left chord network");
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private URL getChordURL(String host, int port) {
        protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
        URL url = null;
        try {
            url = new URL(protocol + "://" + host + ":" + port + "/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    public Chord getChord() {
        return chord;
    }

    public ID getChordID() {
        return chordID;
    }

}
