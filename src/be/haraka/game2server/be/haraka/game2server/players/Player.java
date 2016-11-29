package be.haraka.game2server.be.haraka.game2server.players;

import java.net.InetAddress;
import java.util.List;


public class Player {

    public InetAddress ipAdress;
    public String username;
    public int port;
    public boolean isOnline;
    public int[] playerGridPos;
    public float[] playerPos;

    public Player(InetAddress ipAdress, String username) {

        //Init new player with ipAdress, username, port
        this.ipAdress = ipAdress;
        this.username = username;
        this.isOnline = true;
        this.playerGridPos = new int[]{0,0};
        this.playerPos = new float[]{0,0};

    }

}
