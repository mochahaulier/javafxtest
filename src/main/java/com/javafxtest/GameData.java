package com.javafxtest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;

public class GameData {
    // shows when gameOver depending on how many tries it took to find the word
    public final List<String> winningStatements = Arrays.asList(
            "GENIUS", "MAGNIFICENT", "IMPRESSIVE",
            "SPLENDID", "GREAT", "PHEW",
            "OH :/");
    
    private int currentColumn;
    private IntegerProperty currentRow;
    private List<List<TileModel>> letters;
    Map<Character, ObjectProperty<TileState>> keyboard;
    List<SimpleBooleanProperty> wordValidity;
    //BooleanBinding wordsValid;
    SimpleBooleanProperty wordGuessed;
    ObservableBooleanValue gameOver;

    private String word;

    public GameData() {
        this.currentColumn = 0;
        this.currentRow = new SimpleIntegerProperty(0);
        this.letters = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            List<TileModel> column = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                column.add(new TileModel(j));
            }
            letters.add(column);
        }
        this.word = GameDictionary.getWord();

        this.keyboard = IntStream.rangeClosed('A', 'Z')
            .mapToObj(c -> (char) c)
            .collect(Collectors.toMap(c -> c, c -> new SimpleObjectProperty<>(TileState.EVALUATED)));

        this.wordValidity = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            this.wordValidity.add(new SimpleBooleanProperty(true));
        }

        // this.wordsValid = new SimpleBooleanProperty(true).and(wordValidity.get(0));
        // for (SimpleBooleanProperty property : this.wordValidity) {
        //     this.wordsValid = this.wordsValid.and(property);
        // }

        this.wordGuessed = new SimpleBooleanProperty(false);

        this.gameOver = Bindings.createBooleanBinding(() -> 
            (this.currentRow.intValue() > 5) || wordGuessed.getValue(),
            this.currentRow, this.wordGuessed);
    }

    public Map<Character, ObjectProperty<TileState>> getKeyboard() {
        return this.keyboard;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public void setCurrentColumn(int currentColumn) {
        this.currentColumn = currentColumn;
    }

    public int getCurrentRow() {
        return this.currentRow.intValue();
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow.set(currentRow);
    }

    public String getWord() {
        return word;
    }

    public List<List<TileModel>> getLetters() {
        return letters;
    } 
}