package CSCI2020.FinalProject.Client;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginScreen {	
	
	public LoginScreen(Stage _stage, ChatScreen _nextScene) {
		stage = _stage;
		nextScene = _nextScene;
		
		
		//The root node
		root = new VBox();

		//Adjust styles
		root.setBackground(new Background(new BackgroundFill(new Color(0.14d, 0.16d, 0.20d, 1.0d), CornerRadii.EMPTY, Insets.EMPTY)));
		
		//Grid pane for the form layout
		GridPane gPane = new GridPane();

		//Adjust styles
		gPane.setBackground(new Background(new BackgroundFill(new Color(0.14d, 0.16d, 0.20d, 1.0d), CornerRadii.EMPTY, Insets.EMPTY)));

		//Labels for the text fields
		Label usernameLabel = new Label("Username: ");
		Label addressLabel = new Label("Server Address: "); 

		//Adjust styles for the labels
		usernameLabel.setTextFill(Color.ANTIQUEWHITE);
		addressLabel.setTextFill(Color.ANTIQUEWHITE);
		
		//Initialise form elements.
		usernameField = new TextField();
		serverAddressField = new TextField();
		
		//Adjust input field styles
		usernameField.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(4.0d), new BorderWidths(1.0f))));
		serverAddressField.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(4.0d), new BorderWidths(1.0f))));

		loginButton = new Button("Log In");
		loginButton.setPrefWidth(100.0d);
		loginButton.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(4.0d), Insets.EMPTY)));
		loginButton.setTextFill(Color.ANTIQUEWHITE);
		
		//Submit the form when pressing enter
		usernameField.setOnKeyReleased(e-> {
			if (e.getCode().equals(KeyCode.ENTER))
			{
				onFormSubmit();
			}
		});
		serverAddressField.setOnKeyReleased(e-> {
			if (e.getCode().equals(KeyCode.ENTER))
			{
				onFormSubmit();
			}
		});
		
		//Submit the form when the login button is pressed.
		loginButton.setOnAction(e -> {
			onFormSubmit();
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
		

		//Initialise form error box
		formErrorMessage = new Text();
		
		formErrorMessage.setTextAlignment(TextAlignment.CENTER);
		formErrorMessage.setFill(Color.RED);
		
		root.getChildren().add(formErrorMessage);
		formErrorMessage.autosize();
		
		scene = new Scene(root);
		
	}

	//Logic for when the login form is submitted.
	public void onFormSubmit() {
		//Reset styles
		usernameField.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(4.0d), new BorderWidths(1.0f))));
		serverAddressField.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(4.0d), new BorderWidths(1.0f))));
		//Verify username field
		String username = usernameField.getText();
		if (username.length()<=2) {
			//Username is too short. Show error message and highlight bad form element.
			showFormErrorMessage("Username must be more than 2 characters long.");
			usernameField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(4.0d), new BorderWidths(1.0f))));
		} else if (!username.matches("^[a-zA-Z0-9]*$")) {
			//Username is bad. Show error message and highlight bad form element.
			showFormErrorMessage("Username cannot contain spaces or special characters!");
			usernameField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(4.0d), new BorderWidths(1.0f))));
		} else {
			//Connect to specified address
			if (ClientNetworking.Connect(serverAddressField.getText(), 8000)) {
				//Connected. Go to next scene.
				nextScene.setUsername(username);
				nextScene.runNetThread();
				nextScene.setLoginScreen(this);
				stage.setScene(nextScene.getScene());
				hideFormErrorMessage();
				
			} else {
				//Connection failed. Show error message and highlight bad form element.
				showFormErrorMessage(String.format("Failed to connect to server \"%s\"", serverAddressField.getText()));
				serverAddressField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(4.0d), new BorderWidths(1.0f))));
			}
		
		}
	}
	
	//Show an error text along the bottom.
	public void showFormErrorMessage(String _message) {
		formErrorMessage.setText(_message);
	}
	
	//Remove the error message from the screen
	public void hideFormErrorMessage() {
		formErrorMessage.setText("");
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
	
	//The root node
	private VBox root;
	
	//Error message for the login form.
	private Text formErrorMessage;
}
