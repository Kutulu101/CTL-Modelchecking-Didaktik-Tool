package CTL_Backend;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.Set;
import java.util.stream.Collectors;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;

public class CTL_Formel_Baum {
    
    // Einstiegs- oder Startpunkt des Baumes
    private erfüllende_Mengen startpunkt = null;
    private Transitionssystem ts;

    // Konstruktor, um den Startpunkt des Baumes festzulegen
    public CTL_Formel_Baum(erfüllende_Mengen startpunkt,Transitionssystem ts) {
        this.startpunkt = startpunkt;
        this.ts = ts;
    }

    // Öffentliche Methode, die von einer anderen Klasse aus aufgerufen werden kann, um den Baum zu zeichnen
    public void zeichneBaum(Pane pane) {
        if (startpunkt != null) {
            // Startet das Zeichnen des Baums an der Position (400, 50)
            drawTree(pane, startpunkt, 400, 50, 200, 80);
        }
    }

    // Methode zum Zeichnen des Baumes (rekursiv)
    private void drawTree(Pane pane, erfüllende_Mengen node, double x, double y, double xOffset, double yOffset) {
        if (node == null) return;

        // Zeichne den aktuellen Knoten
        String symbol;
        //ersetzte bei bedarf übergänge durch den konkreten übergang
        if (node instanceof HatÜbergang && node.get_symbol().contains("übergänge")) {
            // Casten von node auf das Interface HatÜbergang
            HatÜbergang hatÜbergang = (HatÜbergang) node;
            
            // Aufrufen von getÜbergänge() und die Übergänge durch Kommas trennen
            Set<Übergang> übergänge = hatÜbergang.getÜbergänge();
            String übergängeString = übergänge.stream()
                .map(Übergang::getZeichen)  // Annahme: Übergang hat eine sinnvolle toString() Methode
                .collect(Collectors.joining(", "));
            
            // Ersetzen von "übergang" mit der Liste der Übergänge
            symbol = node.get_symbol().replace("übergänge", übergängeString);
        }
        else symbol = node.get_symbol();
        
        NodeBox combo_at_node = createNodeBox(symbol,node);
        StackPane nodeBox = combo_at_node.getStackPane();
        nodeBox.setLayoutX(x);
        nodeBox.setLayoutY(y);
        pane.getChildren().add(nodeBox);

        
        // Wenn das Element das Interface Detail_Lösung implementiert
        if (node instanceof detail_lösung) {
            addToggleDetailSolution(nodeBox,  (detail_lösung)node,pane);
        }


        // Wenn der Knoten eine innere Menge hat (Ast), zeichne die innere Menge rekursiv
        if (node instanceof Ast) {
            erfüllende_Mengen innerNode = ((Ast) node).getInnere_Menge();
            double childX = x - xOffset;
            double childY = y + yOffset;
            drawLine(pane, x, y, childX, childY);
            drawTree(pane, innerNode, childX, childY, xOffset / 2, yOffset);
        }
        // Wenn der Knoten zwei innere Mengen hat (Verzweigung), zeichne beide rekursiv
        else if (node instanceof Verzweigung) {
            erfüllende_Mengen leftNode = ((Verzweigung) node).getLinke_Seite();
            erfüllende_Mengen rightNode = ((Verzweigung) node).getRechte_Seite();
            double leftChildX = x - xOffset;
            double rightChildX = x + xOffset;
            double childY = y + yOffset;

            drawLine(pane, x, y, leftChildX, childY);
            drawLine(pane, x, y, rightChildX, childY);

            drawTree(pane, leftNode, leftChildX, childY, xOffset / 2, yOffset);
            drawTree(pane, rightNode, rightChildX, childY, xOffset / 2, yOffset);
        }
        // Wenn der Knoten keine inneren Mengen hat (Blatt), beende die Rekursion
        else if (node instanceof Blatt) {
            return;
        }
    }
    
 // Neue private Methode zum Hinzufügen des Pluszeichens und der Textbox
    private void addToggleDetailSolution(StackPane nodeBox, detail_lösung menge, Pane pane) {
        
    	// Toggle-Button erstellen und gestalten
        Button toggleButton = new Button("+");
        toggleButton.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 5px; " +
            "-fx-font-size: 8px; " +
            "-fx-text-fill: black; " +
            "-fx-min-width: 5px; " +
            "-fx-min-height: 5px;"
        );

