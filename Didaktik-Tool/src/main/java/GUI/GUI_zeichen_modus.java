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
import javafx.application.Application;
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

public class GUI_zeichen_modus extends Application {
   private String selectedFormula;
   protected AnchorPane drawingPane1;
   private AnchorPane drawingPane2;
   Rectangle2D screenBounds;
   protected double total_screen_width;
   protected double total_screen_height;
   boolean opened_via_Main_menu = false;
   HashSet<Zustandsformel> zustandsformeln = new HashSet();
   protected BooleanProperty draw_relations = new SimpleBooleanProperty(false);
   protected List<String> vorauswahl_transitionen;
   protected Circle firstCircle = null;
   protected Circle secondCircle = null;
   protected Text firstCircle_label = null;
   protected Text secondCircle_label = null;
   private Pane firstParentPane = null;
   private Pane secondParentPane = null;
   boolean inputActive = false;
   private List<Transitionssystem> transitionssystem_liste = new LinkedList();
   protected List<Circle_Group_Builder> circlebuilder_liste = new LinkedList();
   protected List<SidebarHandler> sidebar_handler_list = new LinkedList();
   protected List<Arrow_Builder> arrow_builder_list = new LinkedList();
   protected List<AnchorPane> pane_list = new LinkedList();

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

   public GUI_zeichen_modus() {
      this.opened_via_Main_menu = false;
   }

