    package GUI;

import CTL_Backend.Transitionssystem;
import CTL_Backend.Zustand;
import CTL_Backend.Zustandsformel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

//Klasse die den Modus CTL-Inequivalenz verwaltet, erbt von Zeichenmodus da viele Methoden ähnlich sind
public class GUI_Inequiv extends GUI_zeichen_modus {
	
	//gewählte Formel
   private String selectedFormula;
   //Sammlung der möglichen Aufgaben
   private HashSet<EquationPair> zustandsformeln;

   //Konstruktor
   public GUI_Inequiv(boolean via_main) {
      this.opened_via_Main_menu = via_main;
      this.initializeZustandsformeln();
   }
   
   //Methode die die möglichen Aufgaben/Formelpaare intialisiert
   private void initializeZustandsformeln() {
      this.zustandsformeln = new HashSet();
      this.zustandsformeln.add(new EquationPair("∃□∃◇(〈a〉1∧¬〈b〉1)", "∃◇∃□(〈a〉1∧¬〈b〉1)"));
      this.zustandsformeln.add(new EquationPair("∃(〈a〉1∨〈b〉1)U〈c〉1", "(∃〈a〉1U〈c〉1)∨(∃〈b〉1 U〈c〉1)"));
      this.zustandsformeln.add(new EquationPair("∃¬〈a〉1U(〈b〉1∧〈c〉1)", "(∃¬〈a〉1U〈b〉1)∧(∃¬〈a〉1U〈c〉1)"));
      this.zustandsformeln.add(new EquationPair("∃□〈a〉1", "∃□[a]1"));
   }

