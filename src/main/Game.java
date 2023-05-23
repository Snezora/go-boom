package main;
import java.io.File;
import java.io.FileInputStream;

import javafx.application.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.fxml.*;


public class Game extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 600, 600);
        Stage stage = new Stage();
        GridPane pane = new GridPane ();

        Image icon = new Image("https://img.freepik.com/free-vector/playing-card-symbols-sticker-white-background_1308-80323.jpg?w=740&t=st=1684851751~exp=1684852351~hmac=9c5c802d4248c8ec3379c9f8e2ae7e338ac0a93c9080dcfc59c6927d9193b8f3");
        stage.getIcons().add(icon);
        stage.setTitle("Card Game WIP");


        Image card = new Image(getClass().getResource("../lib/cards/cA.png").toString());
        ImageView image = new ImageView(card);
        image.setScaleX(0.2);
        image.setScaleY(0.2);

        root.getChildren().add(image);

        stage.setScene(scene);
        stage.show();
    }

}
