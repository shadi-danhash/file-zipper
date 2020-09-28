package GUI;

import compressors.LzwUnzipper;
import compressors.ShannonFanoUnzipper;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class DecompressAction {

    static Button zipButton, backButton;
    static VBox vBox;
    static Application mRoot;
    static File srcFile;
    static File destFile;

    static private final float BUTTON_H = 10 * 5, BUTTON_W = 64 * 5, FONT_SIZE = 32;

    public static Node getUI(Application root) {
        mRoot = root;
        vBox = new VBox();
        vBox.getChildren().addAll(getSrcFileChooser(), getDestFileChooser(), getButtons());
        vBox.setSpacing(20);
        return vBox;
    }


    private static Node getButtons() {
        VBox vBox = new VBox();
        zipButton = new Button("Un ZIP");
        backButton = new Button("Back");
        decorateButtons(zipButton, backButton);
        vBox.getChildren().addAll(zipButton, backButton);
        zipButton.setOnAction(e -> actionUnzip());
        backButton.setOnAction(e -> actionBack());
        vBox.setSpacing(20);
        return vBox;
    }

    private static Node getSrcFileChooser() {
        Button button = new Button("choose your source file"); //to open file chooser stage
        Label label = new Label("");//file name or path
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("SF files (*.sf)", "*.sf");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("LZW files (*.lzw)", "*.lzw");
        fileChooser.getExtensionFilters().addAll(extFilter1, extFilter2);
        fileChooser.setInitialDirectory(new File("..\\MultiMediaProject"));
        button.setOnAction(e -> {
            srcFile = fileChooser.showOpenDialog(new Stage());
            if (srcFile != null) {
                label.setText(srcFile + "");
            }
        });
        return new HBox(button, label);
    }

    private static Node getDestFileChooser() {
        Button button = new Button("choose destination file"); //to open file chooser stage
        Label label = new Label("");//file name or path
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("..\\MultiMediaProject"));
        button.setOnAction(e -> {
            destFile = fileChooser.showSaveDialog(new Stage());
            if (srcFile != null) {
                label.setText(srcFile + "");
                System.out.println(srcFile);
            }
        });
        return new HBox(button, label);
    }

    private static void actionUnzip() {

        String src = srcFile + "";
        String dest = destFile + "";
        String extension = "";

        int i = src.lastIndexOf('.');
        if (i > 0)
            extension = src.substring(i + 1);
        if (extension.equals("sf")) {

            ShannonFanoUnzipper.AutoUnzip(src, dest);

        } else if (extension.equals("lzw")) {
            LzwUnzipper.AutoUnzip(src,dest);
        }
       Inform();
    }
    private static void actionBack() {
        ((HomeGUI) mRoot).restart();
    }

    public static void Inform(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Done");
        alert.show();
//        Stage stage = new Stage();
//        stage.setScene(new Scene(new Label("Done")));
//        stage.show();
    }

    private static void decorateButtons(Button... args) {
        for (Button button : args) {
            button.setPrefHeight(BUTTON_H);
            button.setPrefWidth(BUTTON_W);
            button.setTextFill(Color.color(1, 1, 1));
            button.setFont(new Font(FONT_SIZE));
            button.setBackground(new Background(
                    new BackgroundFill(Color.color(
                            235.0 / 255, 52.0 / 255, 140.0 / 255), null, null)));

        }

        for (Button button : args) {
            button.setOnMouseEntered(event -> {
                button.setCursor(Cursor.HAND);
                button.setTextFill(Color.color(1, 1, 0.1));
                button.setFont(new Font(FONT_SIZE + 4));
            });
            button.setOnMouseExited(event -> {
                button.setCursor(Cursor.DEFAULT);
                button.setTextFill(Color.color(1, 1, 1));
                button.setFont(new Font(FONT_SIZE));
            });
            button.setOnMousePressed(event -> {
                button.setCursor(Cursor.CLOSED_HAND);
            });
            button.setOnMouseReleased(event -> {
                button.setCursor(Cursor.HAND);
            });
        }
    }
}
