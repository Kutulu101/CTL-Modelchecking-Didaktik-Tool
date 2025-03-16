package GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Text;

//Klasse die Line erweiter um eine Quadkurve, erleichter die Berechung von Start und Endpunkten durch Polymorphismus
public  class CustomLine extends Line implements line_to_represent_relation {
   
	// Liste von Relationen, die durch Linien repräsentiert werden
	private List<Relation> representedRelations = new ArrayList<>();
	
   //gekrümmte Kurve
   private Path curvedPath = new Path();
   
   //Pfeilspitze der gekrpmmten Linie
   private Polygon arrowHeadCurved;
	//Pfeilspitze der geraden Linie
   private Polygon arrowHeadStraight;
   
	//ReturnLabel und Arrowhead für die gekrümmte Linie
   	private Label returnlabelcurved;
    private double textfieldXcurved;
    private double textfieldYcurved;
    
    //Psotion des Scheitelpunktes
    private double scheitelpunkt_x;
    private double scheitelpunkt_y;
    
	//ReturnLabel und Arrwohead für die gerade Linie
   	private Label returnlabelstraight;
    private double textfieldXstraight;
    private double textfieldYstraight;
    
    //Punkt der zum Berechnen des Arrows genutzt wird
    private double arrow_curved_startpoint_x;
    private double arrow_curved_startpoint_y;
    
    
    //Liste mit den Listners und dem Interface
    private static final List<CreationListener> listeners = new ArrayList<>();

    public interface CreationListener {
        void onCreated(CustomLine customLine);
    }
   

    CustomLine(Circle firstCircle,Circle secondCircle,Text firstCircleLabel,Text secondCircleLabel, int offset, List<String>vorauswahl_transitionen,boolean should_curve) {
       
    	// Initiale Werte setzen (können später überschrieben werden)
        super(0, 0, 0, 0);
        
      //extrahiere ParentPane
  	   Pane parentPane = (Pane) firstCircle.getParent().getParent();
  	   
   	   //Kreismittelpunkte extrhaieren
     	double startX = firstCircle.getCenterX();
         double startY = firstCircle.getCenterY();
         double endX = secondCircle.getCenterX();
         double endY = secondCircle.getCenterY();
         
         EdgePoints points = new EdgePoints(startX, startY, endX, endY, firstCircle.getRadius(), secondCircle.getRadius(), offset);
         
  	 
       //Erzeugt Quadratische Kurve
       this.update_curve_and_line(firstCircle,secondCircle,points, offset);
       this.arrowHeadCurved = createArrowHead();
       this.arrowHeadStraight = createArrowHead();
       updateArrowHeadstraight(points);
       updateArrowHeadcurved(points);
       
       parentPane.getChildren().addAll(this,this.arrowHeadStraight,this.arrowHeadCurved,this.curvedPath);
       
       this.showTextInputField(offset,firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, vorauswahl_transitionen);
       
       //zuerst nur gerade sichtbar
       if(should_curve) {
    	   showCurved();
       }
       else{showStraight();}
       
    }
    

	//Berechnet Endpunkte neu und updatet die Linie
 	public void updateLines(Relation relation) {
    	   
 		//Kreismittelpunkte extrhaieren
      		double startX = relation.getFirstCircle().getCenterX();
          double startY = relation.getFirstCircle().getCenterY();
          double endX = relation.getSecondCircle().getCenterX();
          double endY = relation.getSecondCircle().getCenterY();
          
          EdgePoints points = new EdgePoints(startX, startY, endX, endY, relation.getFirstCircle().getRadius(), relation.getSecondCircle().getRadius(), relation.getOffset());
         
 
 	    //CustomLine updaten
 	    CustomLine line = (CustomLine) relation.getLine();
 	    line.update_curve_and_line(relation.getFirstCircle(),relation.getSecondCircle(),points, relation.getOffset());
 	    
 	    //Arrows Updaten
 	    this.updateArrowHeadcurved(points);
 	    this.updateArrowHeadstraight(points);
 	    
 	    //Textlabel updaten
 	    this.positionLabelcurved();
 	    this.positionLabelstraight();
 	}   
 	
    private void update_curve_and_line(Circle firstCircle, Circle secondCircle, EdgePoints points,int offset) {
    	
         
         this.setStartX(points.startX);
         this.setStartY(points.startY);
         this.setEndX(points.endX);
         this.setEndY(points.endY);
         this.updateCurve(points.startX, points.startY, points.endX, points.endY);
     }
       
