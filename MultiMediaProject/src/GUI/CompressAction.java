package GUI;

import com.sun.imageio.plugins.common.LZWCompressor;
import compressors.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class CompressAction {

    static RadioButton shannonFanoButton, lzwButton, lossyButton, img, txt, folder;
    static Button zipButton, backButton;
    static ToggleGroup algorithmsGroup, filetypeGroup;
    static VBox vBox;
    static Application mRoot;
    static File srcFile;
    static File destFile;

    static private final float BUTTON_H = 10 * 5, BUTTON_W = 64 * 5, FONT_SIZE = 32;

    public static Node getUI(Application root) {
        mRoot = root;
        vBox = new VBox();
        vBox.getChildren().addAll(getDataTypeNode(), getAlgorithmsNode()
                , getSrcFileChooser(), getDestFileChooser(), getButtons());
        setButtonsListeners();
        vBox.setSpacing(20);
        return vBox;
    }

    private static Node getAlgorithmsNode() {
        algorithmsGroup = new ToggleGroup();
        VBox algorithm = new VBox();
        Label label = new Label("compress algorithm :");
        Separator separator = new Separator(Orientation.HORIZONTAL);
        shannonFanoButton = new RadioButton("compress with Shannon-Fano algorithm");
        shannonFanoButton.setToggleGroup(algorithmsGroup);
        lzwButton = new RadioButton("compress with LZW algorithm");
        lzwButton.setToggleGroup(algorithmsGroup);
        lossyButton = new RadioButton("compress image with a lossy algorithm (jpeg)");
        //todo write the name of algorithm
        lossyButton.setToggleGroup(algorithmsGroup);
        algorithm.getChildren().addAll(label, shannonFanoButton, lzwButton, lossyButton);
        algorithm.setAlignment(Pos.BASELINE_LEFT);
        shannonFanoButton.setSelected(true);
        lossyButton.setDisable(true);
        algorithm.setPadding(new Insets(5));
        return algorithm;
    }

    private static Node getDataTypeNode() {
        filetypeGroup = new ToggleGroup();
        VBox dataType = new VBox();
        Label label = new Label("Data type:");
        Separator separator = new Separator(Orientation.HORIZONTAL);
        img = new RadioButton("image file (.jpg)");
        img.setToggleGroup(filetypeGroup);
        txt = new RadioButton("text.txt file (.txt)");
        txt.setToggleGroup(filetypeGroup);
        folder = new RadioButton("folder");
        folder.setToggleGroup(filetypeGroup);
        dataType.getChildren().addAll(label, txt, img, folder);
        dataType.setAlignment(Pos.BASELINE_LEFT);
        txt.setSelected(true);
        dataType.setPadding(new Insets(5));
        return dataType;
    }

    private static void setButtonsListeners() {
        txt.setOnAction(e -> {
            lossyButton.setDisable(true);
        });
        folder.setOnAction(e -> {
            lossyButton.setDisable(true);
        });
        img.setOnAction(e -> {
            lossyButton.setDisable(false);
        });
    }

    private static Node getButtons() {
        VBox vBox = new VBox();
        zipButton = new Button("ZIP");
        backButton = new Button("Back");
        decorateButtons(zipButton, backButton);
        vBox.getChildren().addAll(zipButton, backButton);
        zipButton.setOnAction(e -> actionZip());
        backButton.setOnAction(e -> actionBack());
        vBox.setSpacing(20);
        return vBox;
    }

    private static Node getSrcFileChooser() {
        Button button = new Button("choose your source file"); //to open file chooser stage
        Label label = new Label("");//file name or path
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("IMG files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().addAll(extFilter1, extFilter2);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File("..\\MultiMediaProject"));
        directoryChooser.setInitialDirectory(new File("..\\MultiMediaProject"));
        button.setOnAction(e -> {
            if (folder.isSelected()) srcFile = directoryChooser.showDialog(new Stage());
            else srcFile = fileChooser.showOpenDialog(new Stage());
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
            }
        });
        return new HBox(button, label);
    }

    private static void actionZip() {
        FileType fileType = FileType.ELSE;
        if (txt.isSelected())
            fileType = FileType.TEXT;
        else if (img.isSelected())
            fileType = FileType.IMAGE;
        else if (folder.isSelected())
            fileType = FileType.FOLDER;

        String src = srcFile + "";
        String dest = destFile + "";

        if (shannonFanoButton.isSelected()) {
            if (fileType == FileType.FOLDER) {
                ShannonFanoZipper.AutoZip(src, dest);
            } else {
                ShannonFanoZipper shannonFanoZipper = new ShannonFanoZipper(src,
                        dest + ".sf", fileType);
                try {
                    shannonFanoZipper.zip();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Inform(src,dest+".sf");
        } else if (lzwButton.isSelected()) {
            LzwZipper.AutoZip(src,dest);
            Inform(src,dest+".lzw");
        } else if (lossyButton.isSelected()) {
            LossyJPEGZipper.zip(src, dest);
            Inform(src,dest+".jpg");
        }


    }
    public static void Inform(String src,String dest){
        File f1 = new File(src);
        File f2 = new File(dest);

        double d = f1.length()/(f2.length()*1.0);
        Stage stage = new Stage();
        stage.setScene(new Scene(new Label("Done \nThe compression ration is : " + d)));
        stage.show();
    }
    private static void actionBack() {
        ((HomeGUI) mRoot).restart();
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
