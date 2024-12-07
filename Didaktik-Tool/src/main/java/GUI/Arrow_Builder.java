   package GUI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

//Klasse die Pfeile zwischen Zuständen (Kreisen) erzeugt und verwaltet und diese für das Backend als Relation zur Verfügugn stellt
public class Arrow_Builder {
   private HashSet<Relation> list_of_relations = new HashSet();
   private double offsetX;
   private double offsetY;
   private static final double EPSILON = 0.1;
   
   //Methode zum Vergleichen von Double Werten
   private static boolean areDoublesEqual(double a, double b) {
      return Math.abs(a - b) < 0.1;
   }

   public HashSet<Relation> getList_of_relations() {
      return this.list_of_relations;
   }
   
   //Löscht alle bsiher erzeugten Relationen
   public void clearRelations() {
      this.list_of_relations.clear();
   }
   
   //Fügt Relation hinzu
   public void add_relation(Relation relation) {
      this.list_of_relations.add(relation);
   }
   
   //Klasse die Pfeile und Relation zwischen zwei Kreisen erzeugt
   public void drawArrow(Pane drawingpane, Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, List<String> vorauswahl_transitionen) {
      
	   //Offset versetzt Pfeil wenn der neue Pfeile mit einem existierenden Überlappen würden
	   int offset = this.off_set_for_existing_relations(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel);
      
	   //Kreismittelpunkte extrhaieren
	  double startX = firstCircle.getCenterX();
      double startY = firstCircle.getCenterY();
      double endX = secondCircle.getCenterX();
      double endY = secondCircle.getCenterY();
      
      //Objekt für Pfeilspitze
      Polygon arrowHead;
      
      //Bei zwei verschiedenen Kreisen wird eine Line erzeugt
      if (startX != endX && startY != endY) {
    	  
    	  //Berechnet die Start und Endpunkte zwischen den Rändern der Kreise und den Anstieg der Linie
         Arrow_Builder.EdgePoints points = this.calculateEdgePoints(startX, startY, endX, endY, firstCircle.getRadius(), secondCircle.getRadius(), offset);
         
         //Anstieg der Linie mit Vorzeichen versehen
         double m;
         if (firstCircle.getCenterY() > secondCircle.getCenterY()) {
            m = points.m;
         } else {
            m = -points.m;
         }
         
         //Erzeugt eine CustomLine, Java Fx Line in Kombination mit einer Quadratischen Kurve
         Arrow_Builder.CustomLine customLine = new Arrow_Builder.CustomLine(points.startX, points.startY, points.endX, points.endY, points.m);
         
         //erzeugt Pfeilspitze
         arrowHead = this.createArrowHead(customLine.getArrow_x(), customLine.getArrow_y(), points.endX, points.endY);
         
         //Fügt Pfeilspitze und CustomLine hinzu
         drawingpane.getChildren().addAll(customLine, customLine.getCurvedPath(), arrowHead);
         
         //Quadratischen Kurve anzeigen, Linie ausblenden
         customLine.showCurved();
         
         //nach dem Erzeugen der Linie soll Textfeld zum eingeben der Transition angezeigt werden
         Label returnLabel = this.showTextInputField(customLine.getX_textfield_point_x(), customLine.getY_textfield_point_y(), offset, customLine, arrowHead, firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, vorauswahl_transitionen);
         drawingpane.getChildren().addAll(returnLabel);
      } 
      else {//Wenn zweimal der gleiche Kreis angklickt wurde Arc erzeugen 
    	  
    	 //zeichen den Arc
         Shape line = this.zeichneTeilkreisLinie(startX, startY, firstCircle.getRadius(), (double)(4 * offset), 210.0);
         
         //Zeichne den ArrowHead
         arrowHead = this.createArcArrowHead((Arc)line, offset);
         
         //Füge ArrowHead und Arc zu Pane hinzu
         drawingpane.getChildren().addAll(line, arrowHead);
         
         //Zeige Input Textfeld
         Label returnLabel = this.showTextInputField(((Arc)line).getCenterX(), ((Arc)line).getCenterY(), offset, line, arrowHead, firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, vorauswahl_transitionen);
         drawingpane.getChildren().addAll(returnLabel);
      }

   }
   
