package CSCI2020.FinalProject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainClass extends Application {
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		LoginScreen loginScreen = new LoginScreen(primaryStage);

		
		primaryStage.setScene(loginScreen.getScene());
		primaryStage.show();
	}
}