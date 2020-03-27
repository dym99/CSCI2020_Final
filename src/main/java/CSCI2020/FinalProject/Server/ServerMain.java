package CSCI2020.FinalProject.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ServerMain extends Application {
	static void ShutDown()
	{
		System.exit(0);
	}

	File file;
	VBox userBox;
	VBox allUsers;

	public void start(Stage primaryStage) {
		//Load the blacklist file
		ServerBlacklist.ReloadBlacklist();
		
		//Set message logging file
		String FileOutPath = "Log.txt";
		file = new File(FileOutPath);

		//Keeping append as 'true' will not erase the data of previous sessions

		LogData("SESSION BEGINS: " +
				java.time.LocalDateTime.now(), false
		);

		//Set up server UI

		//Root node
		HBox root = new HBox();
		VBox serverOutput = new VBox();
		root.getChildren().add(serverOutput);

		//Scroll pane for viewing chat log
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefSize(600, 480);

		serverOutput.getChildren().add(scrollPane);

		//Scroll pane for online users
		ScrollPane userList = new ScrollPane();
		userList.setPrefWidth(200);
		userBox = new VBox();
		allUsers = new VBox();
		userBox.getChildren().add(new Text("Connected Clients: "));
		userBox.getChildren().add(allUsers);
		userList.setContent(userBox);
		root.getChildren().add(userList);

		serverLog = new VBox();
		scrollPane.setContent(serverLog);
		
		//If the scroll pane is at the bottom when a message is added, keep it at the bottom. 
		serverLog.heightProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> _observable, Object _oldvalue, Object _newValue) {
				if (atBottom) {
					scrollPane.setVvalue(1D);
				}
			}
		});

		//Callback for when the application is closed.
		//Ensures all the threads started are killed when a user closes the window.
		primaryStage.setOnCloseRequest(e->{
			LogData("SESSION ENDS: " +
					java.time.LocalDateTime.now(), true
			);

			ShutDown();
		});

		//Set up the scene
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

		
		serverLog.getChildren().add(new Text("Starting server..."));
		
		clients = new ArrayList<HandleClient>();
		
		//Start server netcode on new thread.
		new Thread( () -> {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(8000);

				while (true) {
					//Accept an incoming TCP connection
					Socket clientSocket = serverSocket.accept();
					
					//If the connected client is blacklisted, terminate the connection, and keep listening.
					if (ServerBlacklist.IsBlacklisted(clientSocket.getRemoteSocketAddress().toString())) {
						clientSocket.close();
						continue;
					}

					Platform.runLater( () -> {
						serverLog.getChildren().add(new Text(
										String.format("[%s] Client connected.\n", clientSocket.getInetAddress().getHostAddress())
										));
	                });

					distributeMessage("/includeUsers@" + clientSocket.getRemoteSocketAddress().toString() + "@ ");
					
					//Add client to client list and begin processing socket I/O
					HandleClient client = new HandleClient(clientSocket, this, allUsers);
					clients.add(client);

					LogData(" -> USER CONNECTED: " + clientSocket.getRemoteSocketAddress().toString() + ", " +
							java.time.LocalDateTime.now(), true
					);
					
					//Start a thread to handle all socket I/O for the client. 
					new Thread(client).start();
				}
			} catch (IOException e) {
				serverLog.getChildren().add(new Text("Failed to start server!\n"));
				e.printStackTrace();
			} finally {
				try {
					serverSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}).start();
	}
	
	//
	//	Methods
	//
	
	//Write to the log file (Log.txt)
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
	
	//Send message to all connected clients
	public void distributeMessage(String _message) {

		Platform.runLater(()->{
			serverLog.getChildren().add(new Text(String.format("Distributing message to %d clients: %s\n", clients.size(), _message)));
		});
		for (HandleClient client : clients) {

			Platform.runLater(()->{
				serverLog.getChildren().add(new Text("Sending message..."));
			});	
			client.enqueueMessage(_message);
		}
	}
	
	//
	//	Server GUI
	//
	public VBox serverLog;
	
	//Variables for the scrolling server log
	boolean atBottom = true;
	boolean pressedOnce = false;
	int chatHeight = 300;
	
	//
	//	Client list
	//
	ArrayList<HandleClient> clients;
	
	//
	//	All code for communicating with client (all socket I/O) handled by inner class
	//
	class HandleClient implements Runnable {
		//Store a reference to the client's socket.
		public HandleClient(Socket _socket, ServerMain _server, VBox users) {
			username = "";
			socket = _socket;
			server = _server;
			messagesToSend = new LinkedList<String>();
			activeUser = new ActiveUser(users, username, _socket.getRemoteSocketAddress().toString(), false);
		}
		
		//Serve the client
		@Override
		public void run() {
			try {
				//Streams for communicating with client.
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

				String allUserNames = "/includeUsers";
				for (int i = 0; i < clients.size() - 1; ++i)
				{
					allUserNames += ("@" + clients.get(i).activeUser.GetSocketIP() + "@" + clients.get(i).username);
				}

				allUserNames += ("@" + clients.get(clients.size() - 1).activeUser.GetSocketIP() + "@ ");

				outputToClient.writeUTF(allUserNames);
				outputToClient.flush();

				//Thread for recieving messages and handling them.
				new Thread(()->{
					while (true) {
						try {
							String message = inputFromClient.readUTF();

							System.out.println(message);
							
							Platform.runLater(()->{
								serverLog.getChildren().add(new Text(String.format("[%s (%s)] %s\n", socket.getInetAddress().getHostAddress(), username, message)));
							});

							if (message.startsWith("/name ")) {
								//Tokenise message
								String tokens[] = message.split(" ");
								String name = "";
								//Skip the first token (which is "/name "), and use the rest as a name.
								for (int i = 1; i < tokens.length; ++i) {
									name = String.join(name, tokens[i]);
								}
								
								//Make sure the name isn't nothing.
								if (!name.equals("")) {
									if (!name.matches("^[a-zA-Z0-9]*$")) {
										//Tell the client that the username is bad.
										outputToClient.writeUTF("New username must consist of only letters and numbers.");
									} else {
										//Prevent duplicate names by appending a 1 until the name is unique
										boolean nameDirty = false;
										do {
											nameDirty = false;
											for (HandleClient client : server.clients) {
												if (name.equals(client.getUsername())) {
													name = name + "1";
													nameDirty = true;
												}
											}
										} while (nameDirty);
										
										//Write the chat message for either
										//the initial join (where the username is actually set)
										//or a name change.
										if (username.equals("")) {
											distributeMessage(String.format("'%s' has joined the chat.", name));
										} else {
											distributeMessage(String.format("'%s' has changed their name to '%s'", username, name));
										}
										LogData(" -> USER '" + username + "' RENAMED TO '" + name + "'" +
												java.time.LocalDateTime.now(), true
										);
	
										username = name;
	
										activeUser.UpdateUsername(name);
	
										distributeMessage("/updateUser@" + activeUser.socketIP + "@" + username);
									}
								}
							} else if (!message.startsWith("/")){

								//Recieved a normal chat message.
								//Distribute it. (message format is "username: message")
								server.distributeMessage(String.format("%s: %s", username, message));

								LogData(java.time.LocalDateTime.now() +
										": " + username + ": " + message, true
								);
							}
						} catch (SocketException se){
							//Peaceful disconnection
							System.out.println("Client disconnection!\n");

							TerminateThread = true;

							//Remove this client from the list of active clients.
							Platform.runLater(()-> {
								clients.remove(this);
								activeUser.Cleanup();
							});
							
						} catch (IOException e) {
							//Client is not responding.
							System.out.println("Client not responding!\nClient forcefully disconnected.");
							
							TerminateThread = true;
							
							//Remove this client from the list of active clients.
							Platform.runLater(()-> {
								clients.remove(this);
								activeUser.Cleanup();
							});

						}

						//Clean up the thread and end it.
						if (TerminateThread)
						{
							try {
								socket.close();
								inputFromClient.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							//Removes the client from the active client list on all clients.
							distributeMessage("/removeUser@" + activeUser.socketIP);
							
							//Prints a message to the chat that displays that this client has disconnected.
							distributeMessage(String.format("'%s' has disconnected from the chat.", username));
							
							LogData(" -> USER DISCONNECTED: " + username + ": " + socket.getRemoteSocketAddress().toString() + ", " +
									java.time.LocalDateTime.now(), true
							);

							break;
						}
					}
				}).start();
				
				//Thread for sending messages.
				new Thread(()->{

					Platform.runLater(()->{
						serverLog.getChildren().add(new Text("Starting message sending thread."));
					});
					while (true) {
						if (!messagesToSend.isEmpty()) {
							Platform.runLater(()->{
								serverLog.getChildren().add(new Text("Sending message..."));
							});
							String message = messagesToSend.remove();
							if (message!=null) {
								try {
									outputToClient.writeUTF(message);
									outputToClient.flush();
									
									Platform.runLater(()->{
										serverLog.getChildren().add(new Text("Sent message."));
									});
								} catch (IOException e) {
									//Print error and break from loop (to end this send message thread)
									Platform.runLater(()->{
										serverLog.getChildren().add(new Text("Sock error on send!"));
									});
									e.printStackTrace();
									break;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {
							System.out.print("");
						}

						//Clean up and end the thread.
						if (TerminateThread)
						{
							try {
								outputToClient.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

							break;
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

			Platform.runLater(()->{
				serverLog.getChildren().add(new Text(String.format("Enqueued message to client: %s\n", username)));
			});
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

		//User's graphical representation
		private ActiveUser activeUser;

		//Whether or not the threads need to die.
		private boolean TerminateThread = false;
	}
	
}