   //Berechnet Endpunkte der Linie und gibt Sie gebündelt zurück
   private Arrow_Builder.EdgePoints calculateEdgePoints(double startX, double startY, double endX, double endY, double radius1, double radius2, int offset) {
      
	   //Anstieg pro Pixel in x- und y Richtung berechnen
	  double dx = endX - startX;
      double dy = endY - startY;
      double distance = Math.sqrt(dx * dx + dy * dy);
      dx /= distance;
      dy /= distance;
      
      //Anstieg der Normalen berechnen
      double nx = -dy;
      
      //Berechne den Schnittpunkt von ersten Kreis  und der Linie zwischen den Kreismittelpunkten
      double startEdgeX = startX + radius1 * dx + (double)offset * nx;
      double startEdgeY = startY + radius1 * dy + (double)offset * dx;

     //Berechne den Schnittpunkt von zweiten Kreis  und der Linie zwischen den Kreismittelpunkten
      double endEdgeX = endX - radius2 * dx + (double)offset * nx;
      double endEdgeY = endY - radius2 * dy + (double)offset * dx;
      
      //Berechne den Anstieg der Geraden
      double m = (endEdgeY - startEdgeY) / (endEdgeX - startEdgeX);
      
      //Gib berechnete Werte gebündelt zurück
      return new Arrow_Builder.EdgePoints(startEdgeX, startEdgeY, endEdgeX, endEdgeY, m);
   }

   private Arc zeichneTeilkreisLinie(double centerX, double centerY, double radius, double startAngle_at_large_circle, double arcAngle) {
      
	  //kosntanter Arc-Radius
	  double arcRadius = 28.0;
	  
	  //Berechne den Punkt an dem der Arc Starten soll
      double arcCenterX = centerX + radius * Math.cos(Math.toRadians(startAngle_at_large_circle));
      double arcCenterY = centerY + radius * Math.sin(Math.toRadians(startAngle_at_large_circle));
      double startAngle = -startAngle_at_large_circle - arcAngle / 2.0;
      
      //Erzeuge Arc 
      Arc arc = new Arc();
      arc.setCenterX(arcCenterX);
      arc.setCenterY(arcCenterY);
      arc.setRadiusX(arcRadius);
      arc.setRadiusY(arcRadius);
      arc.setStartAngle(startAngle);
      arc.setLength(arcAngle);
      arc.setType(ArcType.OPEN);
      arc.setFill(Color.TRANSPARENT);
      arc.setStroke(Color.BLACK);
      
      //gib Arc zurück
      return arc;
   }
   
   //Erzeugt einen Pfeil desen Winkel an einer Geraden ausgerichtet wird
   private Polygon createArrowHead(double startX, double startY, double endX, double endY) {
	   
      //Pfeil erzeugen
      Polygon arrowHead = new Polygon();
      //richtet ArrowHEad an Linie aus
      updateArrowHead(arrowHead,startX,startY,endX,endY);
      arrowHead.setFill(Color.BLACK);
      
      //Pfeil zurückgeben
      return arrowHead;
   }
   
 //Erzeugt einen Pfeil desen Winkel an einem Arc ausgerichtet wird
   private Polygon createArcArrowHead(Arc arc, int offset) {
      
      //Erzeuge Pfeil 
      Polygon arrowHead = new Polygon();
      updateArrowHeadForArc(arc,arrowHead,offset);
      arrowHead.setFill(Color.BLACK);
      
      //Gib Pfeil zurück
      return arrowHead;
   }
   
