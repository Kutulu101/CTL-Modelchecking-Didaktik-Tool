   package GUI;

import CTL_Backend.Transitionssystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
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

public class GUI_freier_Modus extends Application {
   boolean opened_via_Main_menu = false;
   private BooleanProperty draw_relations = new SimpleBooleanProperty(false);
   private Circle firstCircle = null;
   private Circle secondCircle = null;
   private Text firstCircle_label = null;
   private Text secondCircle_label = null;
   private boolean inputActive = false;
   private List<String> vorauswahl_transitionen;
   HBox FormelBox = new HBox(10.0D);
   private Arrow_Builder arrow_builder = new Arrow_Builder();
   private Circle_Group_Builder circle_builder;
   private Combobox_Handler combobox_handler;
   private SidebarHandler sidebar_handler;
   Rectangle2D screenBounds;
   protected double total_screen_width;
   protected double total_screen_height;

   public GUI_freier_Modus(boolean via_main) {
      this.circle_builder = new Circle_Group_Builder(this.arrow_builder);
      this.combobox_handler = new Combobox_Handler(this.FormelBox);
      this.sidebar_handler = new SidebarHandler(this.circle_builder);
      this.opened_via_Main_menu = via_main;
   }

   public void start(Stage primaryStage) {
      BorderPane root = new BorderPane();
      Pane drawingPane = new Pane();
      drawingPane.setStyle("-fx-background-color: lightgray;");
      root.setCenter(drawingPane);
      HBox buttonBox = new HBox(10.0D);
      Button btnAddCircle = new Button("Zustand hinzufügen");
      Button btnRelation = new Button("Transitionen einzeichnen");
      Button btnUndocomboBox = new Button("Undo Formeleingabe");
      Button btnTS_entfernen = new Button("aktuelles TS löschen");
      Button btnBerechnen = new Button("Berechne Lösungsmengen");
      Button btnNeustart = new Button("Programm Neustarten");
      Button btnBeenden = new Button("Programm beenden");
      buttonBox.getChildren().addAll(btnAddCircle, btnRelation, btnUndocomboBox, btnTS_entfernen, btnBerechnen, btnNeustart, btnBeenden);
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
      Separator separator = new Separator();
      separator.setOrientation(Orientation.HORIZONTAL);
      VBox topBox = new VBox(10.0D);
      topBox.getChildren().addAll(buttonBox, separator, eingabeBox);
      topBox.setPadding(new Insets(10.0D));
      root.setTop(topBox);
      this.combobox_handler.handle_first_combobox(root);
      this.draw_relations.addListener((observable, oldValue, newValue) -> {
         this.firstCircle = null;
         this.secondCircle = null;
         this.firstCircle_label = null;
         this.secondCircle_label = null;
         if (this.draw_relations.get()) {
            btnRelation.setText("Einzeichnen Beenden");
            root.lookupAll(".circle_with_text").forEach((node) -> {
               Group group = (Group)node;
               if (group.getChildren().get(0) instanceof Circle) {
                  ((Circle)group.getChildren().get(0)).setFill(Color.YELLOW);
               }

            });
         } else {
            btnRelation.setText("Einzeichnen Starten");
            root.lookupAll(".circle_with_text").forEach((node) -> {
               Group group = (Group)node;
               if (group.getChildren().get(0) instanceof Circle) {
                  ((Circle)group.getChildren().get(0)).setFill(Color.BLUE);
               }

            });
         }

      });
      btnBeenden.setOnAction((e) -> {
         if (this.opened_via_Main_menu) {
            Stage stage = new Stage();
            GUI_Main main_menu = new GUI_Main();
            main_menu.start(stage);
         }

         primaryStage.close();
      });
      btnTS_entfernen.setOnAction((event) -> {
         drawingPane.getChildren().clear();
         this.circle_builder.clearCircleGroups();
         this.arrow_builder.clearRelations();
         this.sidebar_handler.removeSidebar();
      });
      btnAddCircle.setOnAction((e) -> {
         Group created_circle = this.circle_builder.create_circle_with_text(this.draw_relations);
         created_circle.setOnMouseClicked((event) -> {
            this.handleCircleClick(event, this.arrow_builder);
         });
         drawingPane.getChildren().add(created_circle);
         this.draw_relations.set(false);
         this.circle_builder.colorAllCircles(drawingPane);
         btnRelation.setText("Transtionenen einzeichnen");
         this.sidebar_handler.removeSidebar();
      });
      btnRelation.setOnAction((e) -> {
         this.draw_relations.set(!this.draw_relations.get());
         this.sidebar_handler.removeSidebar();
      });
      btnBerechnen.setOnAction((event) -> {
         Alert alert;
         if (!this.combobox_handler.getZustandsformel().getFormel_string().endsWith("Formelende")) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Berechnung verweigert");
            alert.setHeaderText((String)null);
            alert.setContentText("Bitte erst Formelende einlesen.");
            alert.showAndWait();
         } else if (this.combobox_handler.checkIfRed()) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Berechnung verweigert");
            alert.setHeaderText((String)null);
            alert.setContentText("Die Eingegeben Formel entspricht nicht der CTL-Syntax");
            alert.showAndWait();
         } else if (this.combobox_handler.isStackEmpty()) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Berechnung verweigert");
            alert.setHeaderText((String)null);
            alert.setContentText("Es wurde kein Symbol eingegeben, die CTL-Formel ist ungültig");
            alert.showAndWait();
         } else {
            this.circle_builder.schliesseAlleEingabefelder(root);
            this.draw_relations.set(false);
            Transitionssystem transsitionssystem = new Transitionssystem(this.arrow_builder.getList_of_relations());
            this.sidebar_handler.createSidebar(this.FormelBox, this.combobox_handler.get_transformed_Zustandsformel(), root, transsitionssystem);
            root.requestLayout();
         }

      });
      btnUndocomboBox.setOnAction((event) -> {
         this.combobox_handler.undo_combobox();
         this.sidebar_handler.removeSidebar();
      });
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
      this.screenBounds = Screen.getPrimary().getVisualBounds();
      this.total_screen_width = this.screenBounds.getWidth();
      this.total_screen_height = this.screenBounds.getHeight() - 10.0D;
      Scene scene = new Scene(root, this.total_screen_width - 100.0D, this.total_screen_height - 100.0D);
      primaryStage.setTitle("Erstelle ein eigenes Transitionssystem und eine eigene CTL-Formel");
      primaryStage.setScene(scene);
      primaryStage.show();
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
            clickedCircle.setFill(Color.YELLOW);
         } else if (this.secondCircle == null) {
            this.secondCircle = clickedCircle;
            this.secondCircle_label = clickedCircle_label;
            clickedCircle.setFill(Color.YELLOW);
            this.inputActive = true;
            arrow_builder.drawArrow(parentPane, this.firstCircle, this.secondCircle, this.firstCircle_label, this.secondCircle_label, this.vorauswahl_transitionen);
            this.firstCircle = null;
            this.secondCircle = null;
            this.firstCircle_label = null;
            this.secondCircle_label = null;
            this.inputActive = false;
         }
      }

   }

   public static void main(String[] args) {
      launch(args);
   }
}