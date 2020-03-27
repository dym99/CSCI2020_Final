package CSCI2020.FinalProject.Client;

import java.util.ArrayList;

import CSCI2020.FinalProject.Server.ActiveUser;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChatScreen {
	VBox userBox;
	VBox allUsers;

	ArrayList<ActiveUser> activeUsers;

	static void ShutDown()
	{
		System.exit(0);
	}

	public ChatScreen(Stage _stage) {
		//Store reference to the stage
		stage = _stage;
		
		//Root node
		VBox root = new VBox();
		FlowPane fPane = new FlowPane();
		
		HBox hBox = new HBox();
		VBox mainChat = new VBox();
		hBox.getChildren().add(mainChat);

		//Scroll pane for viewing users
		ScrollPane userList = new ScrollPane();
		userList.setPrefWidth(200);
		userBox = new VBox();
		allUsers = new VBox();
		
		Text activeUserLabel = new Text("ONLINE: ");
		activeUserLabel.setFill(Color.ANTIQUEWHITE);
		activeUserLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12.0d));
		
		
		userBox.getChildren().add(activeUserLabel);
		userBox.getChildren().add(allUsers);
		userList.setContent(userBox);
		hBox.getChildren().add(userList);

		activeUsers = new ArrayList<>();

		//Scroll pane for viewing chat log
		sp = new ScrollPane();
		sp.setPrefSize(600, chatHeight);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		

		Button backButton = new Button("< Log Out");
		backButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, new CornerRadii(4.0d), new Insets(0.0d))));
		backButton.setTextFill(Color.ANTIQUEWHITE);

		backButton.setOnAction(e->{
			disconnectClient("");
		});

		mainChat.getChildren().add(sp);

		chatBox = new VBox();
		//sp.setContent(chatBox);

		//If the scroll pane is at the bottom when a message is added, keep it at the bottom. 
		chatBox.heightProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> _observable, Object _oldvalue, Object _newValue) {
				if (atBottom) {
					sp.setVvalue(1D);
				}
			}
		});

		inputText = new TextField();
		inputText.setPromptText("Type here!");
		inputText.setOnKeyReleased(e -> {
			//System.out.println("TYPED! " + Integer.toString(Character.getNumericValue()));

			//=-=-=-=-=-=-=-HERE-=-=-=-=-=-=-=-=//
			//The following functionality can AND SHOULD instead send a message to a server,
			//which relays it to all clients (even the sender), and the receiving function
			//should instead update the scrolling text

			if (e.getCode().equals(KeyCode.ENTER))
			{
				ClientNetworking.Send(inputText.getText());
				
				inputText.clear();


				//sp.setVvalue(1D);
			}
		});

		mainChat.getChildren().add(inputText);

		fPane.getChildren().add(backButton);

		root.getChildren().add(fPane);
		root.getChildren().add(hBox);
		
		//Initialise the scene
		scene = new Scene(root);
		
		//Adjust scene styles.
		Background mainBG = new Background(new BackgroundFill(new Color(0.14d, 0.16d, 0.20d, 1.0d), CornerRadii.EMPTY, Insets.EMPTY));
		Background mainBGDarker = new Background(new BackgroundFill(new Color(0.1d, 0.12d, 0.15d, 1.0d), CornerRadii.EMPTY, Insets.EMPTY));

		chatBox.setBackground(mainBG);
		sp.setBackground(mainBG);
		sp.setContent(chatBox);
		
		fPane.setBackground(mainBG);
		mainChat.setBackground(mainBG);
		hBox.setBackground(mainBG);
		root.setBackground(mainBG);
		
		allUsers.setBackground(mainBGDarker);
		userBox.setBackground(mainBGDarker);
		userList.setBackground(mainBGDarker);
		userList.setFitToHeight(true);
		userList.setFitToWidth(true);
	}

	
	//Thread to receive all incoming messages.
	public void runNetThread() {
		shouldEndNetThread = false;
		new Thread(()->{
			while (!shouldEndNetThread) {
				//Recieve incoming messages
				String message = ClientNetworking.Recv();
				
				//If message is recieved successfully...
				if (!message.equals("")) {
					//Message from the server regarding a new active client.
					if (message.startsWith("/includeUsers"))
					{
						String[] split = message.split("@");
						for (int i = 1; i < split.length; i += 2)
						{
							activeUsers.add(new ActiveUser(allUsers, split[i + 1], split[i], true));
						}
					}
					//Message from the server regarding a change in a username.
					else if (message.startsWith("/updateUser"))
					{
						String[] split = message.split("@");

						for (int i = 0; i < activeUsers.size(); ++i)
						{
							if (activeUsers.get(i).GetSocketIP().equals(split[1]))
							{
								activeUsers.get(i).UpdateUsername(split[2]);
							}
						}
					}
					//Message from the server regarding a user that left the chat.
					else if (message.startsWith("/removeUser"))
					{
						String[] split = message.split("@");
						for (int i = activeUsers.size() - 1; i >= 0; --i)
						{
							if (activeUsers.get(i).GetSocketIP().equals(split[1]))
							{
								activeUsers.get(i).Cleanup();
								activeUsers.remove(i);
							}
						}
					}
					//Regular chat message.
					else if (!message.startsWith("/")) {
						//Update the chat text box's status and print the message.
						atBottom = true;
						if (sp.getVvalue() < 1 && chatBox.getHeight() >= sp.getHeight()) {
							atBottom = false;
						}
						Platform.runLater(() -> {
							Text text = new Text(message);
							text.setFill(Color.ANTIQUEWHITE);
							chatBox.getChildren().add(text);
						});
					}
				}
			}
		}).start();
	}
	
	//End the thread for receiving incoming messages.
	public void endNetThread() {
		shouldEndNetThread = true;
	}

	//Disconnection function
	public void disconnectClient(String _message)
	{
		//Disconnect the socket.
		ClientNetworking.Disconnect();
		
		//Stop the networking thread. (For receiving messages)
		endNetThread();

		//Cleanup clients and messages
		for (int i = activeUsers.size() - 1; i >= 0; --i)
		{
			activeUsers.get(i).Cleanup();
		}

		chatBox.getChildren().clear();
		activeUsers.clear();
		
		//Return to login screen
		if (loginScreen!=null) {
			stage.setScene(loginScreen.getScene());
		}
		
		//Display error message
		loginScreen.showFormErrorMessage(_message);
	}
	
	//Getter for the scene for the chat screen
	public Scene getScene() {
		return scene;
	}


	//Getter and setter for username
	public void setUsername(String _username) {
		username = _username;
		ClientNetworking.Send(String.format("/name %s", _username));
	}
	public String getUsername() {
		return username;
	}

	
	//Getter and setter for a reference to the login screen
	public LoginScreen getLoginScreen() {
		return loginScreen;
	}
	public void setLoginScreen(LoginScreen _loginScreen) {
		loginScreen = _loginScreen;
	}
	
	//
	// Private variables for the scene.
	//

	//The scene for the chat screen
	private Scene scene;
	
	//A reference to the primary stage
	private Stage stage;
	
	//A reference to the login screen (to return to login on disconnect)
	private LoginScreen loginScreen;

	//Input field for the user's text.
	TextField inputText;
	
	//VBox for scrolling chat log
	VBox chatBox;
	
	//ScrollPane for scrolling chat log
	ScrollPane sp;
	
	//Variables for the scrolling chat log
	boolean atBottom = true;
	boolean pressedOnce = false;
	int chatHeight = 300;
	
	//Variable to manage closing the networking thread.
	boolean shouldEndNetThread = false;
	
	//This client's username
	private String username;
}
