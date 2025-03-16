    package GUI;


import CTL_Backend.CTL_Formel_Baum;
import CTL_Backend.ErfüllendeMengeExeption;
import CTL_Backend.ErrorDialog;
import CTL_Backend.NormalFormException;
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

//Klasse die Sidebars verwaltet
public class SidebarHandler {
	
	//kontrolliert ob Sidebar eingeklappt
   private boolean isExpanded = true;
   
   //bis zu zwei Sidebars sind denkbar links und rechts
   private StackPane sidebar_container_right;
   private StackPane sidebar_container_left;
   
   //VBoxen der Sidebars
   private VBox sidebar_right = null;
   private VBox sidebar_left = null;
   
   //Diverse Flags um Sichtbarkeiten zu verwalten
   private boolean isSolutionNormalFormVisible = false;
   private boolean is_colored = false;
   private boolean isFormulaTreeCompleteVisible = false;
   private boolean isSatisfyingSetsVisible = false;
   
   //Referenz auf den Lösungsmenge färben Button, um hin an Formelbaum-Stage übergeben zu können
   private Button color_button = null;
   
   //zwischen Sidebars und FormelbaumStage geteilte Button
   private Button shared_button_right = null;
   private Button shared_button_left = null;
   
   //für die Darstellung der Umformung in Normalform
   ScrollPane normalformpane = null;
   
   //Vbox für die Umformung in Normalform
   VBox untere_Ausgabe = null;
   
   //Formalbaum Stage
   Stage formel_baum_stage = null;
   CTL_Formel_Baum ctl_baum;
   
   //Circle-Builder Referenz
   private Circle_Group_Builder circle_builder = null;
   
   //Konstruktor
   public SidebarHandler(Circle_Group_Builder circle_builder) {
      this.circle_builder = circle_builder;
   }
   
   //erstellt eine rechts ausgerichtete Sidebar
   public void createSidebar(HBox formelbox, Zustandsformel zustandsformel, BorderPane root, Transitionssystem ts){
	    // erzeuge den CTL-Formelbaum basierend auf der Zustandsformel und dem Transitionssystem
	    this.ctl_baum = new CTL_Formel_Baum(zustandsformel.getStart_der_rekursiven_Definition(), ts);

	    // Erstellen der Sidebar als vertikales Layout (VBox)
	    VBox sidebar = new VBox();
	    sidebar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
	    sidebar.setSpacing(20.0D); // Abstand zwischen den Elementen
	    sidebar.setPrefHeight(400.0D); // Festlegen der bevorzugten Höhe der Sidebar

	    // Button: Anzeigen/Verstecken der Lösung in Normalform
	    Button button1 = this.createToggleButton("Zeige Lösung Normalform", "Verstecke Lösung Normalform", (button) -> {
	        try {
	        	toggleNormalFormSolution(formelbox, zustandsformel, root, button);
	        } catch (NormalFormException e) {
	        	// Anzeigen einer Infobox bei Fehler
	        	 ErrorDialog.show(
	        	            "Fehler bei der Umformung in die Normalform",
	        	            "Ein Fehler ist aufgetreten: " + e.getMessage() + 
	        	            "\nBitte starten Sie das Programm mit einer anderen Eingabe neu."
	        	        );
	        	 removeSidebar(root);
	        }
	    });
	    
	    Tooltips_für_Buttons.setTooltip_normalform(button1);

	    // Button: Zustände einfärben oder Farben zurücksetzen
	    Button button2 = this.createToggleButton("Färbe erfüllende Zustände", "Farbe zurücksetzen", (button) -> {
	        this.is_colored = !this.is_colored;
	        
	        // Aktualisieren des Button-Textes
	        button.setText(this.is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
	        
	        // Aufrufen der Methode zum Einfärben basierend auf der Lösungsmenge
	        this.circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), this.is_colored);
	    });
	    this.color_button = button2; // Speichern des Buttons für zukünftige Verweise
	    
	    Tooltips_für_Buttons.setTooltip_färben(button2);

