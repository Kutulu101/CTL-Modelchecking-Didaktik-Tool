package GUI;

import CTL_Backend.Transitionssystem;
import CTL_Backend.Zustand;
import CTL_Backend.Zustandsformel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

//Klasse die den Zeichen-Modus startet
public class GUI_zeichen_modus extends Application {
   
	private String selectedFormula;
	//zwei Zeichenflächen
   protected AnchorPane drawingPane1;
   private AnchorPane drawingPane2;
   
   //Variabeln für Vollbildmodus
   Rectangle2D screenBounds;
   protected double total_screen_width;
   protected double total_screen_height;
   
   //Flag für Rückkehr ins Main-Menu
   boolean opened_via_Main_menu = false;
   
   //zum Speicher der auswählbaren Zustandsformeln
   HashSet<Zustandsformel> zustandsformeln = new HashSet();
   
   //Flag die anzeigt ob gerade Relationen eingezeichnet werden können
   protected BooleanProperty draw_relations = new SimpleBooleanProperty(false);
   
   //Vorasugewählte Transitionen
   protected List<String> vorauswahl_transitionen;
   
   //zum Verwalten der angeklickten Kreise beim Relationen zeichnen
   protected Circle firstCircle = null;
   protected Circle secondCircle = null;
   protected Text firstCircle_label = null;
   protected Text secondCircle_label = null;
   
   private Pane firstParentPane = null;
   private Pane secondParentPane = null;
   
   //Flag ob InputField aktiv ist
   boolean inputActive = false;
   
   //Listen zum speichern der GUI Elemente
   private List<Transitionssystem> transitionssystem_liste = new LinkedList();
   protected List<Circle_Group_Builder> circlebuilder_liste = new LinkedList();
   protected List<SidebarHandler> sidebar_handler_list = new LinkedList();
   protected List<Arrow_Builder> arrow_builder_list = new LinkedList();
   protected List<AnchorPane> pane_list = new LinkedList();
   
   //Zum Überwachen der Vorauswahltransitionen
   private boolean isUpdatingText;

   //Kosntruktor der auswählbare Zustandsformel erzeugt
   public GUI_zeichen_modus(boolean via_main) {
      this.opened_via_Main_menu = via_main;
      this.zustandsformeln.add(new Zustandsformel("∀□〈a〉1"));
      this.zustandsformeln.add(new Zustandsformel("∃○〈b〉1"));
      this.zustandsformeln.add(new Zustandsformel("∀□[a]0"));
      this.zustandsformeln.add(new Zustandsformel("□〈a〉¬0"));
      this.zustandsformeln.add(new Zustandsformel("∃□[a]1"));
      this.zustandsformeln.add(new Zustandsformel("∃□〈c〉1∧〈c〉1"));
      this.zustandsformeln.add(new Zustandsformel("∃□[a]1"));
      this.zustandsformeln.add(new Zustandsformel("∃□∃◇(〈a〉∧¬〈b〉1)"));
      this.zustandsformeln.add(new Zustandsformel("∃◇∃□(〈a〉∧¬〈b〉1)"));
      this.zustandsformeln.add(new Zustandsformel("∃(〈a〉1∨〈b〉1)U〈c〉1"));
      this.zustandsformeln.add(new Zustandsformel("∃〈a〉1U〈c〉1)∨(∃〈b〉1U〈c〉1)"));
      this.zustandsformeln.add(new Zustandsformel("∃¬〈a〉1U(〈b〉1∧〈c〉1)"));
      this.zustandsformeln.add(new Zustandsformel("(∃¬〈a〉1U〈b〉1)∧(∃¬〈a〉1U〈c〉1)"));
   }
   
   //notwendiger Kosntruktor für die Erbende Klasse
   public GUI_zeichen_modus() {
      this.opened_via_Main_menu = false;
   }
   
