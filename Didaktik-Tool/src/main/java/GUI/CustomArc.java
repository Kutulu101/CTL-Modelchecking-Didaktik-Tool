package GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

public class CustomArc extends Arc implements line_to_represent_relation{
	
	// Liste von Relationen, die durch Linien repräsentiert werden
		private List<Relation> representedRelations = new ArrayList<>();
		//Label
		private Label returnlabel;
		//ArrowHead
		private Polygon arrowHead;
		
		
		// Konstruktor für CustomArc
	    public CustomArc(Circle firstcircle, Text firstcircleLabel, int startAngle_at_large_circle, List<String>vorauswahl_transitionen) {
	    	super(); // Konstruktor der Superklasse Arc aufrufen
	    	
	    	//den zu Überspannenden Winkel
	    	double arcAngle = 210.0;

	        
	  	  double centerX = firstcircle.getCenterX();
	      double centerY = firstcircle.getCenterY();
	      double radius = firstcircle.getRadius();
	      
	      //Parentpane
	      Pane parentPane = (Pane) firstcircle.getParent().getParent();
	        
	        // Konstanter Arc-Radius
	        double arcRadius = 28.0;
	        
	        //korrektrufaktor für Arc
	        double korr_arc = startAngle_at_large_circle * 4;

	        // Berechne den Punkt, an dem der Arc starten soll
	        double arcCenterX = centerX + radius * Math.cos(Math.toRadians(korr_arc));
	        double arcCenterY = centerY + radius * Math.sin(Math.toRadians(korr_arc));
	        double startAngle = -korr_arc - arcAngle / 2.0;

	        // Setze die Eigenschaften des Arcs
	        this.setCenterX(arcCenterX);
	        this.setCenterY(arcCenterY);
	        this.setRadiusX(arcRadius);
	        this.setRadiusY(arcRadius);
	        this.setStartAngle(startAngle);
	        this.setLength(arcAngle);
	        this.setType(ArcType.OPEN);
	        this.setFill(Color.TRANSPARENT);
	        this.setStroke(Color.BLACK);
	        
	        //Pfeilspitze erstellen
	       createArcArrowHead(startAngle_at_large_circle);
		   updateArrowHeadForArc(startAngle_at_large_circle);
	        
		   //Label erstellen
	        this.showTextInputField(arcCenterX, arcCenterY, startAngle_at_large_circle, firstcircle, firstcircleLabel,vorauswahl_transitionen);
	        
	         //positioniere das Label am Scheitelpunkt
	         positionLabel(firstcircle);
	         
	      //Füge ArrowHead und Arc zu Pane hinzu
	        parentPane.getChildren().addAll(this, this.getArrowHead(),this.getReturnlabel());
	    } 
	    
	 //Erzeugt einen Pfeil desen Winkel an einem Arc ausgerichtet wird
	   private void createArcArrowHead(int offset) {
	      
	      //Erzeuge Pfeil 
	      Polygon arrowHead = new Polygon();
	      arrowHead.setFill(Color.BLACK);
	      
	      //Gib Pfeil zurück
	      this.arrowHead =  arrowHead;
	   }
	   
		//Methode die Arc Updated
		public void updateArc(Relation relation) {
			
			//Berechnen der neun Position von Arc
		    Arc arc = (Arc) relation.getLine();
		    double arcRadius = 28.0;
		    double radius = relation.getFirstCircle().getRadius();
		    double centerX = relation.getFirstCircle().getCenterX();
		    double centerY = relation.getFirstCircle().getCenterY();
		    double startAngleAtLargeCircle = 4 * relation.getOffset();
		    int arcAngle = 210;

		    double arcCenterX = centerX + radius * Math.cos(Math.toRadians(startAngleAtLargeCircle));
		    double arcCenterY = centerY + radius * Math.sin(Math.toRadians(startAngleAtLargeCircle));
		    double startAngle = -startAngleAtLargeCircle - (arcAngle / 2.0);
		    
		    arc.setCenterX(arcCenterX);
		    arc.setCenterY(arcCenterY);
		    arc.setRadiusX(arcRadius);
		    arc.setRadiusY(arcRadius);
		    arc.setStartAngle(startAngle);
		    arc.setLength(arcAngle);
		    
		    //ArrowHeas updaten
		    this.updateArrowHeadForArc(relation.getOffset());
		   
		    //update Position TextLabel
		    positionLabel(relation.getFirstCircle());
		}
	   
