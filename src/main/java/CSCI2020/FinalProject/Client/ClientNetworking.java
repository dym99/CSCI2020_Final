package CSCI2020.FinalProject.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientNetworking {
	public static boolean Connect(String address, int port) {
		try {
			socket = new Socket(address, port);
			
			toServer = new DataOutputStream(socket.getOutputStream());
			fromServer = new DataInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean Send(String _message) {
		try {
			toServer.writeUTF(_message);
			toServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String Recv() {
		if (fromServer != null) {
			try {
				String message = fromServer.readUTF();
				System.out.println(message);
				return message;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No message");
				return "";
			}
		} else {
			System.out.println("fromServer is null");
			int i = 1/0;
			return "";
		}
	}
	
	private static Socket socket;
	private static DataOutputStream toServer;
	private static DataInputStream fromServer;
}
