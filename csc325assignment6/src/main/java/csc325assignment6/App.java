package csc325assignment6;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//Options: appearance, date, sound, save


public class App extends Application {

    @Override
    public void start(Stage stage) {
        
        stage.setTitle("Settings");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10,10,0, 10));
        Scene scene = new Scene(vbox, 200,300);
        
        // Dropdown box for appearance 
        Label displayLabel = new Label("Set Display:");
        ComboBox<String> display = new ComboBox<>();
        display.getItems().addAll("Default", "Dark","Light");
        display.setValue("Default");

        //Textbox to set date 
        Label dateLabel = new Label("Set Date:");
        TextField date = new TextField();
        date.setPromptText("MM/DD/YYYY");
        Label dateInput = new Label("Today's date: 00/00/0000");

        //Slider to set volume
        Label volumeLabel = new Label("Set Volume:");
        Slider volume = new Slider(0, 100, 0);
        volume.setShowTickMarks(true);
        volume.setSnapToTicks(true);
        volume.setShowTickLabels(true);

        //Button to save and apply the settings 
        Button save = new Button("Save and Apply Settings");

        //event handling for applying settings
        save.setOnAction(e -> {
        
        if(display.getValue().equals("Dark")) {
        scene.getRoot().setStyle("-fx-base: #191821; -fx-text-fill: white;");
        vbox.getChildren().forEach(node -> node.setStyle("-fx-text-fill: white;"));
        } else if(display.getValue().equals("Light")) {
        scene.getRoot().setStyle("-fx-base: #fcd7e7; -fx-text-fill: black;");
        vbox.getChildren().forEach(node -> node.setStyle("-fx-text-fill: black;"));
        } else{
        scene.getRoot().setStyle(""); 
        vbox.getChildren().forEach(node -> node.setStyle(""));
        }
        

        dateInput.setText("Today's date: " + date.getText());
});
        

        vbox.getChildren().addAll(displayLabel, display, dateLabel, date, dateInput, volumeLabel, volume, save);
        stage.setScene(scene);
        stage.show();
    }

   
    
    public static void main(String[] args) {
        launch(args);
    }

}