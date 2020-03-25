package CSCI2020.FinalProject;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen {	
	
	public LoginScreen(Stage _stage) {
		m_stage = _stage;
		
		//The root node
		VBox root = new VBox();
		
		//Grid pane for the form layout
		GridPane gPane = new GridPane();

		//Labels for the text fields
		Label usernameLabel = new Label("Username: ");
		Label addressLabel = new Label("Server Address: "); 
		
		//Initialise form elements.
		m_usernameField = new TextField();
		m_serverAddressField = new TextField();

		m_loginButton = new Button("Login");
		
		m_loginButton.setOnAction(e -> {
			//TODO: Login
			ChatScreen chatScene = new ChatScreen(m_usernameField.getText());
			
			m_stage.setScene(chatScene.getScene());
		});
		
		//Add all the form elements to the grid pane.
		gPane.add(usernameLabel, 0, 0);
		gPane.add(addressLabel, 0, 1);

		gPane.add(m_usernameField, 1, 0);
		gPane.add(m_serverAddressField, 1, 1);
		
		gPane.add(m_loginButton, 1, 2);
		
		//Add padding to the grid pane
		gPane.setPadding(new Insets(48.0d));
		
		root.getChildren().add(gPane);
		
		m_scene = new Scene(root);
	}

	//Getter for the scene for the login screen
	public Scene getScene() {
		return m_scene;
	}
	
	//Store reference to the stage
	private Stage m_stage;
	
	
	//
	// Private variables for the scene.
	//
	
	//The scene for the login screen
	private Scene m_scene;

	//Username to login with
	private TextField m_usernameField;
	
	//Address to connect to
	private TextField m_serverAddressField;
	
	//Login button
	private Button m_loginButton;
}