	    // Button: Kompletten Formelbaum anzeigen oder ausblenden
	    Button button3 = this.createToggleButton("Zeige Formelbaum komplett", "Formelbaum ausblenden", (button) -> {
	        this.isFormulaTreeCompleteVisible = !this.isFormulaTreeCompleteVisible;
	        
	        // Erstellen des Formelbaum-Stages, falls noch nicht vorhanden
	        if (this.formel_baum_stage == null) {
	            String formelString = zustandsformel.getFormel_string().replace("Formelende", "");
	            String normalForm = zustandsformel.getFormel_string_normal_form().replace("Formelende", "");
	            try {
	                this.create_formelbaum_stage(500, 
	                    "Formelbaum für: " + formelString + 
	                    " in Normelform: " + normalForm, 
	                    this.shared_button_right, "");
	            } catch (ErfüllendeMengeExeption e) {
	                // Anzeigen einer Infobox bei Fehler
	                ErrorDialog.show(
	                    "Fehler beim Berechnen der erfüllenden Menge",
	                    "Ein Fehler ist aufgetreten: " + e.getMessage() + 
	                    "\nBitte überprüfen Sie die Eingabe und versuchen Sie es erneut."
	                );
	            }

	        } else {
	            // Sichtbarkeit des Formelbaums aktualisieren
	            this.setFormelBaumStageVisibility(this.isFormulaTreeCompleteVisible);
	        }

	        // Aktualisieren des Button-Textes
	        button.setText(this.isFormulaTreeCompleteVisible ? "Formelbaum ausblenden" : "Zeige Formelbaum komplett");
	    });
	    this.shared_button_right = button3; // Speichern des Buttons für zukünftige Verweise
	    
	    Tooltips_für_Buttons.setTooltip_formelbaum(button3);
	    
	    // Hinzufügen der Buttons zur Sidebar
	    sidebar.getChildren().addAll(button1, button2, button3);

	    // Toggle-Button: Sidebar ein-/ausblenden
	    Button toggleButton = new Button("+");
	    toggleButton.setPrefSize(30.0D, 30.0D);
	    toggleButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15;");
	    toggleButton.setOnAction((e) -> {
	        // Sidebar umschalten (sichtbar/versteckt)
	        this.toggleSidebar(sidebar);
	        toggleButton.setText(this.isExpanded ? "+" : "-"); // Button-Text entsprechend anpassen
	    });

	    // Container für Sidebar und Toggle-Button
	    StackPane container = new StackPane();
	    HBox sidebarContainer = new HBox(new Node[]{sidebar});
	    sidebarContainer.setAlignment(Pos.CENTER_LEFT); // Positionierung der Sidebar
	    HBox toggleButtonContainer = new HBox(new Node[]{toggleButton});
	    toggleButtonContainer.setAlignment(Pos.CENTER_LEFT); // Positionierung des Toggle-Buttons
	    toggleButtonContainer.setPadding(new Insets(0.0D, 0.0D, 0.0D, -15.0D)); // Abstandseinstellungen
	    toggleButtonContainer.setMaxHeight(1.0D);
	    toggleButtonContainer.setClip((Node)null); // Kein Clipping für den Toggle-Button

	    // Hinzufügen von Sidebar und Toggle-Button zum Container
	    container.getChildren().addAll(sidebarContainer, toggleButtonContainer);

