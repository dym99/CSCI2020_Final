package CSCI2020.FinalProject.Client;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainClass extends Application {
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		//Instantiate class for handling the chat screen
		ChatScreen chatScreen = new ChatScreen(primaryStage);
		
		//Instantiate class for handling the login screen
		LoginScreen loginScreen = new LoginScreen(primaryStage, chatScreen);

		//Store a static reference to the chat screen
		ClientNetworking.SetChatScreen(chatScreen);
		
		primaryStage.setOnCloseRequest(e-> {
			System.out.println("Trying to shut down...");
			ChatScreen.ShutDown();
		});

		primaryStage.setScene(loginScreen.getScene());
		primaryStage.show();
	}
}