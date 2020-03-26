package CSCI2020.FinalProject.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ServerMain extends Application {

	File file;
	public void start(Stage primaryStage) {

		//Set message logging file

		String FileOutPath = "Log.txt";
		file = new File(FileOutPath);

		//Keeping append as 'true' will not erase the data of previous sessions

		LogData("SESSION BEGINS: " +
				java.time.LocalDateTime.now(), true
		);

		//Set up server UI

		serverLog = new TextArea();
		serverLog.setPrefSize(640.0d, 480.0d);
		
		ScrollPane scrollPane = new ScrollPane(serverLog);
		scrollPane.setMinSize(640.0d, 480.0d);
		
		serverLog.autosize();

		primaryStage.setOnCloseRequest(e->{
			LogData("SESSION ENDS: " +
					java.time.LocalDateTime.now(), true
			);
		});

		Scene scene = new Scene(scrollPane);
		primaryStage.setScene(scene);
		primaryStage.show();

		serverLog.appendText("Starting server...\r\n");
		
		clients = new ArrayList<HandleClient>();
		
		//Start server netcode on new thread.
		new Thread( () -> {
			try {
				ServerSocket serverSocket = new ServerSocket(8000);
				
				while (true) {
					//Accept an incoming TCP connection
					Socket clientSocket = serverSocket.accept();
					
					Platform.runLater( () -> {
						serverLog.appendText(String.format("[{0}] Client connected.\r\n", clientSocket.getInetAddress().getHostAddress()));
	                });
					
					//Add client to client list and begin processing socket I/O
					HandleClient client = new HandleClient(clientSocket, this);
					clients.add(client);
					new Thread(client).start();
				}
				
			} catch (IOException e) {
				serverLog.appendText("Failed to start server!\r\n");
				e.printStackTrace();
			}
		}).start();
	}
	
	//
	//	Methods
	//
	private void LogData(String data, boolean append)
	{
		FileWriter fr = null;
		try {
			fr = new FileWriter(file, append);
			fr.write(data + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void distributeMessage(String _message) {
		for (HandleClient client : clients) {
			client.enqueueMessage(_message);
		}
	}
	
	//
	//	Server GUI
	//
	TextArea serverLog;
	
	//
	//	Client list
	//
	ArrayList<HandleClient> clients;
	
	//
	//	All code for communicating with client handled by inner class
	//
	class HandleClient implements Runnable {
		//Store a reference to the client's socket.
		public HandleClient(Socket _socket, ServerMain _server) {
			username = "";
			socket = _socket;
			server = _server;
			messagesToSend = new LinkedList<String>();
		}
		
		//Serve the client
		@Override
		public void run() {
			try {
				//Streams for communicating with client.
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

				//Thread for recieving messages and handling them.
				new Thread(()->{
					while (true) {
						try {
							String message = inputFromClient.readUTF();
							
							serverLog.appendText(String.format("[%s] %s\r\n", socket.getInetAddress().getHostAddress(), message));
							
							if (message.startsWith("/name ")) {
								//Tokenise message
								String tokens[] = message.split(" ");
								String name = "";
								//Skip the first token (which is "/name "), and use the rest as a name.
								for (int i = 1; i < tokens.length; ++i) {
									name = String.join(name, tokens[i]);
								}
								if (!username.equals("")) {
									username = name;

									LogData(" -> " + username + " JOINED: " +
											java.time.LocalDateTime.now(), true
									);
								}
							} else {
								//Recieved a normal chat message.
								//Distribute it. (message format is "username: message")
								server.distributeMessage(String.format("%s: %s\r\n", username, message));

								LogData(java.time.LocalDateTime.now() +
										": " + username + ": " + message, true
								);
							}
						} catch (IOException e) {
							//Print error and break from loop (to end this recieve message thread)
							System.out.println("Sock error on recieve!\n");
							e.printStackTrace();
							break;
						}
					}
				}).start();
				
				//Thread for sending messages.
				new Thread(()->{
					while (true) {
						if (!messagesToSend.isEmpty()) {
							String message = messagesToSend.poll();
							try {
								outputToClient.writeUTF(message);
								serverLog.appendText("Sent message.\r\n");
							} catch (IOException e) {
								//Print error and break from loop (to end this send message thread)
								System.out.println("Sock error on send!\n");
								e.printStackTrace();
								break;
							}
						}
					}
				}).start();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Prepare message to be sent on this thread.
		public void enqueueMessage(String _message) {
			messagesToSend.add(_message);
		}
		
		//Retrieve this client's username.
		public String getUsername() {
			return username;
		}
		
		private LinkedList<String> messagesToSend;
		
		//Socket for communicating with a client
		private Socket socket;
		
		//Server class reference
		private ServerMain server;
		
		//This client's current username
		private String username;
		
	}
	
}
