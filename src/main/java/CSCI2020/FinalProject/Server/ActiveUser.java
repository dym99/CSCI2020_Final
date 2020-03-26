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

    public ActiveUser(Pane _parent, String name)
    {
        username = new Text();
        userBox = new VBox();

        userBox.getChildren().add(username);

        UpdateUsername(name);

        parent = _parent;
        Platform.runLater( () -> {
            parent.getChildren().add(userBox);
        });
    }

    public void UpdateUsername(String name)
    {
        username.setText(name);
    }

    public void Cleanup() //pseudo destructor
    {
        parent.getChildren().remove(userBox);
    }
}
