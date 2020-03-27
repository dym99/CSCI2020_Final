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
    VBox userBox;

    Text username;

    Pane parent;

    String socketIP;

    public ActiveUser(Pane _parent, String name, String sockAddr)
    {
        socketIP = sockAddr;

        username = new Text();
        userBox = new VBox();

        username.setFill(Color.ANTIQUEWHITE);
        userBox.setBackground(new Background(new BackgroundFill(new Color(0.1d, 0.12d, 0.15d, 1.0d), CornerRadii.EMPTY, Insets.EMPTY)));
        
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