   //Startet Scene
   public void start(Stage primaryStage) {
	   
	   //Basis Pane der GUI
      BorderPane root = new BorderPane();
      
      //Auswahl der CTL-Formel
      Label instructionLabel = new Label("Wähle eine CTL-Formel aus:");
      instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      Label selectedFormulaLabel = new Label();
      selectedFormulaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      ComboBox<String> comboBox = new ComboBox();
      
      //Befüllt ComboBox
      for(Zustandsformel formel:this.zustandsformeln){
         comboBox.getItems().add(formel.getFormel_string());
      }
      
      //Initialisiere Buttons
      VBox comboBoxContainer = new VBox(50.0, new Node[]{instructionLabel, comboBox});
      comboBoxContainer.setAlignment(Pos.CENTER);
      Button btnBeenden = new Button("Programm beenden");
      Button btnneustart = new Button("Programm neustarten");
      Button btnprüfen = new Button("Transitionssysteme prüfen");
      
      //Registriere Events
      
      //Beende Programm kehre zu Main-Menu zurück
      btnBeenden.setOnAction((event) -> {
         if (this.opened_via_Main_menu) {
            Stage stage = new Stage();
            GUI_Main main_menu = new GUI_Main();
            main_menu.start(stage);
         }
         primaryStage.close();
      });
      
      Tooltips_für_Buttons.setTooltip_beenden_Modus(btnBeenden);
      
      //startet das Programm neu
      btnneustart.setOnAction((event) -> {
         try {
        	 //nullt alle bisher eingelesenen und erstellten Elemente
            this.draw_relations.set(false);
            this.firstCircle = null;
            this.secondCircle = null;
            this.firstCircle_label = null;
            this.secondCircle_label = null;

         //	 Iteration über sidebar_handler_liste
            for (SidebarHandler sidebar : sidebar_handler_list) {
                sidebar.removeSidebar(null);
            }

            // Iteration über arrow_builder_list
            for (Arrow_Builder arrow_builder : arrow_builder_list) {
                arrow_builder.clearRelations();
            }

            // Iteration über circlebuilder_liste
            for (Circle_Group_Builder circle_builder : circlebuilder_liste) {
                circle_builder.clearCircleGroups();
            }

            this.pane_list = new LinkedList();
            this.arrow_builder_list = new LinkedList();
            this.sidebar_handler_list = new LinkedList();
            this.circlebuilder_liste = new LinkedList();
            this.inputActive = false;
            
            //schließe Stage
            primaryStage.close();
            Stage newStage = new Stage();
            this.start(newStage);
         } catch (Exception var5) {
            var5.printStackTrace();
         }

      });
      
      Tooltips_für_Buttons.setTooltip_neustart(btnneustart);
      
      //Event um Berechnungen zu prüfen
      btnprüfen.setOnAction((event) -> {
    	  
          //bendet Einzeichnen der Relationen
          this.draw_relations.set(false);
          
          //erzeuge Zustandsformel-Objekt
         Zustandsformel zustandsformel = new Zustandsformel(this.selectedFormula);
         
         
         //Prüfe die Aufgabe
         List<HashSet<Zustand>> ergebnisListe = new LinkedList();
         int nichtLeereSets;
         
         sidebar_handler_list = new LinkedList();
         
         //Prüfe ob ein TS die Gleichung erfüllt und eines nicht
         for(nichtLeereSets = 0; nichtLeereSets < this.pane_list.size(); ++nichtLeereSets) {
        	 
        	 //schließt alle EingabeFelder
            ((Circle_Group_Builder)this.circlebuilder_liste.get(nichtLeereSets)).bereite_berechnung_vor(root);
            
            //erzeugt SidebarHandler
            this.sidebar_handler_list.add(new SidebarHandler((Circle_Group_Builder)this.circlebuilder_liste.get(nichtLeereSets)));
            
            //erzeugt neues Transitionssystem
            Transitionssystem ts = new Transitionssystem(((Arrow_Builder)this.arrow_builder_list.get(nichtLeereSets)).getList_of_relations());
            String extra_beschriftung = "";
            
            //erzeugt Beschriftung für Sidebars
            if (nichtLeereSets > 0) {
               extra_beschriftung = " mit Lösungsmengen für das Transtionssystem auf der linken Seite";
            } else {
               extra_beschriftung = " mit Lösungsmengen für das Transtionssystem auf der rechten Seite";
            }
            
            //erzeugt Sidebar und führt "Färbe Zustände" aus
            ((SidebarHandler)this.sidebar_handler_list.get(nichtLeereSets)).createReducedSidebarRight((AnchorPane)this.pane_list.get(nichtLeereSets), zustandsformel, ts, 0, extra_beschriftung);
            ((SidebarHandler)this.sidebar_handler_list.get(nichtLeereSets)).handleColorButtonClick(((SidebarHandler)this.sidebar_handler_list.get(nichtLeereSets)).getColor_button(), zustandsformel, ts);
            ergebnisListe.add((HashSet)zustandsformel.get_Lösungsmenge(ts));
         }
         
         nichtLeereSets = 0;
         int leereSets = 0;
         
         //zählz nicht leere Sets
         for(HashSet<Zustand> ergebnisSet:ergebnisListe) {
            if (ergebnisSet.isEmpty()) {
               ++leereSets;
            } else {
               ++nichtLeereSets;
            }
         }
         
         //Zeigt Ergebniss als Msg-Box
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle("Ergebnis der Überprüfung");
         if (nichtLeereSets == 1 && leereSets == 1) {
            alert.setHeaderText("Ergebnis: Aufgabe korrekt");
            alert.setContentText("Herzlichen Glückwunsch, die Aufgabe ist korrekt!");
         } else if (leereSets == 2) {
            alert.setHeaderText("Ergebnis: Keine Übereinstimmung");
            alert.setContentText("Leider erfüllt keine Zustand in keinem der Transitionssysteme die CTL-Formel.");
         } else if (nichtLeereSets > 1) {
            alert.setHeaderText("Ergebnis: Mehrfachübereinstimmung");
            alert.setContentText("Leider erfüllen Zustände in beiden Transitionssystemen die CTL-Formel.");
         } else {
            alert.setHeaderText("Ergebnis: Fehlerhafte Eingabe");
            alert.setContentText("Leider war ihre Eingabe unvollständig");
         }

         alert.showAndWait();
      });
      
      Tooltips_für_Buttons.setTooltip_prüfen(btnprüfen);
      
      //füge Buttons zu Layout hinzu
      HBox main_button_box = new HBox(20.0D);
      main_button_box.getChildren().addAll(btnprüfen, btnneustart, btnBeenden);
      main_button_box.setAlignment(Pos.CENTER);
      
      //Vorasuwahl Transitionen
      Label label = new Label("Transitionen vorauswählen (getrennt durch Komma): ");
      TextField eingabeFeld = new TextField();
      
      eingabeFeld.textProperty().addListener((observable, oldValue, newValue) -> {
    	try {
	          if (isUpdatingText) {
	              return; // Verhindert rekursive Aufrufe, wenn der Text programmiert geändert wird
	          }
	          
	  	    if (!newValue.isEmpty()) {
	  	        List<String> transitionen = Arrays.asList(newValue.split(","));
	
	  	        // Überprüfung: Kein Element darf leer sein und es müssen nur einzelne Zeichen sein
	  	        if (transitionen.stream().allMatch(t -> t.trim().length() == 1 && !t.trim().isEmpty())) {
	  	            this.vorauswahl_transitionen = new ArrayList<>(transitionen.stream().map(String::trim).collect(Collectors.toList()));
	  	        } else {
	  	            this.vorauswahl_transitionen.clear();
	
	  	            // Clear asynchron ausführen, um die Exception zu vermeiden
	                // Asynchrone Korrektur, falls nötig
	                Platform.runLater(() -> {
	                    isUpdatingText = true; // Schutzschalter aktivieren
	                    eingabeFeld.clear();
	                    isUpdatingText = false; // Schutzschalter deaktivieren
	                });  	            
	  	            showAlert("Ungültige Eingabe", "Bitte geben Sie nur einzelne Zeichen ein, getrennt durch Komma.",Alert.AlertType.WARNING);
	  	        }
	  	    } else {
	  	    	
	            // Leere Eingabe: Übergangsliste zurücksetzen
	            vorauswahl_transitionen.clear();
	            
	  	    	// Clear asynchron ausführen, um die Exception zu vermeiden
	            Platform.runLater(() -> {
	                isUpdatingText = true; // Schutzschalter aktivieren
	                eingabeFeld.clear();
	                isUpdatingText = false; // Schutzschalter deaktivieren
	            });
	  	    }
    	}catch (IllegalArgumentException e) {
          // Exception ignorieren, da diese Programmablauf nciht beienflusst
      } 
  	});
      Tooltips_für_Buttons.setTooltip_globale_Transition(label);
      
      //Positionieren der Vorauswahl
      HBox eingabeBox = new HBox(10.0D);
      eingabeBox.getChildren().addAll(label, eingabeFeld);
      eingabeBox.setPrefWidth(400.0D);
      eingabeBox.setAlignment(Pos.CENTER);
     
      //Positionieren Vorauswahl und Buttons in BorderPane
      VBox gesamte_box = new VBox(30.0D);
      gesamte_box.getChildren().addAll(selectedFormulaLabel, main_button_box, eingabeBox);
      gesamte_box.setPadding(new Insets(10.0D));
      
      //Combobox Action Event registrieren, nach Auswahl einer Formel wird LAyout erzeugt
      comboBox.setOnAction((e) -> {
         this.selectedFormula = (String)comboBox.getSelectionModel().getSelectedItem();
         if (this.selectedFormula != null) {
            selectedFormulaLabel.setText("Zeichne zwei Transitionssysteme, im Linken soll mindestens ein Zustand die CTL-Formel: " + this.selectedFormula + " erfüllen und im Rechten keiner");
            root.getChildren().remove(comboBoxContainer);
            root.setTop(gesamte_box);
            root.setBottom(this.createDrawingPaneContainer());
         }

      });
     
      //Platziert ersten Combobox Container
      root.setCenter(comboBoxContainer);
      this.screenBounds = Screen.getPrimary().getVisualBounds();
      this.total_screen_width = this.screenBounds.getWidth();
      this.total_screen_height = this.screenBounds.getHeight() - 10.0D;
      Scene scene = new Scene(root, this.total_screen_width, this.total_screen_height);
      primaryStage.setTitle("Zeichenmodus");
      primaryStage.setScene(scene);
      primaryStage.show();
   }
   
