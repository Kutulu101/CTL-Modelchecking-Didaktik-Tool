package GUI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;

import java.util.HashSet;

import GUI.Relation;

public class Arrow_Builder {
	
	private HashSet<Relation> list_of_relations = new HashSet<>();
	GUI_Main app;
	
	public Arrow_Builder(GUI_Main app){
		this.app = app;
	}
	
	
    public HashSet<Relation> getList_of_relations() {
		return list_of_relations;
	}
    
    // Öffentliche Methode zum Leeren der Liste, für Neustart
    public void clearRelations() {
    	list_of_relations.clear();
 
    }
    
    public void add_relation(Relation relation) {
    	this.list_of_relations.add(relation);
    }

	//Funktion zum Zeichnen des Pfeils
    public void drawArrow(Pane drawingpane, Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel) {
        
    	// wenn es schon eine relation gibt, offset setzen und in der Relation speichern
    	int offset = off_set_for_existing_relations(firstCircle,secondCircle);

    	
    	System.out.println(offset);

    	double startX = firstCircle.getCenterX();
    	double startY = firstCircle.getCenterY();
    	double endX = secondCircle.getCenterX();
    	double endY = secondCircle.getCenterY();

        double dx = endX - startX;
        double dy = endY - startY;

        Shape line; // Gemeinsame Variable für Line oder Arc
        Polygon arrowHead;
        Label arrowLabel;

        // Unterscheidung zwischen Line und Arc
        if (endX != startX && startY != endY) {
            // Berechne die Länge des Richtungsvektors (Abstand)
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            // Normiere den Richtungsvektor
            dx /= distance;
            dy /= distance;
            
            // Berechne den Normalvektor (senkrecht zum Richtungsvektor)
            double nx = -dy;
            double ny = dx;
            
            // Berechne die Randpunkte der Kreise unter Berücksichtigung des Offsets
            double startEdgeX = startX + firstCircle.getRadius() * dx + offset * nx;
            double startEdgeY = startY + firstCircle.getRadius() * dy + offset * ny;
            double endEdgeX = endX - secondCircle.getRadius() * dx + offset * nx;
            double endEdgeY = endY - secondCircle.getRadius() * dy + offset * ny;

            // Zeichne die Linie
            line = new Line(startEdgeX, startEdgeY, endEdgeX, endEdgeY);

            // Zeichne den Pfeilkopf für die Linie
            arrowHead = createArrowHead(startEdgeX, startEdgeY, endEdgeX, endEdgeY);
            
            //hinzufügen des Pfeils zur Pane
            drawingpane.getChildren().addAll(line,arrowHead);
            
            // Beschriftung der Pfeile und Gruppieren als Relation
            this.showTextInputField(startEdgeX - (startEdgeX - endEdgeX) / 2, startEdgeY - (startEdgeY - endEdgeY) / 2, 0, line, arrowHead,firstCircle, secondCircle, firstCircleLabel,secondCircleLabel);
        } else {
            // Zeichne den Arc auf sich selbst
            line = zeichneTeilkreisLinie(startX, startY, firstCircle.getRadius(), 4*offset, 210);

            // Berechnen des Endpunkts des Bogens
            double endXarc = line.getBoundsInLocal().getMinX();
            double endYarc = line.getBoundsInLocal().getMinY();

            // Erstellen der Pfeilspitze für den Arc
            arrowHead = new Polygon();
            double arrowSize = 15;
            double angle = Math.toRadians(195+4*offset); // Korrigiere den Winkel nach Bedarf

            arrowHead.getPoints().addAll(
                endXarc, endYarc, // Spitze des Pfeils
                endXarc - arrowSize * Math.cos(angle - Math.toRadians(30)), endYarc - arrowSize * Math.sin(angle - Math.toRadians(30)), // linke Seite des Pfeils
                endXarc - arrowSize * Math.cos(angle + Math.toRadians(30)), endYarc - arrowSize * Math.sin(angle + Math.toRadians(30))  // rechte Seite des Pfeils
            );
            
          //hinzufügen des Pfeils zur Pane
            drawingpane.getChildren().addAll(line,arrowHead);
            
            // Beschriftung der Pfeile, und Speicher als gesamte Gruppe
            this.showTextInputField(endXarc, endYarc, 50, line, arrowHead,firstCircle, secondCircle, firstCircleLabel,secondCircleLabel);
        }



    }
    
