package GUI;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import CTL_Backend.Transitionssystem;


import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.*;


public class GUI_freier_Modus extends Application {
	
	public GUI_freier_Modus(boolean via_main) {
		this.opened_via_Main_menu = via_main;
	}
	
	
	//ob zum Hauptmenü zurückgekehrt werden soll
    boolean opened_via_Main_menu = false;
	
    //ermöglicht das einzeichenen von Relationen
	private BooleanProperty draw_relations = new SimpleBooleanProperty(false);
	
	//Merker zum Verwalten der Relationseinzeichnung, durch anklicken
    private Circle firstCircle = null;
    private Circle secondCircle = null;
    private Text firstCircle_label = null;
    private Text secondCircle_label = null;
    
    //blockiert Eingaben
    private boolean inputActive = false;
    
  //Array zur Speicherung der Vorauswahltransition
    private List<String> vorauswahl_transitionen;
    
    // Erstelle ein Behälter für die Combo und Labelboxen mit den CTL Symbolen
    HBox FormelBox = new HBox(10);
    
    //erstelle einen Circle_Builder und Arrow Builder
    private Arrow_Builder arrow_builder = new Arrow_Builder();
    private Circle_Group_Builder circle_builder = new Circle_Group_Builder(arrow_builder);
    private Combobox_Handler combobox_handler = new Combobox_Handler(FormelBox);
    private SidebarHandler sidebar_handler = new SidebarHandler(circle_builder);
	
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
        Button btnRelation = new Button("Transitionen einzeichnen");
        Button btnUndocomboBox = new Button("Undo Formeleingabe");
        Button btnTS_entfernen = new Button("aktuelles TS löschen");
        Button btnBerechnen = new Button("Berechne Lösungsmengen");
        Button btnNeustart = new Button("Programm Neustarten");
        Button btnBeenden = new Button("Programm beenden");
        buttonBox.getChildren().addAll(btnAddCircle,btnRelation,btnUndocomboBox,btnTS_entfernen,btnBerechnen,btnNeustart, btnBeenden);
        
        
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

	     // Erstellen eines Separators (Linie) zwischen buttonBox und eingabeBox
	     Separator separator = new Separator();
	     separator.setOrientation(Orientation.HORIZONTAL); // Horizontale Linie

	     // Erstellen einer VBox, um buttonBox, Separator und eingabeBox vertikal anzuordnen
	     VBox topBox = new VBox(10); // Abstand zwischen den Boxen
	     topBox.getChildren().addAll(buttonBox, separator, eingabeBox);

	  // Einbinden der VBox in das Layout oben in der root
	     root.setTop(topBox);
	     
        
        //Erstellen der Comboboxen für die Formeleingabe
        combobox_handler.handle_first_combobox(root);
        
        //############################Registrieren der Events##########################
        
        // Listener hinzufügen, um Änderungen an drawRelations zu überwachen
        draw_relations.addListener((observable, oldValue, newValue) -> {
            // Hier wird der Code ausgeführt, wenn drawRelations geändert wird
            firstCircle = null;
            secondCircle = null;
            firstCircle_label = null;
            secondCircle_label = null;
            
            if (draw_relations.get()) {
                // Wenn draw_relations aktiviert ist: Button-Text ändern und Kreise gelb färben
                btnRelation.setText("Einzeichnen Beenden");

                // Alle Nodes mit der Klasse ".circle_with_text" durchsuchen und gelb färben
                root.lookupAll(".circle_with_text").forEach(node -> {
                    Group group = (Group) node;
                    if (group.getChildren().get(0) instanceof Circle) {
                        ((Circle) group.getChildren().get(0)).setFill(Color.YELLOW);
                    }
                });
            } else {
                // Wenn draw_relations deaktiviert ist: Button-Text zurücksetzen und Kreise blau färben
                btnRelation.setText("Einzeichnen Starten");

                // Alle Nodes mit der Klasse ".circle_with_text" durchsuchen und blau färben
                root.lookupAll(".circle_with_text").forEach(node -> {
                    Group group = (Group) node;
                    if (group.getChildren().get(0) instanceof Circle) {
                        ((Circle) group.getChildren().get(0)).setFill(Color.BLUE);
                    }
                });
            }
            
        });
        
        // Funktion zum Beenden des Programms
        btnBeenden.setOnAction(e -> {
        	if(this.opened_via_Main_menu) {
        		Stage stage = new Stage();
                GUI_Main main_menu = new GUI_Main();
                main_menu.start(stage);
        	}
        	primaryStage.close();
        });
        
        //Funkiton zum löschen des gezeichnet TS
        btnTS_entfernen.setOnAction(event -> {
            // Alle Kinder (Formen) vom Pane entfernen und Referenzen löschen
            drawingPane.getChildren().clear();
            circle_builder.clearCircleGroups();
            arrow_builder.clearRelations();
            this.sidebar_handler.removeSidebar();
        });
       
