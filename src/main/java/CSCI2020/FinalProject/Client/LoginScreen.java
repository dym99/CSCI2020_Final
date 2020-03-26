package CSCI2020.FinalProject.Client;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen {	
	
	public LoginScreen(Stage _stage, ChatScreen _nextScene) {
		stage = _stage;
		nextScene = _nextScene;
		
		//The root node
		VBox root = new VBox();
		
		//Grid pane for the form layout
		GridPane gPane = new GridPane();

		//Labels for the text fields
		Label usernameLabel = new Label("Username: ");
		Label addressLabel = new Label("Server Address: "); 
		
		//Initialise form elements.
		usernameField = new TextField();
		serverAddressField = new TextField();

		loginButton = new Button("Login");
		
		loginButton.setOnAction(e -> {
			//Verify username field
			String username = usernameField.getText();
			if (!username.matches("^[a-zA-Z0-9 ]*$")) {
				//Username is bad.
				//TODO: Feedback for bad username (only letters and numbers, no special characters)
			} else {
				//Connect to specified address
				if (ClientNetworking.Connect(serverAddressField.getText(), 8000)) {
					//Connected. Go to next scene.
					nextScene.setUsername(username);
					stage.setScene(nextScene.getScene());
					
				} else {
					//Connection failed.
				}
			
			}
		});
		
		//Add all the form elements to the grid pane.
		gPane.add(usernameLabel, 0, 0);
		gPane.add(addressLabel, 0, 1);

		gPane.add(usernameField, 1, 0);
		gPane.add(serverAddressField, 1, 1);
		
		gPane.add(loginButton, 1, 2);
		
		//Add padding to the grid pane
		gPane.setPadding(new Insets(48.0d));
		
		root.getChildren().add(gPane);
		
		scene = new Scene(root);
	}

	//Getter for the scene for the login screen
	public Scene getScene() {
		return scene;
	}
	
	//Store reference to the stage
	private Stage stage;
	
	//Store reference to the next scene
	private ChatScreen nextScene;
	
	//
	// Private variables for the scene.
	//
	
	//The scene for the login screen
	private Scene scene;

	//Username to login with
	private TextField usernameField;
	
	//Address to connect to
	private TextField serverAddressField;
	
	//Login button
	private Button loginButton;
}
