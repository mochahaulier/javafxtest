package com.javafxtest;

import java.util.List;

import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class GameAnimation {
    
    static void flashTile(Node lbl) {
        ScaleTransition st1 = new ScaleTransition(Duration.millis(20.0), lbl);
        st1.setToX(1.07);
        st1.setToY(1.07);
        st1.setCycleCount(2);
        st1.setAutoReverse(true);
        ScaleTransition st2 = new ScaleTransition(Duration.millis(20.0), lbl);
        st2.setToX(0.93);
        st2.setToY(0.93);
        st2.setCycleCount(2);
        st2.setAutoReverse(true);
        SequentialTransition seqT = new SequentialTransition(st1, st2);
        seqT.play();
     }
     
     static void flipTile(Node lbl, int column, TileState newState) {
        Duration dur2 = Duration.millis(250);  

        PauseTransition pause = new PauseTransition(Duration.millis(column * 250));

        RotateTransition rotate = new RotateTransition(dur2, lbl);  
        rotate.setByAngle(90f);  
        rotate.setAxis(Rotate.X_AXIS);
        // change the color of the tile at the end of first rotation
        rotate.setOnFinished((finish) -> TileState.updatePseudoClass(lbl, newState));

        RotateTransition rotate2 = new RotateTransition(dur2, lbl);
        rotate2.setByAngle(-90f);  
        rotate2.setAxis(Rotate.X_AXIS);
        
        SequentialTransition seqT = new SequentialTransition (pause, rotate, rotate2);
        seqT.play();  
     }

    static void wiggleRow(List<Node> labels) {
        for (Node lbl: labels) {
            lbl.setTranslateX(-5.0);

            TranslateTransition tt = new TranslateTransition(Duration.millis(50), lbl);
            tt.setByX(10);
            tt.setCycleCount(6);            
            tt.setAutoReverse(true);
            tt.setOnFinished((finish) -> lbl.setTranslateX(0.0));
            tt.play();
        }
     }

    static void jumpRow(List<Node> labels) {
        for (int i=0; i<labels.size(); i++) {
            Node lbl = labels.get(i);
            
            PauseTransition pause = new PauseTransition(Duration.millis(i * 42 + 1500));        
            
            TranslateTransition tt1 = new TranslateTransition(Duration.millis(125), lbl);
            tt1.setFromY(0);
            tt1.setByY(-55);
            tt1.setCycleCount(2);            
            tt1.setAutoReverse(true);
            tt1.setOnFinished((finish) -> lbl.setTranslateY(0.0));
            
            TranslateTransition tt2= new TranslateTransition(Duration.millis(100), lbl);
            tt2.setFromY(0);
            tt2.setByY(-20);
            tt2.setCycleCount(2);            
            tt2.setAutoReverse(true);
            tt2.setOnFinished((finish) -> lbl.setTranslateY(0.0));

            SequentialTransition seqT = new SequentialTransition(pause, tt1, tt2);
            seqT.play();
        }
     }
}