    //MEthode die Quadratische Kruve aus den Start und Endpunkt einer Geraden bestimmt, der Scheitelpunkt sklaiert mit dem Anstieg aus optischen Gründen
    //da neue Kurve erzeugt wird auch geignet für das Updaten wenn Kreise via Drag and Drop verschoben wurden
    private void updateCurve(double startX, double startY, double endX, double endY) {
       //Idee gedanklich Mittelpunkt um eine Faktor nach außen versetzten und diesen als dreitten Punkt der Quadratischen Kruve benutzen
  	  double midX = (startX + endX) / 2.0;
       double midY = (startY + endY) / 2.0;
       
       //Normale zur Linie berechnen
       double deltaX = endX - startX;
       double deltaY = endY - startY;
       double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
       
       double perpX = -deltaY / length;
       double perpY = deltaX / length;
       
       //Stützpunkt berechnen
       double controlX = midX + perpX * 0.8D * Math.abs(deltaY);
       double controlY = midY + perpY * 0.8D * Math.abs(deltaX);
       
       //Scheitelpunkt der Kurve bei 0.5 Berechnen
       this.scheitelpunkt_x = 0.25D * startX + 0.5D * controlX + 0.25D * endX;
       this.scheitelpunkt_y = 0.25D * startY + 0.5D * controlY + 0.25D * endY;
       
       //Psoition des Textfeldes minimal versetzten
       this.textfieldXstraight = midX + perpX * 10D;
       this.textfieldYstraight = midY + perpY * 10D;
       
       //Position des Testfeldes minimal versetzten
       this.textfieldXcurved = this.scheitelpunkt_x + perpX * 5D;
       this.textfieldYcurved = this.scheitelpunkt_y + perpY * 5D;
       
       //Punkt kurz vor Ende zur Berechnung der Pfeilausrichtung
       double t = 0.99999999D;
       this.arrow_curved_startpoint_x = (1.0D - t) * (1.0D - t) * startX + 2.0D * (1.0D - t) * t * controlX + t * t * endX;
       this.arrow_curved_startpoint_y = (1.0D - t) * (1.0D - t) * startY + 2.0D * (1.0D - t) * t * controlY + t * t * endY;
       
       //Kurve erstellen
       this.curvedPath.getElements().clear();
       MoveTo moveTo = new MoveTo(startX, startY);
       QuadCurveTo quadCurveTo = new QuadCurveTo(controlX, controlY, endX, endY);
       this.curvedPath.getElements().addAll(moveTo, quadCurveTo);
       this.curvedPath.setStroke(Color.BLACK);
       this.curvedPath.setFill(Color.TRANSPARENT);
       
    }
    
    //Sichtbarkeit der Kurve verändern
    public void showCurved() {
    	this.curvedPath.setVisible(true);
       this.setVisible(false);
       this.returnlabelcurved.setVisible(true);
       this.returnlabelstraight.setVisible(false);
       this.arrowHeadCurved.setVisible(true);
       this.arrowHeadStraight.setVisible(false);
    }

  //Sichtbarkeit der Line verändern
    public void showStraight() {
       
       this.curvedPath.setVisible(false);
       this.setVisible(true);
       this.returnlabelcurved.setVisible(false);
       this.returnlabelstraight.setVisible(true);
       this.arrowHeadCurved.setVisible(false);
       this.arrowHeadStraight.setVisible(true);
    }
    
	private void showNothing() {
	       this.setVisible(false);
	       this.curvedPath.setVisible(false);
	       this.returnlabelcurved.setVisible(false);
	       this.returnlabelstraight.setVisible(false);
	       this.arrowHeadCurved.setVisible(false);
	       this.arrowHeadStraight.setVisible(false);
		
	}
    
    private Polygon createArrowHead() {
        Polygon arrowHead = new Polygon();
        arrowHead.setFill(Color.BLACK);
        return arrowHead;
    }

    
    private void updateArrowHeadcurved(EdgePoints points) {
    	
        double angle = Math.atan2(points.endY-this.get_curved_Arrow_Startpoint_y(),points.endX- this.get_curved_Arrow_startpoint_x()) + Math.PI;
        double arrowLength = 10.0;
        double arrowWidth = 7.0;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x1 = arrow_curved_startpoint_x + arrowLength * cos - arrowWidth * sin;
        double y1 = arrow_curved_startpoint_y + arrowLength * sin + arrowWidth * cos;
        double x2 = arrow_curved_startpoint_x + arrowLength * cos + arrowWidth * sin;
        double y2 = arrow_curved_startpoint_y + arrowLength * sin - arrowWidth * cos;
        arrowHeadCurved.getPoints().setAll(arrow_curved_startpoint_x, arrow_curved_startpoint_y, x1, y1, x2, y2);
    }
    
