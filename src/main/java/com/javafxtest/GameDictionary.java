package com.javafxtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameDictionary {
   private static List<String> hidden = getHidden();
   private static List<String> valid = getValid();

   private static List<String> getHidden() {
      List<String> hidden = new ArrayList<>();
      try (Scanner scan2 = new Scanner(App.class.getResource("hiddenwords.txt").openStream())) {
         while (scan2.hasNext()) {
            hidden.add(scan2.next());
         }
      } catch (Exception e2) {
         System.out.printf("Caught Exception: %s%n", e2.getMessage());
         e2.printStackTrace();
      }
      return hidden;
   }

   private static List<String> getValid() {
      List<String> valid = new ArrayList<>();
      try (Scanner scan = new Scanner(App.class.getResource("validwords.txt").openStream())) {
         while (scan.hasNext()) {
            valid.add(scan.next());
         }
      } catch (Exception e) {
         System.out.printf("Caught Exception: %s%n", e.getMessage());
         e.printStackTrace();
      }
      return valid;
   }

   public static String getWord() {
      return hidden.get((new Random()).nextInt(hidden.size())).toUpperCase();
   }
 
   public static Boolean isKnownWord(String word) {
       return (Stream.concat(hidden.stream(), valid.stream()).collect(Collectors.toList()))
       .contains(word.toLowerCase());
   }
}




   
   