   //Hilfsmethode die Container mit formatierten Drawing-Panes zurückgibt
   protected HBox createDrawingPaneContainer() {
	    // Erstellt zwei Zeichenflächen und fügt sie zu einer Liste hinzu.
	    // Die beiden Zeichenflächen werden in einem HBox-Container platziert, formatiert und zentriert wird.
	    this.drawingPane1 = this.createDrawingPane("Zeichenfläche 1", (this.total_screen_width - 100.0) / 2.0, this.total_screen_height - 200.0);
	    this.drawingPane2 = this.createDrawingPane("Zeichenfläche 2", (this.total_screen_width - 100.0) / 2.0, this.total_screen_height - 200.0);
	    this.pane_list.add(this.drawingPane1);
	    this.pane_list.add(this.drawingPane2);
	    
	    HBox drawingPaneContainer = new HBox(10.0, new Node[]{this.drawingPane1, this.drawingPane2});
	    drawingPaneContainer.setStyle("-fx-padding: 10px;");
	    drawingPaneContainer.setAlignment(Pos.CENTER);
	    
	    return drawingPaneContainer;
	}
   
   //Erzeugt eine einzelene Drawing Pane
	AnchorPane createDrawingPane(String labelText, double d, double f) {
	    
		// Initialisiert Builder-Objekte für Pfeile und Kreise, speichert sie in Listen
	    Arrow_Builder arrow_builder = new Arrow_Builder();
	    Circle_Group_Builder circle_builder = new Circle_Group_Builder(arrow_builder);
	    this.circlebuilder_liste.add(circle_builder);
	    this.arrow_builder_list.add(arrow_builder);
	    
	    // Erstellt ein Zeichenbereich
	    Pane drawingPane = new Pane();
	    drawingPane.setStyle("-fx-border-color: black; -fx-background-color: white;");
	    drawingPane.setMouseTransparent(false);
	    
	    // Erstellt ein Label für den Namen der Zeichenfläche 
	    Label label = new Label(labelText);
	    label.setTextAlignment(TextAlignment.CENTER);
	    label.setStyle("-fx-font-size: 14px; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1px;");
	    label.setPadding(new Insets(10.0D));
	    
	    //fügt Buttons hinzu
	    Button btnAddCircle = new Button("Zustand hinzufügen");
	    Button btnRelation = new Button("Transitionen einzeichnen");
	    Button btnTS_entfernen = new Button("TS löschen");
	    HBox buttonBox = new HBox(10.0D);
	    buttonBox.getChildren().addAll(btnAddCircle, btnRelation, btnTS_entfernen);
	    buttonBox.setStyle("-fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1px;");
	    buttonBox.setPadding(new Insets(10.0D));
	    
	    // Listener für die Statusänderung von Beziehungen (Einzeichnen starten/beenden).
	    this.draw_relations.addListener((observable, oldValue, newValue) -> {
	        //nullt angeklickte Elemente
	    	this.firstCircle = null;
	        this.secondCircle = null;
	        this.firstCircle_label = null;
	        this.secondCircle_label = null;
	        
	        //toggled Relations-Button, und färbt Kreise gelb
	        if (this.draw_relations.get()) {
	            btnRelation.setText("Einzeichnen Beenden");
	            circle_builder.colorAllCirclesYellow(drawingPane);
	        } else {
	        	//färbt Kreise und ändert Button-Bschriftung
	        	btnRelation.setText("Transitionen einzeichnen");
		        circle_builder.colorAllCirclesBlue(drawingPane);
		        
	        }
	    });
	    
	    // Löscht alle Elemente aus dem Zeichenbereich und setzt Builder zurück.
	    btnTS_entfernen.setOnAction((event) -> {
	        drawingPane.getChildren().clear();
	        circle_builder.clearCircleGroups();
	        arrow_builder.clearRelations();
	        this.draw_relations.set(false);

	        for(SidebarHandler sidebar: this.sidebar_handler_list) {
	            sidebar.removeSidebar(null);
	        }
	    });
	    
	    Tooltips_für_Buttons.setTooltip_Ts_loeschen(btnTS_entfernen);
	    
	    // Fügt einen neuen Kreis hinzu
	    btnAddCircle.setOnAction((e) -> {
	        Group createdCircle = circle_builder.create_circle_with_text(this.draw_relations);
	        
	        //Event Registrieren
	        createdCircle.setOnMouseClicked((event) -> {
	        	//Hilfsmehtode zum Klicken auf Kreise verwalten
	            this.handleCircleClick(event, arrow_builder);
	        });
	        
	        //Kreis hinzufügen
	        createdCircle.toFront();
	        drawingPane.getChildren().add(createdCircle);
	        
	        //draw-Relations zurücksetzten
	        this.draw_relations.set(false);
	        
	        //entfernt alle Sidebars
	        for(SidebarHandler sidebar:this.sidebar_handler_list) {
	            sidebar.removeSidebar(null);
	        }
	    });
	    
	    Tooltips_für_Buttons.setTooltip_neuerZustand(btnAddCircle);
	    
	    // Aktiviert/deaktiviert das Einzeichnen von Relationen.
	    btnRelation.setOnAction((e) -> {
	    	//toggled draw_relations
	        this.draw_relations.set(!this.draw_relations.get());
	        
	        //entferne alle Sidebars
	        //entfernt alle Sidebars
	        for(SidebarHandler sidebar:this.sidebar_handler_list) {
	            sidebar.removeSidebar(null);
	        }
	        
	        sidebar_handler_list = new LinkedList();
	    });
	    
	    Tooltips_für_Buttons.setTooltip_relation_einzeichen(btnRelation);

	    // Anker-Layout für das Setzen der Elemente im Zeichenbereich.
	    AnchorPane root = new AnchorPane();
	    root.setPrefSize(d, f);
	    AnchorPane.setTopAnchor(buttonBox, 10.0D);
	    AnchorPane.setLeftAnchor(buttonBox, 10.0D);
	    AnchorPane.setRightAnchor(buttonBox, 10.0D);
	    AnchorPane.setTopAnchor(drawingPane, 50.0D);
	    AnchorPane.setLeftAnchor(drawingPane, 10.0D);
	    AnchorPane.setRightAnchor(drawingPane, 10.0D);
	    AnchorPane.setBottomAnchor(drawingPane, 50.0D);
	    AnchorPane.setBottomAnchor(label, 10.0D);
	    AnchorPane.setLeftAnchor(label, 10.0D);
	    AnchorPane.setRightAnchor(label, 10.0D);
	    root.getChildren().addAll(buttonBox, drawingPane, label);
	    return root;
	}

