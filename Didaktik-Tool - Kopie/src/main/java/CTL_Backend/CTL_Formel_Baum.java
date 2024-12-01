package CTL_Backend;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class CTL_Formel_Baum {
    
    // Einstiegs- oder Startpunkt des Baumes
    private erfüllende_Mengen startpunkt = null;
    private Transitionssystem ts;
	private boolean lösungsmengen_anzeigen;
	private List<NodeBox> allNodeBoxes = new ArrayList<>(); // Liste aller NodeBox-Instanzen

    public boolean isLösungsmengen_anzeigen() {
		return lösungsmengen_anzeigen;
	}

	public void setLösungsmengen_anzeigen(boolean lösungsmengen_anzeigen) {
		this.lösungsmengen_anzeigen = lösungsmengen_anzeigen;
		
		for(NodeBox node:allNodeBoxes) {
			if(lösungsmengen_anzeigen) {
				node.makeToggleButtonVisible();
				if(node instanceof NodeBox_with_detail_solution) {
					((NodeBox_with_detail_solution) node).make_detail_toggle_button_visible();
				}
			}else {
				node.makeToggleButtonInvisible();
				if(node instanceof NodeBox_with_detail_solution) {
					((NodeBox_with_detail_solution) node).make_detail_toggle_button_unvisible();
					}
			}
		}
	}

	// Konstruktor, um den Startpunkt des Baumes festzulegen
    public CTL_Formel_Baum(erfüllende_Mengen startpunkt,Transitionssystem ts) {
        this.startpunkt = startpunkt;
        this.ts = ts;
    }

    // Öffentliche Methode, die von einer anderen Klasse aus aufgerufen werden kann, um den Baum zu zeichnen
    public StackPane zeichneBaum(int startpunkt_auf_pane_X, int startpunkt_auf_pane_y) {
        StackPane baumPane = new StackPane(); // Container für das Ein-/Ausblenden
        Pane zeichenPane = new Pane(); // Zeichenfläche für den Baum
        

        if (startpunkt != null) {
        	//Berechnet die Lösungen, Detaillösungen usw.
        	startpunkt.berechne(ts);
            // Zeichnet den Baum auf der Zeichenfläche
            drawTree(zeichenPane, startpunkt, startpunkt_auf_pane_X, startpunkt_auf_pane_y, 200, 80);
        }
        
        zeichenPane.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY
            )));

        // Füge die Zeichenfläche zum StackPane hinzu
        baumPane.getChildren().add(zeichenPane);
        baumPane.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY
            )));

        return baumPane;
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
        
        NodeBox combo_at_node;
        
        // Wenn das Element das Interface Detail_Lösung implementiert
        if (node instanceof detail_lösung) {
        	combo_at_node = new NodeBox_with_detail_solution(node, symbol, ts);
            Pane nodeBox = ((NodeBox_with_detail_solution) combo_at_node).getDetail_toggle_pane_complete();
            nodeBox.setLayoutX(x);
            nodeBox.setLayoutY(y);
        	pane.getChildren().add(nodeBox);
        }else {
        	combo_at_node = new NodeBox(symbol,node, ts);
            StackPane nodeBox = combo_at_node.getStackPane();
            nodeBox.setLayoutX(x);
            nodeBox.setLayoutY(y);
        	pane.getChildren().add(nodeBox);
        }

        allNodeBoxes.add(combo_at_node);

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
    
    // Zeichne eine Linie zwischen Eltern- und Kindknoten
    private void drawLine(Pane pane, double startX, double startY, double endX, double endY) {
    	Line line = new Line(startX+40, startY+40, endX+40, endY);
        line.setStroke(Color.BLACK);
        pane.getChildren().add(line);
    }
}
    
  //eigne Klasse die es ermöglicht auf lösungsset umzuschalten
class NodeBox {
    protected StackPane stackpane;
    protected erfüllende_Mengen erfüllende_menge;
    private boolean isShowingSolutionSet; // Zustand für den Toggle
    private String nodename;
    private Button togglebutton;

    public NodeBox(String nodeName, erfüllende_Mengen erfüllende_menge, Transitionssystem ts) {
        this.erfüllende_menge = erfüllende_menge;
        this.nodename = nodeName;
        this.isShowingSolutionSet = false; // Standardzustand ist false
        
        // Rechteck NodeBox
        Rectangle rect = new Rectangle(80, 40);
        rect.setFill(Color.LIGHTBLUE);
        rect.setStroke(Color.BLACK);
        
        // Text auf dem Node
        Text text = new Text(nodeName);
        text.setStyle("-fx-font-size: 6;");
        
        // ToggleButton, der die Lösung anzeigen kann
        Button toggleButton = new Button("->");

        // Button auf 10 Pixel Durchmesser anpassen und Form als Kreis setzen
        double radius = 7; // Radius ist die Hälfte des Durchmessers
        Circle circleShape = new Circle(radius);
        toggleButton.setShape(circleShape);
        toggleButton.setMinSize(2 * radius, 2 * radius); // Minimale Größe
        toggleButton.setMaxSize(2 * radius, 2 * radius); // Maximale Größe

        // Hintergrund- und Randfarbe einstellen
        toggleButton.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: black; " +
            "-fx-border-width: 1px;"+
            "-fx-font-size: 6px;"
        );
        
