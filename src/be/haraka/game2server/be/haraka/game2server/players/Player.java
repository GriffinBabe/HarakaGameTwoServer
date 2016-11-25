package be.haraka.game2server.be.haraka.game2server.players;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class Player {

    public InetAddress ipAdress;
    public String username;
    public int port;
    public boolean isOnline;

    public Player(InetAddress ipAdress, String username) {

        //Init new player with ipAdress, username, port
        this.ipAdress = ipAdress;
        this.username = username;
        this.isOnline = true;

    }

}