	// Verarbeitet Klicks auf Kreise, um Relationen zwischen zwei Kreisen zu erstellen.
	private void handleCircleClick(MouseEvent event, Arrow_Builder arrow_builder) {
		
		//nur wenn keine Relationen eingezeichnet werden und kein InputField offen ist
	    if (this.draw_relations.get() && !this.inputActive) {
	    	
	    	//extrahiere angeklickte Elemente
	        Group clickedGroup = (Group)event.getSource();
	        Circle clickedCircle = (Circle)clickedGroup.getChildren().get(0);
	        Text clickedCircle_label = (Text)clickedGroup.getChildren().get(1);
	        Pane parentPane = (Pane)clickedGroup.getParent();
	        
	        //Wenn erster Kreis angeklickt wird, speichere zwischen
	        if (this.firstCircle == null) {
	            this.firstCircle = clickedCircle;
	            this.firstCircle_label = clickedCircle_label;
	            this.firstParentPane = parentPane;
	            clickedCircle.setFill(Color.YELLOW);
	        } 
	        //wenn bereits ein Kreis angeklickt wurde
	        else if (this.secondCircle == null) {
	            this.secondCircle = clickedCircle;
	            this.secondCircle_label = clickedCircle_label;
	            this.secondParentPane = parentPane;
	            clickedCircle.setFill(Color.YELLOW);
	            
	            //nur verarbeiten wenn gleiche Pane
	            if (this.firstParentPane == this.secondParentPane) {
	                this.inputActive = true;
	                arrow_builder.drawArrow(parentPane, this.firstCircle, this.secondCircle, this.firstCircle_label, this.secondCircle_label, this.vorauswahl_transitionen);
	                
	            } 
	            //Wenn aus verschiedenen Panes Warning ausgeben
	            else {
	                this.showAlert("Fehler", "Die Kreise befinden sich auf unterschiedlichen Panes!", Alert.AlertType.WARNING);
	                this.firstCircle.setFill(Color.BLACK);
	                this.secondCircle.setFill(Color.BLACK);
	            }
	            
	            //Nullt angeklickte Kreise
	            this.firstCircle = null;
	            this.secondCircle = null;
	            this.firstCircle_label = null;
	            this.secondCircle_label = null;
	            this.firstParentPane = null;
	            this.secondParentPane = null;
	            this.inputActive = false;
	        }
	    }
	}
	

	// Zeigt eine Nachricht in einer Alert-Box an.
	protected void showAlert(String title, String message, Alert.AlertType alertType) {
	    Alert alert = new Alert(alertType);
	    alert.setTitle(title);
	    alert.setHeaderText((String)null);
	    alert.setContentText(message);
	    alert.showAndWait();
	}


   public static void main(String[] args) {
      launch(args);
   }
}