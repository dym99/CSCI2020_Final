package CSCI2020.FinalProject.Server;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.*;

public class ActiveUser {
    VBox userBox;

    Text username;

    Pane parent;

    String socketIP;

    public ActiveUser(Pane _parent, String name, String sockAddr)
    {
        socketIP = sockAddr;

        username = new Text();
        userBox = new VBox();

        userBox.getChildren().add(username);

        UpdateUsername(name);

        parent = _parent;
        Platform.runLater( () -> {
            parent.getChildren().add(userBox);
        });
    }

    public String GetSocketIP()
    {
        return socketIP;
    }

    public void UpdateUsername(String name)
    {
        Platform.runLater( () -> {
            username.setText(name);
        });
    }

    public void Cleanup() //pseudo destructor
    {
        Platform.runLater( () -> {
            parent.getChildren().remove(userBox);
        });
    }
}
