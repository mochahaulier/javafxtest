package com.javafxtest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import java.io.IOException;


public class PrimaryController {
    @FXML
    private StackPane parentContainer;
    // the TilePane that holds the (5x6) tiles
    @FXML
    private TilePane mainTilePane;
    @FXML
    private FlowPane keyFlowPane;
    // list of all the tiles
    private List<Label> tiles;
    // list of all the keys
    private List<Button> keys;

    private GameData data = new GameData();

    public void initialize() {
        // get all of the (5x6) labels in the TilePane
        tiles = mainTilePane.getChildren()
        .stream()
        .map(Label.class::cast)
        .collect(Collectors.toList());
        
        for (int i=0; i<6; i++) {
            for (int j=0; j<5; j++) {
                Label label = tiles.get(j+i*5);
                ObjectProperty<Character> obj = data.getLetters().get(i).get(j).getLetter();
                label.textProperty().bind(Bindings.createStringBinding(() -> {
                    final Character value = obj.getValue();
                    return value != null ? value.toString().toUpperCase() : "";
                }, obj)); 
                label.getStyleClass().add("letter");    
                
                TileModel letterModel = data.getLetters().get(i).get(j);
                letterModel.getStatus().addListener((observable, oldValue, newValue) -> {
                    if (newValue.ordinal() > TileState.EVALUATED.ordinal()) {
                        GameAnimation.flipTile(label, letterModel.getColumn(), newValue);
                    } else {
                        if (newValue.ordinal() == TileState.EVALUATED.ordinal()) {
                            GameAnimation.flashTile(label);                                                
                        }
                        TileState.updatePseudoClass(label, newValue);
                    }
                });
            }            
            data.wordValidity.get(i).addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    GameAnimation.wiggleRow(tiles.stream()
                            .skip(data.getCurrentRow() * 5)
                            .limit(5)
                            .collect(Collectors.toList()));                            
                    GameToast.show("NOT A VALID WORD", keys.get(0));
                }
            });
            data.wordGuessed.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    GameAnimation.jumpRow(tiles.stream()
                        .skip(data.getCurrentRow() * 5)
                        .limit(5)
                        .collect(Collectors.toList()));
                        delay(1950, () -> 
                        GameToast.show(data.winningStatements.get(data.getCurrentRow()-1), keys.get(0)));
                }
            });
        }
        data.gameOver.addListener((observable, oldValue, newValue) -> {
            if (newValue) { 
                try {
                    //App.setRoot("secondary");

                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("secondary.fxml")
                    );
                    Parent root = loader.load();
                    Scene scene = parentContainer.getScene();
                    //Set Y of second scene to Height of window
                    root.translateYProperty().set(scene.getHeight());
                    //Add second scene. Now both first and second scene is present
                    parentContainer.getChildren().add(root);
                    SecondaryController controller = loader.getController();
                    controller.initData(data);

                    //Create new TimeLine animation
                    Timeline timeline = new Timeline();
                    //Animate Y property
                    KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_OUT);
                    KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
                    timeline.getKeyFrames().add(kf);
                    //After completing animation, remove first scene
                    timeline.setOnFinished(t -> {
                        parentContainer.getChildren().remove(0);
                    });
                    delay(3150, () -> timeline.play());
    
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            }
        });

        // get all of the keys in the TilePane and add listeners in addPseudoClass
        keys = keyFlowPane.getChildren()
            .stream()
            .map(HBox.class::cast)
            .flatMap(i -> i.getChildren().stream())
            .map(Button.class::cast)
            .filter(button -> button.getText().length() == 1)
            .peek(key -> TileState.addPseudoClass(key, data.getKeyboard().get(key.getText().charAt(0))))
            .collect(Collectors.toList());                         
    }

    @FXML
    private void submitGuess() throws IOException  { 
        String guess = data.getLetters().get(data.getCurrentRow())
            .stream()
            .map(i -> i.getLetter().getValue())
            .collect(Collector.of(
                StringBuilder::new,
                StringBuilder::append,
                StringBuilder::append,
                StringBuilder::toString));

        if (guess.length() < 5) return;

        data.wordValidity.get(data.getCurrentRow()).set(true);
        if (!GameDictionary.isKnownWord(guess)) {
            data.wordValidity.get(data.getCurrentRow()).set(false);
            return;
        }

        // save all non correctly placed letters
        List<Character> present = IntStream.range(0, data.getWord().length())
        .filter(i -> guess.charAt(i) != data.getWord().charAt(i))
        .mapToObj(data.getWord()::charAt)
        .collect(Collectors.toCollection(ArrayList::new));

        data.wordGuessed.set(present.isEmpty());

        for (int i = 0; i < data.getWord().length(); i++) {
            if (guess.charAt(i) == data.getWord().charAt(i)) {
                data.getLetters().get(data.getCurrentRow()).get(i)
                .setStatus(TileState.CORRECT);
            } else if (present.remove((Character)guess.charAt(i))) {
                data.getLetters().get(data.getCurrentRow()).get(i)
                .setStatus(TileState.PRESENT);
            } else {
                data.getLetters().get(data.getCurrentRow()).get(i)
                .setStatus(TileState.ABSENT);
            }
        }

        delay(1500, () -> setKeyboard());
        // A PauseTransition doesn't have to be associated with a node. You can just create one, use its setOnFinished handler for the code to execute after the pause, and call play() to start it.
        data.setCurrentRow(data.getCurrentRow() + 1);
        //GameToast.show(String.valueOf(data.getCurrentRow()), keys.get(0));                
        data.setCurrentColumn(0);
    }

    @FXML
    private void addLetter(ActionEvent event) {
        if (data.getCurrentColumn() == 5 || data.gameOver.getValue()) return;
        data.getLetters()
            .get(data.getCurrentRow())
            .get(data.getCurrentColumn())
            .setLetter(((Button)event.getSource()).getText().toCharArray()[0]);        
        data.getLetters()
            .get(data.getCurrentRow())
            .get(data.getCurrentColumn())
            .setStatus(TileState.EVALUATED);
        data.setCurrentColumn(data.getCurrentColumn() + 1);  
    }

    @FXML 
    private void removeLetter() {
        if (data.getCurrentColumn() == 0) return;
        data.setCurrentColumn(data.getCurrentColumn() - 1);
        data.getLetters().get(data.getCurrentRow()).get(data.getCurrentColumn()).reset();
    }

    void setKeyboard() {       
        data.getKeyboard().entrySet().forEach(entry -> {
            data.getLetters().stream()
            .flatMap(List::stream)
            .filter(i -> i.getLetter().getValue() == entry.getKey())
            .map(i -> i.getStatus().getValue())
            .max(Comparator.comparing(TileState::ordinal))
            .ifPresent(maxValue -> entry.getValue().set(maxValue));
        });
    }

    public static void delay(long millis, Runnable continuation) {
      Task<Void> sleeper = new Task<Void>() {
          @Override
          protected Void call() throws Exception {
              try { Thread.sleep(millis); }
              catch (InterruptedException e) { }
              return null;
          }
      };
      sleeper.setOnSucceeded(event -> continuation.run());
      new Thread(sleeper).start();
    }

    public GameData getGameData() {
        return data;
    }
}
