package CSCI2020.FinalProject.Client;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainClass extends Application {
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		ChatScreen chatScreen = new ChatScreen();
		
		LoginScreen loginScreen = new LoginScreen(primaryStage, chatScreen);

		primaryStage.setOnCloseRequest(e-> {
			System.out.println("Trying to shut down...");
			ChatScreen.ShutDown();
		});

		primaryStage.setScene(loginScreen.getScene());
		primaryStage.show();
	}
}