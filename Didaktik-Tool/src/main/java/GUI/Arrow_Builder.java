   package GUI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javafx.application.Platform;
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
   private HashSet<line_to_represent_relation> list_of_lines = new HashSet();
   
   public Arrow_Builder() {
       // Relation-Objekte überwachen
       CustomLine.addCreationListener(customline -> this.manage_visibility());
   }

   //Methode zum Vergleichen von Double Werten
   private static boolean areDoublesEqual(double a, double b) {
      return Math.abs(a - b) < 0.1;
   }

   public HashSet<Relation> getList_of_relations() {
	   syncRelations();
      return this.list_of_relations;
   }
   
   //Löscht alle bsiher erzeugten Relationen
   public void clearRelations() {
	   this.list_of_lines.clear();
      this.list_of_relations.clear();
   }
   
	   
   public void drawArrow(Pane drawingpane, Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, List<String> vorauswahl_transitionen) {
      
	   //Offset versetzt Pfeil wenn der neue Pfeile mit einem existierenden Überlappen würden
	   int offset = this.off_set_for_existing_relations(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel);
      
	   //Kreismittelpunkte extrhaieren
	  double startX = firstCircle.getCenterX();
      double startY = firstCircle.getCenterY();
      double endX = secondCircle.getCenterX();
      double endY = secondCircle.getCenterY();
            
      //Bei zwei verschiedenen Kreisen wird eine CustomLine erzeugt
      if (startX != endX && startY != endY) {
    	  
         //Erzeugt eine CustomLine, Java Fx Line in Kombination mit einer Quadratischen Kurve
         CustomLine customLine = new CustomLine(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel,offset,vorauswahl_transitionen,shouldCurve(firstCircle, secondCircle));
         
         //füge Relation hinzu
         this.list_of_relations.addAll(customLine.getRelations());
         
         //füge customline hinzu
         this.list_of_lines.add(customLine);
         
         
      } 
      else {//Wenn zweimal der gleiche Kreis angklickt wurde Arc erzeugen 

         CustomArc customarc = new CustomArc(firstCircle,firstCircleLabel, offset,vorauswahl_transitionen);
          
         //füge customarc hinzu
         this.list_of_lines.add(customarc);
         
       //füge Relation hinzu
         this.list_of_relations.addAll(customarc.getRelations());
         
      }
   }
   
   //Berechnet den notwenigen Versatz damit sich keine Linien die gleiche Kreise verbiden überlappen
   private int off_set_for_existing_relations(Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel) {
      
	   syncRelations();
	   
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
      EdgePoints points = new EdgePoints(firstCircle.getCenterX(), firstCircle.getCenterY(), secondCircle.getCenterX(), secondCircle.getCenterY(), firstCircle.getRadius(), secondCircle.getRadius(), offset);
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
	   
	   syncRelations();
	   
	   
	   //Sucht alle Relationen in denen der Kreis vorkommt
	    for (Relation relation : this.list_of_relations) {
	        if (!relation.getFirstCircle().equals(movedCircle) && !relation.getSecondCircle().equals(movedCircle)) {
	            continue;
	        }
	        if (relation.getLine() instanceof CustomLine) {
	            ((CustomLine) relation.getLine()).updateLines(relation);
	        } else {
	        	((CustomArc) relation.getLine()).updateArc(relation);
	        }
	    }
	}
   
   //wird von customline über eine  Listner aufgerufen nach dem eine neue Relation erstellt wurde, Zeitverzögert wegen der Eingabebox
   private void manage_visibility() {
	   
	   syncRelations();
	    // Prüft für jede Relation, ob es andere Relationen mit denselben Kreisen gibt
	    for (Relation relation : this.list_of_relations) {
	        Circle firstCircle = relation.getFirstCircle();
	        Circle secondCircle = relation.getSecondCircle();

	        // Falls es andere Relationen gibt, die dieselben Kreise enthalten, showCurved() aufrufen, aber nur wenn eine Relation nciht mit sich selsbt verglichen wird
	        for (Relation otherRelation : this.list_of_relations) {
	            if (relation != otherRelation &&containsCircles(otherRelation, firstCircle, secondCircle)&& otherRelation.getLine() instanceof CustomLine) {
	                ((CustomLine) otherRelation.getLine()).showCurved();
	            }
	        }
	    }
   	}
   
   private void syncRelations() {
	    // Erneutes Hinzufügen wegen evtl. Synchro-Probleme
	    for (line_to_represent_relation line : list_of_lines) {
	        this.list_of_relations.addAll(line.getRelations());
	    }
	}
   
   private boolean shouldCurve(Circle firstCircle, Circle secondCircle) {
	    // Synchronisiere die Relationen
	    syncRelations();

	    // Prüfe, ob es andere Relationen mit denselben Kreisen gibt
	    for (Relation relation : this.list_of_relations) {
	        // Wenn eine andere Relation die gleichen Kreise enthält, gib true zurück
	        if (containsCircles(relation, firstCircle, secondCircle)) {
	            return true; // Linie sollte gekrümmt sein
	        }
	    }

	    // Wenn keine andere Relation mit denselben Kreisen existiert, gib false zurück
	    return false; // Linie bleibt gerade
	}
}	
   




