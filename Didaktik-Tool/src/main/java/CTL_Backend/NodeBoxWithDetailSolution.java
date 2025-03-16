package CTL_Backend;

import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

//Klasse die Node-Box erweitert für alle erfüllenden Mengen die ihre Lösung durch Fixpunkt-Itteration bestimmen
class NodeBox_with_detail_solution extends NodeBox {
	
	//erweitert um Button der aber zusammen mit StackPane auf extra Pane gelagert wird
    private Pane detail_toggle_pane_complete;
    private Button toggle_button_detail;
    
    //Konstruktor ruft erst super auf und erzeugt dann die Pane mit dem extra Button
    public NodeBox_with_detail_solution(ErfüllendeMenge data, String nodename, Transitionssystem ts, NodeBox parent) {
        super(nodename, data, ts, parent);
        this.detail_toggle_pane_complete = this.createToggleDetailSolution_Pane((detail_lösung) this.erfuellendeMenge);
    }

    private Pane createToggleDetailSolution_Pane(detail_lösung menge) {
    	
        // Bestimme die maximale Zeilenlänge
        double maxWidth = getMaxTextWidth(menge.get_schritt_weise_lösung());
     
        
    	//Erzeuge und formatiere neuen Button
        Button toggleButton = new Button("+");
        toggleButton.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-size: 7px; -fx-text-fill: black; -fx-min-width: 5px; -fx-min-height: 5px; -fx-border-color: black; -fx-border-width: 1px;");
        this.toggle_button_detail = toggleButton;
        Label textBox = new Label(menge.get_schritt_weise_lösung());
        textBox.setPrefWidth(maxWidth);
        textBox.setWrapText(true);
        textBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px; -fx-font-size: 10px;");
        
   
        //Erzeuge und Formatiere ScrollPane die die Itterationsschritte enthält
        ScrollPane scrollPane = new ScrollPane(textBox);
        scrollPane.setPrefWidth(maxWidth + 20); // Padding hinzufügen
        scrollPane.setFitToWidth(true);
        scrollPane.setVisible(false);
        
        //Paltzieren der Elemente auf der Pane
        toggleButton.setTranslateX(70.0);
        toggleButton.setTranslateY(-10.0);
        scrollPane.setTranslateX(75.0);
        scrollPane.setTranslateY(-5.0);
        
        //Registieren des Events zum Toogle der Sichtbarkeit von ScrollPane
        toggleButton.setOnAction(event -> {
            if (toggleButton.getText().equals("+")) {
                toggleButton.setText("-");
                scrollPane.setVisible(true);
                scrollPane.requestLayout();
            } else {
                toggleButton.setText("+");
                scrollPane.setVisible(false);
            }
        });
        
        //Sichtbarkeit am Anfang auf False setzen
        toggleButton.setVisible(false);
        
        //Erzeuge die Pane und füge die Elemente hinzu
        Pane container = new Pane();
        container.setPrefHeight(10.0);
        container.setPrefWidth(10.0);
        container.getChildren().add(this.stackpane);
        container.getChildren().addAll(scrollPane, toggleButton);
        container.toFront();
        
        //Listner auf StackPane setzten, damit sich die Pane und die Zusatzelemente selbstständig mit StackPane bewegen
        this.stackpane.translateXProperty().addListener((observable, oldValue, newValue) -> {
            toggleButton.setTranslateX(newValue.doubleValue() + 70.0);
            scrollPane.setTranslateX(newValue.doubleValue() + 75.0);
        });

        this.stackpane.translateYProperty().addListener((observable, oldValue, newValue) -> {
            toggleButton.setTranslateY(newValue.doubleValue() - 10.0);
            scrollPane.setTranslateY(newValue.doubleValue() - 5.0);        
        });

        return container;
    }
    
    //MEthode um Zeilenbreite zu bestimmen
    private double getMaxTextWidth(String text) {
        String[] lines = text.split("\\n");
        double maxWidth = 0;
        for (String line : lines) {
            double lineWidth = line.length() * 7; // Annahme: ca. 7px pro Zeichen
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }
        return maxWidth;
    }
    
    //Methoden zum ändern der Sichtbarkeit
    public void make_detail_toggle_button_visible() {
        this.toggle_button_detail.setVisible(true);
        this.detail_toggle_pane_complete.toFront();
    }

    public void make_detail_toggle_button_unvisible() {
        this.toggle_button_detail.setVisible(false);
    }

    public Pane getDetail_toggle_pane_complete() {
        return this.detail_toggle_pane_complete;
    }
    
    //Überschreiben der Super-Methode weil zwischen Rectangle und der höchsten Pane eine Pane mehr liegt als bei super
    @Override 
    public Bounds getRectangleBounds() {
        StackPane stackPane = (StackPane) this.rechteck.getParent();
        Pane containerPane = (Pane) stackPane.getParent();
        Pane parentPane = (Pane) containerPane.getParent();
        Bounds boundsInScene = this.rechteck.localToScene(this.rechteck.getBoundsInLocal());
        return parentPane.sceneToLocal(boundsInScene);
    }
}

