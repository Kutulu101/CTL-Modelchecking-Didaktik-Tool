package GUI;

import CTL_Backend.Zustandsformel;
import CTL_Backend.CTL_Formel_Baum;
import CTL_Backend.Transitionssystem;
import CTL_Backend.Umformung;
import CTL_Backend.Zustand;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.control.Label;
import javafx.scene.text.*;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import java.util.Random;
import java.util.Set;
import java.util.Map;
import java.util.Optional; 
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TextField;
import java.util.concurrent.atomic.AtomicReference;
import javafx.util.Callback;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import javafx.scene.control.ScrollPane;

public class GUI_Main extends Application {
	
	static int circle_counter = 0;
	static boolean draw_relations = false;
    private Circle firstCircle = null;
    private Circle secondCircle = null;
    private Text firstCircle_label = null;
    private Text secondCircle_label = null;
    private HashSet<Relation> list_of_relations = new HashSet<>();
    private List<Group> list_of_circle_groups = new ArrayList<>();
    private boolean inputActive = false;
    static Zustandsformel zustandsformel = new Zustandsformel ("");
    private int counter_labelboxen = 0;
    // Stack zum Speichern der Labels und ComboBoxen (beide sind vom Typ Node)
    private Stack<Node> historyStack = new Stack<>();
    private boolean in_transtions_eingabe = false;
    int i;

    
    
    
    
    //zum ersetzten der ComboBox
    private AtomicReference<ComboBox<String>> eingabeComboboxRef = new AtomicReference<>();
    
    // Erstelle eine ComboBox mit mathematischen Symbolen
    HBox FormelBox = new HBox(10);

	
	
    @Override
    public void start(Stage primaryStage) {
    	
    	
    	//########Layout erstellen################
    	// Erstelle das Hauptlayout
        BorderPane root = new BorderPane();

        // Erstelle einen Pane für das Drag and Drop
        Pane drawingPane = new Pane();
        drawingPane.setStyle("-fx-background-color: lightgray;"); // Hintergrundfarbe für das freie Feld
        root.setCenter(drawingPane);
    	
    	
        // Erstelle eine HBox für die Buttons
        HBox buttonBox = new HBox(10);
        Button btnAddCircle = new Button("Zustand hinzufügen");
        Button btnRelation = new Button("Relationen einzeichnen");
        Button btnUndocomboBox = new Button("Undo Formeleingabe");
        Button btnTS_entfernen = new Button("aktuelles TS löschen");
        Button btnBerechnen = new Button("Berechne Lösungsmengen");
        Button btnNeustart = new Button("Programm Neustarten");
        Button btnBeenden = new Button("Programm beenden");
        buttonBox.getChildren().addAll(btnAddCircle,btnRelation,btnUndocomboBox,btnTS_entfernen,btnBerechnen,btnNeustart, btnBeenden);
        root.setTop(buttonBox);
        
        //Erstellen der Comboboxen für die Formeleingabe
        // Erstelle ein Label für den Text
        Label textLabel = new Label("Formel eingeben:");

        // Füge das Label zur HBox hinzu
        FormelBox.getChildren().add(textLabel);
        
        //erzeugen und einfügen der Combobox
        ComboBox<String> eingabeCombobox = new ComboBox<>();
        
        //zustandformel berechnet aus dem bereits eingelsenen String welche Symbole verwendet werden können
        configureComboBox(eingabeCombobox,zustandsformel.einlesbare_Symbole());
        historyStack.push(eingabeCombobox);
        FormelBox.getChildren().add(eingabeCombobox);
        
        //AtomicRef auf die ComboBox
        eingabeComboboxRef.set(eingabeCombobox);
        //Formelbox einfügen
        root.setBottom(FormelBox);
        
        //############################Registrieren der Events##########################
        
        // Funktion zum Beenden des Programms
        btnBeenden.setOnAction(e -> {
            //for (Relation rel: list_of_relations) {
            	//System.out.println(rel.getDetailsString());
            //}
        	//Transitionssystem transsitionssystem = new Transitionssystem(list_of_relations);
        	//transsitionssystem.printAllZustände();
        	primaryStage.close();
        });
        
        //Funkiton zum löschen des gezeichnet TS
        btnTS_entfernen.setOnAction(event -> {
            // Alle Kinder (Formen) vom Pane entfernen
            drawingPane.getChildren().clear();
            circle_counter = 0;
        });
       
        // Funktion zum Hinzufügen eines Kreises, enthält Erstellung,Beschriftung, Pfeile
        btnAddCircle.setOnAction(e -> {     
	        drawingPane.getChildren().add(create_circle_with_text()); // Füge den Kreis zum Pane hinzu
	        draw_relations = false;
	        colorAllCircles(drawingPane);
        });
        
        btnRelation.setOnAction(e-> {draw_relations = true;
	        root.lookupAll(".circle_with_text").forEach(node -> {
	            Group group = (Group) node;
	            if (group.getChildren().get(0) instanceof Circle) {
	                ((Circle) group.getChildren().get(0)).setFill(Color.YELLOW);
	            }
	        });
        });
        
        //Funktion zum berechnen der Normalform und der Lösungsmenge
        btnBerechnen.setOnAction(event -> {
            // 1. Prüfen, ob die Formel mit "Formelende" endet
            if (!zustandsformel.getFormel_string().endsWith("Formelende")) {
                // Wenn nein, zeigen wir eine MessageBox an
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warnung");
                alert.setHeaderText(null);
                alert.setContentText("Bitte erst Formelende einlesen.");
                alert.showAndWait();
            } else {
                // 2. Wenn ja, führen wir die Methode turn_to_normal_form aus
                zustandsformel.turn_to_normal_form();
                System.out.println("Zustandsformel in Normalform: " + zustandsformel.getFormel_string_normal_form());
                erstelleUmformungsLabelsUndFügeHinzu(FormelBox,zustandsformel,root);
                Transitionssystem transsitionssystem = new Transitionssystem(list_of_relations);
                zustandsformel.print_erfüllende_zustände(transsitionssystem);
                this.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(transsitionssystem),list_of_circle_groups);
                this.zeige_schritt_für_schritt_lösung(root, zustandsformel,transsitionssystem);
            }
        });
     
