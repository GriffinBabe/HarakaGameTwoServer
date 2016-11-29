package be.haraka.game2server.packets;

import be.haraka.game2server.main.GameServer;

public class Packet02GetPlayersStates extends Packet {

	public Packet02GetPlayersStates(byte[] data) {
		super(02);
	}

	@Override
	public void writeData(GameServer server) {
		
	}

	@Override
	public byte[] getData() {
		return null;
	}
	
	

}
