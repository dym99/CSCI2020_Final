package CSCI2020.FinalProject;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class ChatScreen {
	
	public ChatScreen() {
		
		//Root node
		VBox root = new VBox();
		
		
		//Initialise the scene
		m_scene = new Scene(root);
	}

	//Getter for the scene for the chat screen
	public Scene getScene() {
		return m_scene;
	}

	
	//Getter and setter for username
	public void setUsername(String _username) {
		m_username = _username;
	}
	public String getUsername() {
		return m_username;
	}

	//
	// Private variables for the scene.
	//
	
	//The scene for the chat screen
	private Scene m_scene;
	
	
	private String m_username;
}
