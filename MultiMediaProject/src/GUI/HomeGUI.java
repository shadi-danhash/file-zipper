package GUI;

import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.paint.*;

public class HomeGUI extends Application {

    private final float BUTTON_H = 10 * 5, BUTTON_W = 64 * 5, VERSION = 1.0f,FONT_SIZE=32;
    private final String COMPRESS ="Compress", DECOMPRESS ="Decompress", DISPLAY_IMAGE ="Display image"
            ,Display_text_file="Display text file";
    private HBox appBox;
    private Stage mStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        mStage= primaryStage;
        appBox= new HBox();
        createUI(appBox);
        Scene scene = new Scene(appBox);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("img/icon.jpg"));
        primaryStage.setTitle("Fast Zip");
    }

    private void createUI(HBox hBox) {
        hBox.setBackground(new Background(
                new BackgroundFill(Color.color(1, 1, 1), null, null)));
        hBox.setPadding(new Insets(10));
        VBox choices = new VBox();
        hBox.setBorder(new Border(new BorderStroke(Color.color(0.9,0.9,0.9),BorderStrokeStyle.SOLID
        ,null,new BorderWidths(1))));
        createLogoPart(hBox);
        hBox.getChildren().add(new Separator(Orientation.VERTICAL));
        choices.setSpacing(20);
        hBox.getChildren().add(choices);
        createChoicesList(choices);
        createVersionNumber(choices);
    }

    private void createLogoPart(HBox hBox) {
        Image logo = new Image("img/fast zip logo.jpg");
        ImageView imageView = new ImageView(logo);
        hBox.getChildren().add(imageView);
    }

    private void createVersionNumber(VBox vBox) {
        Label label = new Label("Version " + VERSION);
        vBox.getChildren().add(label);
        vBox.setAlignment(Pos.CENTER);
    }

    private void createChoicesList(VBox vBox) {
        Button compress = new Button(COMPRESS);
        Button decompress = new Button(DECOMPRESS);
        Button displayImage = new Button(DISPLAY_IMAGE);
        Button displayTextFile = new Button(Display_text_file);
        decorateButtons(compress, decompress, displayImage, displayTextFile);
        setButtonsListeners(compress, decompress, displayImage, displayTextFile);
        vBox.getChildren().addAll(compress, decompress, displayImage, displayTextFile);
    }

    private void decorateButtons(Button... args) {
        for (Button button : args) {
            button.setPrefHeight(BUTTON_H);
            button.setPrefWidth(BUTTON_W);
            button.setTextFill(Color.color(1, 1, 1));
            button.setFont(new Font(FONT_SIZE));
            button.setBackground(new Background(
                    new BackgroundFill(Color.color(
                            235.0 / 255, 52.0 / 255, 140.0 / 255), null, null)));

        }
    }

    private void setButtonsListeners(Button... args) {
        for (Button button : args) {
            button.setOnMouseEntered(event -> {
                button.setCursor(Cursor.HAND);
                button.setTextFill(Color.color(1,1,0.1));
                button.setFont(new Font(FONT_SIZE+4));
            });
            button.setOnMouseExited(event -> {
                button.setCursor(Cursor.DEFAULT);
                button.setTextFill(Color.color(1,1,1));
                button.setFont(new Font(FONT_SIZE));
            });
            button.setOnMousePressed(event -> {
                button.setCursor(Cursor.CLOSED_HAND);
            });
            button.setOnMouseReleased(event -> {
                button.setCursor(Cursor.HAND);
            });
            button.setOnMouseClicked(event -> {
                switch (button.getText()){
                    case COMPRESS:
                        appBox.getChildren().remove(2);
                        appBox.getChildren().add(CompressAction.getUI(this));
                        break;
                    case DECOMPRESS:
                        appBox.getChildren().remove(2);
                        appBox.getChildren().add(DecompressAction.getUI(this));

                        break;
                    case DISPLAY_IMAGE:
                        ShowImageAction.showInNewStage();
                        break;
                    case Display_text_file:
                        ShowTextAction.showInNewStage();
                        break;
                    default:
                        throw new RuntimeException(button.getText()+" NOT SUPPORTED YET");
                }
            });
        }
    }

    public void restart(){
        try {
            start(mStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