	   private void updateArrowHeadForArc(double offset) {
		   
		   //Kosntante Größe 
		    double arrowSize = 15.0;
		    double arrowAngle = Math.toRadians(30.0);
		    
		    //Winkel abhänig von Offset
		    double endAngle = Math.toRadians((double)(4 * offset + 105));
		    
		    double endXarc = this.getCenterX() + this.getRadiusX() * Math.cos(endAngle);
		    double endYarc = this.getCenterY() +this.getRadiusY() * Math.sin(endAngle);
		    
		    double finalAngle = endAngle + Math.PI/2 - 0.25;
		    
		    //berechnen und Setzen der Punkte
		    arrowHead.getPoints().setAll(endXarc,
		    		endYarc, 
		    		endXarc - arrowSize * Math.cos(finalAngle - arrowAngle), 
		    		endYarc - arrowSize * Math.sin(finalAngle - arrowAngle), 
		    		endXarc - arrowSize * Math.cos(finalAngle + arrowAngle), 
		    		endYarc - arrowSize * Math.sin(finalAngle + arrowAngle));
		}
	   
	   
	   private void showTextInputField(double x, double y, int arcAngle,Circle firstCircle, Text firstCircleLabel, List<String> vorauswahl_transitionen) {
	    	  
		 //extrahiere ParentPane
	 	   Pane parentPane = (Pane) firstCircle.getParent().getParent();
	 	   
	 	       
		   //erzeuge und formatiere Label mit eingelesener Transition
	      this.returnlabel = new Label();
	      returnlabel.setMinWidth(50.0);
	      returnlabel.setMinHeight(30.0);
	      returnlabel.setAlignment(Pos.CENTER);
	      returnlabel.setStyle("-fx-font-size: 18px;");
	 	        
	       //Wenn Vorauswahl Transitionen bekannt sind, wird Combobox angezeigt
	       if (vorauswahl_transitionen != null && !vorauswahl_transitionen.isEmpty()) {
	          
	     	  //Combobox erzeugen, positionieren und befüllen
	     	  ComboBox<String> comboBox = new ComboBox();
	          comboBox.getItems().addAll(vorauswahl_transitionen);
	          comboBox.setLayoutX(x);
	          comboBox.setLayoutY(y);
	          comboBox.setPromptText("Wählen Sie eine Option...");
	          parentPane.getChildren().add(comboBox);
	          
	          //Verarbeitung der Combobox-Eingabe
	          comboBox.setOnAction((event) -> {
	         	 
	         	 //Eingabe einlesen
	             String selectedValue = (String)comboBox.getValue();
	             
	             //Eingabe an Label übergeben
	    	     returnlabel.setText(selectedValue);
	    	     
	             //entferne Combobox
	             parentPane.getChildren().remove(comboBox);
	             
	       
	             //Erzeuge ein Relation Objekt und speichere es
	             this.representedRelations.add(new Relation(firstCircle, firstCircle, firstCircleLabel, firstCircleLabel,this,selectedValue, arcAngle));
	          });
	       } else {//Wenn keine Transitionen vorausgewählt wurden
	     	  createInputField(x, y, parentPane, firstCircle, firstCircleLabel, arcAngle);
	       }     
	      
	      //Drag and Drop für Return Label ermöglichen
	      this.returnlabel.setOnMousePressed((event2) -> {
	         returnlabel.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
	      });
	      returnlabel.setOnMouseDragged((event2) -> {
	         double[] initialMousePosition = (double[])returnlabel.getUserData();
	         double initialX = initialMousePosition[0];
	         double initialY = initialMousePosition[1];
	         double deltaX = event2.getSceneX() - initialX;
	         double deltaY = event2.getSceneY() - initialY;
	         returnlabel.setLayoutX(returnlabel.getLayoutX() + deltaX);
	         returnlabel.setLayoutY(returnlabel.getLayoutY() + deltaY);
	         returnlabel.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
	      });
	       

	   }
	   
