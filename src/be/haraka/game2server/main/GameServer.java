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
import java.util.List;


public class GameServer extends Thread
{
	private DatagramSocket socket;
	private ArrayList<Player> inGamePlayers = new ArrayList<Player>();

	public GameServer()
	{
		//Init the object, creating a socket, init the inGamePlayers
		
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

	//On vérifie ce qu'est le packet et en fonction on intéragit
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		Packet packet = null;

		Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		switch (type) {
			default:
				
			case INVALID:
				break;
				
			case LOGIN:
				//On crée un packet de Login à partir du packet data
				packet = new Packet00Login(data);

				//Vérifie si la liste à déjà un élément
				if (inGamePlayers.size() == 0){
					inGamePlayers.add(new Player(address, ((Packet00Login)packet).getUsername()));
					System.out.println("First player connected since server start");
					System.out.println("["+address.getHostAddress()+":"+port+"] "+((Packet00Login)packet).getUsername()+" has connected!");
				}
				else{
					//Vérifie si l'username donné n'existe pas déjà dans les joueurs qui ont déjà été connectés
					for (Player p : inGamePlayers) {
						//Si il  existe dans les joueur qui ont déjà étés connectés, alors il le considère de nouveau en ligne
						if (((Packet00Login)packet).getUsername() == p.username) {
							p.isOnline = true;
							System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login)packet).getUsername() + " reconnected!");
							break;
						}
						//Si il n'existe pas dans les joueurs qui ont déjà étés connectés, il l'ajoute
						inGamePlayers.add(new Player(address, ((Packet00Login)packet).getUsername()));
						System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login)packet).getUsername() + " has connected!");
					}
				}
				break;

			case DISCONNECT:
				packet  = new Packet01Disconnect(data);
				System.out.println("Received disconnect packet from username: "+((Packet01Disconnect)packet).getUsername());

				//Cherche le player avec l'username correspodant et le met en offline
				for (Player p : inGamePlayers) {
					System.out.println(p.username);
					if ((String) p.username == (String) ((Packet01Disconnect) packet).getUsername()) {
						System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login)packet).getUsername() + " disconnected!");
						p.isOnline = false;
						break;
					}
				}
				break;
		}
	}

	//On  envoye  les données à un client d'une adresse en particulier
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