    private Arc zeichneTeilkreisLinie(double centerX, double centerY, double radius, double startAngle_at_large_circle, double arcAngle) {
        
        // Der Radius des Arcs, immer auf denselben Wert
        double arcRadius = 28;

        // Berechne den Mittelpunkt des Arcs
        double arcCenterX = centerX + (radius) * Math.cos(Math.toRadians(startAngle_at_large_circle));
        double arcCenterY = centerY + (radius) * Math.sin(Math.toRadians(startAngle_at_large_circle));

        // Der Startwinkel des Arcs ist relative zum Mittelpunkt des Arcs und hängt von deinem gewünschten Winkel ab
        double startAngle = -startAngle_at_large_circle - arcAngle / 2;

        // Erzeuge den Arc
        Arc arc = new Arc();
        arc.setCenterX(arcCenterX);
        arc.setCenterY(arcCenterY);
        arc.setRadiusX(arcRadius);
        arc.setRadiusY(arcRadius);
        arc.setStartAngle(startAngle);
        arc.setLength(arcAngle);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.BLACK); // Setze die Farbe des Randes hier, z.B. Schwarz

        return arc;
    }
    
    //Funktion zum Zeichnen der Pfeilspitze
    private Polygon createArrowHead(double startX, double startY, double endX, double endY) {
        double arrowLength = 10;
        double arrowWidth = 7;

        double angle = Math.atan2(endY - startY, endX - startX) - Math.PI;

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        double x1 = endX + arrowLength * cos - arrowWidth * sin;
        double y1 = endY + arrowLength * sin + arrowWidth * cos;
        double x2 = endX + arrowLength * cos + arrowWidth * sin;
        double y2 = endY + arrowLength * sin - arrowWidth * cos;

        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(endX, endY, x1, y1, x2, y2);
        arrowHead.setFill(Color.BLACK);

        return arrowHead;
    }
    
    // gibt den Offset zurück
    private int off_set_for_existing_relations(Circle firstCircle, Circle secondCircle) {
        
    	Relation first_found_relation = null;
    	int offset = 0;
    	
    	for (Relation relation : list_of_relations) {
        	//Bestimmen des Offset wertes sollten die Kriese bereits in einer Relation sein
            if ((relation.getFirstCircle().equals(firstCircle) && relation.getSecondCircle().equals(secondCircle)) ||
                (relation.getFirstCircle().equals(secondCircle) && relation.getSecondCircle().equals(firstCircle))) {
            	boolean is_reversed = false;
            	if(relation.getFirstCircle().equals(secondCircle) && relation.getSecondCircle().equals(firstCircle)) {
            		is_reversed = true;
            	}
            	if (first_found_relation == null) {
	            	first_found_relation = relation;
	            	first_found_relation.increaseOffset_counter(is_reversed);
	            	offset = first_found_relation.getOffset_counter();
            	}else{relation.setOffset_counter(first_found_relation.getOffset_counter());}
            }
        }
        return offset;
    }
    
  //Erzeugt nach Abfrage das Textfeld für den Relationspfeil, nciuht ausgelagert weil es die first und second clicked circles entfernt
    void showTextInputField(double x, double y, int offset,Shape line,Polygon arrowHead,Circle firstCircle, Circle secondCircle, Text firstCircle_label,Text secondCircle_label) {
    	

   	
       // Erstelle ein Textfeld für die Benutzereingabe
       TextField inputField = new TextField();
       inputField.setLayoutX(x);
       inputField.setLayoutY(y);
       inputField.setPromptText("Zeichen Komma getrennt eingeben...");

       // Füge das Textfeld zur Szene hinzu
       Pane parentPane = (Pane) firstCircle.getParent().getParent();
       parentPane.getChildren().add(inputField);

       // Erstelle ein leeres Label, das später aktualisiert wird
       Label returnLabel = new Label();
       returnLabel.setLayoutX(x);
       returnLabel.setLayoutY(y);
       returnLabel.setMinWidth(50 + offset);
       returnLabel.setAlignment(Pos.CENTER);
       returnLabel.setStyle("-fx-font-size: 18px;");
       
       // Listener für Eingaben hinzufügen
       inputField.setOnAction(event -> {
           String userInput = inputField.getText();

           // Setze den Text des Labels auf die Benutzereingabe
           returnLabel.setText(userInput);

           // Entferne das Textfeld und füge das Label zur Szene hinzu
           parentPane.getChildren().add(returnLabel);
           parentPane.getChildren().remove(inputField);
           
           // Ablegen als Gruppe um später leichter wieder zu finden, noch aufteilen wenn mehrere Übergänge möglich sind for loop gesplittet am Komma
           String[] labels = returnLabel.getText().split(",");
            
           for (String label:labels) {
           	
           	this.add_relation(new Relation(firstCircle, secondCircle, firstCircle_label, secondCircle_label, line, arrowHead,returnLabel, label));
           }
          
       });

   }
	
	
	
}
