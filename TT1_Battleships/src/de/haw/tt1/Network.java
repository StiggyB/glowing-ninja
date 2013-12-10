package de.haw.tt1;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * Sets up the chord-network
 * @author Benjamin Burchard
 *
 */
public class Network {
    
    private String host;
    private NCImpl nc = new NCImpl();

    public Network(int port) {
        PropertiesLoader.loadPropertyFile();
        
        Chord chord = new ChordImpl();
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        try {
            chord.setCallback(this.nc);
            chord.create(getChordURL(host, port));
        } catch (ServiceException e) {
            throw new RuntimeException(" Could not create DHT !", e);
        }
    }

    private URL getChordURL(String host, int port) {
        String protocol = URL.KNOWN_PROTOCOLS
                .get(URL.SOCKET_PROTOCOL);
        URL url = null;
        try {
            url = new URL(protocol + "://" + host + ":" + port + "/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

}