        // Ereignisbehandlung für die ComboBox
        eingabeCombobox.setOnAction(this::handleComboBoxAction);
        
        //Undo für die Combobox
        btnUndocomboBox.setOnAction(event -> undo_combobox());
        
        //Button zum Neustarten
        btnNeustart.setOnAction(event -> {
            try {
                // Alle Daten und GUI-Elemente zurücksetzen
                circle_counter = 0;
                draw_relations = false;
                firstCircle = null;
                secondCircle = null;
                firstCircle_label = null;
                secondCircle_label = null;
                list_of_relations.clear();
                list_of_circle_groups.clear();
                inputActive = false;
                zustandsformel = new Zustandsformel("");
                counter_labelboxen = 0;
                historyStack.clear();
                eingabeComboboxRef.set(null);
                FormelBox.getChildren().clear();
                
                // Stage schließen
                primaryStage.close();
                
                // Neuen Stage erstellen und initialisieren
                Stage newStage = new Stage();
                start(newStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
      

        //################################Starten der Szene###############
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Erstelle ein eigenes Transitionssystem und eine eigene CTL-Formel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // Methode zum Aktivieren von Drag-and-Drop für einen Kreis mit Text
    private void enableDragAndDrop(Group circlewithtext) {
    	
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
    //Methode die Group aus Kreis und Beschriftung erzeugt
    private Group create_circle_with_text() {
    	
    	//Kreis
        Circle circle = new Circle(50, Color.BLUE); // Erstelle einen blauen Kreis mit Radius 50
        //Beschriftung
        circle_counter +=1;
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
        enableDragAndDrop(circleWithText);
        circleWithText.setOnMouseClicked(this::handleCircleClick); //füge das anklicken der Gruppe als Event hinzu
        //CSS-Tag anfügen
        circleWithText.getStyleClass().add("circle_with_text");
        //in Liste einfügen
        list_of_circle_groups.add(circleWithText);
        return circleWithText;
    }
    
    private void handleCircleClick(MouseEvent event) {
        
    	if (draw_relations && !inputActive) { // nur wenn im Relation-Zeichnen Modus 
            Group clickedGroup = (Group) event.getSource();
            Circle clickedCircle = (Circle) clickedGroup.getChildren().get(0);
            Text clickedCircle_label = (Text) clickedGroup.getChildren().get(1);

            // Extrahiere das übergeordnete Pane
            Pane parentPane = (Pane) clickedGroup.getParent();

            if (firstCircle == null) {
                // Speichere den ersten Kreis
                firstCircle = clickedCircle;
                firstCircle_label = clickedCircle_label;
            } else if (secondCircle == null) {
                // Speichere den zweiten Kreis und zeichne den Pfeil
                secondCircle = clickedCircle;
                secondCircle_label = clickedCircle_label;
                drawArrow(parentPane); // Übergabe des Pane an die Methode
                // Setze die Kreise zurück, um weitere Pfeile zeichnen zu können

            }
        }
    	
    }
    
    //Funktion zum Zeichnen des Pfeils
    private void drawArrow(Pane drawingpane) {
        
    	// wenn es schon eine relation gibt, offset seten und in der Relation speichern
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
            showTextInputField(startEdgeX - (startEdgeX - endEdgeX) / 2, startEdgeY - (startEdgeY - endEdgeY) / 2, 0, line, arrowHead);
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
            showTextInputField(endXarc, endYarc, 50, line, arrowHead);
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
    
    private char read_transition(String headline){
        
        // Öffne eine Input-Box (TextInputDialog) für die Eingabe eines Charakters
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Transition Eingabe");
        dialog.setHeaderText(headline); // Verwende die headline hier
        dialog.setContentText("Charakter:");

        // Zeige den Dialog und warte auf die Eingabe des Benutzers
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String input = result.get();
            if (input.length() == 1) {
                // Wenn ein einzelner Charakter eingegeben wurde, verarbeite ihn
                char enteredChar = input.charAt(0);
                System.out.println("Eingegebener Charakter: " + enteredChar);
                return enteredChar; // Rückgabe des gültigen Zeichens
            } else {
                // Falls der Benutzer mehr als einen Charakter eingibt
                System.out.println("Bitte nur einen einzelnen Charakter eingeben.");
                return read_transition("Bitte nur einen Charakter eingeben!"); // Rekursiver Aufruf mit neuer Nachricht
            }
        } else {
            // Falls der Benutzer den Dialog abbricht
            System.out.println("Eingabe abgebrochen.");
            return '\0'; // Rückgabewert, der anzeigt, dass keine gültige Eingabe gemacht wurde
        }
    }
    

    private void handleComboBoxAction(javafx.event.ActionEvent event) {
    	
    	ComboBox<String> sourceComboBox = (ComboBox<String>) event.getSource();
    	String entered_symbol;
    	
        // Überprüfe, ob die ausgewählte Option "Transition eingeben" ist
        if ("Transition eingeben".equals(sourceComboBox.getValue())) {
        	entered_symbol = Character.toString(read_transition("Bitte die gewünschte Transition eingeben"));
        }
        else {entered_symbol = (String) sourceComboBox.getValue();}
        
     // Tooltip für das ausgewählte Item extrahieren
        Tooltip selectedTooltip = new Tooltip(zustandsformel.einlesbare_Symbole().get(entered_symbol));
        
        zustandsformel.ein_char_einlesen(entered_symbol);
        System.out.println(zustandsformel.getFormel_string());
        replaceComboBoxWithTextField(entered_symbol,selectedTooltip);
        
        
    }

    private void replaceComboBoxWithTextField(String enteredSymbol, Tooltip tooltip) {

        ComboBox<String> oldComboBox = eingabeComboboxRef.get();
        if (oldComboBox != null) {
        	historyStack.pop();
            FormelBox.getChildren().remove(oldComboBox); // Entferne die alte ComboBox
        }else {System.out.println("Combobox nicht gefunden");};
    	
        Label textField = new Label();
        textField.setText(enteredSymbol);
     
        // Tooltip für das Label erstellen
        Tooltip.install(textField, tooltip); // Tooltip auf das Label anwenden
        
        //neue Combobox nur wenn nicht Formelende gelesen wurde 
        if(!(enteredSymbol.contains("Formelende"))){
	        ComboBox<String> newComboBox = new ComboBox<>();
	        configureComboBox(newComboBox,zustandsformel.einlesbare_Symbole());
	        
	        FormelBox.getChildren().addAll(textField, newComboBox);
	        
	        // Füge das neue Label und die ComboBox zum Stack hinzu (beide sind vom Typ Node)
	        historyStack.push(textField);
	        historyStack.push(newComboBox);
	        
	        
	        // neue Combobox an Event anhängen
	        newComboBox.setOnAction(this::handleComboBoxAction);
	
	        // Update der Referenz auf die neue ComboBox
	        eingabeComboboxRef.set(newComboBox);
	        //System.out.println("Nach der " + i +  ". Eingabe: " +  eingabeComboboxRef);
        }
    }
    
    private void showTextInputField(double x, double y, int offset,Shape line,Polygon arrowHead) {
    	
    	//Guard damit nicht andere Kreise angeklickt werden können
    	inputActive = true;
    	
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
            	
            	list_of_relations.add(new Relation(firstCircle, secondCircle, firstCircle_label, secondCircle_label, line, arrowHead,returnLabel, label));
            }
            
           
            
            
            //setzt die Kreise zurück:                 
            firstCircle = null;
            secondCircle = null;
            firstCircle_label = null;
            secondCircle_label = null;
            
            //gibt wieder frei
            inputActive = false;
            
            //beendet im Relation-Zeichnen Modus, irgendwas stimmt nicht der Modus wird komisch ausgeschalten???????
            draw_relations = true;
        });

    }
      
    // Methode, um rekursiv alle Kreise zu färben
    private void colorAllCircles(Node node) {
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
    
 // Methode zum Entfernen des Elternelements (HBox) einer ComboBox

    
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
    
    private void configureComboBox(ComboBox<String> comboBox, Map<String, String> gruende) {
        
        // Die Keys der Map als ObservableList für die ComboBox-Items verwenden
        ObservableList<String> items = FXCollections.observableArrayList(gruende.keySet());
        comboBox.setItems(items);
    	
    	// Benutzerdefinierte ListCell für ComboBox
        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String symbol, boolean empty) {
                        super.updateItem(symbol, empty);
                        if (symbol != null && !empty) {
                            setText(symbol);

                            // Wenn Symbol in der Map der nicht einlesbaren Symbole ist und der Grund nicht "" ist
                            if (gruende.containsKey(symbol) && !"".equals(gruende.get(symbol))) {
                                // Text und Hintergrund grau färben
                                setStyle("-fx-text-fill: gray; -fx-background-color: lightgray;");
                                
                                // Tooltip mit dem Grund für die Ablehnung hinzufügen
                                Tooltip tooltip = new Tooltip(zustandsformel.einlesbare_Symbole().get(symbol)+getDefinitionForSymbol(symbol,"CTL-Definitionen.txt","Definition nicht gefunden") +"\n\n"+ getDefinitionForSymbol(symbol,"CTL-Symboleumgangsprachlich.txt","" ));
                                
                                setTooltip(tooltip);
                                
                                
                            } else {
                                // Standardtextfarbe für einlesbare Symbole
                                setStyle("-fx-text-fill: black;");
                                setTooltip(new Tooltip(getDefinitionForSymbol(symbol,"CTL-Definitionen.txt","Definition nicht gefunden")+"\n\n"+ getDefinitionForSymbol(symbol,"CTL-Symboleumgangsprachlich.txt","" ))); 
                            }
                        } else {
                            setText(null);
                            setTooltip(null);
                        }
                    }
                };
            }
        });
    }
    
    private String getDefinitionForSymbol(String symbol, String source_name, String not_found_string) {
        String definition = "";
        File file = new File(source_name); 
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            StringBuilder currentDefinition = new StringBuilder();
            String line;
            boolean isDefinitionFound = false;
            
            while ((line = reader.readLine()) != null) {
                // Prüfe, ob die Zeile das Symbol enthält und mit ":" gefolgt wird
                if (line.startsWith(symbol + ":")) {
                    isDefinitionFound = true; // Definition beginnt
                    int commentIndex = line.indexOf("//");
                    if (commentIndex != -1) {
                        // Wenn "//" in der gleichen Zeile ist, trenne hier und beende das Sammeln der Definition
                        currentDefinition.append(line.substring(line.indexOf(":") + 1, commentIndex).trim());
                        break;
                    } else {
                        currentDefinition.append(line.substring(line.indexOf(":") + 1).trim()).append("\n");
                    }
                } 
                // Falls eine Definition gefunden wurde, füge die nächsten Zeilen hinzu
                else if (isDefinitionFound) {
                    int commentIndex = line.indexOf("//");
                    if (commentIndex != -1) {
                        // Falls "//" in der aktuellen Zeile gefunden wird, füge nur den Teil vor "//" hinzu und beende das Sammeln
                        currentDefinition.append(line.substring(0, commentIndex).trim());
                        break;
                    } else {
                        currentDefinition.append(line.trim()).append("\n"); // Füge jede Zeile der Definition hinzu
                    }
                }
            }
            
            definition = currentDefinition.toString().trim(); // Entferne unnötige Leerzeichen und Zeilenumbrüche am Ende
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return definition.isEmpty() ? not_found_string : definition;
    }


    
    private void erstelleUmformungsLabelsUndFügeHinzu(HBox formelbox, Zustandsformel zustandsformel, BorderPane parentContainer) {
        // Erstelle eine VBox, die die formelbox und die Umformungslabels enthält
        VBox gesamteVBox = new VBox();

        // Füge die formelbox als erstes Element in die VBox hinzu
        gesamteVBox.getChildren().add(formelbox);

        // Erstelle die Umformungslabels und füge sie zur VBox hinzu
        for (Umformung umformung : zustandsformel.getErsetzungen()) {

            // Entferne "Formelende" für die Ausgabe
            String nach_ersetzung_ohne_FE = umformung.getNach_der_Ersetzung();
            if (nach_ersetzung_ohne_FE.contains("Formelende")) {
                nach_ersetzung_ohne_FE = nach_ersetzung_ohne_FE.replace("Formelende", "");
            }

            String vor_ersetzung_ohne_FE = umformung.getVor_der_Ersetzung();
            if (umformung.getVor_der_Ersetzung().contains("Formelende")) {
                vor_ersetzung_ohne_FE = vor_ersetzung_ohne_FE.replace("Formelende", "");
            }

            // Erstelle den Text, der fett gedruckt werden soll
            String regelText = umformung.getErsetzt_mit_regel_nummer() + ": ";
            Text fettText = new Text(regelText);
            fettText.setStyle("-fx-font-weight: bold;");  // Fettgedruckter Teil

            Text normalerText = new Text(vor_ersetzung_ohne_FE + " -> " + nach_ersetzung_ohne_FE);  // Normaler Teil nach dem Pfeil

            // Erstelle ein TextFlow-Objekt, um mehrere Text-Objekte zu kombinieren
            TextFlow textFlow = new TextFlow(fettText, normalerText);

            // Label mit TextFlow als Inhalt
            Label label = new Label();
            label.setGraphic(textFlow);
            label.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
            
            // Füge das Label der VBox hinzu (die Labels kommen nach der formelbox)
            gesamteVBox.getChildren().add(label);
        }

        // Setze das Layout-Spacing und Padding für die VBox
        gesamteVBox.setSpacing(10);
        gesamteVBox.setPadding(new Insets(10));

        // Erstelle ein ScrollPane für die gesamte VBox
        ScrollPane scrollPane = new ScrollPane(gesamteVBox);
        scrollPane.setMaxHeight(100);
        scrollPane.setFitToWidth(true); // Optional: ScrollPane an die Breite anpassen

        // Füge das ScrollPane in den Bottom-Bereich des parentContainer ein
        parentContainer.setBottom(scrollPane);
    }
    
 // Methode zum Rückgängig machen der letzten Eingabe, nur wenn mindestens 2 Elemente im Stack sind
    private void undo_combobox() {
        // Prüfen, ob genug Elemente im Stack vorhanden sind
        if (historyStack.size() >= 2 || (historyStack.size() == 1 && historyStack.peek() instanceof Label)) {
            
            // Prüfen, ob das oberste Element im Stack eine ComboBox ist
            if (historyStack.peek() instanceof ComboBox) {
                // Entferne die letzte ComboBox vom Stack und aus der GUI
                Node lastComboBox = historyStack.pop();
                FormelBox.getChildren().remove(lastComboBox);

                // Entferne das letzte Label vom Stack und aus der GUI
                Node lastLabel = historyStack.pop();
                FormelBox.getChildren().remove(lastLabel);
            }
            //##########Entferne das letzte Symbol aus der Zustandsformel, funktioniert noch nicht bei Transitionen
            zustandsformel.entferneLetztenChar();
            System.out.println("######### " + zustandsformel.getFormel_string());

            // Neue leere ComboBox einfügen, um den Zustand vor der letzten Aktion wiederherzustellen
            ComboBox<String> newComboBox = new ComboBox<>();
            configureComboBox(newComboBox, zustandsformel.einlesbare_Symbole());

            FormelBox.getChildren().add(newComboBox);
            historyStack.push(newComboBox);
            newComboBox.setOnAction(this::handleComboBoxAction);

            // Update der Referenz auf die neue ComboBox
            eingabeComboboxRef.set(newComboBox);
        } 

        // Fallback für den Fall, dass nicht genug Elemente im Stack sind
        else {
            System.out.println("Nicht genug Elemente, um eine Rückgängig-Aktion durchzuführen.");
        }
    }

    
    private void färbeKreiseNachZustand(Set<Zustand> lösungsmenge, List<Group> groups) {
        for (Group group : groups) {
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
    
    public void zeige_schritt_für_schritt_lösung(BorderPane root, Zustandsformel zustandsformel,Transitionssystem ts) {
    	CTL_Formel_Baum ctl_baum = new CTL_Formel_Baum(zustandsformel. getStart_der_rekursiven_Definition(),ts);
    	ctl_baum.zeichneBaum(root);
    }
    

    

    public static void main(String[] args) {
        launch(args);
    }
}
