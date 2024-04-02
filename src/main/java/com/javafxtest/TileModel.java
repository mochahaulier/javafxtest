package com.javafxtest;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TileModel {
    private final int column;
    private final ObjectProperty<Character> letter;
    private final ObjectProperty<TileState> status;

    public TileModel(int column) {
        this.column = column;
        this.letter = new SimpleObjectProperty<>(' ');
        this.status = new SimpleObjectProperty<>(TileState.EMPTY);
    }

    public int getColumn() {
        return column;
    }

    public ObjectProperty<Character> getLetter() {
        return letter;
    }

    public void setLetter(Character c) {
        this.letter.set(c);
    }

    public ObjectProperty<TileState> getStatus() {
        return status;
    }

    public void setStatus(TileState status) {
        this.status.set(status);
    }

    public void reset() {
        letter.setValue(' ');
        status.setValue(TileState.EMPTY);
    }
}
