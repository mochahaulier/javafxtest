package com.javafxtest;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameToast {
    
    private static final int POPUP_TIMEOUT = 1000;
    private static Popup createPopup(final String message) {
        final Popup popup = new Popup();
        Label label = new Label(message);        
        label.getStylesheets().add(App.class.getResource("primary.css").toExternalForm());
        label.getStyleClass().add("popup");

        popup.setAutoFix(true);
        popup.getContent().add(label);
        return popup;
    }

    public static void show(final String message, final Control control) {
        Stage stage = (Stage) control.getScene().getWindow();
        final Popup popup = createPopup(message);
        
        popup.setOnShown(e -> {
            popup.setX(stage.getX() + stage.getWidth() / 2 - popup.getWidth() / 2);
            popup.setY(stage.getY() + stage.getHeight() / 2 - popup.getHeight() / 2);
            DoubleProperty opacity = new SimpleDoubleProperty(0);
            opacity.addListener((obs, oldX, newX) -> popup.setOpacity(newX.doubleValue()));
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.2), new KeyValue(opacity, 1));
            Timeline animation = new Timeline(keyFrame);
            animation.play();
        });

        popup.show(stage);

        new Timeline(new KeyFrame(Duration.millis(POPUP_TIMEOUT),
            ae -> {
                DoubleProperty opacity = new SimpleDoubleProperty(1);
                opacity.addListener((obs, oldX, newX) -> popup.setOpacity(newX.doubleValue()));
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.2), new KeyValue(opacity, 0));
                Timeline animation = new Timeline(keyFrame);
                animation.setOnFinished(e -> popup.hide());
                animation.play();
            })).play();
    }
}

