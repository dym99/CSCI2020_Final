package CSCI2020.FinalProject.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Platform;

public class ClientNetworking {
	public static void SetChatScreen(ChatScreen _screen) {
		chatScreen = _screen;
	}
	
	public static boolean Connect(String address, int port) {
		try {
			socket = new Socket(address, port);
			
			toServer = new DataOutputStream(socket.getOutputStream());
			fromServer = new DataInputStream(socket.getInputStream());
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void Disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			} catch (SocketException se) {
				System.out.println("Server has kicked you!");

				Platform.runLater(()->{
					chatScreen.disconnectClient("The connection was forcibly closed by the remote host.");
				});

				return "";
			} catch (EOFException e) {
				System.out.println("Server is not responding.");
				Platform.runLater(()->{
					chatScreen.disconnectClient("The server stopped responding.");
				});
				return "";
			} catch (IOException e) {
				e.printStackTrace();
				Platform.runLater(()->{
					chatScreen.disconnectClient("An unknown error has occored.");
				});
				return "";
			}
		} else {
			System.out.println("fromServer is null");
			return "";
		}
	}
	
	private static Socket socket;
	private static DataOutputStream toServer;
	private static DataInputStream fromServer;
	
	private static ChatScreen chatScreen;
}
