package com.javafxtest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.Node;

public class SecondaryController {
    // Add references to the controls in Layout2.fxml
    @FXML
    private Label gameState;
    @FXML
    private Label labelTop;
    @FXML
    private Label labelBottom;
    @FXML
    private Button nextButton;

    @FXML
    private HBox tileBox;
    // list of all the tiles
    private List<Label> tiles;

    @FXML
    private StackPane parentContainer2;

    @FXML
    private void switchToPrimary() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("primary.fxml")
            );
            Parent root = loader.load();
            Scene scene = parentContainer2.getScene();
            root.translateYProperty().set(0);
            parentContainer2.translateYProperty().set(0);
            parentContainer2.getChildren().add(root);
            PrimaryController controller = loader.getController();
            // swap the two, so the new is in back
            ObservableList<Node> workingCollection = 
                FXCollections.observableArrayList(parentContainer2.getChildren());
            Collections.swap(workingCollection, 0, 1);
            parentContainer2.getChildren().setAll(workingCollection);

            Node nodeMove = parentContainer2.getChildren().get(1);
            // Create new TimeLine animation
            Timeline timeline = new Timeline();
            // Animate Y property
            KeyValue kv = new KeyValue(nodeMove.translateYProperty(), 
                scene.getHeight(), Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
            timeline.getKeyFrames().add(kf);
            // After completing animation, remove first scene
            timeline.setOnFinished(t -> {
                parentContainer2.getChildren().remove(1);
            });
            timeline.play();            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }    

    public void initialize() {
        nextButton.getStyleClass().add("key-button");
        TileState.updatePseudoClass(nextButton, TileState.CORRECT);
    }

    void initData(GameData data) {      
        tiles = tileBox.getChildren()
          .stream()
          .map(Label.class::cast)
          .peek(lbl -> {
                lbl.getStyleClass().add("letter");
                TileState.updatePseudoClass(lbl, TileState.CORRECT);
            })
          .collect(Collectors.toList());
  
        for (int i=0; i<5; i++) {
            tiles.get(i).setText(String.valueOf(data.getWord().charAt(i)));
        }
   
        gameState.setText(data.winningStatements.get(data.getCurrentRow()));

        labelTop.setText(data.wordGuessed.getValue() ?
            "YOU FOUND THE WORD" : "YOU DIDN'T FIND THE WORD");
        int guesses = (guesses = data.getCurrentRow()) > 5  ? guesses : guesses + 1;
        labelBottom.setText("WITHIN " + String.valueOf(guesses) + 
            (guesses > 1 ? " GUESSES" : " GUESS"));
    }
}