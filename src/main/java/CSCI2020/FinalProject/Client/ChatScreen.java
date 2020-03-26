package CSCI2020.FinalProject.Client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatScreen {

	public ChatScreen() {

		//Root node
		VBox root = new VBox();

		//Scroll pane for viewing chat log
		ScrollPane sp = new ScrollPane();
		sp.setPrefSize(600, chatHeight);

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

		//Initialise the scene
		m_scene = new Scene(root);
		
		//Thread to recieve all incoming messages.
		new Thread(()->{
			while (true) {
				//Recieve incoming messages
				String message = ClientNetworking.Recv();
				
				//If message is recieved successfully...
				if (!message.equals("")) {
					//Print it.

					atBottom = true;
					if (sp.getVvalue() < 1 && chatBox.getHeight() >= sp.getHeight())
						atBottom = false;

					chatBox.getChildren().add(new Text(message));

				}
			}
		});
	}

	//Getter for the scene for the chat screen
	public Scene getScene() {
		return m_scene;
	}


	//Getter and setter for username
	public void setUsername(String _username) {
		username = _username;
		ClientNetworking.Send(String.format("/name %s", _username));
	}
	public String getUsername() {
		return username;
	}

	//
	// Private variables for the scene.
	//

	//The scene for the chat screen
	private Scene m_scene;

	//Input field for the user's text.
	TextField inputText;
	
	//VBox for scrolling chat log
	VBox chatBox;
	
	//Variables for the scrolling chat log
	boolean atBottom = true;
	boolean pressedOnce = false;
	int chatHeight = 300;
	
	//This client's username
	private String username;
}
