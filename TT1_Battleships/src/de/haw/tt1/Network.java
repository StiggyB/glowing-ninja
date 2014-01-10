package de.haw.tt1;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * Network class, sets up the chord-network.
 * 
 * @author Benjamin Burchard
 * 
 */
public class Network {

    private String         localHost;
    private int            localPort = 0;
    private String         protocol;
    private NCImpl         nc        = new NCImpl();
    private ChordImpl      chord;
    private URL            nodeURL;
    private ID             chordID;

    private static Network instance  = new Network();

    private Network(int localPort) {
        PropertiesLoader.loadPropertyFile();

        // this.chordID = getRandomID();
        try {
            localHost = // "localhost";
            InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        this.localPort = localPort;
        this.nodeURL = getChordURL(this.localHost, this.localPort);
        this.chord = new ChordImpl();
        this.chord.setURL(this.nodeURL);
        this.chord.setCallback(nc);
    }

    private Network() {
        PropertiesLoader.loadPropertyFile();

        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        this.chord = new ChordImpl();
        this.chord.setCallback(nc);
    }

    /**
     * Return the instance of this network
     * 
     * @return this
     */
    public static Network getInstance() {
        return instance;
    }

    /**
     * Generate random ID
     * 
     * @return ID random
     */
    public ID getRandomID() {
        // divide by 8 to get bytes, getLength measures in bits
        // and length of IDs has to be the same
        byte[] r = new byte[chordID.getLength() / 8];
        for (int i = 0; i < r.length; i++) {
            r[i] = (byte) (Math.random() * 0xFF);
        }
        return (new ID(r));
    }

    /**
     * Set the chord URL port.
     * 
     * @param localPort
     */
    public void setURLPort(int localPort) {
        this.nodeURL = getChordURL(this.localHost,
                this.localPort = localPort);
        this.chord.setURL(nodeURL);
    }

    /**
     * Create a chord network.
     */
    public void create() {
        try {
            chord.create(nodeURL);
            chordID = chord.getID();
            System.out.println("Created chord on " + nodeURL);
        } catch (ServiceException e) {
            throw new RuntimeException("Could not create DHT!", e);
        }
    }

    /**
     * Join a chord network.
     * 
     * @param host
     *            to join
     * @param port
     *            at the host to join
     */
    public void join(String host, int port) {
        URL bootstrapURL = getChordURL(host, port);
        try {
            chord.join(bootstrapURL);
            chordID = chord.getID();
            System.out.println("Joined chord network: "
                    + bootstrapURL.toString());
        } catch (ServiceException e) {
            throw new RuntimeException("Could not join DHT!", e);
        }
    }

    /**
     * Leave the chord network
     */
    public void leave() {
        chord.leave();
        System.out.println("Left chord network");
    }

    /**
     * Shoot at id
     * 
     * @param id
     *            to shoot at
     */
    public void shoot(ID id) {
        chord.retrieve(id);
    }

    /**
     * Build chord URL with host and port.
     * 
     * @param host
     *            host name
     * @param port
     *            port number
     * @return url the chord URL
     */
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

    /**
     * Returns the chord implementation.
     * 
     * @return chord
     */
    public ChordImpl getChord() {
        return chord;
    }

    /**
     * Returns the chord ID
     * 
     * @return chordID
     */
    public ID getChordID() {
        return chordID;
    }

    /**
     * Returns the predecessor ID
     * 
     * @return predecessor ID
     */
    public ID getPredecessorID() {
        return chord.getPredecessorID();
    }

}
