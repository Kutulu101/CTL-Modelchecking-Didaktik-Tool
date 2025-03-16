   package GUI;


import CTL_Backend.Transitionssystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

//Klasse die den freien Modus steuert und User-Eingaben verwaltet
public class GUI_freier_Modus extends Application {
	
	//Variable ob zum Main-Menu zurückgekehrt werden soll
   boolean opened_via_Main_menu = false;
   
   //Observable welches verwaltet ob Zustände hinzugefügt oder Relationen gezeichnet werden können
   private BooleanProperty draw_relations = new SimpleBooleanProperty(false);
   
   //Handlen das Klicken von auf Kreise um Relationen zu zeichnen
   private Circle firstCircle = null;
   private Circle secondCircle = null;
   private Text firstCircle_label = null;
   private Text secondCircle_label = null;
   
   //Ob Eingabefleder aktiv sind
   private boolean inputActive = false;
   
   //Zur VOrauswahl von Transitionen
   private List<String> vorauswahl_transitionen;
   
   //Für Label und Comboboxen
   HBox FormelBox = new HBox(10.0D);
   
   //Instanzen die GUI Elemente verwalten
   private Arrow_Builder arrow_builder = new Arrow_Builder();
   private Circle_Group_Builder circle_builder;
   private Combobox_Handler combobox_handler;
   private SidebarHandler sidebar_handler;
   
   //Bildschrimgröße
   Rectangle2D screenBounds;
   protected double total_screen_width;
   protected double total_screen_height;
   
   //Antwort des Bedieners welche Zustände erfüllen
   String selectedStates = "";
   
 //Pane für Hintergrund
   BorderPane root;
   
   //Flag zum Überwachen der Vorauswahl
   private boolean isUpdatingText = false; 

   public GUI_freier_Modus(boolean via_main) {
	   
	  //Initlalisiert die Verwalter der GUI-ELemente
      this.circle_builder = new Circle_Group_Builder(this.arrow_builder);
      this.combobox_handler = new Combobox_Handler(this.FormelBox);
      this.sidebar_handler = new SidebarHandler(this.circle_builder);
      this.opened_via_Main_menu = via_main;
   }