   public void start(Stage primaryStage) {
      BorderPane root = new BorderPane();
      Label instructionLabel = new Label("Wähle eine CTL-Formel aus:");
      instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      Label selectedFormulaLabel = new Label();
      selectedFormulaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      ComboBox<String> comboBox = new ComboBox();
      Iterator var6 = this.zustandsformeln.iterator();

      while(var6.hasNext()) {
         Zustandsformel formel = (Zustandsformel)var6.next();
         comboBox.getItems().add(formel.getFormel_string());
      }

      VBox comboBoxContainer = new VBox(50.0D, new Node[]{instructionLabel, comboBox});
      comboBoxContainer.setAlignment(Pos.CENTER);
      Button btnBeenden = new Button("Programm beenden");
      Button btnneustart = new Button("Programm neustarten");
      Button btnprüfen = new Button("Transitionssysteme prüfen");
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

            this.pane_list = new LinkedList();
            this.arrow_builder_list = new LinkedList();
            this.sidebar_handler_list = new LinkedList();
            this.circlebuilder_liste = new LinkedList();
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
            selectedFormulaLabel.setText("Zeichne zwei Transitionssysteme von denen eins die CTL-Formel: " + this.selectedFormula + " erfüllen soll und eins nicht");
            root.getChildren().remove(comboBoxContainer);
            root.setTop(gesamte_box);
            root.setBottom(this.createDrawingPaneContainer());
         }

      });
      btnprüfen.setOnAction((event) -> {
         this.draw_relations.set(false);
         Zustandsformel zustandsformel = new Zustandsformel(this.selectedFormula);
         List<HashSet<Zustand>> ergebnisListe = new LinkedList();

         int nichtLeereSets;
         for(nichtLeereSets = 0; nichtLeereSets < this.pane_list.size(); ++nichtLeereSets) {
            ((Circle_Group_Builder)this.circlebuilder_liste.get(nichtLeereSets)).schliesseAlleEingabefelder(root);
            this.sidebar_handler_list.add(new SidebarHandler((Circle_Group_Builder)this.circlebuilder_liste.get(nichtLeereSets)));
            Transitionssystem ts = new Transitionssystem(((Arrow_Builder)this.arrow_builder_list.get(nichtLeereSets)).getList_of_relations());
            String extra_beschriftung = "";
            if (nichtLeereSets > 0) {
               extra_beschriftung = " mit Lösungsmengen für das Transtionssystem auf der linken Seite";
            } else {
               extra_beschriftung = " mit Lösungsmengen für das Transtionssystem auf der linken Seite";
            }

            ((SidebarHandler)this.sidebar_handler_list.get(nichtLeereSets)).createReducedSidebarRight((AnchorPane)this.pane_list.get(nichtLeereSets), zustandsformel, ts, 0, extra_beschriftung);
            ((SidebarHandler)this.sidebar_handler_list.get(nichtLeereSets)).handleButtonClick(((SidebarHandler)this.sidebar_handler_list.get(nichtLeereSets)).getColor_button(), zustandsformel, ts);
            ergebnisListe.add((HashSet)zustandsformel.get_Lösungsmenge(ts));
         }

         nichtLeereSets = 0;
         int leereSets = 0;
         Iterator var10 = ergebnisListe.iterator();

         while(var10.hasNext()) {
            HashSet<Zustand> ergebnisSet = (HashSet)var10.next();
            if (ergebnisSet.isEmpty()) {
               ++leereSets;
            } else {
               ++nichtLeereSets;
            }
         }

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
      primaryStage.setTitle("Zeichenmodus");
      primaryStage.setScene(scene);
      primaryStage.show();
   }

   protected HBox createDrawingPaneContainer() {
      this.drawingPane1 = this.createDrawingPane("Zeichenfläche 1", (this.total_screen_width - 100.0D) / 2.0D, this.total_screen_height - 200.0D);
      this.drawingPane2 = this.createDrawingPane("Zeichenfläche 2", (this.total_screen_width - 100.0D) / 2.0D, this.total_screen_height - 200.0D);
      this.pane_list.add(this.drawingPane1);
      this.pane_list.add(this.drawingPane2);
      HBox drawingPaneContainer = new HBox(10.0D, new Node[]{this.drawingPane1, this.drawingPane2});
      drawingPaneContainer.setStyle("-fx-padding: 10px;");
      drawingPaneContainer.setAlignment(Pos.CENTER);
      return drawingPaneContainer;
   }

   AnchorPane createDrawingPane(String labelText, double d, double f) {
      Arrow_Builder arrow_builder = new Arrow_Builder();
      Circle_Group_Builder circle_builder = new Circle_Group_Builder(arrow_builder);
      this.circlebuilder_liste.add(circle_builder);
      this.arrow_builder_list.add(arrow_builder);
      Pane drawingPane = new Pane();
      drawingPane.setStyle("-fx-border-color: black; -fx-background-color: white;");
      drawingPane.setMouseTransparent(false);
      Label label = new Label(labelText);
      label.setTextAlignment(TextAlignment.CENTER);
      label.setStyle("-fx-font-size: 14px; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1px;");
      label.setPadding(new Insets(10.0D));
      Button btnAddCircle = new Button("Zustand hinzufügen");
      Button btnRelation = new Button("Transitionen einzeichnen");
      Button btnTS_entfernen = new Button("TS löschen");
      HBox buttonBox = new HBox(10.0D);
      buttonBox.getChildren().addAll(btnAddCircle, btnRelation, btnTS_entfernen);
      buttonBox.setStyle("-fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1px;");
      buttonBox.setPadding(new Insets(10.0D));
      this.draw_relations.addListener((observable, oldValue, newValue) -> {
         this.firstCircle = null;
         this.secondCircle = null;
         this.firstCircle_label = null;
         this.secondCircle_label = null;
         if (this.draw_relations.get()) {
            btnRelation.setText("Einzeichnen Beenden");
            drawingPane.lookupAll(".circle_with_text").forEach((node) -> {
               Group group = (Group)node;
               if (group.getChildren().get(0) instanceof Circle) {
                  ((Circle)group.getChildren().get(0)).setFill(Color.YELLOW);
               }

            });
         } else {
            btnRelation.setText("Einzeichnen Starten");
            drawingPane.lookupAll(".circle_with_text").forEach((node) -> {
               Group group = (Group)node;
               if (group.getChildren().get(0) instanceof Circle) {
                  ((Circle)group.getChildren().get(0)).setFill(Color.BLUE);
               }

            });
         }

      });
      btnTS_entfernen.setOnAction((event) -> {
         drawingPane.getChildren().clear();
         circle_builder.clearCircleGroups();
         arrow_builder.clearRelations();
         this.draw_relations.set(false);
         Iterator var5 = this.sidebar_handler_list.iterator();

         while(var5.hasNext()) {
            SidebarHandler sidebar = (SidebarHandler)var5.next();
            sidebar.removeSidebar();
         }

      });
      btnAddCircle.setOnAction((e) -> {
         Group createdCircle = circle_builder.create_circle_with_text(this.draw_relations);
         createdCircle.setOnMouseClicked((event) -> {
            this.handleCircleClick(event, arrow_builder);
         });
         createdCircle.toFront();
         drawingPane.getChildren().add(createdCircle);
         this.draw_relations.set(false);
         circle_builder.colorAllCircles(drawingPane);
         btnRelation.setText("Transitionen einzeichnen");
         Iterator var7 = this.sidebar_handler_list.iterator();

         while(var7.hasNext()) {
            SidebarHandler sidebar = (SidebarHandler)var7.next();
            sidebar.removeSidebar();
         }

      });
      btnRelation.setOnAction((e) -> {
         this.draw_relations.set(!this.draw_relations.get());
         Iterator var2 = this.sidebar_handler_list.iterator();

         while(var2.hasNext()) {
            SidebarHandler sidebar = (SidebarHandler)var2.next();
            sidebar.removeSidebar();
         }

      });
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

   private void handleCircleClick(MouseEvent event, Arrow_Builder arrow_builder) {
      if (this.draw_relations.get() && !this.inputActive) {
         Group clickedGroup = (Group)event.getSource();
         Circle clickedCircle = (Circle)clickedGroup.getChildren().get(0);
         Text clickedCircle_label = (Text)clickedGroup.getChildren().get(1);
         Pane parentPane = (Pane)clickedGroup.getParent();
         if (this.firstCircle == null) {
            this.firstCircle = clickedCircle;
            this.firstCircle_label = clickedCircle_label;
            this.firstParentPane = parentPane;
            clickedCircle.setFill(Color.YELLOW);
         } else if (this.secondCircle == null) {
            this.secondCircle = clickedCircle;
            this.secondCircle_label = clickedCircle_label;
            this.secondParentPane = parentPane;
            clickedCircle.setFill(Color.YELLOW);
            if (this.firstParentPane == this.secondParentPane) {
               this.inputActive = true;
               arrow_builder.drawArrow(parentPane, this.firstCircle, this.secondCircle, this.firstCircle_label, this.secondCircle_label, this.vorauswahl_transitionen);
            } else {
               this.showAlert("Fehler", "Die Kreise befinden sich auf unterschiedlichen Panes!", Alert.AlertType.WARNING);
               this.firstCircle.setFill(Color.BLACK);
               this.secondCircle.setFill(Color.BLACK);
            }

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

   private void showAlert(String title, String message, Alert.AlertType alertType) {
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