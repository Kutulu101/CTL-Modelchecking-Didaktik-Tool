    package GUI;

import CTL_Backend.Zustand;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
//Klasse zum Erzeugen und Verwalten der Beschriftet Kreise die Zustände repräsentieren
public class Circle_Group_Builder {
	
	//Liste zum Verwalten der Kreise
   private int circle_counter = 0;
   private List<Group> list_of_circle_groups = new ArrayList();
   
   //hält ArrowBuilder um bei Drag and Drop udate der Linien zu triggern
   private Arrow_Builder arrow_builder;

   Circle_Group_Builder(Arrow_Builder arrow_builder) {
      this.arrow_builder = arrow_builder;
   }

   public List<Group> getList_of_circle_groups() {
      return this.list_of_circle_groups;
   }
   
   //Löschte alle Kreise aus der Liste
   public void clearCircleGroups() {
      this.list_of_circle_groups.clear();
      this.circle_counter = 0;
   }

	// Methode, die eine neue Gruppe erstellt, bestehend aus einem Kreis und einem zugehörigen Text.
	public Group create_circle_with_text(BooleanProperty draw_relations) {
		
	    // Erstellen eines neuen Kreises mit Radius 50 und blauer Füllfarbe
	    Circle circle = new Circle(50.0D, Color.BLUE);
	
	    // Erhöhen des Zählers für Kreise und Generieren eines eindeutigen Textes für diesen Kreis
	    ++this.circle_counter;
	    Text text = new Text("z" + this.circle_counter);
	
	    // Erstellen eines zufälligen Offsets für die Positionierung des Kreises
	    Random random = new Random();
	    double offsetX = random.nextDouble() * 60.0D;
	    double offsetY = random.nextDouble() * 60.0D;
	
	    // Setzen der Position des Kreises mit dem zufälligen Offset
	    circle.setCenterX(100.0D + offsetX);
	    circle.setCenterY(100.0D + offsetY);
	
	    // Zentrieren des Textes relativ zur Position des Kreises
	    text.setX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2.0D);
	    text.setY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4.0D);
	
	    // Erstellen einer neuen Gruppe, die den Kreis und den Text enthält
	    Group circleWithText = new Group();
	    circleWithText.getChildren().addAll(circle, text);
	
	    // Aktivieren der Drag-and-Drop-Funktionalität für die Gruppe
	    this.enableDragAndDrop(circleWithText, draw_relations);
	
	    // Hinzufügen einer CSS-Klasse zur Gruppe für Styling-Zwecke
	    circleWithText.getStyleClass().add("circle_with_text");
	
	    // Hinzufügen der Gruppe zur Liste aller Kreisgruppen
	    this.list_of_circle_groups.add(circleWithText);
	
	    // Zurückgeben der erstellten Gruppe
	    return circleWithText;
	}


	// Methode, die Drag-and-Drop-Funktionalität für eine Gruppe (Kreis mit Text) aktiviert
	// und optional das Zeichnen von Relationen unterstützt.
	private void enableDragAndDrop(Group circleWithText, BooleanProperty draw_relations) {
	    // Hole den Kreis (Circle) und den Text (Text) aus der Gruppe
	    Circle circle = (Circle) circleWithText.getChildren().get(0);
	    Text text = (Text) circleWithText.getChildren().get(1);
	
	    // Arrays zum Speichern des Offsets für die Drag-Bewegung
	    double[] offsetX = new double[1];
	    double[] offsetY = new double[1];
	
	    // Event-Handler für das Drücken der Maus (MousePressed)
	    circle.setOnMousePressed((event) -> {
	        // Färbt den Kreis rot, um zu zeigen, dass er bewegt wird
	        circle.setFill(Color.RED);
	
	        // Berechnet und speichert den Offset zwischen dem Mausklick und der aktuellen Kreisposition
	        offsetX[0] = event.getSceneX() - circle.getCenterX();
	        offsetY[0] = event.getSceneY() - circle.getCenterY();
	    });
	
	    // Event-Handler für das Ziehen der Maus (MouseDragged)
	    circle.setOnMouseDragged((event) -> {
	        // Überprüft, ob Relationen nicht gezeichnet werden sollen
	        if (!draw_relations.get()) {
	            // Aktualisiert die Position des Kreises basierend auf der Mausbewegung
	            circle.setCenterX(event.getSceneX() - offsetX[0]);
	            circle.setCenterY(event.getSceneY() - offsetY[0]);
	
	            // Positioniert den Text entsprechend der neuen Kreisposition
	            text.setX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2.0D);
	            text.setY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4.0D);
	
	            // Aktualisiert die Pfeile (oder Relationen), die mit diesem Kreis verbunden sind
	            this.arrow_builder.updateArrows(circle);
	        }
	    });

    // Event-Handler für das Loslassen der Maus (MouseReleased)
    circle.setOnMouseReleased((event) -> {
        // Wenn keine Relationen gezeichnet werden sollen, färbe den Kreis blau
        if (!draw_relations.get()) {
            circle.setFill(Color.BLUE);
        } 
        // Wenn Relationen gezeichnet werden, färbe den Kreis gelb
        else {
            circle.setFill(Color.YELLOW);
        }
    });
}


	// Methode, die Kreise basierend auf einem Zustand einfärbt.
	// Die Farbe hängt davon ab, ob der Kreis in der Lösungsmenge enthalten ist und ob "is_colored" wahr ist.
	public void färbeKreiseNachZustand(Set<Zustand> lösungsmenge, boolean is_colored) {

	    // Schleife durch jede Gruppe in der Liste
	    for(Group group:this.list_of_circle_groups) {
	    	
	        // Überprüfen, ob die Gruppe mindestens zwei Kinder hat (Kreis und Text)
	        if (group.getChildren().size() < 2) {
	            continue; // Überspringe Gruppen mit weniger als zwei Kindern
	        }
	
	        // Hole die ersten beiden Kinder: Kreis und Text
	        Node kreisNode = group.getChildren().get(0);
	        Node textNode = group.getChildren().get(1);
	
	        // Überprüfen, ob das erste Kind ein Kreis (Circle) ist
	        if (!(kreisNode instanceof Circle)) {
	            continue; // Überspringe, wenn kein Kreis
	        }
	
	        // Überprüfen, ob das zweite Kind ein Text (Text) ist
	        if (!(textNode instanceof Text)) {
	            continue; // Überspringe, wenn kein Text
	        }
	
	        // Cast der Knoten zu Circle und Text
	        Circle kreis = (Circle) kreisNode;
	        Text text = (Text) textNode;
	
	        // Hole den Namen aus dem Text-Node
	        String textName = text.getText();
	
	        // Überprüfen, ob der Name des Zustands in der Lösungsmenge enthalten ist
	        boolean gefunden = lösungsmenge.stream()
	            .map(Zustand::getName) // Hole die Namen aller Zustände
	            .anyMatch((zustandsName) -> zustandsName.equals(textName)); // Prüfen, ob der Name übereinstimmt
	
	        // Basierend auf der Bedingung den Kreis einfärben
	        if (gefunden && is_colored) {
	            // Wenn gefunden und "is_colored" wahr, färbe grün
	            kreis.setFill(Color.GREEN);
	        } else if (is_colored) {
	            // Wenn nicht gefunden, aber "is_colored" wahr, färbe rot
	            kreis.setFill(Color.RED);
	        } else {
	            // Wenn "is_colored" falsch, färbe blau
	            kreis.setFill(Color.BLUE);
	        }
	    }
	}


   	// Diese Methode färbt alle Kreise (Circle) in einer Szene rekursiv blau.
	public void colorAllCirclesBlue(Node node) {
	    // Überprüfen, ob der aktuelle Knoten ein Kreis ist
	    if (node instanceof Circle) {
	        // Wenn ja, setze die Füllfarbe des Kreises auf Blau
	        ((Circle) node).setFill(Color.BLUE);
	    } else if (node instanceof Pane) {
	        // Wenn der aktuelle Knoten ein Pane ist, durchlaufe alle Kinder
	        for (Node child : ((Pane) node).getChildren()) {
	            // Rekursiver Aufruf für jedes Kind
	            this.colorAllCirclesBlue(child);
	        }
	    } else if (node instanceof Group) {
	        // Wenn der aktuelle Knoten eine Gruppe ist, durchlaufe alle Kinder
	        for (Node child : ((Group) node).getChildren()) {
	            // Rekursiver Aufruf für jedes Kind
	            this.colorAllCirclesBlue(child);
	        }
	    }
	}
	
   	// Diese Methode färbt alle Kreise (Circle) in einer Szene rekursiv gelb.
	public void colorAllCirclesYellow(Node node) {
	    // Überprüfen, ob der aktuelle Knoten ein Kreis ist
	    if (node instanceof Circle) {
	        // Wenn ja, setze die Füllfarbe des Kreises auf Blau
	        ((Circle) node).setFill(Color.YELLOW);
	    } else if (node instanceof Pane) {
	        // Wenn der aktuelle Knoten ein Pane ist, durchlaufe alle Kinder
	        for (Node child : ((Pane) node).getChildren()) {
	            // Rekursiver Aufruf für jedes Kind
	            this.colorAllCirclesYellow(child);
	        }
	    } else if (node instanceof Group) {
	        // Wenn der aktuelle Knoten eine Gruppe ist, durchlaufe alle Kinder
	        for (Node child : ((Group) node).getChildren()) {
	            // Rekursiver Aufruf für jedes Kind
	            this.colorAllCirclesYellow(child);
	        }
	    }
	}
	
	public void bereite_berechnung_vor(Parent parent) {
		this.schliesseAlleEingabefelder(parent);
		this.blende_unvernetze_kreise_aus();
	}
	


	// Methode, die alle Eingabefelder (TextField) schließt, um zu verhindern, 
	// dass Berechnungen während der Eingabe gestartet werden können
	private void schliesseAlleEingabefelder(Parent parent) {
	    // Schleife durch alle unveränderbaren Kinder des übergebenen Elternknotens
	    for (Object node : parent.getChildrenUnmodifiable()) {
	        // Überprüfen, ob das aktuelle Kind ein TextField ist
	        if (node instanceof TextField) {
	            // Casten des Knotens zu TextField
	            TextField textField = (TextField) node;
	            // Überprüfen, ob ein OnAction-Handler für das TextField definiert ist
	            if (textField.getOnAction() != null) {
	                // Den definierten OnAction-Handler auslösen, um das Feld zu "schließen"
	                textField.getOnAction().handle(new ActionEvent());
	            }
	        }
	        // Falls das Kind selbst ein Parent ist (z. B. ein Container), rekursiver Aufruf
	        else if (node instanceof Parent) {
	            this.schliesseAlleEingabefelder((Parent) node);
	        }
	    }
	}
	
	private void blende_unvernetze_kreise_aus() {
		
	    for (Group circleGroup : list_of_circle_groups) {
	        // Der Kreis ist immer das erste Kind in der Gruppe
	        if (circleGroup.getChildren().isEmpty() || !(circleGroup.getChildren().get(0) instanceof Circle)) {
	            continue;
	        }
	        Circle circle = (Circle) circleGroup.getChildren().get(0);
	        
	        boolean is_in_relation = false;
	        for (Relation relation : this.arrow_builder.getList_of_relations()) {
	            if (relation.getFirstCircle().equals(circle) || relation.getSecondCircle().equals(circle)) {
	                is_in_relation = true;
	                break;
	            }
	        }
	        
	        // Falls kein Bezug existiert, die Sichtbarkeit auf false setzen
	        circleGroup.setVisible(is_in_relation);
	    }
	}

}