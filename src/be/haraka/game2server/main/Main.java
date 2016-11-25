package be.haraka.game2server.main;

public class Main {

	public static void main(String[] args) 
	{
		System.out.println("-----[Haraka game2 server]-----");
		GameServer socketServer = new GameServer();
		socketServer.start();

	}

}
