package be.haraka.game2server.main;

import be.haraka.game2server.be.haraka.game2server.players.Player;
import be.haraka.game2server.packets.Packet;
import be.haraka.game2server.packets.Packet00Login;
import be.haraka.game2server.packets.Packet01Disconnect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;


public class GameServer extends Thread
{
	private DatagramSocket socket;
	private ArrayList<Player> inGamePlayers = new ArrayList<Player>();

	public GameServer()
	{
		//Initializing the object, creating a socket, initializing the inGamePlayers
		
			try 
			{
				this.socket = new DatagramSocket(1331);
			} 
			
			catch (SocketException e1) 
			{
				e1.printStackTrace();
			}

			
		}

	public void run()
	{
		System.out.println("Server started...");
		//Creating a packet of 1024 bytes that we are sending
		while(true)
		{
			
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			try 
			{
				socket.receive(packet);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}

			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	//We check the packet Type and act about it
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		Packet packet = null;

		Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		switch (type) {
			default:
				
			case INVALID:
				break;
				
			case LOGIN:
				//We give login packet attributes to the packet
				packet = new Packet00Login(data);
				//We use the playerLogin function
				playerLogin(inGamePlayers,((Packet00Login) packet).getUsername(),address);
				break;

			case DISCONNECT:
				//We give disconnect packet attributes to the packet
				packet  = new Packet01Disconnect(data);
				System.out.println("Received disconnect packet from username: "+((Packet01Disconnect)packet).getUsername());

				//We use the playerDisconnect function
				playerDisconnect(inGamePlayers,((Packet01Disconnect)packet).getUsername());
				break;
		}
	}

	//We send a packet to a target client
	public void sendData(byte[] data,InetAddress ipAddress, int port)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
		
		try 
		{
			socket.send(packet);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	//We send a packet to all clients
	public void sendDataToAllClients(byte[] data){
		for (Player p: inGamePlayers) {
			sendData(data, p.ipAdress, p.port);
		}
	}

	
	//We manage a login packet
	private void playerLogin(ArrayList<Player> inGamePlayers, String username, InetAddress ipAdress) {
		for (Player p : inGamePlayers) {
			if (username.equals(p.username)) {
				System.out.println(p.username+" reconnected!");
				p.isOnline = true;
				return;
				}
			}
			inGamePlayers.add(new Player(ipAdress,username));
			System.out.println(username+" connected!");
		}
	
	//We manage a disconnect packet
	private void playerDisconnect(ArrayList<Player> inGamePlayers, String username) {
		for (Player p : inGamePlayers) {
			if (username.equals(p.username)) {
				System.out.println(p.username+" disconnected!");
				p.isOnline = false;
				return;
			}
		}
		
	}
}
