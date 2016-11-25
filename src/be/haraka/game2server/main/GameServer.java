package be.haraka.game2server.main;

import be.haraka.game2server.be.haraka.game2server.players.Player;
import be.haraka.game2server.packets.Packet;
import be.haraka.game2server.packets.Packet00Login;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;


public class GameServer extends Thread
{
	private DatagramSocket socket;
	private List<Player> inGamePlayers;

	public GameServer()
	{
		//Init the object, creating a socket$
		
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
		//Creating a packet of 1024 bytes that we are sending
		while(true)
		{
			System.out.println("Server started...");
			
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

			/*
			String message = new String(packet.getData());
			
			if (message.trim().equalsIgnoreCase("ping"))
			{
				System.out.println("CLIENT ["+packet.getAddress().getHostAddress()+":"+packet.getPort()+"]> " + message);
				sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
			}
			*/
		}
	}

	//On vérifie ce qu'est le packet et en fonction on intéragit
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		switch (type) {
			default:
			case INVALID:
				break;
			case LOGIN:
				//On crée un packet de Login à partir du packet data
				Packet00Login packet = new Packet00Login(data);
				//Vérifie si l'username donné n'existe pas déjà dans les joueurs qui ont déjà été connectés
				for (Player p : inGamePlayers) {
					//Si il  existe dans les joueur qui ont déjà étés connectés, alors il le considère de nouveau en ligne
					if (packet.getUsername() == p.username) {
						p.isOnline  = true;
						System.out.println("["+address.getHostAddress()+":"+port+"] "+packet.getUsername()+" reconnected!");
						break;
					}
					//Si il n'existe pas dans les joueurs qui ont déjà étés connectés, il l'ajoute
					inGamePlayers.add(new Player(address, packet.getUsername()));
					System.out.println("["+address.getHostAddress()+":"+port+"] "+packet.getUsername()+" has connected!");


				}
				break;
			case DISCONNECT:
				break;
		}
	}

	//On  envoye  les données à un client en particulier
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

	//On envoye les données à tout les clients
	public void sendDataToAllClients(byte[] data){
		for (Player p: inGamePlayers) {
			sendData(data, p.ipAdress, p.port);
		}
	}
}