   public void start(Stage primaryStage) {
	   
	  //Pane für Hintergrund
      root = new BorderPane();
      
      //Pane auf der Transiitonsystem gezeichnet wird
      Pane drawingPane = new Pane();
      drawingPane.setStyle("-fx-background-color: lightgray;");
      root.setCenter(drawingPane);
      
      //ButtonBox für Eingaben
      HBox buttonBox = new HBox(10.0D);
      Button btnAddCircle = new Button("Zustand hinzufügen");
      Button btnRelation = new Button("Transitionen einzeichnen");
      Button btnUndocomboBox = new Button("Undo Formeleingabe");
      Button btnTS_entfernen = new Button("aktuelles TS löschen");
      Button btnBerechnen = new Button("Berechne Lösungsmengen");
      Button btnNeustart = new Button("Programm Neustarten");
      Button btnBeenden = new Button("Programm beenden");
      
      
     

      buttonBox.getChildren().addAll(btnAddCircle, btnRelation, btnUndocomboBox, btnTS_entfernen, btnBerechnen, btnNeustart, btnBeenden);
      
      //Trnasitonen vorauswählen ermöglich es dem Benutzer die Trnasitionen die eingebenn werden könne  zu reduzieren
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
	    	            combobox_handler.setVorauswahl_transitionen(vorauswahl_transitionen);
	    	        } else {
	    	            this.vorauswahl_transitionen.clear();
	    	            combobox_handler.setVorauswahl_transitionen(vorauswahl_transitionen);
	
	    	            // Clear asynchron ausführen, um die Exception zu vermeiden
	    	            Platform.runLater(() -> {
	                        isUpdatingText = true; // Schutzschalter aktivieren
	                        eingabeFeld.clear();
	                        isUpdatingText = false; // Schutzschalter deaktivieren
	                    });
	    	            
	    	            showAlert("Ungültige Eingabe", "Bitte geben Sie nur einzelne Zeichen ein, getrennt durch Komma.");
	    	        }
	    	    } else {
	    	    	
	                // Leere Eingabe: Übergangsliste zurücksetzen
	                vorauswahl_transitionen.clear();
	                combobox_handler.setVorauswahl_transitionen(vorauswahl_transitionen);
	    	    	
	    	    	// Clear asynchron ausführen, um die Exception zu vermeiden
	    	    	 Platform.runLater(() -> {
	                     isUpdatingText = true; // Schutzschalter aktivieren
	                     eingabeFeld.clear();
	                     isUpdatingText = false; // Schutzschalter deaktivieren
	                 });
	    	        combobox_handler.setVorauswahl_transitionen(vorauswahl_transitionen);
	    	    }
    	  }catch (IllegalArgumentException e) {
          // Exception ignorieren, da diese Programmablauf nciht beienflusst
    	  }
    	});
      
      Tooltips_für_Buttons.setTooltip_globale_Transition(label);
      
      HBox eingabeBox = new HBox(10.0);
      eingabeBox.getChildren().addAll(label, eingabeFeld);
      eingabeBox.setPrefWidth(400.0);
      
      //Formatieren des LAyouts von ButtonBox und EingabeBox
      Separator separator = new Separator();
      separator.setOrientation(Orientation.HORIZONTAL);
      VBox topBox = new VBox(10.0D);
      topBox.getChildren().addAll(buttonBox, separator, eingabeBox);
      topBox.setPadding(new Insets(10.0D));
      root.setTop(topBox);
      
      //Startet die Comboboxen-Eingabe Meachnik
      this.combobox_handler.handle_first_combobox(root);
      
      //Listner zu draw Relations um zwischen den Eingabemöglichkeiten zu wechseln
      this.draw_relations.addListener((ChangeListener<? super Boolean>) (observable, oldValue, newValue) -> {
         //Beu Wechsel der Modi werden evtl. bereits angeklickte Kreise genullt
    	 this.firstCircle = null;
         this.secondCircle = null;
         this.firstCircle_label = null;
         this.secondCircle_label = null;
         
         //Toggled die Buttons
         if (this.draw_relations.get()) {
            btnRelation.setText("Einzeichnen Beenden");          
            this.circle_builder.colorAllCirclesYellow(drawingPane);
         } else {
            btnRelation.setText("Einzeichnen Starten");
            //färbe alle Kreise Blau
            this.circle_builder.colorAllCirclesBlue(drawingPane);
         }

      });
      
      //Events für die Buttons registrieren
      //Beenden des Programmes
      btnBeenden.setOnAction((e) -> {
    	  
    	  //Wenn über Main-Menu aufgerufen Rückkehr zum Main Menu
         if (this.opened_via_Main_menu) {
            Stage stage = new Stage();
            GUI_Main main_menu = new GUI_Main();
            main_menu.start(stage);
         }

         primaryStage.close();
      });
      
      Tooltips_für_Buttons.setTooltip_beenden_Modus(btnBeenden);
      
      //Entferent das aktuell TS
      btnTS_entfernen.setOnAction((event) -> {
         drawingPane.getChildren().clear();
         this.circle_builder.clearCircleGroups();
         this.arrow_builder.clearRelations();
         this.sidebar_handler.removeSidebar(null);
      });
      
      Tooltips_für_Buttons.setTooltip_Ts_loeschen(btnTS_entfernen);
      
      //Fügt Kreis/Zustand hinzu
      btnAddCircle.setOnAction((e) -> {
         Group created_circle = this.circle_builder.create_circle_with_text(this.draw_relations);
         
         //Registriere das klicken auf den Circle als Event
         //Passiert hier und nicht in Circle-Builder, da dieses Event von anderen Variablen der GUI abhängt
         created_circle.setOnMouseClicked((event) -> {
            this.handleCircleClick(event, this.arrow_builder);
         });
         
         //Füge Kreis hinzu
         drawingPane.getChildren().add(created_circle);
         //beendet das Einzeichnen von Relatioen
         this.draw_relations.set(false);
         btnRelation.setText("Transtionenen einzeichnen");
         this.sidebar_handler.removeSidebar(null);
      });
      
      Tooltips_für_Buttons.setTooltip_neuerZustand(btnAddCircle);
      
      //Wechselt den Modus und ermöglocht dadruch das Einzeichnen von Relationen
      btnRelation.setOnAction((e) -> {
         this.draw_relations.set(!this.draw_relations.get());
         
         //entfernt Sidebar da sich TS verändert und Lösung hinfällig ist
         this.sidebar_handler.removeSidebar(drawingPane);
       
      });
      
      Tooltips_für_Buttons.setTooltip_relation_einzeichen(btnRelation);
      
      //Button der Berechnung startet 
      btnBerechnen.setOnAction((event) -> {
         Alert alert;
         //Starte Berechnung nur wenn Eingabn zulässig sind
         //Formelende muss eingelesen wurden sein
         if (!this.combobox_handler.getZustandsformel().getFormel_string().endsWith("Formelende")) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Berechnung verweigert");
            alert.setHeaderText((String)null);
            alert.setContentText("Bitte erst Formelende einlesen.");
            alert.showAndWait();
            //keine falsch eingelesenne Symbole
         } else if (this.combobox_handler.checkIfRed()) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Berechnung verweigert");
            alert.setHeaderText((String)null);
            alert.setContentText("Die Eingegeben Formel entspricht nicht der CTL-Syntax");
            alert.showAndWait();
            //Wenn kein Symbol eingelesen wurde
         } else if (this.combobox_handler.isStackEmpty()) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Berechnung verweigert");
            alert.setHeaderText((String)null);
            alert.setContentText("Es wurde kein Symbol eingegeben, die CTL-Formel ist ungültig");
            alert.showAndWait();
         } else {//Berechnung starten
        	 
        	 //schließt Eingabefelder und beendet Relation einzeichnen
            this.circle_builder.bereite_berechnung_vor(root);
            this.draw_relations.set(false);
            //Erzeugt Transitionsystem aus Relationen
            Transitionssystem transsitionssystem = new Transitionssystem(this.arrow_builder.getList_of_relations());
            this.sidebar_handler.createSidebar(this.FormelBox, this.combobox_handler.get_transformed_Zustandsformel(), root, transsitionssystem);
            root.requestLayout();
            }

      });
      
      Tooltips_für_Buttons.setTooltip_berechen(btnBerechnen);
      
      //Entfernt das letzte eingelesene Zeichen
      btnUndocomboBox.setOnAction((event) -> {
         this.combobox_handler.undo_combobox();
         this.sidebar_handler.removeSidebar(null);
      });
      
      Tooltips_für_Buttons.setTooltip_undo(btnUndocomboBox);
      
      //Startet das Programm neu
      btnNeustart.setOnAction((event) -> {
         try {
            this.draw_relations.set(false);
            this.firstCircle = null;
            this.secondCircle = null;
            this.firstCircle_label = null;
            this.secondCircle_label = null;
            this.arrow_builder.clearRelations();
            this.circle_builder.clearCircleGroups();
            this.combobox_handler.clear_combobox_handler();
            this.inputActive = false;
            primaryStage.close();
            Stage newStage = new Stage();
            this.start(newStage);
         } catch (Exception var4) {
            var4.printStackTrace();
         }

      });
      
      Tooltips_für_Buttons.setTooltip_neustart(btnNeustart);
      
      //Startet die Scene in Vollbiildmodus
      this.screenBounds = Screen.getPrimary().getVisualBounds();
      this.total_screen_width = this.screenBounds.getWidth();
      this.total_screen_height = this.screenBounds.getHeight() - 10.0D;
      Scene scene = new Scene(root, this.total_screen_width - 100.0D, this.total_screen_height - 100.0D);
      primaryStage.setTitle("Erstelle ein eigenes Transitionssystem und eine eigene CTL-Formel");
      primaryStage.setScene(scene);
      primaryStage.show();
   }

   //Methode die das Klciken auf einen Kreis verwaltet
   private void handleCircleClick(MouseEvent event, Arrow_Builder arrow_builder) {
	   //nur wenn keine Relation eingezeichnet wird und auch kein Textfeld aktiv ist
      if (this.draw_relations.get() && !this.inputActive) {
    	  //extrhaiere den Kreis
         Group clickedGroup = (Group)event.getSource();
         Circle clickedCircle = (Circle)clickedGroup.getChildren().get(0);
         Text clickedCircle_label = (Text)clickedGroup.getChildren().get(1);
         Pane parentPane = (Pane)clickedGroup.getParent();
         
         //Wenn erster angeklickter Kreis, Objekt speichern
         if (this.firstCircle == null) {
            this.firstCircle = clickedCircle;
            this.firstCircle_label = clickedCircle_label;
            clickedCircle.setFill(Color.YELLOW);
         //Wenn zweiter Kreis angeklickt, verarbeiten
         } else if (this.secondCircle == null) {
            this.secondCircle = clickedCircle;
            this.secondCircle_label = clickedCircle_label;
            clickedCircle.setFill(Color.YELLOW);
            //setzte Flag für geöffnetes Eingabefeld
            this.inputActive = true;
            //starte das Zeichnen des Pfeiles
            arrow_builder.drawArrow(parentPane, this.firstCircle, this.secondCircle, this.firstCircle_label, this.secondCircle_label, this.vorauswahl_transitionen);
            //Setzt die gespeicherten Kreise zurück
            this.firstCircle = null;
            this.secondCircle = null;
            this.firstCircle_label = null;
            this.secondCircle_label = null;
            this.inputActive = false;
         }
      }

   }
   
   private void showAlert(String title, String message) {
       Alert alert = new Alert(Alert.AlertType.WARNING);
       alert.setTitle(title);
       alert.setHeaderText(null);
       alert.setContentText(message);
       alert.showAndWait();
   }
   
   

   public static void main(String[] args) {
      launch(args);
   }
}