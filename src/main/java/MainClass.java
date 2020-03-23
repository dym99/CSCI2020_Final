import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainClass extends Application {
	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//Grid pane for layout
		GridPane gPane = new GridPane();
		
		
		
		//Set the scene up
		Scene scene = new Scene(gPane);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}