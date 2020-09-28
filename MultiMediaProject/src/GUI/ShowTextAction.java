package GUI;

import compressors.FileType;
import compressors.Reader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;


public class ShowTextAction {

    static private final float H = 10 * 50, W = 15 * 50,FONT_SIZE=16;
    public static void showInNewStage(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter1);
        File file=fileChooser.showOpenDialog(new Stage());
        String text ;
        try {
            text = Reader.read(file,FileType.TEXT);
        } catch (IOException e) {
            e.printStackTrace();
            text = "Cannot open file !";
        }
        Label label = new Label(text);
        label.setFont(new Font("Comic Sans MS",FONT_SIZE));
        label.setBackground(new Background(new BackgroundFill(
                Color.WHITE,null,null)));
        ScrollPane scrollPane = new ScrollPane(label);
        scrollPane.setPrefSize(W,H);

        Stage stage = new Stage();
        stage.setScene(new Scene(scrollPane));
        stage.show();
        stage.getIcons().add(new Image("img/icon.jpg"));
        stage.setTitle("Text file " + file);
        stage.setFullScreen(false);
    }
}
