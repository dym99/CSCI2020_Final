package CSCI2020.FinalProject.Client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChatScreen {
	static void ShutDown()
	{
		System.exit(0);
	}

	public ChatScreen(Stage _stage) {
		//Store reference to the stage
		stage = _stage;
		
		//Root node
		VBox root = new VBox();

		//Scroll pane for viewing chat log
		sp = new ScrollPane();
		sp.setPrefSize(600, chatHeight);

		Button backButton = new Button("Back To Login");

		backButton.setOnAction(e->{
			disconnectClient();
		});

		root.getChildren().add(sp);

		chatBox = new VBox();
		sp.setContent(chatBox);

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

		root.getChildren().add(inputText);

		root.getChildren().add(backButton);

		//Initialise the scene
		scene = new Scene(root);
		
		
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
					//If the message is conveying that the server disconnected you:
					if (message.equals("/disconnect")) {
						
					}
					//Update the chat text box's status and print the message.
					atBottom = true;
					if (sp.getVvalue() < 1 && chatBox.getHeight() >= sp.getHeight()) {
						atBottom = false;
					}
					Platform.runLater(()->{
						chatBox.getChildren().add(new Text(message));
					});
				}
			}
		}).start();
	}
	
	//End the thread for receiving incoming messages.
	public void endNetThread() {
		shouldEndNetThread = true;
	}

	//Disconnection function
	public void disconnectClient()
	{
		//Disconnect the socket.
		ClientNetworking.Disconnect();
		
		//Stop the networking thread. (For receiving messages)
		endNetThread();
		
		//Return to login screen
		if (loginScreen!=null) {
			stage.setScene(loginScreen.getScene());
		}
		
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