	   public void createInputField(double x, double y, Pane parentPane, Circle firstCircle,Text firstCircleLabel, int offset) {
		    // Erzeuge und positioniere das Textfeld zur Eingabe
		    TextField inputField = new TextField();
		    inputField.setLayoutX(x);
		    inputField.setLayoutY(y);
		    inputField.setPromptText("Zeichen Komma getrennt eingeben...");
		    parentPane.getChildren().add(inputField);
		    
		    // Verarbeitung der Eingabe
		    inputField.setOnAction((event) -> {
		        String userInput = inputField.getText().replaceAll(",$|^,", "");;
		        String[] labels = userInput.split(",");
		        
		        // Überprüfung der Eingabe
		        for (String label : labels) {
		            if (label.length() != 1 && label !="") {
		                // Falls ungültig, zeige eine Warnung und starte erneut
		                showWarningAndRetry(x, y, parentPane, firstCircle, firstCircleLabel, offset);
		                parentPane.getChildren().remove(inputField);
		                return;
		            }
		        }
		        
		        // Falls gültig, setze das Label und erstelle die Relationen
		        returnlabel.setText(userInput);
		        parentPane.getChildren().remove(inputField);
		        for (String label : labels) {
		        	 this.representedRelations.add(new Relation(firstCircle, firstCircle, firstCircleLabel, firstCircleLabel,this, label, offset));
		        }
		    });
		}

		private void showWarningAndRetry(double x, double y, Pane parentPane, Circle firstCircle,  Text firstCircleLabel, int offset) {
		    // Erzeuge und zeige eine Warnung
		    Alert alert = new Alert(Alert.AlertType.WARNING);
		    alert.setTitle("Ungültige Eingabe");
		    alert.setHeaderText(null);
		    alert.setContentText("Bitte genau ein Zeichen pro Label eingeben, getrennt durch Kommas.");
		    alert.showAndWait();
		    
		    // Erneute Eingabeaufforderung
		    createInputField(x, y, parentPane, firstCircle,firstCircleLabel,offset);
		}
	   
	   
	   
		 //Positioniert das Label auf der gedachten Linie zwischen Scheitel des Arcs und des Kreises, sodass Label immer außen steht
		   private void positionLabel(Circle circle) {
		      
			   //Scheitelpunkt berechnen
		       double centerX = this.getCenterX();
		       double centerY = this.getCenterY();
		       double radiusX = this.getRadiusX();
		       double radiusY = this.getRadiusY();
		       double startAngle = Math.toRadians(this.getStartAngle());
		       double length = Math.toRadians(this.getLength());
		       double midAngle = startAngle + length / 2.0;
		       
		       //Scheitelpunkt
		       double x = centerX + radiusX * Math.cos(midAngle);
		       double y = centerY - radiusY * Math.sin(midAngle);
			   
			   
			   //Richtung berechnen
			   double circleCenterX = circle.getCenterX();
		      double circleCenterY = circle.getCenterY();
		      double deltaX = x - circleCenterX;
		      double deltaY = y - circleCenterY;
		      double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		      double directionX = deltaX / distance;
		      double directionY = deltaY / distance;
		      
		      //Abstand um 12 Vergrößen für bessere Optik
		      double labelDistance = distance + 12.0D;
		      
		      //Label Pos. berechnen
		      double labelCenterX = circleCenterX + directionX * labelDistance;
		      double labelCenterY = circleCenterY + directionY * labelDistance;
		      
		      //Label zentrieren
		      returnlabel.setLayoutX(labelCenterX - 25.0D);
		      returnlabel.setLayoutY(labelCenterY - 15.0D);
		   }
		   

	@Override
	public List<Relation> getRelations() {
		// TODO Auto-generated method stub
		return representedRelations;
	}
	public Label getReturnlabel() {
		return returnlabel;
	}
	public Polygon getArrowHead() {
		return arrowHead;
	}
	
	

}