        // Funktion zum Hinzufügen eines Kreises, enthält Erstellung,Beschriftung, Pfeile
        btnAddCircle.setOnAction(e -> {
        	Group created_circle = circle_builder.create_circle_with_text(draw_relations);//erzeugt eine Gruppe aus Kreis und Beschriftungsfeld
        	created_circle.setOnMouseClicked(event -> handleCircleClick(event, arrow_builder)); //füge das anklicken der Gruppe als Event hinzu
	        drawingPane.getChildren().add(created_circle); // Füge den Kreis zum Pane hinzu
	        draw_relations.set(false);
	        circle_builder.colorAllCircles(drawingPane);
	        btnRelation.setText("Transtionenen einzeichnen");
	        this.sidebar_handler.removeSidebar();
        });
        
        btnRelation.setOnAction(e -> {
            // Toggle für draw_relations
            draw_relations.set(!draw_relations.get());
            this.sidebar_handler.removeSidebar();
        });

        
        //Funktion zum berechnen der Normalform und der Lösungsmenge
        btnBerechnen.setOnAction(event -> {
            // 1. Prüfen, ob die Formel mit "Formelende" endet
            if (!combobox_handler.getZustandsformel().getFormel_string().endsWith("Formelende")) {
                // Wenn nein, zeigen wir eine MessageBox an
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warnung");
                alert.setHeaderText(null);
                alert.setContentText("Bitte erst Formelende einlesen.");
                alert.showAndWait();
            } else {
            	
            	//darw modus beenden
            	draw_relations.set(false);
    	        
    	        //Sidebar hinzufügen
    	        Transitionssystem transsitionssystem = new Transitionssystem(arrow_builder.getList_of_relations());
    	        sidebar_handler.createSidebar(FormelBox,combobox_handler.get_transformed_Zustandsformel(),root,transsitionssystem);
    	        root.requestLayout();  // Layout erzwingen
    	        
            }
        });
        
        //Undo für die Combobox
        btnUndocomboBox.setOnAction(event -> {
        		combobox_handler.undo_combobox();
        		this.sidebar_handler.removeSidebar();

        });
        
        //Button zum Neustarten
        btnNeustart.setOnAction(event -> {
            try {
                // Alle Daten und GUI-Elemente zurücksetzen
                draw_relations.set(false);
                firstCircle = null;
                secondCircle = null;
                firstCircle_label = null;
                secondCircle_label = null;
                arrow_builder.clearRelations();
                circle_builder.clearCircleGroups();
                combobox_handler.clear_combobox_handler();
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
        
        



      

        //################################Starten der Szene###############
        Scene scene = new Scene(root, 1100, 600);
        primaryStage.setTitle("Erstelle ein eigenes Transitionssystem und eine eigene CTL-Formel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    //Methode zum behandeln des Kreis anklickens (zeichnet Relationen ein), wurde nciht in Circle_Group_Builder ausgelagert weil das Speciher der Klick-Reihenfolge, nur von der GUi gehandelt werden kann
    private void handleCircleClick(MouseEvent event, Arrow_Builder arrow_builder) {
        
    	if (draw_relations.get() && !inputActive) { // nur wenn im Relation-Zeichnen Modus 
            Group clickedGroup = (Group) event.getSource();
            Circle clickedCircle = (Circle) clickedGroup.getChildren().get(0);
            Text clickedCircle_label = (Text) clickedGroup.getChildren().get(1);

            // Extrahiere das übergeordnete Pane
            Pane parentPane = (Pane) clickedGroup.getParent();

            if (firstCircle == null) {
                // Speichere den ersten Kreis
                firstCircle = clickedCircle;
                firstCircle_label = clickedCircle_label;
                clickedCircle.setFill(Color.YELLOW);
            } else if (secondCircle == null) {
                // Speichere den zweiten Kreis und zeichne den Pfeil
                secondCircle = clickedCircle;
                secondCircle_label = clickedCircle_label;
                clickedCircle.setFill(Color.YELLOW);
                
               	//Guard damit nicht andere Kreise angeklickt werden können
               	inputActive = true;
                
               	//Zeichnet den Pfeil für Relation ein
                arrow_builder.drawArrow(parentPane,firstCircle,secondCircle,firstCircle_label,secondCircle_label,this.vorauswahl_transitionen); // Übergabe des Pane an die Methode
                
                // Setze die Kreise zurück, um weitere Pfeile zeichnen zu können
                //setzt die Kreise zurück:                 
                firstCircle = null;
                secondCircle = null;
                firstCircle_label = null;
                secondCircle_label = null;
                
                //gibt wieder frei
                inputActive = false;
            }
        }
    	
    }
    
        

    public static void main(String[] args) {
        launch(args);
    }
}