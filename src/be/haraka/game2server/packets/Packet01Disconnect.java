package be.haraka.game2server.packets;

import be.haraka.game2server.main.GameServer;

public class Packet01Disconnect extends Packet {

    private String username;

    public Packet01Disconnect(byte[] data) {
        super(01);
        this.username = readData(data);
    }

    public Packet01Disconnect(String username){
        super(01);
        this.username = username;
    }

    @Override
    public void writeData(GameServer server) {
        server.sendDataToAllClients(getData());

    }


    public String getUsername() {
        return username;
    }

    @Override
    public byte[] getData() {
        return ("01" + this.username).getBytes();
    }



}
