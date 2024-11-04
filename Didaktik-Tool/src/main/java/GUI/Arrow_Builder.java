package GUI;

import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.List;

public class Arrow_Builder {

    private HashSet<Relation> list_of_relations = new HashSet<>();
    private double offsetX;
    private double offsetY;
    private static final double EPSILON = 0.1;


    private boolean areDoublesEqual(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    public HashSet<Relation> getList_of_relations() {
        return list_of_relations;
    }

    public void clearRelations() {
        list_of_relations.clear();
    }

    public void add_relation(Relation relation) {
        this.list_of_relations.add(relation);
    }

    public void drawArrow(Pane drawingpane, Circle firstCircle, Circle secondCircle, 
                          Text firstCircleLabel, Text secondCircleLabel,List<String> vorauswahl_transitionen) {

        int offset = off_set_for_existing_relations(firstCircle, secondCircle);

        double startX = firstCircle.getCenterX();
        double startY = firstCircle.getCenterY();
        double endX = secondCircle.getCenterX();
        double endY = secondCircle.getCenterY();

        Shape line;
        Polygon arrowHead;

        if (startX != endX && startY != endY) {
            EdgePoints points = calculateEdgePoints(startX, startY, endX, endY, 
                                                    firstCircle.getRadius(), 
                                                    secondCircle.getRadius(), offset);

            line = new Line(points.startX, points.startY, points.endX, points.endY);
            arrowHead = createArrowHead(points.startX, points.startY, points.endX, points.endY);

            drawingpane.getChildren().addAll(line, arrowHead);
            showTextInputField((points.startX + points.endX) / 2, (points.startY + points.endY) / 2,
                               0, line, arrowHead, firstCircle, secondCircle, 
                               firstCircleLabel, secondCircleLabel,vorauswahl_transitionen);
        } else {
            line = zeichneTeilkreisLinie(startX, startY, firstCircle.getRadius(), 4 * offset, 210);
            arrowHead = createArcArrowHead((Arc) line, offset);
            drawingpane.getChildren().addAll(line, arrowHead);

            showTextInputField(((Arc) line).getCenterX(), ((Arc) line).getCenterY(),
                               50, line, arrowHead, firstCircle, secondCircle, 
                               firstCircleLabel, secondCircleLabel,vorauswahl_transitionen);
        }
    }

    private EdgePoints calculateEdgePoints(double startX, double startY, double endX, double endY, 
                                           double radius1, double radius2, int offset) {

        double dx = endX - startX;
        double dy = endY - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        dx /= distance;
        dy /= distance;

        double nx = -dy;
        double ny = dx;

        double startEdgeX = startX + radius1 * dx + offset * nx;
        double startEdgeY = startY + radius1 * dy + offset * ny;
        double endEdgeX = endX - radius2 * dx + offset * nx;
        double endEdgeY = endY - radius2 * dy + offset * ny;

        return new EdgePoints(startEdgeX, startEdgeY, endEdgeX, endEdgeY);
    }

    private Arc zeichneTeilkreisLinie(double centerX, double centerY, double radius, 
                                      double startAngle_at_large_circle, double arcAngle) {
        double arcRadius = 28;
        double arcCenterX = centerX + radius * Math.cos(Math.toRadians(startAngle_at_large_circle));
        double arcCenterY = centerY + radius * Math.sin(Math.toRadians(startAngle_at_large_circle));
        double startAngle = -startAngle_at_large_circle - arcAngle / 2;

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

        return arc;
    }

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

    private Polygon createArcArrowHead(Arc arc, int offset) {
    	
        // Berechnen des Endpunkts des Bogens
        double endAngle = Math.toRadians(4 * offset + 105); // 105°-Rotation entlang des Bogens
        double endXarc = (arc).getCenterX() + arc.getRadiusX() * Math.cos(endAngle);
        double endYarc = arc.getCenterY() + arc.getRadiusY() * Math.sin(endAngle);
    	
    	// Pfeilspitze erstellen und 180° um die Spitze drehen
        Polygon arrowHead = new Polygon();
        double arrowSize = 15;
        double arrowAngle = Math.toRadians(30); // Öffnungswinkel der Pfeilspitze

        // Winkel anpassen: um 180° drehen und um 105° entlang des Bogens verschieben
        double finalAngle = endAngle + Math.PI/2-0.3; // ca. 90°-Drehung = π Radianten

        arrowHead.getPoints().addAll(
            endXarc, endYarc, // Spitze des Pfeils
            endXarc - arrowSize * Math.cos(finalAngle - arrowAngle), 
            endYarc - arrowSize * Math.sin(finalAngle - arrowAngle), // linke Seite des Pfeils
            endXarc - arrowSize * Math.cos(finalAngle + arrowAngle), 
            endYarc - arrowSize * Math.sin(finalAngle + arrowAngle)  // rechte Seite des Pfeils
        );

        arrowHead.setFill(Color.BLACK);

        return arrowHead;
    }

    private void showTextInputField(double x, double y, int offset, Shape line, Polygon arrowHead,Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, List<String> vorauswahl_transitionen) {

Pane parentPane = (Pane) firstCircle.getParent().getParent();
Label returnLabel = new Label();
returnLabel.setLayoutX(x);
returnLabel.setLayoutY(y);
returnLabel.setMinWidth(50 + offset);
returnLabel.setAlignment(Pos.CENTER);
returnLabel.setStyle("-fx-font-size: 18px;");

// Überprüfen, ob die Liste nicht leer ist
if (vorauswahl_transitionen != null && !vorauswahl_transitionen.isEmpty()) {
// Erstellen und konfigurieren der ComboBox
ComboBox<String> comboBox = new ComboBox<>();
comboBox.getItems().addAll(vorauswahl_transitionen);
comboBox.setLayoutX(x);
comboBox.setLayoutY(y);
comboBox.setPromptText("Wählen Sie eine Option...");

parentPane.getChildren().add(comboBox);

// Action-Event für die ComboBox
comboBox.setOnAction(event -> {
String selectedValue = comboBox.getValue();
returnLabel.setText(selectedValue);
parentPane.getChildren().add(returnLabel);
parentPane.getChildren().remove(comboBox);

// Label Drag-and-Drop Verhalten
returnLabel.setOnMousePressed(event2 -> {
offsetX = event2.getX();
offsetY = event2.getY();
});

returnLabel.setOnMouseDragged(event2 -> {
returnLabel.setLayoutX(event2.getSceneX() - offsetX);
returnLabel.setLayoutY(event2.getSceneY() - offsetY);
});

// Verarbeitung des ausgewählten Wertes
add_relation(new Relation(firstCircle, secondCircle, firstCircleLabel, 
                  secondCircleLabel, line, arrowHead, returnLabel, selectedValue));
});
} else {
// Wenn die Liste leer ist, ein TextField erstellen
TextField inputField = new TextField();
inputField.setLayoutX(x);
inputField.setLayoutY(y);
inputField.setPromptText("Zeichen Komma getrennt eingeben...");

parentPane.getChildren().add(inputField);

inputField.setOnAction(event -> {
String userInput = inputField.getText();
returnLabel.setText(userInput);
parentPane.getChildren().add(returnLabel);
parentPane.getChildren().remove(inputField);

// Label Drag-and-Drop Verhalten
returnLabel.setOnMousePressed(event2 -> {
offsetX = event2.getX();
offsetY = event2.getY();
});

returnLabel.setOnMouseDragged(event2 -> {
returnLabel.setLayoutX(event2.getSceneX() - offsetX);
returnLabel.setLayoutY(event2.getSceneY() - offsetY);
});

// Verarbeitung des Texteingangs
String[] labels = returnLabel.getText().split(",");
for (String label : labels) {
add_relation(new Relation(firstCircle, secondCircle, firstCircleLabel, 
                      secondCircleLabel, line, arrowHead, returnLabel, label));
}
});
}
}

    private int off_set_for_existing_relations(Circle firstCircle, Circle secondCircle) {
        int offset = 0;
        int maxDepth = 20;

        while (maxDepth-- > 0) {
            boolean conflictFound = false;
            for (Relation relation : list_of_relations) {
                if (containsCircles(relation, firstCircle, secondCircle) &&
                    isConflict(relation.getLine(), firstCircle, secondCircle, offset)) {
                    conflictFound = true;
                    offset = relation.get_and_increase_Offset();
                    break;
                }
            }
            if (!conflictFound) return offset;
        }
        return offset;
    }

    private boolean containsCircles(Relation relation, Circle firstCircle, Circle secondCircle) {
        return (relation.getFirstCircle().equals(firstCircle) && 
                relation.getSecondCircle().equals(secondCircle)) ||
               (relation.getFirstCircle().equals(secondCircle) && 
                relation.getSecondCircle().equals(firstCircle));
    }

    private boolean isConflict(Shape shape, Circle firstCircle, Circle secondCircle, int offset) {
        if (shape instanceof Line) {
            return isLineConflict((Line) shape, firstCircle, secondCircle, offset);
        } else if (shape instanceof Arc) {
            return isArcConflict((Arc) shape, offset);
        }
        return false;
    }

    private boolean isLineConflict(Line line, Circle firstCircle, Circle secondCircle, int offset) {
        EdgePoints points = calculateEdgePoints(
                firstCircle.getCenterX(), firstCircle.getCenterY(), 
                secondCircle.getCenterX(), secondCircle.getCenterY(), 
                firstCircle.getRadius(), secondCircle.getRadius(), offset
        );

        return (areDoublesEqual(line.getStartX(), points.startX) && areDoublesEqual(line.getStartY(), points.startY) &&
                areDoublesEqual(line.getEndX(), points.endX) && areDoublesEqual(line.getEndY(), points.endY)) ||
               (areDoublesEqual(line.getStartX(), points.endX) && areDoublesEqual(line.getStartY(), points.endY) &&
                areDoublesEqual(line.getEndX(), points.startX) && areDoublesEqual(line.getEndY(), points.startY));
    }


    private boolean isArcConflict(Arc arc, int offset) {

        // Berechne die Arc-Eigenschaften mit dem aktuellen Offset
        double arcRadius = 28;
        double startAngleAtLargeCircle = 4 * offset;
        double arcAngle = 210;

        double centerX = arc.getCenterX();
        double centerY = arc.getCenterY();

        // Berechne den Mittelpunkt des Arcs
        double arcCenterX = centerX + arcRadius * Math.cos(Math.toRadians(startAngleAtLargeCircle));
        double arcCenterY = centerY + arcRadius * Math.sin(Math.toRadians(startAngleAtLargeCircle));

        // Berechne den Startwinkel des Arcs
        double startAngle = -startAngleAtLargeCircle - arcAngle / 2;
        
        // Prüfe, ob die berechneten Werte mit denen des existierenden Arcs übereinstimmen
        return areDoublesEqual(arc.getStartAngle(), startAngle);
    }
    
    public void updateArrows(Circle movedCircle) {
        // Über alle Relationen iterieren
        for (Relation relation : list_of_relations) {
            // Prüfen, ob der verschobene Kreis Teil der Relation ist
            if (relation.getFirstCircle().equals(movedCircle) || relation.getSecondCircle().equals(movedCircle)) {
	            	
	            	// wenn es schon eine relation gibt, offset setzen und in der Relation speichern
	            	int offset = relation.getOffset_counter();
	            	
	            	// Neue Positionen der Kreise
	                double startX = relation.getFirstCircle().getCenterX();
	                double startY = relation.getFirstCircle().getCenterY();
	                double endX = relation.getSecondCircle().getCenterX();
	                double endY = relation.getSecondCircle().getCenterY();
	                
	            if (endX != startX && startY != endY) {
	                EdgePoints points = calculateEdgePoints(startX, startY, endX, endY, 
                            relation.getFirstCircle().getRadius(), 
                            relation.getSecondCircle().getRadius(), offset);
	                
	                // Aktualisiere die Linie
	                Line line = (Line) relation.getLine();  // Cast zu Line
	                line.setStartX(points.startX);
	                line.setStartY(points.startY);
	                line.setEndX(points.endX);
	                line.setEndY(points.endY);
	                
	                // Aktualisiere den Pfeilkopf
	                updateArrowHead(relation.getArrowHead(), points.startX, points.startY, points.endX, points.endY);
	                
	                //aktualisiere Textbox
	                relation.getArrowLabel().setLayoutX(points.startX - (points.startX - points.endX) / 2);
	                relation.getArrowLabel().setLayoutY(points.startY - (points.startY - points.endY) / 2);
	                
	            }else {//wenn Arc auf sich selbst
	            	
	            	Arc arc = (Arc) relation.getLine();
	            	
	                // Der Radius des Arcs, immer auf denselben Wert
	                double arcRadius = 28;
	                double radius = relation.getFirstCircle().getRadius();
	                double centerX = relation.getFirstCircle().getCenterX();
	                double centerY = relation.getFirstCircle().getCenterY();
	                
	                //Winkel
	                double startAngle_at_large_circle = 4*relation.getOffset_counter();
	                int arcAngle = 210;
	                // Berechne den Mittelpunkt des Arcs
	                double arcCenterX = centerX + (radius) * Math.cos(Math.toRadians(startAngle_at_large_circle));
	                double arcCenterY = centerY + (radius) * Math.sin(Math.toRadians(startAngle_at_large_circle));

	                // Der Startwinkel des Arcs ist relative zum Mittelpunkt des Arcs und hängt von deinem gewünschten Winkel ab
	                double startAngle = -startAngle_at_large_circle - arcAngle / 2;

	                // Update den Arc
	                arc.setCenterX(arcCenterX);
	                arc.setCenterY(arcCenterY);
	                arc.setRadiusX(arcRadius);
	                arc.setRadiusY(arcRadius);
	                arc.setStartAngle(startAngle);
	                arc.setLength(arcAngle);
	                
	                double arrowSize = 15;
	                double angle = Math.toRadians(195+4*offset); // Korrigiere den Winkel nach Bedarf
	                
	                Polygon arrowHead = relation.getArrowHead();
	                // Berechnen des Endpunkts des Bogens
	                double endXarc = arc.getBoundsInLocal().getMinX();
	                double endYarc = arc.getBoundsInLocal().getMinY();

	                arrowHead.getPoints().setAll(
	                	    endXarc, endYarc, // Spitze des Pfeils
	                	    endXarc - arrowSize * Math.cos(angle - Math.toRadians(30)), 
	                	    endYarc - arrowSize * Math.sin(angle - Math.toRadians(30)), // Linke Seite des Pfeils
	                	    endXarc - arrowSize * Math.cos(angle + Math.toRadians(30)), 
	                	    endYarc - arrowSize * Math.sin(angle + Math.toRadians(30))  // Rechte Seite des Pfeils
	                	);
	                
	              //aktualisiere Textbox
	                relation.getArrowLabel().setLayoutX(endXarc);
	                relation.getArrowLabel().setLayoutY(endYarc);
	            }
            }
        }
        }
    
    // Hilfsmethode zum Aktualisieren des Pfeilkopfs
    private void updateArrowHead(Polygon arrowHead, double startX, double startY, double endX, double endY) {
        
    	// Berechne den Winkel der Linie
        double angle = Math.atan2(endY - startY, endX - startX)+ Math.PI;

        // Größe und Winkel für den Pfeilkopf
        double arrowLength = 10;
        double arrowWidth = 7;

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        
        double x1 = endX + arrowLength * cos - arrowWidth * sin;
        double y1 = endY + arrowLength * sin + arrowWidth * cos;
        double x2 = endX + arrowLength * cos + arrowWidth * sin;
        double y2 = endY + arrowLength * sin - arrowWidth * cos;

        // Setze die neuen Koordinaten für das Polygon (den Pfeilkopf)
        arrowHead.getPoints().setAll(
            endX, endY,
            x1, y1,
            x2, y2
        );
    }

    private static class EdgePoints {
        final double startX, startY, endX, endY;

        EdgePoints(double startX, double startY, double endX, double endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }
}

