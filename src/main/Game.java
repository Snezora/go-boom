package main;
import java.io.File;

import javafx.application.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Game extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        Stage stage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root, Color.BLACK);

        Image icon = new Image("https://github.com/Snezora/go-boom/blob/main/lib/cards/sA.png");
        stage.getIcons().add(icon);
        stage.setTitle("Card Game WIP");

        stage.setScene(scene);
        stage.show();
    }

}