        // Label für die Detail-Lösung
        Label textBox = new Label(menge.get_schritt_weise_lösung());
        textBox.setWrapText(true); 
        textBox.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-padding: 10px; " +
            "-fx-font-size: 10px;"
        );
           

        // ScrollPane erstellen
        ScrollPane scrollPane = new ScrollPane(textBox);
        scrollPane.setPrefWidth(300); 
        scrollPane.setFitToWidth(true);
        scrollPane.setVisible(false); 
        
     // Positioniere den Button und die ScrollPane relativ zur nodeBox
        toggleButton.setLayoutX(nodeBox.getLayoutX() +30); 
        toggleButton.setLayoutY(nodeBox.getLayoutY() - 25);

        scrollPane.setLayoutX(nodeBox.getLayoutX() +35); 
        scrollPane.setLayoutY(nodeBox.getLayoutY() -25);



        // Toggle-Action für den Button
        toggleButton.setOnAction(event -> {
            if (toggleButton.getText().equals("+")) {
                toggleButton.setText("-");
                scrollPane.setVisible(true); // ScrollPane anzeigen
                scrollPane.requestLayout(); // Layout anpassen
            } else {
                toggleButton.setText("+");
                scrollPane.setVisible(false); // ScrollPane verstecken
            }
        });
        
        // Füge den Toggle-Button und die ScrollPane an der ParentPane hinzu
        Pane container = new Pane();  // Statt BorderPane
        container.setStyle(
            "-fx-background-color: lightgrey;");
        container.getChildren().addAll(scrollPane, toggleButton);
        pane.getChildren().add(container);
        // Clipping für das Pane deaktivieren
        pane.setClip(null);
    }





    // Erstellt ein grafisches Element für einen Knoten
    private NodeBox createNodeBox(String nodeName, erfüllende_Mengen ctl_lösungsmenge) {
        Rectangle rect = new Rectangle(80, 40);
        rect.setFill(Color.LIGHTBLUE);
        rect.setStroke(Color.BLACK);
        
        Text text = new Text(nodeName);
        text.setStyle("-fx-font-size: 6;");

        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect, text);

        NodeBox nodeBox = new NodeBox(stack, ctl_lösungsmenge,nodeName); // Erstelle NodeBox-Instanz

        // Mache das Rechteck klickbar
        rect.setOnMouseClicked(event -> {
            nodeBox.toggleSolutionSet(); // Toggle-Zustand in NodeBox umschalten
            
            if (nodeBox.isShowingSolutionSet()) {
                // Zeige die Lösungsmengen an
                Set<Zustand> lösungsMenge = ctl_lösungsmenge.berechne(ts);
                StringBuilder names = new StringBuilder();
                for (Zustand zustand : lösungsMenge) {
                    if (names.length() > 0) names.append(", ");
                    names.append(zustand.getName());
                }
                text.setText(names.toString()); // Setze den Text auf die Lösungsmengen
                rect.setFill(Color.LIGHTGREEN); // Ändere die Farbe zu Grün
            } else {
                // Zeige den ursprünglichen Text an
                text.setText(nodeName); // Setze den Text zurück
                rect.setFill(Color.LIGHTBLUE); // Ändere die Farbe zurück zu Blau
            }
        });

        return nodeBox; // Gebe die NodeBox zurück
    }

    // Zeichne eine Linie zwischen Eltern- und Kindknoten
    private void drawLine(Pane pane, double startX, double startY, double endX, double endY) {
    	Line line = new Line(startX, startY + 20, endX, endY-20);
        line.setStroke(Color.BLACK);
        pane.getChildren().add(line);
    }
}
    
    //eigne Klasse die es ermöglicht auf lösungsset umzuschalten
class NodeBox {
        private StackPane stackpane;
        private erfüllende_Mengen data;
        private boolean isShowingSolutionSet; // Zustand für den Toggle
        private String nodename;

        public NodeBox(StackPane stack, erfüllende_Mengen data,String nodename) {
            this.stackpane = stack;
            this.data = data;
            this.isShowingSolutionSet = false; // Standardzustand ist false
            this.nodename = nodename;
        }

        public StackPane getStackPane() {
            return stackpane;
        }

        public erfüllende_Mengen getData() {
            return data;
        }
        

        public String getNodename() {
			return nodename;
		}

		public boolean isShowingSolutionSet() {
            return isShowingSolutionSet;
        }

        public void toggleSolutionSet() {
            isShowingSolutionSet = !isShowingSolutionSet;
        }
    }

