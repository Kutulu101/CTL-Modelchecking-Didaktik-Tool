package GUI;

import java.util.HashSet;
import java.util.function.Consumer;

import CTL_Backend.CTL_Formel_Baum;
import CTL_Backend.Transitionssystem;
import CTL_Backend.Umformung;
import CTL_Backend.Zustand;
import CTL_Backend.Zustandsformel;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class SidebarHandler {

    private boolean isExpanded = true; // Standardmäßig ausgeklappt
    private StackPane sidebar_container_right;
    private StackPane sidebar_container_left;
    private VBox sidebar_right =null;
    private VBox sidebar_left=null;
    

    // Toggle-Variablen für jeden Button
    private boolean isSolutionNormalFormVisible = false;
    private boolean is_colored = false;
    private boolean isFormulaTreeCompleteVisible = false;
    private boolean isSatisfyingSetsVisible = false;
    
    private Button color_button = null;

    
    //Scrollpane und Vbox für Normalform
    ScrollPane normalformpane = null;
    VBox untere_Ausgabe = null;
    //
    StackPane formel_baum_pane = null;
    //ctl_baum
    CTL_Formel_Baum ctl_baum;
    
    private Circle_Group_Builder circle_builder = null;
    
    public SidebarHandler(Circle_Group_Builder circle_builder){
    	this.circle_builder = circle_builder;
    }

    public void createSidebar(HBox formelbox, Zustandsformel zustandsformel, BorderPane root, Transitionssystem ts) {
       
    	this.ctl_baum = new CTL_Formel_Baum(zustandsformel.getStart_der_rekursiven_Definition(), ts);
    	
    	
    	// Erstelle die Sidebar und setze deren Layout-Eigenschaften
        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
        sidebar.setSpacing(20); 
        sidebar.setPrefHeight(400);

     // Hinzufügen der Buttons zur Sidebar
        Button button1 = createToggleButton("Zeige Lösung Normalform", "Verstecke Lösung Normalform", (button) -> {
            isSolutionNormalFormVisible = !isSolutionNormalFormVisible;
            if (this.normalformpane == null) {
                this.normalformpane = this.erstelleUmformungsLabelsUndFügeHinzu(formelbox, zustandsformel, root);
            } else {
                this.normalformpane.setVisible(isSolutionNormalFormVisible);
                this.normalformpane.setManaged(isSolutionNormalFormVisible); 
            }
            // Toggle button text
            button.setText(isSolutionNormalFormVisible ? "Verstecke Lösung Normalform" : "Zeige Lösung Normalform");
        });

        Button button2 = createToggleButton("Färbe erfüllende Zustände", "Farbe zurücksetzen", (button) -> {
            is_colored = !is_colored;
            button.setText(is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
            circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), is_colored);
        });
        color_button = button2;

        Button button3 = createToggleButton("Zeige Formelbaum komplett", "Formelbaum ausblenden", (button) -> {
            isFormulaTreeCompleteVisible = !isFormulaTreeCompleteVisible;
            if (this.formel_baum_pane == null) {
                this.bringe_formelbaum_auf_pane(root, -100, 0,500);
            } else {
                this.formel_baum_pane.setVisible(isFormulaTreeCompleteVisible);
                this.formel_baum_pane.setManaged(isFormulaTreeCompleteVisible); 
            }
            // Toggle button text
            button.setText(isFormulaTreeCompleteVisible ? "Formelbaum ausblenden" : "Zeige Formelbaum komplett");
        });

	    Button button4 = createToggleButton("Erfüllende Mengen sichtbar machen","Erfüllende Mengen unsichtbar", (button) -> {
	    	isSatisfyingSetsVisible = !isSatisfyingSetsVisible;
	        this.ctl_baum.setLösungsmengen_anzeigen(isSatisfyingSetsVisible);
	        
	    });


        // Buttons zur Sidebar hinzufügen
        sidebar.getChildren().addAll(button1, button2, button3, button4);

        // Erstelle den Toggle-Button
        Button toggleButton = new Button("+");
        toggleButton.setPrefSize(30, 30); 
        toggleButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 15; " +
            "-fx-background-radius: 15;"
        ); 
        
        // Event-Handler für den Toggle-Button
        toggleButton.setOnAction(e -> {
            toggleSidebar(sidebar);
            toggleButton.setText(isExpanded ? "+" : "-"); // Text je nach Zustand ändern
        });

        // Container für die Sidebar und den Toggle-Button
        StackPane container = new StackPane(); // Verwende StackPane als übergeordnetes Layout
       

        // Sidebar in eine HBox einfügen, damit der Toggle-Button korrekt am Rand sitzt
        HBox sidebarContainer = new HBox(sidebar);
        sidebarContainer.setAlignment(Pos.CENTER_LEFT); // Sidebar links ausrichten


        // Toggle-Button in einer HBox platzieren, um die Position zu steuern
        HBox toggleButtonContainer = new HBox(toggleButton);
        toggleButtonContainer.setAlignment(Pos.CENTER_LEFT); // Button mittig am linken Rand ausrichten
        toggleButtonContainer.setPadding(new Insets(0, 0, 0, -15)); // Leicht nach links verschieben
        toggleButtonContainer.setMaxHeight(1);//Workaround 1 Pixel regaoert theoretich ncith auf klicks, dieser ist aber weit unter dne Buttons
        toggleButtonContainer.setClip(null);

        // Sidebar und Toggle-Button in das StackPane einfügen
        container.getChildren().addAll(sidebarContainer, toggleButtonContainer);
        //Container wird direkt hinzugefügt
        root.setRight(container);
        
        this.sidebar_right = sidebar;
        this.sidebar_container_right = container; // Sidebar-Referenz speichern
    }

    private void toggleSidebar(VBox sidebar) {
        isExpanded = !isExpanded; // Zustand umschalten
        sidebar.setVisible(isExpanded); // Sichtbarkeit ändern
        sidebar.setManaged(isExpanded); // Layout aktualisieren

        // Position des Toggle-Buttons je nach Zustand der Sidebar anpassen
        if (isExpanded) {
            StackPane.setAlignment(sidebar, Pos.CENTER_LEFT); // Sidebar links ausrichten
        } else {
            StackPane.setAlignment(sidebar, Pos.CENTER_RIGHT); // Button nach rechts verschieben
        }
    }

    private Button createToggleButton(String textOn, String textOff, Consumer<Button> action) {
        Button button = new Button(textOn);
        button.setOnAction(e -> action.accept(button));
        return button;
    }
    
 // Zeigt die Schritt-für-Schritt-Umformung in Normalform an
    private ScrollPane erstelleUmformungsLabelsUndFügeHinzu(HBox formelbox, Zustandsformel zustandsformel, BorderPane parentContainer) {
        
        // Sicherstellen, dass in Normalform umgewandelt wurde
        zustandsformel.turn_to_normal_form();
        
        // Erstelle eine VBox, die die formelbox und die Umformungslabels enthält
        VBox gesamteVBox = new VBox();
        gesamteVBox.setPadding(new Insets(0)); // Kein Padding für gesamteVBox

        // Füge die formelbox als erstes Element in die VBox hinzu
        gesamteVBox.getChildren().add(formelbox);
        
        // Erstelle die Umformungslabels und füge sie zur VBox hinzu
        VBox umformungsVBox = new VBox(); // Separate VBox für die Umformungslabels
        umformungsVBox.setPadding(new Insets(0)); // Kein Padding für umformungsVBox

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
            label.setStyle("-fx-font-size: 14px; -fx-padding: 0;"); // Padding im Label auf 0 setzen
            label.setMinHeight(Region.USE_PREF_SIZE); // Mindesthöhe auf PrefSize setzen, um zusätzlichen Abstand zu vermeiden
            VBox.setMargin(label, new Insets(0)); // Keine Margin für jedes Label

            // Füge das Label der neuen VBox hinzu
            umformungsVBox.getChildren().add(label);
        }

        // Setze das Layout-Spacing und Padding für die VBox
        umformungsVBox.setSpacing(1); // Setze das Spacing auf 0, um den Abstand zu minimieren

        // Erstelle ein ScrollPane für die Umformungslabels
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(umformungsVBox);
        scrollPane.setMaxHeight(100); // Maximale Höhe des ScrollPane
        scrollPane.setFitToWidth(true); // ScrollPane an die Breite anpassen

        // Füge die ScrollPane zur gesamten VBox hinzu
        gesamteVBox.getChildren().add(scrollPane);

        // Referenz innerhalb der Klasse Sidebarhandler setzen
        this.untere_Ausgabe = gesamteVBox; // Setze die gesamteVBox als Referenz

        // Füge die gesamte VBox in den Bottom-Bereich des parentContainer ein
        parentContainer.setBottom(gesamteVBox);
        
        return scrollPane;
    }



   

 
    
 private void bringe_formelbaum_auf_pane(Pane root,int pos_x_des_baumes,int pos_ydes_baumes,int x_offset) {
        
	    this.formel_baum_pane = this.ctl_baum.zeichneBaum(pos_x_des_baumes, pos_ydes_baumes);
	    placeFormulaPane(root, x_offset);
	    bringSidebarsToFront();
	}

	private void placeFormulaPane(Pane root, int x_offset) {
	    if (root instanceof BorderPane) {
	        HBox hbox = extractHBoxFromBorderPaneTop(root);
	        if (hbox == null) {
	            throw new IllegalStateException("Im oberen Bereich wird eine HBox erwartet.");
	        }
	        
	        Bounds hboxBounds = hbox.localToScene(hbox.getBoundsInLocal());
	        formel_baum_pane.setLayoutY(hboxBounds.getMaxY() + 30);  // 30 Pixel unter der HBox
	        formel_baum_pane.setLayoutX(hboxBounds.getMinX() + x_offset);  // Gleiche X-Position wie HBox
	    } else {
	        formel_baum_pane.setTranslateX((root.getWidth() - formel_baum_pane.getWidth()) / 2);
	        formel_baum_pane.setTranslateY(50);
	    }

	    root.getChildren().add(formel_baum_pane);
	    formel_baum_pane.toFront();
	}

	private HBox extractHBoxFromBorderPaneTop(Pane root) {
	    VBox vbox = (VBox) ((BorderPane) root).getTop();
	    if (vbox != null && vbox.getChildren().size() > 1) {
	        return (HBox) vbox.getChildren().get(2);
	    }
	    return null;
	}

	private void bringSidebarsToFront() {
	    if (this.sidebar_right != null) {
	        this.sidebar_right.toFront();
	    }
	    if (this.sidebar_left != null) {
	        this.sidebar_left.toFront();
	    }
	}

    private StackPane create_reducedSidebar(Zustandsformel zustandsformel, AnchorPane root, Transitionssystem ts,int x_offset){
        this.ctl_baum = new CTL_Formel_Baum(zustandsformel.getStart_der_rekursiven_Definition(), ts);

        // Erstelle die Sidebar und setze deren Layout-Eigenschaften
        VBox local_sidebar = new VBox();
        local_sidebar .setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
        local_sidebar .setSpacing(20);
        local_sidebar .setMaxHeight(200);
        local_sidebar .setMaxWidth(200);
        
        // Füge den String zustandsformel.toString() oben in der Sidebar hinzu
        Label formulaLabel = new Label(zustandsformel.getFormel_string());
        formulaLabel.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 14;"); // Anderer Grauton und Schriftgröße anpassen


        Button button2 = createToggleButton("Färbe erfüllende Zustände", "Farbe zurücksetzen", (button) -> {
            is_colored = !is_colored;
            button.setText(is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
            circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), is_colored);
        });
        
        color_button = button2;

        Button button3 = createToggleButton("Zeige Formelbaum", "Formelbaum ausblenden", (button) -> {
            isFormulaTreeCompleteVisible = !isFormulaTreeCompleteVisible;
            if (this.formel_baum_pane == null) {

                this.bringe_formelbaum_auf_pane(root, -100, 0,x_offset);
            } else {
                this.formel_baum_pane.setVisible(isFormulaTreeCompleteVisible);
                this.formel_baum_pane.setManaged(isFormulaTreeCompleteVisible); 
            }
            button.setText(isFormulaTreeCompleteVisible ? "Formelbaum ausblenden" : "Zeige Formelbaum");
        });

        Button button4 = createToggleButton("Erfüllende Mengen sichtbar machen","Erfüllende Mengen unsichtbar", (button) -> {
            isSatisfyingSetsVisible = !isSatisfyingSetsVisible;
            this.ctl_baum.setLösungsmengen_anzeigen(isSatisfyingSetsVisible);
        });

        // Buttons zur Sidebar hinzufügen
        local_sidebar.getChildren().addAll(button2, button3, button4);

        Button toggleButton = new Button("-");
        toggleButton.setPrefSize(30, 30);
        toggleButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 15; " +
            "-fx-background-radius: 15;"
        ); 

        toggleButton.setOnAction(e -> {
            toggleSidebar(local_sidebar);
            toggleButton.setText(isExpanded ? "-" : "+");
        });

        // Container für Sidebar und Toggle-Button
        StackPane container = new StackPane();
        HBox sidebarContainer = new HBox(local_sidebar);
        
        HBox toggleButtonContainer = new HBox(toggleButton);
        toggleButtonContainer.setMaxHeight(1);//Workaround 1 Pixel regaoert theoretich ncith auf klicks, dieser ist aber weit unter dne Buttons
        toggleButtonContainer.setClip(null);

        container.getChildren().addAll(sidebarContainer, toggleButtonContainer);
        

        return container;
    }

    public void createReducedSidebarLeft(AnchorPane root, Zustandsformel zustandsformel, Transitionssystem ts, int x_offset) {
        // Erzeuge und platziere die Sidebar
        StackPane container = create_reducedSidebar(zustandsformel, root, ts,x_offset);
        HBox sidebarContainerLeft = (HBox) container.getChildren().get(0);
        this.sidebar_left = (VBox) sidebarContainerLeft.getChildren().get(0);
        this.sidebar_container_left = container;
        HBox toggleButtonContainerLeft = (HBox) container.getChildren().get(1);

        sidebarContainerLeft.setAlignment(Pos.CENTER_LEFT);
        toggleButtonContainerLeft.setAlignment(Pos.CENTER_RIGHT);
        toggleButtonContainerLeft.setPadding(new Insets(0, -30, 0, 0));
        toggleButtonContainerLeft.setMaxHeight(1);
        sidebarContainerLeft.toFront();

        // Positioniere die Sidebar innerhalb des AnchorPane
        AnchorPane.setLeftAnchor(container, 12.0);
        AnchorPane.setTopAnchor(container, root.getHeight() / 2 -70);

        root.getChildren().add(container);
    }

    public void createReducedSidebarRight(AnchorPane root, Zustandsformel zustandsformel, Transitionssystem ts, int x_offset) {
        // Erzeuge und platziere die Sidebar
        StackPane container = create_reducedSidebar(zustandsformel, root, ts,x_offset);
        HBox sidebarContainerRight = (HBox) container.getChildren().get(0);
        this.sidebar_right = (VBox) sidebarContainerRight.getChildren().get(0);
        this.sidebar_container_right = container;
        HBox toggleButtonContainerRight = (HBox) container.getChildren().get(1);

        // Ausrichtung und Layout der Sidebar anpassen
        sidebarContainerRight.setAlignment(Pos.CENTER_RIGHT);
        toggleButtonContainerRight.setAlignment(Pos.CENTER_LEFT);
        toggleButtonContainerRight.setPadding(new Insets(0, 0, 0, -30));
        toggleButtonContainerRight.setMaxHeight(1);
        sidebarContainerRight.toFront();

        // Positioniere die Sidebar innerhalb des AnchorPane auf der rechten Seite
        AnchorPane.setRightAnchor(container, 12.0);
        AnchorPane.setTopAnchor(container, root.getHeight() / 2-70);


        root.getChildren().add(container);
    }
    
    // Methode zum Entfernen der Sidebar ohne direkten Zugriff auf root
 // Methode zum Entfernen der Sidebar ohne direkten Zugriff auf root
    public void removeSidebar() {
        if (this.sidebar_container_right != null) {
            Parent parent = sidebar_container_right.getParent();
            if (parent instanceof Pane) {
                // Entferne die sidebar_container direkt aus dem Parent
                ((Pane) parent).getChildren().remove(sidebar_container_right);

                if (this.formel_baum_pane != null) {
                    ((Pane) parent).getChildren().remove(formel_baum_pane); // Element wirklich entfernen
                    this.formel_baum_pane = null;
                }

                circle_builder.färbeKreiseNachZustand(new HashSet<Zustand>(), false);

                if (this.normalformpane != null) {
                    ((Pane) parent).getChildren().remove(normalformpane); // Element wirklich entfernen
                    this.normalformpane = null;
                }
                
               isSolutionNormalFormVisible = false;
               is_colored = false;
               isFormulaTreeCompleteVisible = false;
               isSatisfyingSetsVisible = false;
                     
            }
        }
        if (this.sidebar_container_left != null) {
            Parent parent = sidebar_container_left.getParent();
            if (parent instanceof Pane) {
                // Entferne die sidebar_container direkt aus dem Parent
                ((Pane) parent).getChildren().remove(sidebar_container_left);

                if (this.formel_baum_pane != null) {
                    ((Pane) parent).getChildren().remove(formel_baum_pane); // Element wirklich entfernen
                    this.formel_baum_pane = null;
                }

                circle_builder.färbeKreiseNachZustand(new HashSet<Zustand>(), false);

                if (this.normalformpane != null) {
                    ((Pane) parent).getChildren().remove(normalformpane); // Element wirklich entfernen
                    this.normalformpane = null;
                }
                
              
               isSolutionNormalFormVisible = false;
               is_colored = false;
               isFormulaTreeCompleteVisible = false;
               isSatisfyingSetsVisible = false;
                     
            }
        }
    }     
    	public void handleButtonClick(Button button,Zustandsformel zustandsformel, Transitionssystem ts) {
    	    // Toggle the Zustand von is_colored
    	    is_colored = !is_colored;
    	    // Setze den Button-Text abhängig vom Zustand von is_colored
    	    	button.setText(is_colored ? "Farbe zurücksetzen" : "Färbe erfüllende Zustände");
    	    // Rufe die Methode zum Färben oder Zurücksetzen der Kreise auf
    	    circle_builder.färbeKreiseNachZustand(zustandsformel.get_Lösungsmenge(ts), is_colored);
    	}

		public Button getColor_button() {
			return color_button;
		}
    	
    	
    
}
   



