package GUI;

import CTL_Backend.Transitionssystem;
import CTL_Backend.Zustand;
import CTL_Backend.Zustandsformel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class GUI_Inequiv extends GUI_zeichen_modus {

    private String selectedFormula;
    private HashSet<EquationPair> zustandsformeln;

    public GUI_Inequiv(boolean via_main) {
        // Setzt die Variable auf Basis des Parameters via_main
        this.opened_via_Main_menu = via_main;

        // Initialisierung des HashSets mit EquationPair-Objekten
        initializeZustandsformeln();
    }

    private void initializeZustandsformeln() {
        zustandsformeln = new HashSet<>();
        zustandsformeln.add(new EquationPair("∃□∃◇(〈a〉1∧¬〈b〉1)", "∃◇∃□(〈a〉1∧¬〈b〉1)"));
        zustandsformeln.add(new EquationPair("∃(〈a〉1∨〈b〉1)U〈c〉1", "(∃〈a〉1U〈c〉1)∨(∃〈b〉1 U〈c〉1)"));
        zustandsformeln.add(new EquationPair("∃¬〈a〉1U(〈b〉1∧〈c〉1)", "(∃¬〈a〉1U〈b〉1)∧(∃¬〈a〉1U〈c〉1)"));
        zustandsformeln.add(new EquationPair( "∃□〈a〉1","∃□[a]1"));
    }
   
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Label über der ComboBox
        Label instructionLabel = new Label("Wähle ein CTL-Formel-Paar aus:");
        instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Label für die Anzeige der gewählten Formel
        Label selectedFormulaLabel = new Label();
        selectedFormulaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ComboBox für die Auswahl der Formeln
        ComboBox<String> comboBox = new ComboBox<>();
        for (EquationPair formel : zustandsformeln) {
            comboBox.getItems().add(formel.toString());
        }
        
        // ComboBox und Instruktionslabel in ein VBox packen
        VBox comboBoxContainer = new VBox(50, instructionLabel, comboBox);
        comboBoxContainer.setAlignment(Pos.CENTER);
        
        
        Button btnBeenden = new Button("Programm beenden");
        Button btnneustart = new Button("Programm neustarten");
        Button btnprüfen = new Button("Transitionssystem prüfen");
        
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
                
                
                for(SidebarHandler sidebar:this.sidebar_handler_list) {
                	sidebar.removeSidebar();
                }  
                
                for(Arrow_Builder arrow_builder:arrow_builder_list) {
                	arrow_builder.clearRelations();
                }
                
                
                for(Circle_Group_Builder circle_builder:circlebuilder_liste) {
                	circle_builder.clearCircleGroups();
                }
                
                this.arrow_builder_list = new LinkedList<>();
                this.sidebar_handler_list = new LinkedList<>();
                this.circlebuilder_liste = new LinkedList<>();
                this.pane_list = new LinkedList<>();
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
	
	     //Erstellen der HBox für Label und Textfeld und Begrenzung auf 400 Pixel Breite
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
                selectedFormulaLabel.setText("Zeichne ein Transitionssysteme, welches die linke Gleichung erfüllt, aber nicht die Rechte: "+ selectedFormula);
                selectedFormulaLabel.setAlignment(Pos.CENTER);
                root.getChildren().remove(comboBoxContainer);//entferne die Combobox
                root.setTop(gesamte_box); // Setze das Label oben
                root.setBottom(createDrawingPaneContainer()); // Setze die Zeichenflächen unten
            }
        });
        
        //Berechne ob eine Zustandsformel erfüllt wird und die zweite nicht
        btnprüfen.setOnAction(event -> {
        	
        	//eingabefelder schließen
        	this.circlebuilder_liste.get(0).schliesseAlleEingabefelder(root);
        	
        	draw_relations.set(false);
        	
            Zustandsformel zustandsformel_links = new Zustandsformel(selectedFormula.split(" ≡ ")[0]);
            Zustandsformel zustandsformel_rechts =new Zustandsformel(selectedFormula.split(" ≡ ")[1]);
            
            //erstelle zwei Sidebar Handler Sidebarhandler
            this.sidebar_handler_list.add(new SidebarHandler(this.circlebuilder_liste.get(0)));
            this.sidebar_handler_list.add(new SidebarHandler(this.circlebuilder_liste.get(0)));
            
            
            
            Transitionssystem ts = new Transitionssystem(arrow_builder_list.get(0).getList_of_relations());
            this.sidebar_handler_list.get(0).createReducedSidebarRight(this.pane_list.get(0), zustandsformel_rechts, ts,200);
            this.sidebar_handler_list.get(1).createReducedSidebarLeft(this.pane_list.get(0), zustandsformel_links, ts,400);
            
           HashSet<Zustand> lösungsmenge_rechts = (HashSet<Zustand>) zustandsformel_rechts.get_Lösungsmenge(ts);
           HashSet<Zustand> lösungsmenge_links = (HashSet<Zustand>) zustandsformel_links.get_Lösungsmenge(ts);
            
            
            // Erstellen der passenden Infobox
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ergebnis der Überprüfung");
            
            if (lösungsmenge_links.size() >0 && lösungsmenge_rechts.size() == 0) {
                alert.setHeaderText("Ergebnis: Aufgabe korrekt");
                alert.setContentText("Herzlichen Glückwunsch, die Aufgabe ist korrekt!");
            } else if (lösungsmenge_links.size() ==0 && lösungsmenge_rechts.size() == 0) {
                alert.setHeaderText("Ergebnis: Keine Übereinstimmung");
                alert.setContentText("Leider erfüllt kein Zustand der Transitionssysteme die CTL-Formel.");
            } else if (lösungsmenge_links.size() >0 && lösungsmenge_rechts.size() >0) {
                alert.setHeaderText("Ergebnis: Mehrfachübereinstimmung");
                alert.setContentText("Leider erfüllen Zustände im Tansitionssysteme beide CTL-Formeln.");
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
        primaryStage.setTitle("CTL-Inequivalenzen");
     
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    protected HBox createDrawingPaneContainer() {
    	
        drawingPane1 = createDrawingPane("Zeichenfläche 1",(total_screen_width-100),total_screen_height - 200);

        this.pane_list.add(drawingPane1);
        
        HBox drawingPaneContainer = new HBox(10, drawingPane1);
        drawingPaneContainer.setStyle("-fx-padding: 10px;");
        drawingPaneContainer.setAlignment(Pos.CENTER);
        return drawingPaneContainer;
    }


    public static void main(String[] args) {
        launch(args);
    }
}

	//Klasse zur Repräsentation eines Paars aus linker und rechter Seite einer Gleichung
	class EquationPair {
	 private final String leftSide;
	 private final String rightSide;
	
	 public EquationPair(String leftSide, String rightSide) {
	     this.leftSide = leftSide;
	     this.rightSide = rightSide;
	 }
	
	 public String getLeftSide() {
	     return leftSide;
	 }
	
	 public String getRightSide() {
	     return rightSide;
	 }
	 
	    // Methode, um die Gleichung als String mit "≡" zu drucken
	    @Override
	    public String toString() {
	        return leftSide + " ≡ " + rightSide;
	    }
}