	    // Platzieren der Sidebar rechts im Root-Layout
	    root.setRight(container);
	    this.sidebar_right = sidebar; // Speichern der Sidebar-Referenz
	    this.sidebar_container_right = container; // Speichern des Container-Referenz
	}
   
   //Mehtode zum Togglen und Erzeugen der NormalformPane, gekapslet wegen Exeption werfen
   private void toggleNormalFormSolution(HBox formelbox, Zustandsformel zustandsformel, BorderPane root, Button button) throws NormalFormException {
	    // Umschalten der Sichtbarkeit der Lösung in der Normalform
	    this.isSolutionNormalFormVisible = !this.isSolutionNormalFormVisible;

	    // Erstellen des Normalform-Panes, falls es noch nicht existiert
	    if (this.normalformpane == null) {
	        // Kann eine NormalFormException werfen
	        this.normalformpane = this.erstelle_Umformung_in_Normalform_ScrollPane(formelbox, zustandsformel, root);
	    } else {
	        // Sichtbarkeit und Verwaltung des Normalform-Panes aktualisieren
	        this.normalformpane.setVisible(this.isSolutionNormalFormVisible);
	        this.normalformpane.setManaged(this.isSolutionNormalFormVisible);
	    }

	    // Aktualisieren des Button-Textes basierend auf der Sichtbarkeit
	    button.setText(this.isSolutionNormalFormVisible ? "Verstecke Lösung Normalform" : "Zeige Lösung Normalform");
	}


   //Mehtode die Toogled ob die Sidebar
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
   
   //Hilfsmehtode die Toogle Button erzeugt und Event anhängt
   private Button createToggleButton(String textOn, String textOff, Consumer<Button> action) {
      Button button = new Button(textOn);
      button.setOnAction((e) -> {
         action.accept(button);
      });
      return button;
   }

   private ScrollPane erstelle_Umformung_in_Normalform_ScrollPane(HBox formelbox, Zustandsformel zustandsformel, BorderPane parentContainer) throws NormalFormException {
	    
	    try {
	        // Konvertiert die Zustandsformel in eine Normalform
	        zustandsformel.turn_to_normal_form();
	    } catch (Exception e) {
	        // Neue Exception werfen mit Kontext
	        throw new NormalFormException("Fehler bei der Umformung in die Normalform.", e);
	    }

	    // Hauptcontainer für die Anzeige von Formeln und Transformationen
	    VBox gesamteVBox = new VBox();
	    gesamteVBox.setPadding(new Insets(0.0D));
	    gesamteVBox.getChildren().add(formelbox);

	    // Container für Transformationen
	    VBox umformungsVBox = new VBox();
	    umformungsVBox.setPadding(new Insets(0.0D));

	    // Iteration über alle Transformationen der Zustandsformel
	    for(Umformung umformung:zustandsformel.getErsetzungen()) {

	        // Entfernt das Wort "Formelende", falls es im Transformationsergebnis enthalten ist
	        String nach_ersetzung_ohne_FE = umformung.getNach_der_Ersetzung();
	        if (nach_ersetzung_ohne_FE.contains("Formelende")) {
	            nach_ersetzung_ohne_FE = nach_ersetzung_ohne_FE.replace("Formelende", "");
	        }

	        // Entfernt das Wort "Formelende", falls es im ursprünglichen Ausdruck enthalten ist
	        String vor_ersetzung_ohne_FE = umformung.getVor_der_Ersetzung();
	        if (vor_ersetzung_ohne_FE .contains("Formelende")) {
	            vor_ersetzung_ohne_FE = vor_ersetzung_ohne_FE.replace("Formelende", "");
	        }

	        // Erzeugt den Text der Regelnummer und hebt ihn fett hervor
	        String regelText = umformung.getErsetzt_mit_regel_nummer() + ": ";
	        Text fettText = new Text(regelText);
	        fettText.setStyle("-fx-font-weight: bold;");

	        // Fügt den Transformationstext hinzu
	        Text normalerText = new Text(vor_ersetzung_ohne_FE + " -> " + nach_ersetzung_ohne_FE);
	        TextFlow textFlow = new TextFlow(new Node[]{fettText, normalerText});

	        // Erstellt ein Label für jede Transformation
	        Label label = new Label();
	        label.setGraphic(textFlow);
	        label.setStyle("-fx-font-size: 14px; -fx-padding: 0;");
	        label.setMinHeight(Double.NEGATIVE_INFINITY);
	        VBox.setMargin(label, new Insets(0.0D));
	        umformungsVBox.getChildren().add(label);
	        System.out.println(normalerText.getText());
	    }

	    // Setzt den Abstand zwischen den Umformungsschritten
	    umformungsVBox.setSpacing(1.0D);

	    // ScrollPane für die Transformationen
	    ScrollPane scrollPane = new ScrollPane();
	    scrollPane.setContent(umformungsVBox);
	    scrollPane.setMaxHeight(100.0D);
	    scrollPane.setFitToWidth(true);

	    // Fügt den ScrollPane zum Hauptcontainer hinzu
	    gesamteVBox.getChildren().add(scrollPane);

	    // Setzt die untere Ausgabe im Parent-Container
	    this.untere_Ausgabe = gesamteVBox;
	    parentContainer.setBottom(gesamteVBox);

	    // Gibt den ScrollPane zurück
	    return scrollPane;
	}

   
   //MEthode die Sichtbarkeit der Formelbaum Stage steuert
   private void setFormelBaumStageVisibility(boolean visible) {
      if (this.formel_baum_stage != null) {
         if (visible) {
            this.formel_baum_stage.show();
         } else {
            this.formel_baum_stage.hide();
         }
      }
   }
   
   //Methode die Formelbaum Stage erzeugt
   private void create_formelbaum_stage(int x_offset, String zustandsformel_String, Button shared_button, String extraBeschriftung) throws ErfüllendeMengeExeption {
      this.formel_baum_stage = this.ctl_baum.zeichneBaum(700.0D, 500.0D, shared_button);
      this.formel_baum_stage.setTitle(zustandsformel_String + extraBeschriftung);
      this.bringSidebarsToFront();
   }
   
   //Hilfsmethode die die Sidebars in den Vordergrund setzt
   private void bringSidebarsToFront() {
      if (this.sidebar_right != null) {
         this.sidebar_right.toFront();
      }

      if (this.sidebar_left != null) {
         this.sidebar_left.toFront();
      }

   }

	// Erstellt eine reduzierte Sidebar und gibt diese zurück
	private StackPane create_reducedSidebar(Zustandsformel zustandsformel, AnchorPane root, Transitionssystem ts, int x_offset, String extra_beschriftung) {
	    // Initialisierung des CTL-Formel-Baums
	    this.ctl_baum = new CTL_Formel_Baum(zustandsformel.getStart_der_rekursiven_Definition(), ts);
	
	    // Erstellen der Sidebar-Komponenten
	    VBox local_sidebar = new VBox();
	    local_sidebar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
	    local_sidebar.setSpacing(20.0);
	    local_sidebar.setMaxHeight(200.0);
	    local_sidebar.setMaxWidth(200.0);
	
	    // Label für die Formel
	    Label formulaLabel = new Label(zustandsformel.getFormel_string());
	    formulaLabel.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 14;");
	
	    // Button für das Färben von Zuständen
	    Button button2 = this.createToggleButton("Färbe erfüllende Zustände", "Farbe zurücksetzen", (button) -> {
	        this.is_colored = !this.is_colored;
	        button.setText(this.is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
	        this.circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), this.is_colored);
	    });
	    button2.textProperty().addListener((observable, oldValue, newValue) -> {
	        this.is_colored = "Farbe zurücksetzen".equals(newValue);
	    });
	    this.color_button = button2;
	    
	    Tooltips_für_Buttons.setTooltip_färben(button2);
	
	    // Button für das Anzeigen des Formelbaums
	    Button button3 = this.createToggleButton("Zeige Formelbaum", "Formelbaum ausblenden", (button) -> {
	        this.isFormulaTreeCompleteVisible = !this.isFormulaTreeCompleteVisible;
	        if (this.formel_baum_stage == null) {
	            // Erstellt den Formelbaum-Stage, falls nicht vorhanden
	        	try {
	        	    this.create_formelbaum_stage(x_offset, 
	        	        "Formelbaum für: " + zustandsformel.getFormel_string().replace("Formelende", "") + 
	        	        " in Normelform: " + zustandsformel.getFormel_string_normal_form(), 
	        	        this.shared_button_right, extra_beschriftung);
	        	} catch (ErfüllendeMengeExeption e) {
	        	    // Anzeigen einer Infobox bei Fehler
	        	    ErrorDialog.show(
	        	        "Fehler beim Berechnen der erfüllenden Menge",
	        	        "Ein Fehler ist aufgetreten: " + e.getMessage() + 
	        	        "\nBitte überprüfen Sie die Eingabe und versuchen Sie es erneut."
	        	    );
	        	};
	        } else {
	            // Setzt die Sichtbarkeit des Formelbaum-Stages
	            this.setFormelBaumStageVisibility(this.isFormulaTreeCompleteVisible);
	        }
	        button.setText(this.isFormulaTreeCompleteVisible ? "Formelbaum ausblenden" : "Zeige Formelbaum");
	    });
	    this.shared_button_right = button3;
	    
	    Tooltips_für_Buttons.setTooltip_formelbaum(button3);
	
	    // Hinzufügen der Komponenten zur Sidebar
	    local_sidebar.getChildren().addAll(formulaLabel, button2, button3);
	
	    // Toggle-Button zum Ein- und Ausklappen der Sidebar
	    Button toggleButton = new Button("-");
	    toggleButton.setPrefSize(30.0, 30.0);
	    toggleButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15;");
	    toggleButton.setOnAction((e) -> {
	        this.toggleSidebar(local_sidebar);
	        toggleButton.setText(this.isExpanded ? "-" : "+");
	    });
	
	    // Verpacken der Sidebar und des Toggle-Buttons in Container
	    StackPane container = new StackPane();
	    HBox sidebarContainer = new HBox(new Node[]{local_sidebar});
	    HBox toggleButtonContainer = new HBox(new Node[]{toggleButton});
	    toggleButtonContainer.setMaxHeight(1.0);
	    toggleButtonContainer.setClip(null);
	    container.getChildren().addAll(sidebarContainer, toggleButtonContainer);
	
	    return container;
	}

	// Erzeugt eine reduzierte Sidebar auf der linken Seite
	public void createReducedSidebarLeft(AnchorPane root, Zustandsformel zustandsformel, Transitionssystem ts, int x_offset, String extra_beschriftung) {
	    // Erstellt die reduzierte Sidebar
	    StackPane container = this.create_reducedSidebar(zustandsformel, root, ts, x_offset, extra_beschriftung);
	
	    // Konfiguriert den linken Sidebar-Container
	    HBox sidebarContainerLeft = (HBox) container.getChildren().get(0);
	    this.sidebar_left = (VBox) sidebarContainerLeft.getChildren().get(0);
	    this.sidebar_container_left = container;
	    
	    //Positioniert und formatiert die Buttonbox
	    HBox toggleButtonContainerLeft = (HBox) container.getChildren().get(1);
	    sidebarContainerLeft.setAlignment(Pos.CENTER_LEFT);
	    toggleButtonContainerLeft.setAlignment(Pos.CENTER_RIGHT);
	    toggleButtonContainerLeft.setPadding(new Insets(0.0, -30.0, 0.0, 0.0));
	    toggleButtonContainerLeft.setMaxHeight(1.0);
	    sidebarContainerLeft.toFront();
	
	    // Positioniert die Sidebar im AnchorPane
	    AnchorPane.setLeftAnchor(container, 12.0);
	    AnchorPane.setTopAnchor(container, root.getHeight() / 2.0 - 70.0);
	    root.getChildren().add(container);
	}
	
	// Erzeugt eine reduzierte Sidebar auf der rechten Seite
	public void createReducedSidebarRight(AnchorPane root, Zustandsformel zustandsformel, Transitionssystem ts, int x_offset, String extra_beschriftung) {
	    // Erstellt die reduzierte Sidebar
	    StackPane container = this.create_reducedSidebar(zustandsformel, root, ts, x_offset, extra_beschriftung);
	
	    // Konfiguriert den rechten Sidebar-Container
	    HBox sidebarContainerRight = (HBox) container.getChildren().get(0);
	    this.sidebar_right = (VBox) sidebarContainerRight.getChildren().get(0);
	    this.sidebar_container_right = container;
	    
	    //Positioniert und formatiert die Buttonbox
	    HBox toggleButtonContainerRight = (HBox) container.getChildren().get(1);
	    sidebarContainerRight.setAlignment(Pos.CENTER_RIGHT);
	    toggleButtonContainerRight.setAlignment(Pos.CENTER_LEFT);
	    toggleButtonContainerRight.setPadding(new Insets(0.0, 0.0, 0.0, -30.0));
	    toggleButtonContainerRight.setMaxHeight(1.0);
	    sidebarContainerRight.toFront();
	
	    // Positioniert die Sidebar im AnchorPane
	    AnchorPane.setRightAnchor(container, 12.0);
	    AnchorPane.setTopAnchor(container, root.getHeight() / 2.0 - 70.0);
	    root.getChildren().add(container);
	}

   
	// Methode, die Sidebar entfernt
	public void removeSidebar(Pane drawing_Pane) {
		
		//merker der den Zustand der Sidebars speichert
		boolean flag_side_bar_was_visible = side_bar_availible();

	    // Entfernt den rechten Sidebar-Container
	    removeSidebarContainer(this.sidebar_container_right);
	    this.sidebar_container_right = null;
	    // Entfernt den linken Sidebar-Container
	    removeSidebarContainer(this.sidebar_container_left);
	    this.sidebar_container_left = null;
	    
	  //gelb färben wenn Sidebars da waren 
	    if(flag_side_bar_was_visible) {
	    	this.circle_builder.colorAllCirclesYellow(drawing_Pane);
	    }
	    

	}
	
	// Hilfsmethode, um einen gegebenen Sidebar-Container zu entfernen
	private void removeSidebarContainer(Parent sidebarContainer) {
	    if (sidebarContainer != null) {
	        Parent parent = sidebarContainer.getParent();
	        if (parent instanceof Pane) {
	            Pane pane = (Pane) parent;
	            // Entfernt den Sidebar-Container aus der übergeordneten Pane
	            pane.getChildren().remove(sidebarContainer);
	            
	            // Entfernt den Formel-Baum-Stage, falls vorhanden
	            if (this.formel_baum_stage != null) {
	            	this.formel_baum_stage.close();
	                this.formel_baum_stage = null;
	            }
	
	            // Setzt die Farben der Kreise zurück
	            this.circle_builder.färbeKreiseNachZustand(new HashSet<>(), false);
	
	            // Entfernt das Normalform-Pane, falls vorhanden
	            if (this.normalformpane != null) {
	                Parent parent_ = this.normalformpane.getParent(); // Den Parent-Node ermitteln
	                if (parent_ instanceof Pane) { // Sicherstellen, dass der Parent ein Pane ist
	                    ((Pane) parent_).getChildren().remove(this.normalformpane);
	                    System.out.println("Normalformpane wurde entfernt.");
	                } else {
	                    System.out.println("Parent ist kein Pane oder null.");
	                }
	                this.normalformpane = null; // Referenz zurücksetzen
	            }

	
	            // Setzt die Sichtbarkeits-Flags zurück
	            this.isSolutionNormalFormVisible = false;
	            this.is_colored = false;
	            this.isFormulaTreeCompleteVisible = false;
	            this.isSatisfyingSetsVisible = false;
	        }
	    }
	}
   
   //Verwaltet das Klicken auf den Color-Button
   public void handleColorButtonClick(Button button, Zustandsformel zustandsformel, Transitionssystem ts) {
      this.is_colored = !this.is_colored;
      button.setText(this.is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
      this.circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), this.is_colored);
   }

   //gibt Color-Button zurück
   public Button getColor_button() {
      return this.color_button;
   }
   
   //Methode die Buttons zwischen der Stage und der Sidebar synchronisiert
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

	private boolean side_bar_availible() {
		return sidebar_container_left != null || sidebar_container_right != null;
	}
}
