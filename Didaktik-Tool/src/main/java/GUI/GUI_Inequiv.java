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

public class GUI_Inequiv extends GUI_zeichen_modus {
   private String selectedFormula;
   private HashSet<EquationPair> zustandsformeln;

   public GUI_Inequiv(boolean via_main) {
      this.opened_via_Main_menu = via_main;
      this.initializeZustandsformeln();
   }

   private void initializeZustandsformeln() {
      this.zustandsformeln = new HashSet();
      this.zustandsformeln.add(new EquationPair("∃□∃◇(〈a〉1∧¬〈b〉1)", "∃◇∃□(〈a〉1∧¬〈b〉1)"));
      this.zustandsformeln.add(new EquationPair("∃(〈a〉1∨〈b〉1)U〈c〉1", "(∃〈a〉1U〈c〉1)∨(∃〈b〉1 U〈c〉1)"));
      this.zustandsformeln.add(new EquationPair("∃¬〈a〉1U(〈b〉1∧〈c〉1)", "(∃¬〈a〉1U〈b〉1)∧(∃¬〈a〉1U〈c〉1)"));
      this.zustandsformeln.add(new EquationPair("∃□〈a〉1", "∃□[a]1"));
   }

   public void start(Stage primaryStage) {
      BorderPane root = new BorderPane();
      Label instructionLabel = new Label("Wähle ein CTL-Formel-Paar aus:");
      instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      Label selectedFormulaLabel = new Label();
      selectedFormulaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      ComboBox<String> comboBox = new ComboBox();
      Iterator var6 = this.zustandsformeln.iterator();

      while(var6.hasNext()) {
         EquationPair formel = (EquationPair)var6.next();
         comboBox.getItems().add(formel.toString());
      }

      VBox comboBoxContainer = new VBox(50.0D, new Node[]{instructionLabel, comboBox});
      comboBoxContainer.setAlignment(Pos.CENTER);
      Button btnBeenden = new Button("Programm beenden");
      Button btnneustart = new Button("Programm neustarten");
      Button btnprüfen = new Button("Transitionssystem prüfen");
      btnBeenden.setOnAction((event) -> {
         if (this.opened_via_Main_menu) {
            Stage stage = new Stage();
            GUI_Main main_menu = new GUI_Main();
            main_menu.start(stage);
         }

         primaryStage.close();
      });
      btnneustart.setOnAction((event) -> {
         try {
            this.draw_relations.set(false);
            this.firstCircle = null;
            this.secondCircle = null;
            this.firstCircle_label = null;
            this.secondCircle_label = null;
            Iterator var3 = this.sidebar_handler_list.iterator();

            while(var3.hasNext()) {
               SidebarHandler sidebar = (SidebarHandler)var3.next();
               sidebar.removeSidebar();
            }

            var3 = this.arrow_builder_list.iterator();

            while(var3.hasNext()) {
               Arrow_Builder arrow_builder = (Arrow_Builder)var3.next();
               arrow_builder.clearRelations();
            }

            var3 = this.circlebuilder_liste.iterator();

            while(var3.hasNext()) {
               Circle_Group_Builder circle_builder = (Circle_Group_Builder)var3.next();
               circle_builder.clearCircleGroups();
            }

            this.arrow_builder_list = new LinkedList();
            this.sidebar_handler_list = new LinkedList();
            this.circlebuilder_liste = new LinkedList();
            this.pane_list = new LinkedList();
            this.inputActive = false;
            primaryStage.close();
            Stage newStage = new Stage();
            this.start(newStage);
         } catch (Exception var5) {
            var5.printStackTrace();
         }

      });
      HBox main_button_box = new HBox(20.0D);
      main_button_box.getChildren().addAll(btnprüfen, btnneustart, btnBeenden);
      main_button_box.setAlignment(Pos.CENTER);
      Label label = new Label("Transitionen vorauswählen (getrennt durch Komma): ");
      TextField eingabeFeld = new TextField();
      eingabeFeld.textProperty().addListener((observable, oldValue, newValue) -> {
         if (!newValue.isEmpty()) {
            this.vorauswahl_transitionen = new ArrayList(Arrays.asList(newValue.split(",")));
         } else {
            this.vorauswahl_transitionen = new ArrayList();
         }

      });
      HBox eingabeBox = new HBox(10.0D);
      eingabeBox.getChildren().addAll(label, eingabeFeld);
      eingabeBox.setPrefWidth(400.0D);
      eingabeBox.setAlignment(Pos.CENTER);
      VBox gesamte_box = new VBox(30.0D);
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
      btnprüfen.setOnAction((event) -> {
         ((Circle_Group_Builder)this.circlebuilder_liste.get(0)).schliesseAlleEingabefelder(root);
         this.draw_relations.set(false);
         Zustandsformel zustandsformel_links = new Zustandsformel(this.selectedFormula.split(" ≡ ")[0]);
         Zustandsformel zustandsformel_rechts = new Zustandsformel(this.selectedFormula.split(" ≡ ")[1]);
         this.sidebar_handler_list.add(new SidebarHandler((Circle_Group_Builder)this.circlebuilder_liste.get(0)));
         this.sidebar_handler_list.add(new SidebarHandler((Circle_Group_Builder)this.circlebuilder_liste.get(0)));
         Transitionssystem ts = new Transitionssystem(((Arrow_Builder)this.arrow_builder_list.get(0)).getList_of_relations());
         ((SidebarHandler)this.sidebar_handler_list.get(0)).createReducedSidebarRight((AnchorPane)this.pane_list.get(0), zustandsformel_rechts, ts, 100, "");
         ((SidebarHandler)this.sidebar_handler_list.get(1)).createReducedSidebarLeft((AnchorPane)this.pane_list.get(0), zustandsformel_links, ts, -100, "");
         SidebarHandler.synchronizeButtonText(((SidebarHandler)this.sidebar_handler_list.get(0)).getColor_button(), ((SidebarHandler)this.sidebar_handler_list.get(1)).getColor_button());
         HashSet<Zustand> lösungsmenge_rechts = (HashSet)zustandsformel_rechts.get_Lösungsmenge(ts);
         HashSet<Zustand> lösungsmenge_links = (HashSet)zustandsformel_links.get_Lösungsmenge(ts);
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle("Ergebnis der Überprüfung");
         if (lösungsmenge_links.size() > 0 && lösungsmenge_rechts.size() == 0) {
            alert.setHeaderText("Ergebnis: Aufgabe korrekt");
            alert.setContentText("Herzlichen Glückwunsch, die Aufgabe ist korrekt!");
         } else if (lösungsmenge_links.size() == 0 && lösungsmenge_rechts.size() == 0) {
            alert.setHeaderText("Ergebnis: Keine Übereinstimmung");
            alert.setContentText("Leider erfüllt kein Zustand der Transitionssysteme die CTL-Formel.");
         } else if (lösungsmenge_links.size() > 0 && lösungsmenge_rechts.size() > 0) {
            alert.setHeaderText("Ergebnis: Mehrfachübereinstimmung");
            alert.setContentText("Leider erfüllen Zustände im Tansitionssysteme beide CTL-Formeln.");
         } else {
            alert.setHeaderText("Ergebnis: Fehlerhafte Eingabe");
            alert.setContentText("Leider war ihre Eingabe unvollständig");
         }

         alert.showAndWait();
      });
      root.setCenter(comboBoxContainer);
      this.screenBounds = Screen.getPrimary().getVisualBounds();
      this.total_screen_width = this.screenBounds.getWidth();
      this.total_screen_height = this.screenBounds.getHeight() - 10.0D;
      Scene scene = new Scene(root, this.total_screen_width, this.total_screen_height);
      primaryStage.setTitle("CTL-Inequivalenzen");
      primaryStage.setScene(scene);
      primaryStage.show();
   }

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