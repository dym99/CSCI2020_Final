package CSCI2020.FinalProject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.swing.*;

public class ChatScreen {
	TextField inputText;
	VBox chatBox;
	boolean atBottom = true;
	boolean pressedOnce = false;
	int chatHeight = 300;

	public ChatScreen() {

		//Root node
		VBox root = new VBox();

		ScrollPane sp = new ScrollPane();
		sp.setPrefSize(600, chatHeight);

		root.getChildren().add(sp);

		chatBox = new VBox();
		sp.setContent(chatBox);

		chatBox.heightProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldvalue, Object newValue) {
				if (atBottom) {
					sp.setVvalue(1D);
				}
			}
		});

		//for (int i = 0; i < 40; ++i)
		//{
		//	chatBox.getChildren().add(new Text("This is a test " + Integer.toString(i)));
		//}

		inputText = new TextField("Type Here!");

		inputText.setOnKeyReleased(e -> {
			//System.out.println("TYPED! " + Integer.toString(Character.getNumericValue()));

			//=-=-=-=-=-=-=-HERE-=-=-=-=-=-=-=-=//
			//The following functionality can AND SHOULD instead send a message to a server,
			//which relays it to all clients (even the sender), and the receiving function
			//should instead update the scrolling text

			if (e.getCode().equals(KeyCode.ENTER))
			{
				atBottom = true;
				if (sp.getVvalue() < 1 && chatBox.getHeight() >= sp.getHeight())
					atBottom = false;

				chatBox.getChildren().add(new Text(getUsername() + ": " + inputText.getText()));

				inputText.clear();


				//sp.setVvalue(1D);
			}
		});

		root.getChildren().add(inputText);

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
