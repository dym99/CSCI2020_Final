package CSCI2020.FinalProject.Server;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ActiveUser {
	//Container for the client list item
    VBox userBox;

    //The client list item
    Text username;

    //The parent container for the item.
    Pane parent;

    //A string representation of this client's address. Format: ("/<ip>:<port>") 
    String socketIP;

    public ActiveUser(Pane _parent, String name, String sockAddr, boolean darkMode)
    {
    	//Store info related to specific client.
        socketIP = sockAddr;

        //Set up item to add to parent.
        username = new Text();
        userBox = new VBox();

        //Styles for the client
        if (darkMode) {
	        username.setFill(Color.ANTIQUEWHITE);
	        userBox.setBackground(new Background(new BackgroundFill(new Color(0.1d, 0.12d, 0.15d, 1.0d), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        
        userBox.getChildren().add(username);

        UpdateUsername(name);

        //Add the item to the specified parent.
        parent = _parent;
        Platform.runLater( () -> {
            parent.getChildren().add(userBox);
        });
    }

    //Get a string representation of the client (Format: "/<ip>:<port>"
    public String GetSocketIP()
    {
        return socketIP;
    }

    //Get the username of this client.
    public void UpdateUsername(String name)
    {
        Platform.runLater( () -> {
            username.setText(name);
        });
    }

    //Remove this from the parent node.
    public void Cleanup()
    {
        Platform.runLater( () -> {
            parent.getChildren().remove(userBox);
        });
    }
}
