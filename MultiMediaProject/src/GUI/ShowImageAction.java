package GUI;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


public class ShowImageAction {



    static private final float H = 10 * 50, W = 15 * 50;
    public static void showInNewStage(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("Image file .jpg", "*.jpg");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("Image file .png", "*.png");
        FileChooser.ExtensionFilter extFilter3 = new FileChooser.ExtensionFilter("Image file .jpeg", "*.jpeg");
        fileChooser.getExtensionFilters().addAll(extFilter1 , extFilter2 , extFilter3);
        File file=fileChooser.showOpenDialog(new Stage());
        Image img;
        try {
            img =new Image(file.toURI().toString());
            ImageView imgView = new ImageView(img);
            ScrollPane scrollPane = new ScrollPane(imgView);

            Stage stage = new Stage();
            stage.setScene(new Scene(scrollPane));
            scrollPane.setMaxSize(W,H);
            stage.show();
            stage.getIcons().add(new Image("img/icon.jpg"));
            stage.setTitle("Text file " + file);
            stage.setFullScreen(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
