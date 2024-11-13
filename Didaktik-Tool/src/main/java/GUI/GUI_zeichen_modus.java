package GUI;

import CTL_Backend.Transitionssystem;
import CTL_Backend.Zustand;
import CTL_Backend.Zustandsformel;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class GUI_zeichen_modus extends Application {
	
	private String selectedFormula;
	protected AnchorPane drawingPane1;
	private AnchorPane drawingPane2;
	Rectangle2D screenBounds;
	protected double total_screen_width;
	protected double total_screen_height;
	
	
	public GUI_zeichen_modus(boolean via_main) {
		
		this.opened_via_Main_menu = via_main;
		
        zustandsformeln.add(new Zustandsformel("∀□〈a〉1"));
        zustandsformeln.add(new Zustandsformel("∃○〈b〉1"));
        zustandsformeln.add(new Zustandsformel("∀□[a]0"));
        zustandsformeln.add(new Zustandsformel("□〈a〉¬0"));
        zustandsformeln.add(new Zustandsformel("∃□[a]1"));
        zustandsformeln.add(new Zustandsformel("∃□〈c〉1∧〈c〉1"));
        zustandsformeln.add(new Zustandsformel("∃□[a]1"));
        zustandsformeln.add(new Zustandsformel("∃□∃◇(〈a〉∧¬〈b〉1)"));
        zustandsformeln.add(new Zustandsformel("∃◇∃□(〈a〉∧¬〈b〉1)"));
        zustandsformeln.add(new Zustandsformel("∃(〈a〉1∨〈b〉1)U〈c〉1"));
        zustandsformeln.add(new Zustandsformel("∃〈a〉1U〈c〉1)∨(∃〈b〉1U〈c〉1)"));
        zustandsformeln.add(new Zustandsformel("∃¬〈a〉1U(〈b〉1∧〈c〉1)"));
        zustandsformeln.add(new Zustandsformel("(∃¬〈a〉1U〈b〉1)∧(∃¬〈a〉1U〈c〉1)"));
	}
	
	public GUI_zeichen_modus(){
		this.opened_via_Main_menu = false;
	}
	
	
	//ob zum Hauptmenü zurückgekehrt werden soll
    boolean opened_via_Main_menu = false;

	HashSet<Zustandsformel> zustandsformeln = new HashSet<>();
    
    //ermöglicht das einzeichenen von Relationen
    protected BooleanProperty draw_relations = new SimpleBooleanProperty(false);
    
    //Array zur Speicherung der Vorauswahltransition
    protected List<String> vorauswahl_transitionen;
	
	//Merker zum Verwalten der Relationseinzeichnung, durch anklicken
    protected Circle firstCircle = null;
    protected Circle secondCircle = null;
    protected Text firstCircle_label = null;
    protected Text secondCircle_label = null;
    private Pane firstParentPane = null;
    private Pane secondParentPane = null;
    
    //blockiert Eingaben
    boolean inputActive = false;
    
    //Lsiten zum Verwalten der Panes,Sidebars und Ts
    private List<Transitionssystem> transitionssystem_liste = new LinkedList<>();
    protected List<Circle_Group_Builder> circlebuilder_liste = new LinkedList<>();
    protected List<SidebarHandler> sidebar_handler_list = new LinkedList<>();
    protected List<Arrow_Builder> arrow_builder_list = new LinkedList<>();
    protected List<AnchorPane> pane_list = new LinkedList<>();


    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Label über der ComboBox
        Label instructionLabel = new Label("Wähle eine CTL-Formel aus:");
        instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Label für die Anzeige der gewählten Formel
        Label selectedFormulaLabel = new Label();
        selectedFormulaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ComboBox für die Auswahl der Formeln
        ComboBox<String> comboBox = new ComboBox<>();
        for (Zustandsformel formel : zustandsformeln) {
            comboBox.getItems().add(formel.getFormel_string());
        }
        
        // ComboBox und Instruktionslabel in ein VBox packen
        VBox comboBoxContainer = new VBox(50, instructionLabel, comboBox);
        comboBoxContainer.setAlignment(Pos.CENTER);
        
        
        Button btnBeenden = new Button("Programm beenden");
        Button btnneustart = new Button("Programm neustarten");
        Button btnprüfen = new Button("Transitionssysteme prüfen");
        
     // Funktion zum Beenden des Programms
        btnBeenden.setOnAction(event -> {
        	if(this.opened_via_Main_menu) {
        		Stage stage = new Stage();
                GUI_Main main_menu = new GUI_Main();
                main_menu.start(stage);
        	}
        	primaryStage.close();
        });
        
        btnneustart.setOnAction(event -> {
            try {
                // Alle Daten und GUI-Elemente zurücksetzen
                draw_relations.set(false);
                firstCircle = null;
                secondCircle = null;
                firstCircle_label = null;
                secondCircle_label = null;
                
                for(SidebarHandler sidebar:sidebar_handler_list) {
                	sidebar.removeSidebar();
                }
                for(Arrow_Builder arrow_builder:arrow_builder_list) {
                	arrow_builder.clearRelations();
                }
                for(Circle_Group_Builder circle_builder:circlebuilder_liste) {
                	circle_builder.clearCircleGroups();
                }
                
                this.pane_list = new LinkedList<>();
                arrow_builder_list = new LinkedList<Arrow_Builder>();
                sidebar_handler_list = new LinkedList<SidebarHandler>();
                circlebuilder_liste = new LinkedList<Circle_Group_Builder>();
                
                
                inputActive = false;
                
                // Stage schließen
                primaryStage.close();
                
                // Neuen Stage erstellen und initialisieren
                Stage newStage = new Stage();
                start(newStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        
        HBox main_button_box = new HBox(20);
        main_button_box.getChildren().addAll(btnprüfen,btnneustart,btnBeenden);
        main_button_box.setAlignment(Pos.CENTER);
        
	     // Vorauswahl der Transitionen für das Transitionssystem
	     // Label und Textfeld erstellen
	     Label label = new Label("Transitionen vorauswählen (getrennt durch Komma): ");
	     TextField eingabeFeld = new TextField();
	
	     // Textfeld-Eingabe beibehalten und in Array aufteilen
	     ((javafx.scene.control.TextField) eingabeFeld).textProperty().addListener((observable, oldValue, newValue) -> {
	    	    if (!newValue.isEmpty()) {
	    	        vorauswahl_transitionen = new ArrayList<>(Arrays.asList(newValue.split(",")));
	    	    } else {
	    	        vorauswahl_transitionen = new ArrayList<>(); // Leere Liste zuweisen
	    	    }
	     });
	
	  // Erstellen der HBox für Label und Textfeld und Begrenzung auf 400 Pixel Breite
	     HBox eingabeBox = new HBox(10); // Abstand zwischen Label und Textfeld
	     eingabeBox.getChildren().addAll(label, eingabeFeld);
	     eingabeBox.setPrefWidth(400); // Begrenzung der Breite auf 400 Pixel
	     eingabeBox.setAlignment(Pos.CENTER);

        VBox gesamte_box = new VBox(30);
        gesamte_box.getChildren().addAll(selectedFormulaLabel,main_button_box,eingabeBox);
        
        //Setze Padding
        gesamte_box.setPadding(new Insets(10));
        
        // Listener für ComboBox-Auswahl
        comboBox.setOnAction(e -> {
            selectedFormula = comboBox.getSelectionModel().getSelectedItem();
            if (selectedFormula != null) {
                selectedFormulaLabel.setText("Zeichne zwei Transitionssysteme von denen eins die CTL-Formel: " + selectedFormula + " erfüllen soll und eins nicht");
                root.getChildren().remove(comboBoxContainer);//entferne die Combobox
                root.setTop(gesamte_box); // Setze das Label oben
                root.setBottom(createDrawingPaneContainer()); // Setze die Zeichenflächen unten
            }
        });
        
        //Berechung für beide Panes
        btnprüfen.setOnAction(event -> {
        	
        	draw_relations.set(false);
        	
            Zustandsformel zustandsformel = new Zustandsformel(selectedFormula);
            
            List<HashSet<Zustand>> ergebnisListe = new LinkedList<>();
            
            // Für alle Transitionensysteme berechnen und Ergebnisse speichern
            for (int i = 0; i < pane_list.size(); i++) {
            	
            	this.circlebuilder_liste.get(i).schliesseAlleEingabefelder(root);
            	
            	sidebar_handler_list.add(new SidebarHandler(this.circlebuilder_liste.get(i)));
                Transitionssystem ts = new Transitionssystem(arrow_builder_list.get(i).getList_of_relations());
                sidebar_handler_list.get(i).createReducedSidebarRight(pane_list.get(i), zustandsformel, ts,200);
              //färben
                sidebar_handler_list.get(i).handleButtonClick(sidebar_handler_list.get(i).getColor_button(),zustandsformel, ts);
                
                // Berechnungsergebnis hinzufügen
                ergebnisListe.add((HashSet<Zustand>) zustandsformel.get_Lösungsmenge(ts));
            }
            
            // Variablen zum Zählen der nicht-leeren Sets und leeren Sets
            int nichtLeereSets = 0;
            int leereSets = 0;
            
            // Überprüfen, welche Sets leer und welche nicht leer sind
            for (HashSet<Zustand> ergebnisSet : ergebnisListe) {
                if (ergebnisSet.isEmpty()) {
                    leereSets++;
                } else {
                    nichtLeereSets++;
                }
            }
            
            // Erstellen der passenden Infobox
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ergebnis der Überprüfung");
            
            if (nichtLeereSets == 1 && leereSets == 1) {
                alert.setHeaderText("Ergebnis: Aufgabe korrekt");
                alert.setContentText("Herzlichen Glückwunsch, die Aufgabe ist korrekt!");
            } else if (leereSets == 2) {
                alert.setHeaderText("Ergebnis: Keine Übereinstimmung");
                alert.setContentText("Leider erfüllt keines der Transitionssysteme die CTL-Formel.");
            } else if (nichtLeereSets > 1) {
                alert.setHeaderText("Ergebnis: Mehrfachübereinstimmung");
                alert.setContentText("Leider erfüllen beide Transitionssysteme die CTL-Formel.");
            }
            else {
            	alert.setHeaderText("Ergebnis: Fehlerhafte Eingabe");
                alert.setContentText("Leider war ihre Eingabe unvollständig");
            }
            
            // Infobox anzeigen
            alert.showAndWait();
            
        });

        //ComboBox hinzufügen
        root.setCenter(comboBoxContainer); // Platzierung der VBox in der Mitte
        
     // Bildschirmgröße holen
        screenBounds = Screen.getPrimary().getVisualBounds();
        total_screen_width = screenBounds.getWidth();
        total_screen_height = screenBounds.getHeight()-10;
        
        // Szene erstellen und Stage anzeigen
        Scene scene = new Scene(root, total_screen_width, total_screen_height);
        primaryStage.setTitle("Zeichenmodus");
     
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Methode zur Erstellung der Zeichenflächen mit Trennlinie
    protected HBox createDrawingPaneContainer() {
        drawingPane1 = createDrawingPane("Zeichenfläche 1",(total_screen_width-100)/2,total_screen_height - 200);
        drawingPane2 = createDrawingPane("Zeichenfläche 2",(total_screen_width-100)/2,total_screen_height -200);
        
        this.pane_list.add(drawingPane1);
        this.pane_list.add(drawingPane2);

        HBox drawingPaneContainer = new HBox(10, drawingPane1, drawingPane2);
        drawingPaneContainer.setStyle("-fx-padding: 10px;");
        drawingPaneContainer.setAlignment(Pos.CENTER);
        return drawingPaneContainer;
    }

    // Methode zur Erstellung einer Zeichenfläche mit Beschriftung
    AnchorPane createDrawingPane(String labelText, double d, double f) {

        // Erstelle einen Circle_Builder und Arrow Builder
        Arrow_Builder arrow_builder = new Arrow_Builder();
        Circle_Group_Builder circle_builder = new Circle_Group_Builder(arrow_builder);
        this.circlebuilder_liste.add(circle_builder);
        this.arrow_builder_list.add(arrow_builder);

        // Haupt-Paneel zur Zeichnung
        Pane drawingPane = new Pane();
        drawingPane.setStyle("-fx-border-color: black; -fx-background-color: white;");
        drawingPane.setMouseTransparent(false);

        // Beschriftung unten
        Label label = new Label(labelText);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-font-size: 14px; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1px;");
        label.setPadding(new Insets(10)); // Padding hinzufügen für bessere Optik

        // Buttons erstellen
        Button btnAddCircle = new Button("Zustand hinzufügen");
        Button btnRelation = new Button("Transitionen einzeichnen");
        Button btnTS_entfernen = new Button("TS löschen");

        // HBox zur Organisation der Buttons oben
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(btnAddCircle, btnRelation, btnTS_entfernen);
        buttonBox.setStyle("-fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1px;"); // Stil für die Button-Box
        buttonBox.setPadding(new Insets(10)); // Padding hinzufügen

        // Relations-Listener für die Aktualisierung
        draw_relations.addListener((observable, oldValue, newValue) -> {
            firstCircle = null;
            secondCircle = null;
            firstCircle_label = null;
            secondCircle_label = null;

            if (draw_relations.get()) {
                btnRelation.setText("Einzeichnen Beenden");
                drawingPane.lookupAll(".circle_with_text").forEach(node -> {
                    Group group = (Group) node;
                    if (group.getChildren().get(0) instanceof Circle) {
                        ((Circle) group.getChildren().get(0)).setFill(Color.YELLOW);
                    }
                });
            } else {
                btnRelation.setText("Einzeichnen Starten");
                drawingPane.lookupAll(".circle_with_text").forEach(node -> {
                    Group group = (Group) node;
                    if (group.getChildren().get(0) instanceof Circle) {
                        ((Circle) group.getChildren().get(0)).setFill(Color.BLUE);
                    }
                });
            }
        });

        // Eventhandler für die Buttons
        btnTS_entfernen.setOnAction(event -> {
            drawingPane.getChildren().clear();
            circle_builder.clearCircleGroups();
            arrow_builder.clearRelations();
            for(SidebarHandler sidebar :this.sidebar_handler_list) {
            	sidebar.removeSidebar();
            }
        });

        btnAddCircle.setOnAction(e -> {
            Group createdCircle = circle_builder.create_circle_with_text(draw_relations);
            createdCircle.setOnMouseClicked(event -> handleCircleClick(event, arrow_builder));
            createdCircle.toFront();
            drawingPane.getChildren().add(createdCircle);
            draw_relations.set(false);
            circle_builder.colorAllCircles(drawingPane);
            btnRelation.setText("Transitionen einzeichnen");
            for(SidebarHandler sidebar :this.sidebar_handler_list) {
            	sidebar.removeSidebar();
            }
        });

        btnRelation.setOnAction(e -> {
        	draw_relations.set(!draw_relations.get());
            for(SidebarHandler sidebar :this.sidebar_handler_list) {
            	sidebar.removeSidebar();
            }
        });

        // AnchorPane zur Platzierung der Komponenten
        AnchorPane root = new AnchorPane();
        root.setPrefSize(d, f);

        // Positionieren der Komponenten in AnchorPane
        AnchorPane.setTopAnchor(buttonBox, 10.0);
        AnchorPane.setLeftAnchor(buttonBox, 10.0);
        AnchorPane.setRightAnchor(buttonBox, 10.0);

        AnchorPane.setTopAnchor(drawingPane, 50.0);
        AnchorPane.setLeftAnchor(drawingPane, 10.0);
        AnchorPane.setRightAnchor(drawingPane, 10.0);
        AnchorPane.setBottomAnchor(drawingPane, 50.0);

        AnchorPane.setBottomAnchor(label, 10.0);
        AnchorPane.setLeftAnchor(label, 10.0);
        AnchorPane.setRightAnchor(label, 10.0);

        // Komponenten zum root-AnchorPane hinzufügen
        root.getChildren().addAll(buttonBox, drawingPane, label);

        return root;
    }


    
   
 // Methode zum Behandeln des Kreisanklickens (zeichnet Relationen ein)
    private void handleCircleClick(MouseEvent event, Arrow_Builder arrow_builder) {

        if (draw_relations.get() && !inputActive) { // Nur wenn im Relation-Zeichnen-Modus
            Group clickedGroup = (Group) event.getSource();
            Circle clickedCircle = (Circle) clickedGroup.getChildren().get(0);
            Text clickedCircle_label = (Text) clickedGroup.getChildren().get(1);

            // Extrahiere das übergeordnete Pane
            Pane parentPane = (Pane) clickedGroup.getParent();

            if (firstCircle == null) {
                // Speichere den ersten Kreis
                firstCircle = clickedCircle;
                firstCircle_label = clickedCircle_label;
                firstParentPane = parentPane; // Speichere auch das übergeordnete Pane
                clickedCircle.setFill(Color.YELLOW);
            } else if (secondCircle == null) {
                // Speichere den zweiten Kreis
                secondCircle = clickedCircle;
                secondCircle_label = clickedCircle_label;
                secondParentPane = parentPane; // Speichere auch das übergeordnete Pane
                clickedCircle.setFill(Color.YELLOW);

                // Überprüfe, ob beide Kreise auf demselben Pane liegen
                if (firstParentPane == secondParentPane) {
                    // Guard, damit nicht andere Kreise angeklickt werden können
                    inputActive = true;

                    // Zeichnet den Pfeil für Relation ein
                    arrow_builder.drawArrow(parentPane, firstCircle, secondCircle, firstCircle_label, secondCircle_label,this.vorauswahl_transitionen);

                } else {
                    // Kreise liegen auf verschiedenen Panes -> zeige Warnung und setze zurück
                    showAlert("Fehler", "Die Kreise befinden sich auf unterschiedlichen Panes!", Alert.AlertType.WARNING);

                    // Setze die Füllung der Kreise zurück
                    firstCircle.setFill(Color.BLACK);  // Ursprüngliche Farbe wiederherstellen
                    secondCircle.setFill(Color.BLACK); // Ursprüngliche Farbe wiederherstellen
                }

                // Setze die Kreise zurück, um weitere Pfeile zeichnen zu können
                firstCircle = null;
                secondCircle = null;
                firstCircle_label = null;
                secondCircle_label = null;
                firstParentPane = null;
                secondParentPane = null;

                // Gib wieder frei
                inputActive = false;
            }
        }
    }

    // Methode zum Anzeigen der Warnmeldung
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}
