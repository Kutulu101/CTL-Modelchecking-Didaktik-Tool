package GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import CTL_Backend.Zustand;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Circle_Group_Builder {
	
	private int circle_counter;
	private List<Group> list_of_circle_groups = new ArrayList<>();
	Application app;
	

	Circle_Group_Builder(Application app){
		this.circle_counter=0;
		this.app = app;
		
	}
	
	public List<Group> getList_of_circle_groups() {
		return list_of_circle_groups;
	}
	
    // Öffentliche Methode zum Leeren der Liste, für Neustart
    public void clearCircleGroups() {
        list_of_circle_groups.clear();
        circle_counter = 0; 
    }
	
	//Methode die Group aus Kreis und Beschriftung erzeugt
    public Group create_circle_with_text(boolean draw_relations) {
    	
    	//Kreis
        Circle circle = new Circle(50, Color.BLUE); // Erstelle einen blauen Kreis mit Radius 50
        //Beschriftung
        this.circle_counter +=1;
        Text text = new Text("z"+circle_counter);
        // Postionierung mit Offset
        Random random = new Random();
        double offsetX = random.nextDouble() * 40; // Zufälliger Versatz bis 40 in x-Richtung
        double offsetY = random.nextDouble() * 40; // Zufälliger Versatz bis 40 in y-Richtung

        circle.setCenterX(100 + offsetX); // Setze die Position mit Versatz
        circle.setCenterY(100 + offsetY); // Setze die Position mit Versatz
        //Beschriftung positionieren
        text.setX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2);
        text.setY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4);
        //Gruppieren
        Group circleWithText = new Group();
        circleWithText.getChildren().addAll(circle, text);
        //registriert die drag and drop Events
        enableDragAndDrop(circleWithText,draw_relations);
        
        //CSS-Tag anfügen
        circleWithText.getStyleClass().add("circle_with_text");
        //in Liste einfügen
        list_of_circle_groups.add(circleWithText);
        return circleWithText;
    }
	
	
	// Methode zum Aktivieren von Drag-and-Drop für einen Kreis mit Text
    private void enableDragAndDrop(Group circlewithtext,boolean draw_relations) {
    	
    	Circle circle;
    	Text text;
    	circle = (Circle)circlewithtext.getChildren().get(0);
    	text = (Text)circlewithtext.getChildren().get(1);
    	
        circle.setOnMousePressed(event -> {
            circle.setFill(Color.RED); // Ändere die Farbe während des Ziehens
        });

        circle.setOnMouseDragged(event -> {
            circle.setCenterX(event.getX());
            circle.setCenterY(event.getY());
            text.setX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2);
            text.setY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4);
        });

        circle.setOnMouseReleased(event -> {
        	if (draw_relations == false) {
        		circle.setFill(Color.BLUE); // Setze die Farbe zurück, wenn das Ziehen beendet ist
        	}
        	else circle.setFill(Color.YELLOW);
        });
    }
    
    public void färbeKreiseNachZustand(Set<Zustand> lösungsmenge) {
        for (Group group : this.list_of_circle_groups) {
            // Prüfen, ob die Gruppe die erwarteten Kinder enthält (Circle an Index 0, Text an Index 1)
            if (group.getChildren().size() >= 2) {
                Node kreisNode = group.getChildren().get(0);  // Kreis ist an Index 0
                Node textNode = group.getChildren().get(1);   // Text ist an Index 1

                if (kreisNode instanceof Circle && textNode instanceof Text) {
                    Circle kreis = (Circle) kreisNode;
                    Text text = (Text) textNode;

                    // Text des Labels
                    String textName = text.getText();

                    // Prüfen, ob der Textname in der Lösungsmenge vorkommt
                    boolean gefunden = lösungsmenge.stream()
                            .map(Zustand::getName)
                            .anyMatch(zustandsName -> zustandsName.equals(textName));

                    // Färben des Kreises basierend auf der Bedingung
                    if (gefunden) {
                        kreis.setFill(Color.GREEN);  // Falls der Zustand gefunden wird
                    } else {
                        kreis.setFill(Color.RED);    // Falls der Zustand nicht in der Lösungsmenge ist
                    }
                }
            }
        }
    }
    
    
    // Methode, um rekursiv alle Kreise zu färben
    public void colorAllCircles(Node node) {
        if (node instanceof Circle) {
            // Wenn es ein Kreis ist, färbe ihn blau
            ((Circle) node).setFill(Color.BLUE);
        } else if (node instanceof Pane) {
            // Wenn es ein Pane ist, durchlaufe die Kinder
            for (Node child : ((Pane) node).getChildren()) {
                colorAllCircles(child);  // rekursive Methode aufrufen
            }
        } else if (node instanceof Group) {
            // Wenn es eine Gruppe ist, durchlaufe die Kinder
            for (Node child : ((Group) node).getChildren()) {
                colorAllCircles(child);  // rekursive Methode aufrufen
            }
        }
    }
}