	//Hilfsmethide die Ausrichtung von Pfeil abhänig von Line updated 
   private void updateArrowHead(Polygon arrowHead, double startX, double startY, double endX, double endY) {
      //Winkel von Line
	   double angle = Math.atan2(endY - startY, endX - startX) + Math.PI;
	   
      //konstante Größe
	   double arrowLength = 10.0;
      double arrowWidth = 7.0;
      
      //Ausrichtung
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      double x1 = endX + arrowLength * cos - arrowWidth * sin;
      double y1 = endY + arrowLength * sin + arrowWidth * cos;
      double x2 = endX + arrowLength * cos + arrowWidth * sin;
      double y2 = endY + arrowLength * sin - arrowWidth * cos;
      
      arrowHead.getPoints().setAll(endX, endY, x1, y1, x2, y2);
   }
   
   private void updateArrowHeadForArc(Arc arc, Polygon arrowHead, double offset) {
	   
	   //Kosntante Größe 
	    double arrowSize = 15.0;
	    double arrowAngle = Math.toRadians(30.0);
	    
	    //Winkel abhänig von Offset
	    double endAngle = Math.toRadians((double)(4 * offset + 105));
	    
	    double endXarc = arc.getCenterX() + arc.getRadiusX() * Math.cos(endAngle);
	    double endYarc = arc.getCenterY() + arc.getRadiusY() * Math.sin(endAngle);
	    
	    double finalAngle = endAngle + Math.PI/2 - 0.25;
	    
	    //berechnen und Setzen der Punkte
	    arrowHead.getPoints().setAll(endXarc,
	    		endYarc, 
	    		endXarc - arrowSize * Math.cos(finalAngle - arrowAngle), 
	    		endYarc - arrowSize * Math.sin(finalAngle - arrowAngle), 
	    		endXarc - arrowSize * Math.cos(finalAngle + arrowAngle), 
	    		endYarc - arrowSize * Math.sin(finalAngle + arrowAngle));
	}
   
