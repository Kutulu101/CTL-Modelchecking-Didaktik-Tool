package GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import CTL_Backend.Zustand;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	private Arrow_Builder  arrow_builder;
	

	Circle_Group_Builder(Arrow_Builder  arrow_builder){
		this.circle_counter=0;
		this.arrow_builder= arrow_builder;
		
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
    public Group create_circle_with_text(BooleanProperty draw_relations) {
    	
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
    private void enableDragAndDrop(Group circleWithText, BooleanProperty draw_relations) {
        Circle circle = (Circle) circleWithText.getChildren().get(0);
        Text text = (Text) circleWithText.getChildren().get(1);

        // Variablen zur Speicherung der Offset-Werte
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];

        circle.setOnMousePressed(event -> {
            circle.setFill(Color.RED); // Ändere die Farbe während des Ziehens
            // Berechne den Offset zwischen der Maus und der Mitte des Kreises
            offsetX[0] = event.getSceneX() - circle.getCenterX();
            offsetY[0] = event.getSceneY() - circle.getCenterY();
        });

        circle.setOnMouseDragged(event -> {
            if (!draw_relations.get()) {
                // Setze die neue Position des Kreises basierend auf dem Offset
                circle.setCenterX(event.getSceneX() - offsetX[0]);
                circle.setCenterY(event.getSceneY() - offsetY[0]);
                text.setX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2);
                text.setY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4);
                arrow_builder.updateArrows(circle);
            }
        });

        circle.setOnMouseReleased(event -> {
            if (!draw_relations.get()) {
                circle.setFill(Color.BLUE); // Setze die Farbe zurück, wenn das Ziehen beendet ist
            } else {
                circle.setFill(Color.YELLOW);
            }
        });
    }
    
    public void färbeKreiseNachZustand(Set<Zustand> lösungsmenge, boolean is_colored) {
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
                    if (gefunden && is_colored) {
                        kreis.setFill(Color.GREEN);  // Falls der Zustand gefunden wird
                    } else if (is_colored) {
                        kreis.setFill(Color.RED);    // Falls der Zustand nicht in der Lösungsmenge ist
                    }
                    else{kreis.setFill(Color.BLUE);}//Blau wenn nciht gefärbt werdne soll
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