   //Startet Stage
   public void start(Stage primaryStage) {
	   
	   //Basis Pane der GUI
      BorderPane root = new BorderPane();
      
      //Start bildet Combobox mit Auswahl an möglichen Gleichungen
      Label instructionLabel = new Label("Wähle ein CTL-Formel-Paar aus:");
      instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      Label selectedFormulaLabel = new Label();
      selectedFormulaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      ComboBox<String> comboBox = new ComboBox();

      //fülle Combobox
       for(EquationPair formel:this.zustandsformeln){
         comboBox.getItems().add(formel.toString());
      }

      //Erzeugt Oberen Teil des LAyout 
      VBox comboBoxContainer = new VBox(50.0, new Node[]{instructionLabel, comboBox});
      comboBoxContainer.setAlignment(Pos.CENTER);
      
      //Initalisiere benötigte Buttons
      Button btnBeenden = new Button("Programm beenden");
      Button btnneustart = new Button("Programm neustarten");
      Button btnprüfen = new Button("Transitionssystem prüfen");
      
      //Registriere Events bei Buttons
      
      //Beende Programm und kehre ggf. zum Main-Menu zurück
      btnBeenden.setOnAction((event) -> {
         if (this.opened_via_Main_menu) {
            Stage stage = new Stage();
            GUI_Main main_menu = new GUI_Main();
            main_menu.start(stage);
         }

         primaryStage.close();
      });
      
      //Startet Programm neu
      btnneustart.setOnAction((event) -> {
         try {
        	 
        	 //entferne alle bisher gespeicherten Elemente
            this.draw_relations.set(false);
            this.firstCircle = null;
            this.secondCircle = null;
            this.firstCircle_label = null;
            this.secondCircle_label = null;

            for(SidebarHandler sidebar:this.sidebar_handler_list) {
               sidebar.removeSidebar();
            }

            for(Arrow_Builder arrow_builder:this.arrow_builder_list) {
               arrow_builder.clearRelations();
            }


            for(Circle_Group_Builder circle_builder:this.circlebuilder_liste) {
               circle_builder.clearCircleGroups();
            }

            //Initalisiere Listen für die Verwaltung der Elemente neu
            this.arrow_builder_list = new LinkedList();
            this.sidebar_handler_list = new LinkedList();
            this.circlebuilder_liste = new LinkedList();
            this.pane_list = new LinkedList();
            this.inputActive = false;
            
            //Schließe Stage und öffne neu
            primaryStage.close();
            Stage newStage = new Stage();
            this.start(newStage);
         } catch (Exception var5) {
            var5.printStackTrace();
         }

      });
      
    //Button der die gemachten Eingaben prüft
      btnprüfen.setOnAction((event) -> {
    	  
    	  //schließt alle EingabeFelder
         ((Circle_Group_Builder)this.circlebuilder_liste.get(0)).schliesseAlleEingabefelder(root);
         //bendet Einzeichnen der Relationen
         this.draw_relations.set(false);
         
         //Erzeugt zwei Zustandsformeln linke und rechte Seite
         Zustandsformel zustandsformel_links = new Zustandsformel(this.selectedFormula.split(" ≡ ")[0]);
         Zustandsformel zustandsformel_rechts = new Zustandsformel(this.selectedFormula.split(" ≡ ")[1]);
         
         //Erzeugt einen Sidebar-Handler pro CTL-Formel
         this.sidebar_handler_list.add(new SidebarHandler((Circle_Group_Builder)this.circlebuilder_liste.get(0)));
         this.sidebar_handler_list.add(new SidebarHandler((Circle_Group_Builder)this.circlebuilder_liste.get(0)));
         
         //Erzeugt Transitionssystem
         Transitionssystem ts = new Transitionssystem(((Arrow_Builder)this.arrow_builder_list.get(0)).getList_of_relations());
         
         //erzeugt zwei Sidebars eins für jede CTL-Formel
         ((SidebarHandler)this.sidebar_handler_list.get(0)).createReducedSidebarRight((AnchorPane)this.pane_list.get(0), zustandsformel_rechts, ts, 100, "");
         ((SidebarHandler)this.sidebar_handler_list.get(1)).createReducedSidebarLeft((AnchorPane)this.pane_list.get(0), zustandsformel_links, ts, -100, "");
         
         //Methode die die beiden Einfärben Buttons synchronisiert
         SidebarHandler.synchronizeButtonText(((SidebarHandler)this.sidebar_handler_list.get(0)).getColor_button(), ((SidebarHandler)this.sidebar_handler_list.get(1)).getColor_button());
         
         //prüft ob die Aufgabe korrekt erfüllt wurde und zeigt Msg-Box an
         HashSet<Zustand> lösungsmenge_rechts = (HashSet)zustandsformel_rechts.get_Lösungsmenge(ts);
         HashSet<Zustand> lösungsmenge_links = (HashSet)zustandsformel_links.get_Lösungsmenge(ts);
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle("Ergebnis der Überprüfung");
         
         if (lösungsmenge_links.size() > 0 && lösungsmenge_rechts.size() == 0) {
            alert.setHeaderText("Ergebnis: Aufgabe korrekt");
            alert.setContentText("Herzlichen Glückwunsch, die Aufgabe ist korrekt!");
         }
         else if (lösungsmenge_links.size() == 0 && lösungsmenge_rechts.size() == 0) {
            alert.setHeaderText("Ergebnis: Keine Übereinstimmung");
            alert.setContentText("Leider erfüllt kein Zustand der Transitionssysteme die CTL-Formel.");
         } 
         else if (lösungsmenge_links.size() > 0 && lösungsmenge_rechts.size() > 0) {
            alert.setHeaderText("Ergebnis: Mehrfachübereinstimmung");
            alert.setContentText("Leider erfüllen Zustände im Tansitionssysteme beide CTL-Formeln.");
         } 
         else {
            alert.setHeaderText("Ergebnis: Fehlerhafte Eingabe");
            alert.setContentText("Leider war ihre Eingabe unvollständig");
         }

         alert.showAndWait();
      });
      
      //Füge Button zu Layout hinzu
      HBox main_button_box = new HBox(20.0);
      main_button_box.getChildren().addAll(btnprüfen, btnneustart, btnBeenden);
      main_button_box.setAlignment(Pos.CENTER);
      
      //Vorauswahl Transitionen
      Label label = new Label("Transitionen vorauswählen (getrennt durch Komma): ");
      TextField eingabeFeld = new TextField();
      eingabeFeld.textProperty().addListener((observable, oldValue, newValue) -> {
         if (!newValue.isEmpty()) {
            this.vorauswahl_transitionen = new ArrayList(Arrays.asList(newValue.split(",")));
         } else {
            this.vorauswahl_transitionen = new ArrayList();
         }

      });
      
      //LAyout von Transitionsvorauswahl
      HBox eingabeBox = new HBox(10.0);
      eingabeBox.getChildren().addAll(label, eingabeFeld);
      eingabeBox.setPrefWidth(400.0);
      eingabeBox.setAlignment(Pos.CENTER);
      
      //LAyout der HBoxen in BorderPane
      VBox gesamte_box = new VBox(30.0);
      gesamte_box.getChildren().addAll(selectedFormulaLabel, main_button_box, eingabeBox);
      gesamte_box.setPadding(new Insets(10.0D));
      comboBox.setOnAction((e) -> {
         this.selectedFormula = (String)comboBox.getSelectionModel().getSelectedItem();
         if (this.selectedFormula != null) {
            selectedFormulaLabel.setText("Zeichne ein Transitionssysteme, welches die linke Gleichung erfüllt, aber nicht die Rechte: " + this.selectedFormula);
            selectedFormulaLabel.setAlignment(Pos.CENTER);
            root.getChildren().remove(comboBoxContainer);
            root.setTop(gesamte_box);
            root.setBottom(this.createDrawingPaneContainer());
         }

      });
      
      //Platziert Combobox
      root.setCenter(comboBoxContainer);
      
      //startet Scene im Vollbild-Modus
      this.screenBounds = Screen.getPrimary().getVisualBounds();
      this.total_screen_width = this.screenBounds.getWidth();
      this.total_screen_height = this.screenBounds.getHeight() - 10.0D;
      Scene scene = new Scene(root, this.total_screen_width, this.total_screen_height);
      primaryStage.setTitle("CTL-Inequivalenzen");
      primaryStage.setScene(scene);
      primaryStage.show();
   }
   
   //erzugt eine Container mit einer Zeichenfläche nach der geerbten Mehtode
   protected HBox createDrawingPaneContainer() {
      this.drawingPane1 = this.createDrawingPane("Zeichenfläche 1", this.total_screen_width - 100.0D, this.total_screen_height - 200.0D);
      this.pane_list.add(this.drawingPane1);
      HBox drawingPaneContainer = new HBox(10.0D, new Node[]{this.drawingPane1});
      drawingPaneContainer.setStyle("-fx-padding: 10px;");
      drawingPaneContainer.setAlignment(Pos.CENTER);
      return drawingPaneContainer;
   }

   public static void main(String[] args) {
      launch(args);
   }
   
   //Hilfsklasse um zwei CTL-Gleichungen zu verbinden
   class EquationPair {
	   private final String leftSide;
	   private final String rightSide;

	   public EquationPair(String leftSide, String rightSide) {
	      this.leftSide = leftSide;
	      this.rightSide = rightSide;
	   }

	   public String getLeftSide() {
	      return this.leftSide;
	   }

	   public String getRightSide() {
	      return this.rightSide;
	   }

	   public String toString() {
	      return this.leftSide + " ≡ " + this.rightSide;
	   }
	}
}