        // Button unsichtbar machen
        toggleButton.setVisible(false);

        // StackPane für NodeBox mit den grafischen Elementen
        stackpane = new StackPane();
        stackpane.getChildren().addAll(rect, text, toggleButton);

        // ToggleButton in die obere linke Ecke setzen
        StackPane.setAlignment(toggleButton, Pos.TOP_LEFT);
        toggleButton.setTranslateX(-5); // Offset für die linke Ecke
        toggleButton.setTranslateY(2 * radius - rect.getHeight() / 2); // Offset für die obere Ecke

        // Klick-Event für ToggleButton
        toggleButton.setOnMouseClicked(event -> {
            handleMouseClickEvent(ts); // Verwendet die neue Methode zum Umschalten
        });

        this.togglebutton = toggleButton;
    }

    public StackPane getStackPane() {
        return stackpane;
    }

    public erfüllende_Mengen getData() {
        return erfüllende_menge;
    }
    
    public void makeToggleButtonVisible() {
        this.togglebutton.setVisible(true);
    }
    
    public void makeToggleButtonInvisible() {
        this.togglebutton.setVisible(false);
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
    
    public void handleMouseClickEvent(Transitionssystem ts) {
        toggleSolutionSet();

        if (isShowingSolutionSet) {
            // Zeige die Lösungsmengen an
            Set<Zustand> lösungsMenge = erfüllende_menge.berechne(ts);  // `ts` muss verfügbar sein
            StringBuilder names = new StringBuilder();
            for (Zustand zustand : lösungsMenge) {
                if (names.length() > 0) names.append(", ");
                names.append(zustand.getName());
            }
            ((Text) stackpane.getChildren().get(1)).setText(names.toString()); // Setze den Text auf die Lösungsmengen
            ((Rectangle) stackpane.getChildren().get(0)).setFill(Color.LIGHTGREEN); // Ändere die Farbe zu Grün
        } else {
            // Setze auf den ursprünglichen Text zurück
            ((Text) stackpane.getChildren().get(1)).setText(nodename); // Ursprünglicher Text
            ((Rectangle) stackpane.getChildren().get(0)).setFill(Color.LIGHTBLUE); // Ursprüngliche Farbe
        }
    }
}


class NodeBox_with_detail_solution extends NodeBox {
    
	private Pane detail_toggle_pane_complete;
    private Button toogel_button_detail;

    public NodeBox_with_detail_solution(erfüllende_Mengen data, String nodename,Transitionssystem ts) {
        super(nodename, data,ts); // Konstruktor der Oberklasse aufrufen
        this.detail_toggle_pane_complete = createToggleDetailSolution_Pane((detail_lösung)this.erfüllende_menge);
    }

	// Neue private Methode zum Hinzufügen des Pluszeichens und der Textbox
    private Pane createToggleDetailSolution_Pane(detail_lösung menge) {
        
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
        
        this.toogel_button_detail = toggleButton;

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
        scrollPane.setPrefWidth(350); 
        scrollPane.setFitToWidth(true);
        scrollPane.setVisible(false); 
        
        // Position toggleButton relative to stackpane using translateX and translateY
        toggleButton.setTranslateX(70);  // Relative offset instead of layoutX
        toggleButton.setTranslateY(-10);

        // Position scrollPane relative to stackpane using translateX and translateY
        scrollPane.setTranslateX(75);  // Relative offset instead of layoutX
        scrollPane.setTranslateY(-5);
        
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
        
        //zuerst unsichtbar
        toggleButton.setVisible(false);
        
        // Füge den Toggle-Button und die ScrollPane an der ParentPane hinzu
        Pane container = new Pane();  // Statt BorderPane
        container.setPrefHeight(10);
        container.setPrefWidth(10);
        container.getChildren().add(this.stackpane);
        container.getChildren().addAll(scrollPane, toggleButton);
        
        return container;
    }
    
    public void make_detail_toggle_button_visible() {
    	this.toogel_button_detail.setVisible(true);
    }
    
    public void make_detail_toggle_button_unvisible() {
    	this.toogel_button_detail.setVisible(false);
    }
    public Pane getDetail_toggle_pane_complete() {
		return detail_toggle_pane_complete;
	}
}
