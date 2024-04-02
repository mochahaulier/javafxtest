package com.javafxtest;

import javafx.beans.property.ObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;

public enum TileState {
   EMPTY, EVALUATED, ABSENT, PRESENT, CORRECT;

   private static final PseudoClass emptyPseudoClass = PseudoClass.getPseudoClass("empty");
   private static final PseudoClass evaluatedPseudoClass = PseudoClass.getPseudoClass("evaluated");
   private static final PseudoClass absentPseudoClass = PseudoClass.getPseudoClass("absent");
   private static final PseudoClass presentPseudoClass = PseudoClass.getPseudoClass("present");
   private static final PseudoClass correctPseudoClass = PseudoClass.getPseudoClass("correct");
   
   public static void updatePseudoClass(Node node, TileState tileState) {
      node.pseudoClassStateChanged(emptyPseudoClass, false);
      node.pseudoClassStateChanged(evaluatedPseudoClass, false);
      node.pseudoClassStateChanged(absentPseudoClass, false);
      node.pseudoClassStateChanged(presentPseudoClass, false);
      node.pseudoClassStateChanged(correctPseudoClass, false);
      
      switch (tileState) {
         case EMPTY:
            node.pseudoClassStateChanged(emptyPseudoClass, true);
            break;
         case EVALUATED:
            node.pseudoClassStateChanged(evaluatedPseudoClass, true);
            break;
         case ABSENT:
            node.pseudoClassStateChanged(absentPseudoClass, true);
            break;
         case PRESENT:
            node.pseudoClassStateChanged(presentPseudoClass, true);
            break;
         case CORRECT:
            node.pseudoClassStateChanged(correctPseudoClass, true);
            break;
      }
   }
   
   public static void addPseudoClass(Node node, ObjectProperty<TileState> property) {
      property.addListener((observable, oldValue, newValue) -> updatePseudoClass(node, newValue));
   }
}