    private void updateArrowHeadstraight(EdgePoints points) {

        //Winkel von Line
 	   double angle = Math.atan2(points.endY - points.startY, points.endX - points.startX) + Math.PI;
 	   
       //konstante Größe
 	   double arrowLength = 10.0;
       double arrowWidth = 7.0;
       
       //Ausrichtung
       double sin = Math.sin(angle);
       double cos = Math.cos(angle);
       double x1 = points.endX + arrowLength * cos - arrowWidth * sin;
       double y1 = points.endY + arrowLength * sin + arrowWidth * cos;
       double x2 = points.endX + arrowLength * cos + arrowWidth * sin;
       double y2 = points.endY + arrowLength * sin - arrowWidth * cos;
       
       this.arrowHeadStraight.getPoints().setAll(points.endX, points.endY, x1, y1, x2, y2);
	}
    
    private void showTextInputField(int offset,Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, List<String> vorauswahl_transitionen) {
        
    	//extrahiere ParentPane
   	   Pane parentPane = (Pane) firstCircle.getParent().getParent();
   	   
   	 //erzeuge und formatiere Label der gekrümmten Linie mit eingelesener Transition
       this.returnlabelcurved = new Label();
       returnlabelcurved.setMinWidth(50.0);
       returnlabelcurved.setMinHeight(30.0);
       returnlabelcurved.setAlignment(Pos.CENTER);
       returnlabelcurved.setStyle("-fx-font-size: 18px;");
       
     //erzeuge und formatiere Label der gerade Linie mit eingelesener Transition
       this.returnlabelstraight = new Label();
       this.returnlabelstraight.setMinWidth(50.0);
       this.returnlabelstraight.setMinHeight(30.0);
       this.returnlabelstraight.setAlignment(Pos.CENTER);
       this.returnlabelstraight.setStyle("-fx-font-size: 18px;");
 	        
       //Wenn Vorauswahl Transitionen bekannt sind, wird Combobox angezeigt
       if (vorauswahl_transitionen != null && !vorauswahl_transitionen.isEmpty()) {
          
     	  //Combobox erzeugen, positionieren und befüllen
     	  ComboBox<String> comboBox = new ComboBox();
          comboBox.getItems().addAll(vorauswahl_transitionen);
          comboBox.setLayoutX(this.textfieldXstraight);
          comboBox.setLayoutY(this.textfieldYstraight);
          comboBox.setPromptText("Wählen Sie eine Option...");
          parentPane.getChildren().add(comboBox);
          
          //Verarbeitung der Combobox-Eingabe
          comboBox.setOnAction((event) -> {
         	 
         	 //Eingabe einlesen
             String selectedValue = (String)comboBox.getValue();
             
             //Eingabe an Label übergeben
    	     returnlabelcurved.setText(selectedValue);
    	     returnlabelstraight.setText(selectedValue);
             
             //entferne Combobox
             parentPane.getChildren().remove(comboBox);
             
       
             //Erzeuge ein Relation Objekt und speichere es und benachritige ArrowBuilder
             this.representedRelations.add(new Relation(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel,this,selectedValue, offset));
             notifyListeners();
          });
       } else {//Wenn keine Transitionen vorausgewählt wurden
     	  createInputField(parentPane, firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, offset);
       } 
      	      
	      //Drag and Drop die Return Label ermöglichen
	      returnlabelcurved.setOnMousePressed((event2) -> {
	         returnlabelcurved.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
	      });
	      returnlabelcurved.setOnMouseDragged((event2) -> {
	         double[] initialMousePosition = (double[])returnlabelcurved.getUserData();
	         double initialX = initialMousePosition[0];
	         double initialY = initialMousePosition[1];
	         double deltaX = event2.getSceneX() - initialX;
	         double deltaY = event2.getSceneY() - initialY;
	         returnlabelcurved.setLayoutX(returnlabelcurved.getLayoutX() + deltaX);
	         returnlabelcurved.setLayoutY(returnlabelcurved.getLayoutY() + deltaY);
	         returnlabelcurved.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
	      });
	      
	      //Drag and Drop für Return Label ermöglichen
	      returnlabelstraight.setOnMousePressed((event2) -> {
	         returnlabelstraight.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
	      });
	      returnlabelstraight.setOnMouseDragged((event2) -> {
	         double[] initialMousePosition = (double[])returnlabelstraight.getUserData();
	         double initialX = initialMousePosition[0];
	         double initialY = initialMousePosition[1];
	         double deltaX = event2.getSceneX() - initialX;
	         double deltaY = event2.getSceneY() - initialY;
	         returnlabelstraight.setLayoutX(returnlabelstraight.getLayoutX() + deltaX);
	         returnlabelstraight.setLayoutY(returnlabelstraight.getLayoutY() + deltaY);
	         returnlabelstraight.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});     
	      });
	      
	       //positioniere Labels
	       positionLabelstraight();
	       positionLabelcurved();
	      
	      parentPane.getChildren().addAll(returnlabelcurved,returnlabelstraight);
    }
     

	public void createInputField(Pane parentPane, Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, int offset) {
   	    
    	   // Erzeuge und positioniere das Textfeld zur Eingabe
   	    TextField inputField = new TextField();
   	    inputField.setLayoutX(this.textfieldXstraight);
   	    inputField.setLayoutY(this.textfieldYstraight);
   	    inputField.setPromptText("Zeichen Komma getrennt eingeben...");
   	    parentPane.getChildren().add(inputField);
   	    
   	    // Verarbeitung der Eingabe
   	    inputField.setOnAction((event) -> {
   	        String userInput = inputField.getText().replaceAll(",$|^,", "");;
   	        String[] labels = userInput.split(",");
   	        
   	        // Überprüfung der Eingabe
   	        for (String label : labels) {
   	            if (label.length() != 1 && label !="") {
   	                // Falls ungültig, zeige eine Warnung und starte erneut
   	                showWarningAndRetry(parentPane, firstCircle, secondCircle, firstCircleLabel, secondCircleLabel,offset);
   	                parentPane.getChildren().remove(inputField);
   	                return;
   	            }
   	        }
   	        
   	        // Falls gültig, setze das Label und erstelle die Relationen
   	        returnlabelcurved.setText(userInput);
   	        returnlabelstraight.setText(userInput);
   	        parentPane.getChildren().remove(inputField);
   	        for (String label : labels) {
                //Erzeuge ein Relation Objekt und speichere es und benachritige ArrowBuilder
                this.representedRelations.add(new Relation(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, this, label, offset));
                notifyListeners();
   	        }
   	    });
   	}
          
   	private void showWarningAndRetry(Pane parentPane, Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, int offset) {
	    // Erzeuge und zeige eine Warnung
	    Alert alert = new Alert(Alert.AlertType.WARNING);
	    alert.setTitle("Ungültige Eingabe");
	    alert.setHeaderText(null);
	    alert.setContentText("Bitte genau ein Zeichen pro Label eingeben, getrennt durch Kommas.");
	    alert.showAndWait();
	    
	    // Erneute Eingabeaufforderung
	    createInputField(parentPane, firstCircle, secondCircle, firstCircleLabel, secondCircleLabel,offset);
	}
   	
    private void positionLabelcurved() {
		this.returnlabelcurved.setLayoutX(this.textfieldXcurved);
		this.returnlabelcurved.setLayoutY(this.textfieldYcurved);
		
		//Offset für X für bessere Ausrichtung
		this.returnlabelcurved.setLayoutX(this.returnlabelcurved.getLayoutX() - 25.0);
		
		//Offset für Y-WErt abhänig von Kurven Richtung
		if(this.textfieldYstraight>this.textfieldYcurved) {
			this.returnlabelcurved.setLayoutY(this.returnlabelcurved.getLayoutY()  - 20.0);
		}else this.returnlabelcurved.setLayoutY(this.returnlabelcurved.getLayoutY()-10);
	}

	private void positionLabelstraight() {
		this.returnlabelstraight.setLayoutX(this.textfieldXstraight);
		this.returnlabelstraight.setLayoutY(this.textfieldYstraight);
		
	}

    public Polygon getArrowHeadCurved() {
        return this.arrowHeadCurved;
    }

    public Path getCurvedPath() {
       return this.curvedPath;
    }

    public double get_curved_Arrow_startpoint_x() {
       return this.arrow_curved_startpoint_x;
    }

    public double get_curved_Arrow_Startpoint_y() {
       return this.arrow_curved_startpoint_y;
    }
    

	public Label getReturnlabelcurved() {
		return returnlabelcurved;
	}

	public Label getReturnlabelstraight() {
		return returnlabelstraight;
	}

	@Override
	public List<Relation> getRelations(){
		return representedRelations;
	}
	
	//Mehtoden für Listner Kommunitaik mit Arrow_Builder
	   public void notifyListeners() {
	       for (CreationListener listener : listeners) {
	           listener.onCreated(this);
	       }
	   }

	   public static void addCreationListener(CreationListener listener) {
	       listeners.add(listener);
	   }
    

 }