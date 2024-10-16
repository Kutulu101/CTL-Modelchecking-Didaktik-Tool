package GUI;

import CTL_Backend.Zustandsformel;
import CTL_Backend.CTL_Formel_Baum;
import CTL_Backend.Transitionssystem;
import CTL_Backend.Umformung;

import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.text.*;

import javafx.scene.control.ScrollPane;

public class GUI_Main extends Application {
	
    
    //ermöglicht das einzeichenen von Relationen
	private BooleanProperty draw_relations = new SimpleBooleanProperty(false);
	
	//Merker zum Verwalten der Relationseinzeichnung, durch anklicken
    private Circle firstCircle = null;
    private Circle secondCircle = null;
    private Text firstCircle_label = null;
    private Text secondCircle_label = null;
    
    //blockiert Eingaben
    private boolean inputActive = false;
    
    
    
    // Erstelle ein Behälter für die Combo und Labelboxen mit den CTL Symbolen
    HBox FormelBox = new HBox(10);
    
    //erstelle einen Circle_Builder und Arrow Builder, diese wissen welcher App sie dienen
    private Arrow_Builder arrow_builder = new Arrow_Builder();
    private Circle_Group_Builder circle_builder = new Circle_Group_Builder(arrow_builder);
    private Combobox_Handler combobox_handler = new Combobox_Handler(FormelBox);
	
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
        root.setTop(buttonBox);
        
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
        	primaryStage.close();
        });
        
        //Funkiton zum löschen des gezeichnet TS
        btnTS_entfernen.setOnAction(event -> {
            // Alle Kinder (Formen) vom Pane entfernen und Referenzen löschen
            drawingPane.getChildren().clear();
            circle_builder.clearCircleGroups();
            arrow_builder.clearRelations();
        });
       
        // Funktion zum Hinzufügen eines Kreises, enthält Erstellung,Beschriftung, Pfeile
        btnAddCircle.setOnAction(e -> {
        	Group created_circle = circle_builder.create_circle_with_text(draw_relations);//erzeugt eine Gruppe aus Kreis und Beschriftungsfeld
        	created_circle.setOnMouseClicked(event -> handleCircleClick(event, arrow_builder)); //füge das anklicken der Gruppe als Event hinzu
	        drawingPane.getChildren().add(created_circle); // Füge den Kreis zum Pane hinzu
	        draw_relations.set(false);
	        circle_builder.colorAllCircles(drawingPane);
	        btnRelation.setText("Transtionenen einzeichnen");
        });
        
        btnRelation.setOnAction(e -> {
            // Toggle für draw_relations
            draw_relations.set(!draw_relations.get());
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
                // 2. Wenn ja, führen wir die Methode turn_to_normal_form aus
            	//darw modus beenden
            	draw_relations.set(false);
    	        circle_builder.colorAllCircles(drawingPane);
    	        btnRelation.setText("Transtionenen einzeichnen");
    	        
    	        //Normalform
            	combobox_handler.getZustandsformel().turn_to_normal_form();
                System.out.println("Zustandsformel in Normalform: " + combobox_handler.getZustandsformel().getFormel_string_normal_form());
                erstelleUmformungsLabelsUndFügeHinzu(FormelBox,combobox_handler.getZustandsformel(),root);
                
                //berechnen und baum zeichnen
                Transitionssystem transsitionssystem = new Transitionssystem(arrow_builder.getList_of_relations());
                transsitionssystem.printAllZustände();
                combobox_handler.getZustandsformel().print_erfüllende_zustände(transsitionssystem);
                circle_builder.färbeKreiseNachZustand(combobox_handler.getZustandsformel().get_Lösungsmenge(transsitionssystem));
                this.zeige_schritt_für_schritt_lösung(root, combobox_handler.getZustandsformel(),transsitionssystem);
            }
        });
        
        //Undo für die Combobox
        btnUndocomboBox.setOnAction(event -> combobox_handler.undo_combobox());
        
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
        Scene scene = new Scene(root, 1000, 600);
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
                arrow_builder.drawArrow(parentPane,firstCircle,secondCircle,firstCircle_label,secondCircle_label); // Übergabe des Pane an die Methode
                
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
    
    //Zeigt die Schrittfür Schritt-Umformung in Normalform an
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

    
    public void zeige_schritt_für_schritt_lösung(BorderPane root, Zustandsformel zustandsformel,Transitionssystem ts) {
    	CTL_Formel_Baum ctl_baum = new CTL_Formel_Baum(zustandsformel. getStart_der_rekursiven_Definition(),ts);
    	ctl_baum.zeichneBaum(root);
    }
    

    

    public static void main(String[] args) {
        launch(args);
    }
}
