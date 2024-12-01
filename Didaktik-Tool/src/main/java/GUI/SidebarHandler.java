    package GUI;

import CTL_Backend.CTL_Formel_Baum;
import CTL_Backend.Transitionssystem;
import CTL_Backend.Umformung;
import CTL_Backend.Zustandsformel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class SidebarHandler {
   private boolean isExpanded = true;
   private StackPane sidebar_container_right;
   private StackPane sidebar_container_left;
   private VBox sidebar_right = null;
   private VBox sidebar_left = null;
   private boolean isSolutionNormalFormVisible = false;
   private boolean is_colored = false;
   private boolean isFormulaTreeCompleteVisible = false;
   private boolean isSatisfyingSetsVisible = false;
   private Button color_button = null;
   private Button shared_button_right = null;
   private Button shared_button_left = null;
   ScrollPane normalformpane = null;
   VBox untere_Ausgabe = null;
   Stage formel_baum_stage = null;
   CTL_Formel_Baum ctl_baum;
   private Circle_Group_Builder circle_builder = null;

   public SidebarHandler(Circle_Group_Builder circle_builder) {
      this.circle_builder = circle_builder;
   }

   public void createSidebar(HBox formelbox, Zustandsformel zustandsformel, BorderPane root, Transitionssystem ts) {
      this.ctl_baum = new CTL_Formel_Baum(zustandsformel.getStart_der_rekursiven_Definition(), ts);
      VBox sidebar = new VBox();
      sidebar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
      sidebar.setSpacing(20.0D);
      sidebar.setPrefHeight(400.0D);
      Button button1 = this.createToggleButton("Zeige Lösung Normalform", "Verstecke Lösung Normalform", (button) -> {
         this.isSolutionNormalFormVisible = !this.isSolutionNormalFormVisible;
         if (this.normalformpane == null) {
            this.normalformpane = this.erstelleUmformungsLabelsUndFügeHinzu(formelbox, zustandsformel, root);
         } else {
            this.normalformpane.setVisible(this.isSolutionNormalFormVisible);
            this.normalformpane.setManaged(this.isSolutionNormalFormVisible);
         }

         button.setText(this.isSolutionNormalFormVisible ? "Verstecke Lösung Normalform" : "Zeige Lösung Normalform");
      });
      Button button2 = this.createToggleButton("Färbe erfüllende Zustände", "Farbe zurücksetzen", (button) -> {
         this.is_colored = !this.is_colored;
         button.setText(this.is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
         this.circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), this.is_colored);
      });
      this.color_button = button2;
      Button button3 = this.createToggleButton("Zeige Formelbaum komplett", "Formelbaum ausblenden", (button) -> {
         this.isFormulaTreeCompleteVisible = !this.isFormulaTreeCompleteVisible;
         if (this.formel_baum_stage == null) {
            String var10002 = zustandsformel.getFormel_string().replace("Formelende", "");
            this.create_formelbaum_stage(500, "Formelbaum für: " + var10002 + " in Normelform: " + zustandsformel.getFormel_string_normal_form().replace("Formelende", ""), this.shared_button_right, "");
         } else {
            this.setFormelBaumStageVisibility(this.isFormulaTreeCompleteVisible);
         }

         button.setText(this.isFormulaTreeCompleteVisible ? "Formelbaum ausblenden" : "Zeige Formelbaum komplett");
      });
      this.shared_button_right = button3;
      sidebar.getChildren().addAll(button1, button2, button3);
      Button toggleButton = new Button("+");
      toggleButton.setPrefSize(30.0D, 30.0D);
      toggleButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15;");
      toggleButton.setOnAction((e) -> {
         this.toggleSidebar(sidebar);
         toggleButton.setText(this.isExpanded ? "+" : "-");
      });
      StackPane container = new StackPane();
      HBox sidebarContainer = new HBox(new Node[]{sidebar});
      sidebarContainer.setAlignment(Pos.CENTER_LEFT);
      HBox toggleButtonContainer = new HBox(new Node[]{toggleButton});
      toggleButtonContainer.setAlignment(Pos.CENTER_LEFT);
      toggleButtonContainer.setPadding(new Insets(0.0D, 0.0D, 0.0D, -15.0D));
      toggleButtonContainer.setMaxHeight(1.0D);
      toggleButtonContainer.setClip((Node)null);
      container.getChildren().addAll(sidebarContainer, toggleButtonContainer);
      root.setRight(container);
      this.sidebar_right = sidebar;
      this.sidebar_container_right = container;
   }

   private void toggleSidebar(VBox sidebar) {
      this.isExpanded = !this.isExpanded;
      sidebar.setVisible(this.isExpanded);
      sidebar.setManaged(this.isExpanded);
      if (this.isExpanded) {
         StackPane.setAlignment(sidebar, Pos.CENTER_LEFT);
      } else {
         StackPane.setAlignment(sidebar, Pos.CENTER_RIGHT);
      }

   }

   private Button createToggleButton(String textOn, String textOff, Consumer<Button> action) {
      Button button = new Button(textOn);
      button.setOnAction((e) -> {
         action.accept(button);
      });
      return button;
   }

   private ScrollPane erstelleUmformungsLabelsUndFügeHinzu(HBox formelbox, Zustandsformel zustandsformel, BorderPane parentContainer) {
      zustandsformel.turn_to_normal_form();
      VBox gesamteVBox = new VBox();
      gesamteVBox.setPadding(new Insets(0.0D));
      gesamteVBox.getChildren().add(formelbox);
      VBox umformungsVBox = new VBox();
      umformungsVBox.setPadding(new Insets(0.0D));
      Iterator var6 = zustandsformel.getErsetzungen().iterator();

      while(var6.hasNext()) {
         Umformung umformung = (Umformung)var6.next();
         String nach_ersetzung_ohne_FE = umformung.getNach_der_Ersetzung();
         if (nach_ersetzung_ohne_FE.contains("Formelende")) {
            nach_ersetzung_ohne_FE = nach_ersetzung_ohne_FE.replace("Formelende", "");
         }

         String vor_ersetzung_ohne_FE = umformung.getVor_der_Ersetzung();
         if (umformung.getVor_der_Ersetzung().contains("Formelende")) {
            vor_ersetzung_ohne_FE = vor_ersetzung_ohne_FE.replace("Formelende", "");
         }

         String regelText = umformung.getErsetzt_mit_regel_nummer() + ": ";
         Text fettText = new Text(regelText);
         fettText.setStyle("-fx-font-weight: bold;");
         Text normalerText = new Text(vor_ersetzung_ohne_FE + " -> " + nach_ersetzung_ohne_FE);
         TextFlow textFlow = new TextFlow(new Node[]{fettText, normalerText});
         Label label = new Label();
         label.setGraphic(textFlow);
         label.setStyle("-fx-font-size: 14px; -fx-padding: 0;");
         label.setMinHeight(Double.NEGATIVE_INFINITY);
         VBox.setMargin(label, new Insets(0.0D));
         umformungsVBox.getChildren().add(label);
      }

      umformungsVBox.setSpacing(1.0D);
      ScrollPane scrollPane = new ScrollPane();
      scrollPane.setContent(umformungsVBox);
      scrollPane.setMaxHeight(100.0D);
      scrollPane.setFitToWidth(true);
      gesamteVBox.getChildren().add(scrollPane);
      this.untere_Ausgabe = gesamteVBox;
      parentContainer.setBottom(gesamteVBox);
      return scrollPane;
   }

   private void setFormelBaumStageVisibility(boolean visible) {
      if (this.formel_baum_stage != null) {
         if (visible) {
            this.formel_baum_stage.show();
         } else {
            this.formel_baum_stage.hide();
         }
      } else {
         System.out.println("Die Stage 'formel_baum_stage' ist null.");
      }

   }

   private void create_formelbaum_stage(int x_offset, String zustandsformel_String, Button shared_button, String extraBeschriftung) {
      this.formel_baum_stage = this.ctl_baum.zeichneBaum(700.0D, 500.0D, shared_button);
      this.formel_baum_stage.setTitle(zustandsformel_String + extraBeschriftung);
      this.bringSidebarsToFront();
   }

   private void bringSidebarsToFront() {
      if (this.sidebar_right != null) {
         this.sidebar_right.toFront();
      }

      if (this.sidebar_left != null) {
         this.sidebar_left.toFront();
      }

   }

   private StackPane create_reducedSidebar(Zustandsformel zustandsformel, AnchorPane root, Transitionssystem ts, int x_offset, String extra_beschriftung) {
      this.ctl_baum = new CTL_Formel_Baum(zustandsformel.getStart_der_rekursiven_Definition(), ts);
      VBox local_sidebar = new VBox();
      local_sidebar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
      local_sidebar.setSpacing(20.0D);
      local_sidebar.setMaxHeight(200.0D);
      local_sidebar.setMaxWidth(200.0D);
      Label formulaLabel = new Label(zustandsformel.getFormel_string());
      formulaLabel.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 14;");
      Button button2 = this.createToggleButton("Färbe erfüllende Zustände", "Farbe zurücksetzen", (button) -> {
         this.is_colored = !this.is_colored;
         button.setText(this.is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
         this.circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), this.is_colored);
      });
      button2.textProperty().addListener((observable, oldValue, newValue) -> {
         this.is_colored = "Farbe zurücksetzen".equals(newValue);
      });
      this.color_button = button2;
      Button button4 = this.createToggleButton("Erfüllende Mengen sichtbar machen", "Erfüllende Mengen unsichtbar", (button) -> {
         this.isSatisfyingSetsVisible = !this.isSatisfyingSetsVisible;
         this.ctl_baum.setLösungsmengen_anzeigen(this.isSatisfyingSetsVisible);
      });
      Button button3 = this.createToggleButton("Zeige Formelbaum", "Formelbaum ausblenden", (button) -> {
         this.isFormulaTreeCompleteVisible = !this.isFormulaTreeCompleteVisible;
         if (this.formel_baum_stage == null) {
            this.create_formelbaum_stage(x_offset, "Formelbaum für: " + zustandsformel.getFormel_string().replace("Formelende", "") + " in Normelform: " + zustandsformel.getFormel_string_normal_form(), this.shared_button_right, extra_beschriftung);
         } else {
            this.setFormelBaumStageVisibility(this.isFormulaTreeCompleteVisible);
         }

         button.setText(this.isFormulaTreeCompleteVisible ? "Formelbaum ausblenden" : "Zeige Formelbaum");
      });
      this.shared_button_right = button3;
      local_sidebar.getChildren().addAll(formulaLabel, button2, button3);
      Button toggleButton = new Button("-");
      toggleButton.setPrefSize(30.0D, 30.0D);
      toggleButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15;");
      toggleButton.setOnAction((e) -> {
         this.toggleSidebar(local_sidebar);
         toggleButton.setText(this.isExpanded ? "-" : "+");
      });
      StackPane container = new StackPane();
      HBox sidebarContainer = new HBox(new Node[]{local_sidebar});
      HBox toggleButtonContainer = new HBox(new Node[]{toggleButton});
      toggleButtonContainer.setMaxHeight(1.0D);
      toggleButtonContainer.setClip((Node)null);
      container.getChildren().addAll(sidebarContainer, toggleButtonContainer);
      return container;
   }

   public void createReducedSidebarLeft(AnchorPane root, Zustandsformel zustandsformel, Transitionssystem ts, int x_offset, String extra_beschriftung) {
      StackPane container = this.create_reducedSidebar(zustandsformel, root, ts, x_offset, extra_beschriftung);
      HBox sidebarContainerLeft = (HBox)container.getChildren().get(0);
      this.sidebar_left = (VBox)sidebarContainerLeft.getChildren().get(0);
      this.sidebar_container_left = container;
      HBox toggleButtonContainerLeft = (HBox)container.getChildren().get(1);
      sidebarContainerLeft.setAlignment(Pos.CENTER_LEFT);
      toggleButtonContainerLeft.setAlignment(Pos.CENTER_RIGHT);
      toggleButtonContainerLeft.setPadding(new Insets(0.0D, -30.0D, 0.0D, 0.0D));
      toggleButtonContainerLeft.setMaxHeight(1.0D);
      sidebarContainerLeft.toFront();
      AnchorPane.setLeftAnchor(container, 12.0D);
      AnchorPane.setTopAnchor(container, root.getHeight() / 2.0D - 70.0D);
      root.getChildren().add(container);
   }

   public void createReducedSidebarRight(AnchorPane root, Zustandsformel zustandsformel, Transitionssystem ts, int x_offset, String extra_beschriftung) {
      StackPane container = this.create_reducedSidebar(zustandsformel, root, ts, x_offset, extra_beschriftung);
      HBox sidebarContainerRight = (HBox)container.getChildren().get(0);
      this.sidebar_right = (VBox)sidebarContainerRight.getChildren().get(0);
      this.sidebar_container_right = container;
      HBox toggleButtonContainerRight = (HBox)container.getChildren().get(1);
      sidebarContainerRight.setAlignment(Pos.CENTER_RIGHT);
      toggleButtonContainerRight.setAlignment(Pos.CENTER_LEFT);
      toggleButtonContainerRight.setPadding(new Insets(0.0D, 0.0D, 0.0D, -30.0D));
      toggleButtonContainerRight.setMaxHeight(1.0D);
      sidebarContainerRight.toFront();
      AnchorPane.setRightAnchor(container, 12.0D);
      AnchorPane.setTopAnchor(container, root.getHeight() / 2.0D - 70.0D);
      root.getChildren().add(container);
   }

   public void removeSidebar() {
      Parent parent;
      if (this.sidebar_container_right != null) {
         parent = this.sidebar_container_right.getParent();
         if (parent instanceof Pane) {
            ((Pane)parent).getChildren().remove(this.sidebar_container_right);
            if (this.formel_baum_stage != null) {
               ((Pane)parent).getChildren().remove(this.formel_baum_stage);
               this.formel_baum_stage = null;
            }

            this.circle_builder.färbeKreiseNachZustand(new HashSet(), false);
            if (this.normalformpane != null) {
               ((Pane)parent).getChildren().remove(this.normalformpane);
               this.normalformpane = null;
            }

            this.isSolutionNormalFormVisible = false;
            this.is_colored = false;
            this.isFormulaTreeCompleteVisible = false;
            this.isSatisfyingSetsVisible = false;
         }
      }

      if (this.sidebar_container_left != null) {
         parent = this.sidebar_container_left.getParent();
         if (parent instanceof Pane) {
            ((Pane)parent).getChildren().remove(this.sidebar_container_left);
            if (this.formel_baum_stage != null) {
               ((Pane)parent).getChildren().remove(this.formel_baum_stage);
               this.formel_baum_stage = null;
            }

            this.circle_builder.färbeKreiseNachZustand(new HashSet(), false);
            if (this.normalformpane != null) {
               ((Pane)parent).getChildren().remove(this.normalformpane);
               this.normalformpane = null;
            }

            this.isSolutionNormalFormVisible = false;
            this.is_colored = false;
            this.isFormulaTreeCompleteVisible = false;
            this.isSatisfyingSetsVisible = false;
         }
      }

   }

   public void handleButtonClick(Button button, Zustandsformel zustandsformel, Transitionssystem ts) {
      this.is_colored = !this.is_colored;
      button.setText(this.is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
      this.circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), this.is_colored);
   }

   public Button getColor_button() {
      return this.color_button;
   }

   public static void synchronizeButtonText(Button button1, Button button2) {
      button1.textProperty().addListener((observable, oldValue, newValue) -> {
         if (!newValue.equals(button2.getText())) {
            button2.setText(newValue);
         }

      });
      button2.textProperty().addListener((observable, oldValue, newValue) -> {
         if (!newValue.equals(button1.getText())) {
            button1.setText(newValue);
         }

      });
   }
}