   //Methode die Textfeld oder Combobox zur Eingabe der Transtions die der Pfeil repräsentieren soll einliest
   private Label showTextInputField(double x, double y, int offset, Shape line, Polygon arrowHead, Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, List<String> vorauswahl_transitionen) {
      
	   //extrahiere ParentPane
	   Pane parentPane = (Pane)firstCircle.getParent().getParent();
	   
	   //erzeuge und formatiere Label mit eingelesener Transition
      Label returnLabel = new Label();
      returnLabel.setMinWidth(50.0);
      returnLabel.setMinHeight(30.0);
      returnLabel.setAlignment(Pos.CENTER);
      returnLabel.setStyle("-fx-font-size: 18px;");
      
      //Wenn keine Vorauswahl Transitionen bekannt sind, wird Combobox angezeigt
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
            returnLabel.setText(selectedValue);
            
            //entferne Combobox
            parentPane.getChildren().remove(comboBox);
            
            //Erzeuge ein Relation Objekt und speichere es
            this.add_relation(new Relation(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, line, arrowHead, returnLabel, selectedValue, offset));
         });
      } else {//Wenn keine Transitionen vorausgewählt wurden
   
    	 //Erzeuge und Positioniere Textfield zur Eingabe
         TextField inputField = new TextField();
         inputField.setLayoutX(x);
         inputField.setLayoutY(y);
         inputField.setPromptText("Zeichen Komma getrennt eingeben...");
         parentPane.getChildren().add(inputField);
         
         //Verarbeitung der Eingabe in Textfield
         inputField.setOnAction((event) -> {
	         String userInput = inputField.getText();
	         returnLabel.setText(userInput);
	         
	         //entferne Textfeld
	         parentPane.getChildren().remove(inputField);
	         
	         //erzeuge eine neue Relation für jede mit Komma getrennte Transition
	        String[] labels = returnLabel.getText().split(",");
	        for(String label: labels) {
	           this.add_relation(new Relation(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, line, arrowHead, returnLabel, label, offset));
	        }
	         });
      }   
      
      //Wenn Line ein Arc soll die Beschriftung genau am Scheitelpunkt psoitioniert werden
      if (line instanceof Arc) {
    	  
         Arc arc = (Arc)line;
         
         //positioniere das Label am Scheitelpunkt
         positionLabel(returnLabel, firstCircle,arc);
         
      } else {//Wenn kein Arc dann Label nur zentrieren
         returnLabel.setLayoutX(x - 25.0);
         returnLabel.setLayoutY(y - 15.0);
      }
      
      //Drag and Drop für Return Label ermöglichen
      returnLabel.setOnMousePressed((event2) -> {
         returnLabel.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
      });
      returnLabel.setOnMouseDragged((event2) -> {
         double[] initialMousePosition = (double[])returnLabel.getUserData();
         double initialX = initialMousePosition[0];
         double initialY = initialMousePosition[1];
         double deltaX = event2.getSceneX() - initialX;
         double deltaY = event2.getSceneY() - initialY;
         returnLabel.setLayoutX(returnLabel.getLayoutX() + deltaX);
         returnLabel.setLayoutY(returnLabel.getLayoutY() + deltaY);
         returnLabel.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
      });
       
      return returnLabel;

   }
   
   //Berechnet den notwenigen Versatz damit sich keine Linien die gleiche Kreise verbiden überlappen
   private int off_set_for_existing_relations(Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel) {
      
	  int offset = 0;
	  
	  //Schrittweite des Versatzes, Primzahl weil Arc rund ist und es erst nach sehr vieleln Pfeilen zu vollständigen Überlappung kommt
      int step = 23;
      
      //maximal 20 Versuche Paltz zu finden
      int max_tiefe = 20;
      
      //Flag ob Pfeile überlappen
      boolean conflictFound = false;
      
      do {
         if (max_tiefe-- <= 0) {
            return offset;
         }
         //durchlaufe alle Relationen 
         for(Relation relation:this.list_of_relations) {
        	 //wenn bewegter Kreis enthalten ist
            if (this.containsCircles(relation, firstCircle, secondCircle) && this.isConflict(relation.getLine(), firstCircle, secondCircle, offset)) {
               //Vergrößere den offset und prüfe nochmal
            	conflictFound = true;
               offset += step; //PFeilrichtung egal weil sich Vorzeichen wegkürzen
               break;
            }
         }
      } while(conflictFound);

      return offset;
   }

   	//Hilfsmehtode prüft ob circle in relation vorhanden ist
   private boolean containsCircles(Relation relation, Circle firstCircle, Circle secondCircle) {
      return relation.getFirstCircle().equals(firstCircle) && relation.getSecondCircle().equals(secondCircle) || relation.getFirstCircle().equals(secondCircle) && relation.getSecondCircle().equals(firstCircle);
   }
   
 //Hilfsmehtode prüft ob sich Linien oder Arcs überlappen
   private boolean isConflict(Shape shape, Circle firstCircle, Circle secondCircle, int offset) {
      if (shape instanceof Line) {
         return this.isLineConflict((Line)shape, firstCircle, secondCircle, offset);
      } else {
         return shape instanceof Arc ? this.isArcConflict((Arc)shape, offset) : false;
      }
   }
   
   //Hilfsmehtode prüft ob sich Linien überlappen, über Start und Endpunke
   private boolean isLineConflict(Line line, Circle firstCircle, Circle secondCircle, int offset) {
      Arrow_Builder.EdgePoints points = this.calculateEdgePoints(firstCircle.getCenterX(), firstCircle.getCenterY(), secondCircle.getCenterX(), secondCircle.getCenterY(), firstCircle.getRadius(), secondCircle.getRadius(), offset);
      return Arrow_Builder.areDoublesEqual(line.getStartX(), points.startX) && Arrow_Builder.areDoublesEqual(line.getStartY(), points.startY) && Arrow_Builder.areDoublesEqual(line.getEndX(), points.endX) && Arrow_Builder.areDoublesEqual(line.getEndY(), points.endY) || Arrow_Builder.areDoublesEqual(line.getStartX(), points.endX) && Arrow_Builder.areDoublesEqual(line.getStartY(), points.endY) && Arrow_Builder.areDoublesEqual(line.getEndX(), points.startX) && Arrow_Builder.areDoublesEqual(line.getEndY(), points.startY);
   }
 //Hilfsmehtode prüft ob sich Arcs überlappen, Über den Start Winkel
   private boolean isArcConflict(Arc arc, int offset) {
      double startAngleAtLargeCircle = (double)(4 * offset);
      double arcAngle = 210.0;
      double startAngle = -startAngleAtLargeCircle - arcAngle / 2.0;
      return Arrow_Builder.areDoublesEqual(arc.getStartAngle(), startAngle);
   }
   
   //Methode die Arrwos updatet wenn ein Kreis durch Drag and Drop bewegt wurde
   public void updateArrows(Circle movedCircle) {
	   //Sucht alle Relationen in denen der Kreis vorkommt
	    for (Relation relation : this.list_of_relations) {
	        if (!relation.getFirstCircle().equals(movedCircle) && !relation.getSecondCircle().equals(movedCircle)) {
	            continue;
	        }
	        if (relation.getLine() instanceof CustomLine) {
	            updateStraightLine(relation);
	        } else {
	            updateArc(relation);
	        }
	    }
	}
   
   //Berechnet Endpunkte neu und updatet die Linie
	private void updateStraightLine(Relation relation) {
		
		//Startpunkte bestimmen
	    int offset = relation.getOffset();
	    double startX = relation.getFirstCircle().getCenterX();
	    double startY = relation.getFirstCircle().getCenterY();
	    double endX = relation.getSecondCircle().getCenterX();
	    double endY = relation.getSecondCircle().getCenterY();
	    
	    //EdgePoints berechnen
	    Arrow_Builder.EdgePoints points = calculateEdgePoints(startX, startY, endX, endY,
	            relation.getFirstCircle().getRadius(),
	            relation.getSecondCircle().getRadius(),
	            offset);
	    
	    //CustomLine updaten
	    Arrow_Builder.CustomLine line = (Arrow_Builder.CustomLine) relation.getLine();
	    line.updateLineAndCurve(points.startX, points.startY, points.endX, points.endY, points.m);
	    line.showCurved();
	    
	    //ArrowHead updaten
	    updateArrowHead(relation.getArrowHead(), line.getArrow_x(), line.getArrow_y(), points.endX, points.endY);
	    
	    //Textlabel updaten
	    relation.getArrowLabel().setLayoutX(line.getX_textfield_point_x());
	    relation.getArrowLabel().setLayoutY(line.getY_textfield_point_y());
	}
	
	//Methode die Arc Updated
	private void updateArc(Relation relation) {
		
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
	    updateArrowHeadForArc(arc, relation.getArrowHead(), relation.getOffset());
	    
	    //update Position TextLabel
	    positionLabel(relation.getArrowLabel(),relation.getFirstCircle(),arc);
        
	}
	
	//Positioniert das Label auf der gedachten Linie zwischen Scheitel des Arcs und des Kreises, sodass Label immer außen steht
   private static void positionLabel(Label label, Circle circle,Arc arc) {
      
	   //Scheitelpunkt berechnen
       double centerX = arc.getCenterX();
       double centerY = arc.getCenterY();
       double radiusX = arc.getRadiusX();
       double radiusY = arc.getRadiusY();
       double startAngle = Math.toRadians(arc.getStartAngle());
       double length = Math.toRadians(arc.getLength());
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
      label.setLayoutX(labelCenterX - 25.0D);
      label.setLayoutY(labelCenterY - 15.0D);
   }
   
   //Hilfsklasse zur Bündelung der Berechnung der Start und Endpunkte einer Linie, inkl. Anstieg
   private static class EdgePoints {
      final double startX;
      final double startY;
      final double endX;
      final double endY;
      final double m;

      EdgePoints(double startX, double startY, double endX, double endY, double m) {
         this.startX = startX;
         this.startY = startY;
         this.endX = endX;
         this.endY = endY;
         this.m = m;
      }
   }

   //Klasse die Line erweiter um eine Quadkurve, erleichter die Berechung von Start und Endpunkten durch Polymorphismus
   private static class CustomLine extends Line {
      
	   //gekrümmte Kurve
	   private Path curvedPath = new Path();
	   
	   //Psotion des Textfeldes
      private double textfieldX;
      private double textfieldY;
      
      //Psotion des Scheitelpunktes
      private double scheitelpunkt_x;
      private double scheitelpunkt_y;
      
      //Punkt der zum Berechnen des Arrows genutzt wird
      private double arrow_x;
      private double arrow_y;

      
      CustomLine(double startX, double startY, double endX, double endY, double m) {
         //
    	  super(startX, startY, endX, endY);
         //Erzeugt Quadratische Kurve
         this.updateCurve(startX, startY, endX, endY, m);
      }
      
      //MEthode die Quadratische Kruve aus den Start und Endpunkt einer Geraden bestimmt, der Scheitelpunkt sklaiert mit dem Anstieg aus optischen Gründen
      //da neue Kurve erzeugt wird auch geignet für das Updaten wenn Kreise via Drag and Drop verschoben wurden
      private void updateCurve(double startX, double startY, double endX, double endY, double m) {
         //Idee gedanklich Mittelpunkt um eine Faktor nach außen versetzten und diesen als dreitten Punkt der Quadratischen Kruve benutzen
    	  double midX = (startX + endX) / 2.0;
         double midY = (startY + endY) / 2.0;
         
         //Normale zur Linie berechnen
         double deltaX = endX - startX;
         double deltaY = endY - startY;
         double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
         
         double perpX = -deltaY / length;
         double perpY = deltaX / length;
         
         //Stützpunkt berechnen
         double controlX = midX + perpX * 0.8D * Math.abs(deltaY);
         double controlY = midY + perpY * 0.8D * Math.abs(deltaX);
         
         //Scheitelpunkt der Kurve bei 0.5 Berechnen
         this.scheitelpunkt_x = 0.25D * startX + 0.5D * controlX + 0.25D * endX;
         this.scheitelpunkt_y = 0.25D * startY + 0.5D * controlY + 0.25D * endY;
         
         //Psoition des Testfeldes minimal versetzten
         this.textfieldX = this.scheitelpunkt_x + perpX * 10.0D;
         this.textfieldY = this.scheitelpunkt_y + perpY * 10.0D;
         
         //Punkt kurz vor Ende zur Berechnung der Pfeilausrichtung
         double t = 0.99D;
         this.arrow_x = (1.0D - t) * (1.0D - t) * startX + 2.0D * (1.0D - t) * t * controlX + t * t * endX;
         this.arrow_y = (1.0D - t) * (1.0D - t) * startY + 2.0D * (1.0D - t) * t * controlY + t * t * endY;
         
         //Kurve erstellen
         this.curvedPath.getElements().clear();
         MoveTo moveTo = new MoveTo(startX, startY);
         QuadCurveTo quadCurveTo = new QuadCurveTo(controlX, controlY, endX, endY);
         this.curvedPath.getElements().addAll(moveTo, quadCurveTo);
         this.curvedPath.setStroke(Color.BLACK);
         this.curvedPath.setFill(Color.TRANSPARENT);
         
         //Sichtbarkeit setzten
         this.curvedPath.setVisible(true);
      }
      
      //Line und Kurve zusammen updaten
      public void updateLineAndCurve(double startX, double startY, double endX, double endY, double m) {
         this.setStartX(startX);
         this.setStartY(startY);
         this.setEndX(endX);
         this.setEndY(endY);
         this.updateCurve(startX, startY, endX, endY, m);
      }
      
      //Sichtbarkeit der Kurve verändern
      public void showCurved() {
         this.setVisible(false);
         this.curvedPath.setVisible(true);
      }

    //Sichtbarkeit der Line verändern
      public void showStraight() {
         this.setVisible(true);
         this.curvedPath.setVisible(false);
      }

      public Path getCurvedPath() {
         return this.curvedPath;
      }

      public double getX_textfield_point_x() {
         return this.textfieldX;
      }

      public double getY_textfield_point_y() {
         return this.textfieldY;
      }

      public double getArrow_x() {
         return this.arrow_x;
      }

      public double getArrow_y() {
         return this.arrow_y;
      }
   }
}