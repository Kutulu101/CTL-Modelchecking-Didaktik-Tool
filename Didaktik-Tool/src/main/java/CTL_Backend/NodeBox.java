package CTL_Backend;

import java.util.Set;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

//Klasse zum Verwalten der grafischen Elemente des Zustandsformelbaums
public class NodeBox {
	//hier werden die GUI-Elemente aufgehangen
    protected StackPane stackpane;
    
    //erfüllende Menge die von NodeBox repräsentiert wird
    protected ErfüllendeMenge erfuellendeMenge;
    
    //Sichtbarkeitsverwaltung der Lösungsmenge
    private boolean isShowingSolutionSet;
    
    protected String nodename;
    
    //grafische Elemente
    protected Button togglebutton;
    protected Rectangle rechteck;
    //innerhalb der Baumstruktur kennt Nodebox sein Parent
    protected NodeBox parent;
    
    //zur Layout Berechnung
    protected double lastShift;
    protected double originalXLayout;
    protected double originalYLayout;

    public NodeBox(String nodeName, ErfüllendeMenge erfuellendeMenge, Transitionssystem ts, NodeBox parent) {
        
    	this.erfuellendeMenge = erfuellendeMenge;
        this.nodename = nodeName;
        this.isShowingSolutionSet = false;
        this.parent = parent;
        
        //erzeugung der GUI Elemente
        
        //Basis bildet beschriftetes Rechteck
        Rectangle rect = new Rectangle(80, 40);
        rect.setFill(Color.LIGHTBLUE);
        rect.setStroke(Color.BLACK);
        this.rechteck = rect;
        
        Text text = new Text(nodeName);
        text.setStyle("-fx-font-size: 6;");
        
        //Button zum Umschalten der Sichtbarkeit der Lösungsmenge erzeugen und formatieren
        Button toggleButton = new Button("->");
        double radius = 7;
        Circle circleShape = new Circle(radius);
        toggleButton.setShape(circleShape);
        toggleButton.setMinSize(2 * radius, 2 * radius);
        toggleButton.setMaxSize(2 * radius, 2 * radius);
        toggleButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-font-size: 6px;");
        toggleButton.setVisible(false);
        
        //Stackpane nimmt GUI Elemente auf und positioniert diese
        this.stackpane = new StackPane();
        this.stackpane.getChildren().addAll(rect, text, toggleButton);
        
        
        //Positionieren von Toggle-Button auf StackPane in oberere linker Ecke
        StackPane.setAlignment(toggleButton, Pos.TOP_LEFT);
        toggleButton.setTranslateX(-5);
        toggleButton.setTranslateY(2 * radius - rect.getHeight() / 2);
        
        //Registrieren des Events bei Button klicken
        toggleButton.setOnMouseClicked(event -> handleMouseClickEvent(ts));
        
        this.togglebutton = toggleButton;
        
        //Drag-and-Drop Events bei Rechtecken registrieren
        rect.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.lastShift = event.getSceneX();
                rect.setFill(Color.RED);
            }
        });
        
        rect.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                double deltaX = event.getSceneX() - this.lastShift;
                this.stackpane.setTranslateX(this.stackpane.getTranslateX() + deltaX);
                this.lastShift = event.getSceneX();
            }
        });
        
        rect.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                rect.setFill(Color.LIGHTBLUE);
            }
        });
    }
    
    //Diverse Getter und Setter zur Verwaltung
    public double getOriginalXLayout() {
        return this.originalXLayout;
    }

    public void setOriginalXLayout(double originalXLayout) {
        this.originalXLayout = originalXLayout;
    }

    public double getOriginalYLayout() {
        return this.originalYLayout;
    }

    public void setOriginalYLayout(double originalYLayout) {
        this.originalYLayout = originalYLayout;
    }
    
    public StackPane getStackPane() {
        return this.stackpane;
    }
    
    //Bringt Stackapne zurück zur ursprünglichen Layout-Position
    public void resetStackPaneLayout() {
        this.stackpane.setLayoutX(this.originalXLayout);
        this.stackpane.setLayoutY(this.originalYLayout);
    }

    public ErfüllendeMenge getErfuellendeMenge() {
        return this.erfuellendeMenge;
    }

    public void makeToggleButtonVisible() {
        this.togglebutton.setVisible(true);
    }

    public void makeToggleButtonInvisible() {
        this.togglebutton.setVisible(false);
    }

    public String getNodename() {
        return this.nodename;
    }

    public boolean isShowingSolutionSet() {
        return this.isShowingSolutionSet;
    }

    public void toggleSolutionSet() {
        this.isShowingSolutionSet = !this.isShowingSolutionSet;
    }
    
    //Gibt die Bounds von Rechteck in Bezug auf die Scene zurück
    public Bounds getRectangleBounds() {
        StackPane stackPane = (StackPane) this.rechteck.getParent();
        Pane parentPane = (Pane) stackPane.getParent();
        Bounds boundsInScene = this.rechteck.localToScene(this.rechteck.getBoundsInLocal());
        return parentPane.sceneToLocal(boundsInScene);
    }

    public NodeBox getParentNodeBox() {
        return this.parent;
    }
    
    public double getLastShift() {
        return this.lastShift;
    }

    public void setLastShift(double lastShift) {
        this.lastShift = lastShift;
    }
    
    //Mehtode die beim Button-Klicken gestartet wird, blendet Lösungsmenge ein/aus und Färbt Rechteck
    public void handleMouseClickEvent(Transitionssystem ts) {
        this.toggleSolutionSet();
        if (this.isShowingSolutionSet) {
            Set<Zustand> loesungsMenge = this.erfuellendeMenge.berechne(ts);
            StringBuilder names = new StringBuilder();
            for (Zustand zustand : loesungsMenge) {
                if (names.length() > 0) {
                    names.append(", ");
                }
                names.append(zustand.getName());
            }
            ((Text) this.stackpane.getChildren().get(1)).setText(names.toString());
            ((Rectangle) this.stackpane.getChildren().get(0)).setFill(Color.LIGHTGREEN);
        } else {
            ((Text) this.stackpane.getChildren().get(1)).setText(this.nodename);
            ((Rectangle) this.stackpane.getChildren().get(0)).setFill(Color.LIGHTBLUE);
        }
    }
    
    //Eigene Methode zum Verschieben der Elemente, mit Skaliereung nach Baumtiefe
    public void moveNodeBy(double xOffset) {
    	//je tiefer der Knoten im Baum je geringer soll der Offset werden, 0.5 reduziert zusätzlich das Wachstum in die Breite
        xOffset = this.scaleOffset(xOffset) + this.getParentNodeBox().getLastShift() * 0.5;
        this.stackpane.setTranslateX(this.stackpane.getTranslateX() + xOffset);
        this.setLastShift(xOffset);
    }
    
 // Methode um Tiefe im Baum zu bestimmen
    protected int calculateDepth() {
        int depth = 0;
        NodeBox current = this; // Start bei der aktuellen NodeBox
        while (current.getParentNodeBox() != null) {
            depth++;
            current = current.getParentNodeBox(); // Gehe zum ParentNodeBox
        }
        return depth;
    }
    //Logarhitmisches Wachstum der Skalierung nach Baum-Tiefe damit Seiten verhältnisse zwischen den Tiefen in etwa gleich bleiben
    protected double scaleOffset(double xOffset) {
        double k = 1;
        double depthFactor = this.calculateDepth();
        if (depthFactor == 0) {
            throw new IllegalArgumentException("Depth cannot be zero.");
        } else {
            double a = 1 / Math.log(1 + 1 / (1 + k));
            return a * Math.log(1 + 1 / (depthFactor + k)) * xOffset;
        }
    }
}
