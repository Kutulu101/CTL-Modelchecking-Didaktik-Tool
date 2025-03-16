    package GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

//Klasse zum gebündelten Verwalten der GUI-Elemente die an einer Relation beteiligt sind
public class Relation {
	
	//alle beteiligten GUI Elemente
   private final Circle firstCircle;
   private final Circle secondCircle;
   private final Text firstCircleLabel;
   private final Text secondCircleLabel;
   private final Shape line;
   private final String detailsString;
   private final int offset;
   


   //Kosntruktor mit allen zu bündelnden Elementen
   public Relation(Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, Shape line, String transition, int offset) {
      
	   this.firstCircle = firstCircle;
      this.secondCircle = secondCircle;
      this.firstCircleLabel = firstCircleLabel;
      this.secondCircleLabel = secondCircleLabel;
      this.line = line;
      this.offset = offset;
      this.detailsString = "Relation: " + firstCircleLabel.getText() + " " + transition + " " + secondCircleLabel.getText();

      
   }
   
   //Diverse Getter und Setter
   public Circle getFirstCircle() {
      return this.firstCircle;
   }

   public Circle getSecondCircle() {
      return this.secondCircle;
   }

   public Text getFirstCircleLabel() {
      return this.firstCircleLabel;
   }

   public Text getSecondCircleLabel() {
      return this.secondCircleLabel;
   }

   public Shape getLine() {
      return this.line;
   }

   public String getDetailsString() {
      return this.detailsString;
   }

   public int getOffset() {
      return this.offset;
   }
}