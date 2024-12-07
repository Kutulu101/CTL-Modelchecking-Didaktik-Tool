 package CTL_Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class CTL_Formel_Baum {
	
	//Startpunkt der rekursiv definierten CTL-Formel
   private erfüllende_Mengen startpunkt = null;
   
   //Transitionssystem zur Berechnung
   private Transitionssystem transitionssystem;
   
   
   
   //Itterierbare Objekte für späteren Zugriff
   private List<NodeBox> allNodeBoxes = new ArrayList();
   private List<Line> lines = new ArrayList();
   
   //Pane auf der der Baum gezeichnet wird
   private Pane zeichenPane;
   
   // Sichtbarkeit um der erfüllenden Zustände zu verwalten
   private boolean ist_erfüllende_menge_sichtbar;
   
   //Variable um Sichtbarkeit der erfüllenden Zustände zu verwalten
   private boolean detail_lösungen_anzeigen;
   
   // Konstruktor zur Initialisierung des Baums mit Startpunkt und Transitionssystem
   public CTL_Formel_Baum(erfüllende_Mengen startpunkt, Transitionssystem transitionssystem) {
       this.startpunkt = startpunkt;
       this.transitionssystem = transitionssystem;
   }

   /**
    * Zeichnet den Baum in einem neuen Fenster.
    */
   public Stage zeichneBaum(double startwidht, double startHeight, Button shared_button) {
	   
	  //erzeuge ZeichenPane auf der Baum dargestellt wird
      this.zeichenPane = new Pane();
      
      // Baum nur zeichnen, wenn ein Startpunkt übergeben wurde
      if (this.startpunkt != null) {
    	 //Lösungsmenge für die CTL-Formel im entsprechenden TS-berechnen
         this.startpunkt.berechne(this.transitionssystem);
         //starte die rekursive Funktion die Baum zeichnet, null wird übergeben weil erstes Element im Baum kein Parent-Elemenmt hat
         this.drawTree(this.zeichenPane, this.startpunkt, startwidht / 2.0, 100.0, 200.0, 80.0, (NodeBox)null);
         //Funktion, die die Linien updatet
         this.updateLines();
      }

      // Gruppe die eine bewegliche Ansicht und Zoomen ermöglicht
      Group group = new Group();
      group.getChildren().add(this.zeichenPane);

      
      //Button zum ausblenden des Baumes übernimmt Action aus shared_Button da beide die selbe Funktion haben sollen
      Button versteckeBaumButton = new Button("Formelbaum ausblenden");
      versteckeBaumButton.setOnAction(shared_button.getOnAction());
      
      //Button der die Group zentriert, um Ansicht zu "nullen"
      Button zentriereButton = new Button("Formelbaum zentrieren");
      zentriereButton.setOnAction(event -> {
          double mitteX = (this.zeichenPane.getWidth() - this.zeichenPane.getLayoutBounds().getWidth()) / 2.0;
          double mitteY = (this.zeichenPane.getHeight() - this.zeichenPane.getLayoutBounds().getHeight()) / 2.0;
          group.setTranslateX(mitteX);
          group.setTranslateY(mitteY);
          group.setScaleX(1.0);
          group.setScaleY(1.0);
      });
      
      //Buttond der Überlappungen im Formelbaum auflöst, geht immer von orginal Layout aus
      Button entzerrenButton = new Button("Baum entzerren");
      entzerrenButton.setOnAction((event) -> {
         this.resetLayout();
         this.checkOverlappingNodeBoxes(0);
         this.updateLines();
      });
      
      //Button der Lösungsmengen und Detaillösungen sichtbar macht
      Button lösungsmengen_sichtbar_Button = new Button("Erfüllende Mengen sichtbar machen");
      lösungsmengen_sichtbar_Button.setOnAction((event) -> {
    	 //Togglet Text und Variable
         this.ist_erfüllende_menge_sichtbar = !this.ist_erfüllende_menge_sichtbar;
         if (this.ist_erfüllende_menge_sichtbar) {
            lösungsmengen_sichtbar_Button.setText("Erfüllende Mengen ausblenden");
         } else {
            lösungsmengen_sichtbar_Button.setText("Erfüllende Mengen sichtbar machen");
         }
         //Ändert Sichtbarkeit
         this.set_lösungsmengen_sichtbarkeit(this.ist_erfüllende_menge_sichtbar);
      });
      
      //Buttons zur HBox hinzufügen, und Formatieren
      HBox buttonBox = new HBox(10.0);
      buttonBox.getChildren().addAll(versteckeBaumButton, lösungsmengen_sichtbar_Button, entzerrenButton, zentriereButton);
      buttonBox.setStyle("-fx-padding: 10;");
      
      //Vbox zum Stylen der Buttonbox
      VBox vbutton_box = new VBox(10.0);
      vbutton_box.setPadding(new Insets(5.0));
      vbutton_box.getChildren().add(buttonBox);
      vbutton_box.setStyle("-fx-background-color: #D3D3D3;");
      
      //Vbox die Group und Buttons aufnimmt und ordnet
      VBox gesamt_box = new VBox();
      StackPane.setAlignment(vbutton_box, Pos.TOP_LEFT);
      gesamt_box.getChildren().addAll(vbutton_box, group);
      VBox.setVgrow(vbutton_box, Priority.ALWAYS);
      StackPane.setAlignment(vbutton_box, Pos.TOP_LEFT);
      
      //verhindert das die Buttons von der transparenten ZeichenPane verdeckt werden können
      this.zeichenPane.setPickOnBounds(false);
      
      //Erzeugt SCene und Stage
      Scene scene = new Scene(gesamt_box, startwidht, startHeight);
      Stage stage = new Stage();
      stage.setScene(scene);
      
      //Bindet die Größen der Baumdarstellung an die Scene um wechseln in Vollbild zu verarbeiten
      this.zeichenPane.prefWidthProperty().bind(scene.widthProperty());
      this.zeichenPane.prefHeightProperty().bind(scene.heightProperty());
      
      //Scroll Event bei scene um Zoomen zu ermöglichen
      scene.setOnScroll((event) -> {
         double zoomFactor = 1.1;
         if (event.getDeltaY() < 0.0) {
            zoomFactor = 0.9;
         }

         double mouseX = event.getSceneX();
         double mouseY = event.getSceneY();
         group.setScaleX(group.getScaleX() * zoomFactor);
         group.setScaleY(group.getScaleY() * zoomFactor);
         group.setTranslateX(group.getTranslateX() - (mouseX - group.getTranslateX()) * (zoomFactor - 1.0D));
         group.setTranslateY(group.getTranslateY() - (mouseY - group.getTranslateY()) * (zoomFactor - 1.0D));
      });
      
      group.setOnMousePressed(event -> {
    	    if (event.isSecondaryButtonDown()) {
    	        System.out.println("Mouse pressed at: " + event.getSceneX() + ", " + event.getSceneY());
    	        group.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
    	    }
    	});
    	group.setOnMouseDragged(event -> {
    	    if (event.isSecondaryButtonDown()) {
    	        System.out.println("Mouse dragged at: " + event.getSceneX() + ", " + event.getSceneY());
    	        double[] data = (double[]) group.getUserData();
    	        double deltaX = event.getSceneX() - data[0];
    	        double deltaY = event.getSceneY() - data[1];
    	        group.setTranslateX(group.getTranslateX() + deltaX);
    	        group.setTranslateY(group.getTranslateY() + deltaY);
    	        group.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
    	    }
    	});

      
      //Nach Drag and Drop  müssen Linien aktualisiert werden
      group.setOnMouseReleased((event) -> {
         this.updateLines();
      });
      
      
      //Abfangen des Klicken auf rotes X (Schließen), Stgae wird nur ausgeblendet
      stage.setOnCloseRequest((event) -> {
         stage.hide();
         event.consume();
         versteckeBaumButton.fire();
      });
      
      //Zeige die Stage
      stage.show();
      
      //gibt Stage für weitere Formatierungen zurück
      return stage;
      
   }
   
   //Rekursive Methode zum Zeichnen des Formelbaums
   private void drawTree(Pane pane, erfüllende_Mengen erfüllende_Menge, double x, double y, double xOffset, double yOffset, NodeBox parent) {
      
	   //Ersetzt aus dem im Node gespeicherten Symbol die Symbole für die Übergänge mit den tatsächlichen Übergängen
	   if (erfüllende_Menge != null) {
         String symbol;
         if (erfüllende_Menge instanceof HatÜbergang && erfüllende_Menge.get_symbol().contains("übergänge")) {
            HatÜbergang hatÜbergang = (HatÜbergang)erfüllende_Menge;
            Set<Übergang> übergänge = hatÜbergang.getÜbergänge();
            String übergängeString = (String)übergänge.stream().map(Übergang::getZeichen).collect(Collectors.joining(", "));
            symbol = erfüllende_Menge.get_symbol().replace("übergänge", übergängeString);
         } else {
            symbol = erfüllende_Menge.get_symbol();
         }
         
         //neue Nodebox die der Pane hinzugefügt wird, basierend auf der erfüllende_Menge
         NodeBox combo_at_node;
         
         //Wenn erfüllende_Menge detailierte Lösungsschritte anzeigen kann/soll
         if (erfüllende_Menge instanceof detail_lösung) {
        	 
        	 //erzeugen des Nodebox-Objektes zur Darstellung der erfüllenden Menge
            combo_at_node = new NodeBox_with_detail_solution(erfüllende_Menge, symbol, this.transitionssystem, parent);
            
            //platzieren auf Pane
            Pane nodeBox = ((NodeBox_with_detail_solution)combo_at_node).getDetail_toggle_pane_complete();
            nodeBox.setLayoutX(x);
            nodeBox.setLayoutY(y);
            
            //ablegen der orginalen Koordinaten
            ((NodeBox)combo_at_node).setOriginalXLayout(x);
            ((NodeBox)combo_at_node).setOriginalYLayout(y);
            
            //Hinzufügen zur Pane
            pane.getChildren().add(nodeBox);
            
         //Wenn eine erfüllende Menge ohne detailierte Lösungsschritte dargestellt werden soll
         } else {
	    	 //erzeugen des Nodebox-Objektes zur Darstellung der erfüllenden Menge
	        combo_at_node = new NodeBox(symbol, erfüllende_Menge, this.transitionssystem, parent);
	        
	        //platzieren auf Pane
	        StackPane nodeBox = ((NodeBox)combo_at_node).getStackPane();
	        nodeBox.setLayoutX(x);
	        nodeBox.setLayoutY(y);
	        
	        //ablegen der orginalen Koordinaten
	        ((NodeBox)combo_at_node).setOriginalXLayout(x);
	        ((NodeBox)combo_at_node).setOriginalYLayout(y);
	        
	       //Hinzufügen zur Pane
	        pane.getChildren().add(nodeBox);
         }
         
         //Ablegen der Nodebox in Liste
         this.allNodeBoxes.add(combo_at_node);
         
         
         //je nach dem ob die erfüllende Menge mit einer, zwei oder keiner erfüllenden Menge definiert wird, werden entsprechende Elemente rekursiv erzeugt
         
         if (erfüllende_Menge instanceof Ast) {// eine erfüllende Menge in Definition
        	
        	 //extrahiere KindNode
        	erfüllende_Mengen KindNode;
            KindNode = ((Ast)erfüllende_Menge).getInnere_Menge();
            
            //berechne Pos. auf Pane
            double childX = x - xOffset;
            double childY = y + yOffset;
            
            //Rufe Methode erneut auf
            this.drawTree(pane, KindNode, childX, childY, xOffset / 2.0D, yOffset, (NodeBox)combo_at_node);
            
         } else if (erfüllende_Menge instanceof Verzweigung) {//wenn zei erfüllende Mengen in Defintion
        	 
        	//extrhaiere beide Kindknoten
        	erfüllende_Mengen leftNode = ((Verzweigung)erfüllende_Menge).getLinke_Seite();
            erfüllende_Mengen rightNode = ((Verzweigung)erfüllende_Menge).getRechte_Seite();
            
            //Berechen Pos. auf Pane
            double leftChildX = x - xOffset;
            double rightChildX = x + xOffset;
            double childY = y + yOffset;
            
            //Rufe Methode zweimal auf
            this.drawTree(pane, leftNode, leftChildX, childY, xOffset / 2.0D, yOffset, (NodeBox)combo_at_node);
            this.drawTree(pane, rightNode, rightChildX, childY, xOffset / 2.0D, yOffset, (NodeBox)combo_at_node);
         } else if (erfüllende_Menge instanceof Blatt) { //Wenn keine erfüllende Menge in Definition Blatt gefunden kehre zurück
            return;
         }

      }
   }
   
   //rekurisve Funktion die Sichtbarkeit der Detailösungen toggled
   public void set_lösungsmengen_sichtbarkeit(boolean lösungsmengen_anzeigen) {
	      
	   this.detail_lösungen_anzeigen = lösungsmengen_anzeigen;
	      //Durchläuft alle Nodeboxen, schaltet Sichtbarkeit der Lösungsmengen und der Detaillösungen um 
	      for(NodeBox nodeBox: this.allNodeBoxes) {
	         if (lösungsmengen_anzeigen) {
	            nodeBox.makeToggleButtonVisible();
	            if (nodeBox instanceof NodeBox_with_detail_solution) {
	               ((NodeBox_with_detail_solution)nodeBox).make_detail_toggle_button_visible();
	            }
	         } else {
	            nodeBox.makeToggleButtonInvisible();
	            if (nodeBox instanceof NodeBox_with_detail_solution) {
	               ((NodeBox_with_detail_solution)nodeBox).make_detail_toggle_button_unvisible();
	            }
	         }
	      }

	   }
   //Rekursive Funktion die versucht Überlappungen durch Verschieben der NodeBoxen aufzulösen
   private void checkOverlappingNodeBoxes(int depth) {
	  
	   //maximale Tiefe von 20, dann Abbruch
      if (depth <= 20) {
    	  
    	 //itteriert über alle Nodeboxen und vergleicht jede Nodebox mit jeder Nodebox
         for(int i = 0; i < this.allNodeBoxes.size(); ++i) {
            for(int j = i + 1; j < this.allNodeBoxes.size(); ++j) {
            
               //extrahieren der Bounds
               NodeBox first = (NodeBox)this.allNodeBoxes.get(i);
               NodeBox second = (NodeBox)this.allNodeBoxes.get(j);
               Bounds boundsFirst = first.getStackPane().localToScene(first.getRectangleBounds());
               Bounds boundsSecond = second.getStackPane().localToScene(second.getRectangleBounds());
               
            // wenn eine NodeboxPaar weniger als 100 Pixel Abstand hat, werden diese je nach Pos. im Baum verrückt
               if (!this.hasMinimumDistance(boundsFirst, boundsSecond, 100.0)) {

                  for(NodeBox nodeBox: this.allNodeBoxes) {
                	  
                	  
                     if (nodeBox.getParentNodeBox() != null) {//Die NodeBox ohne Parent also die Wurzel wird nicht verrückt
                    	
                    	 //es muss aber immer auf die Ausgangsituation also vor der letzten Verschiebung zurück gerechnet werden
                        double parentX = nodeBox.getParentNodeBox().getStackPane().localToScene(0.0, 0.0).getX();
                        double parent_X_before_shift = parentX - nodeBox.getParentNodeBox().getLastShift();
                        double boxX = nodeBox.getStackPane().localToScene(0.0, 0.0).getX();
                        
                        if (nodeBox.getParentNodeBox().getParentNodeBox() == null) {//die erste Stufe wird um 20 Pixel nach Links bzw. rechts gerückt je anch Orientierung zur Wurzel
                           if (boxX < parent_X_before_shift) {
                              nodeBox.moveNodeBy(-20.0);
                           } else if (boxX > parent_X_before_shift) {
                              nodeBox.moveNodeBy(20.0);
                           }
                        }
                        //ab der zwieten Stufe ist die Verschiebung abhänig vom Eltern-Element
                        if (nodeBox.getParentNodeBox().getParentNodeBox() != null) {
                        	//bei Verzweigungen wird die äußere Box im Raum um das 1.5 Fache und die innere Box nach außen geschoben
                        	//dadurch wandern beide Boxen im Baum nach Außen und vergrößeren Ihren Abstand, während die Parentbox mittig bleibt
                           if (nodeBox.getParentNodeBox().getErfuellendeMenge() instanceof Verzweigung) {
                              if (nodeBox.getParentNodeBox().getLastShift() > 0.0) {
                                 if (boxX > parent_X_before_shift) {
                                    nodeBox.moveNodeBy(1.5 * nodeBox.getParentNodeBox().getLastShift());
                                 } else if (boxX < parent_X_before_shift) {
                                    nodeBox.moveNodeBy(0.5 * nodeBox.getParentNodeBox().getLastShift());
                                 }
                              } else if (boxX < parent_X_before_shift) {
                                 nodeBox.moveNodeBy(1.5 * nodeBox.getParentNodeBox().getLastShift());
                              } else if (boxX > parent_X_before_shift) {
                                 nodeBox.moveNodeBy(0.5 * nodeBox.getParentNodeBox().getLastShift());
                              }
                           } else { //Äste und Blätter werden um eine etwas größeren Betrag als die Parent verschoben um die Skallierung auzugleichen
                              nodeBox.moveNodeBy(1.2D * nodeBox.getParentNodeBox().getLastShift());
                           }
                        }
                     }
                  }
                  //wenn eine Verschiebung notwendig war, rufe die Methode erneut auf
                  this.checkOverlappingNodeBoxes(depth + 1);
                  return;
               }
            }
         }

      }
   }

   private boolean hasMinimumDistance(Bounds boundsFirst, Bounds boundsSecond, double minDistance) {
      return boundsFirst.getMaxX() + minDistance < boundsSecond.getMinX() || boundsFirst.getMaxY() + minDistance < boundsSecond.getMinY() || boundsSecond.getMaxX() + minDistance < boundsFirst.getMinX() || boundsSecond.getMaxY() + minDistance < boundsFirst.getMinY();
   }

   private void updateLines() {
	   
	   //entferne alle alten Linien und bereinige die Liste der Lines
      for(Line line: this.lines) {
         this.zeichenPane.getChildren().remove(line);
      }
      this.lines.clear();

      //itterie über alle NodeBoxen
      for(NodeBox nodeBox: this.allNodeBoxes){

         NodeBox parentNode = nodeBox.getParentNodeBox();
         
         //Wenn NodeBox nicht die Wurzel ist
         if (parentNode != null) {
        	
        	 //Verbinde die Max-Werte von NodeBox Y mit den Min-Werten des Parent Y und die beiden X-Center Werte
            Bounds nodeBounds = nodeBox.getRectangleBounds();
            Bounds parentBounds = parentNode.getRectangleBounds();
            double EndX = nodeBounds.getCenterX();
            double EndY = nodeBounds.getMinY();
            double StartX = parentBounds.getCenterX();
            double StartY = parentBounds.getMaxY();
            
            //erstelle Line und Füge hinzu
            Line line = new Line(StartX, StartY, EndX, EndY);
            this.zeichenPane.getChildren().add(line);
            this.lines.add(line);
         }
      }

   }
   
   //Methode die das orginal-Layout des Baums wiederherstellt
   public void resetLayout() {
	   
	   //Itteriere über alle Nodeboxen und setzte die Verschiebungen zurück auf den Originalwert
      for(NodeBox nodeBox:this.allNodeBoxes) {
    	  
    	  //Wen die Box die Detail_Solutionschlatfläche hat, müssen auch alle Children zurückgesetzt werden
         if (nodeBox instanceof NodeBox_with_detail_solution) { 
        	 
            NodeBox_with_detail_solution detailNodeBox = (NodeBox_with_detail_solution)nodeBox;
            detailNodeBox.getDetail_toggle_pane_complete().setLayoutX(nodeBox.getOriginalXLayout());
            detailNodeBox.getDetail_toggle_pane_complete().setLayoutY(nodeBox.getOriginalYLayout());
            detailNodeBox.getDetail_toggle_pane_complete().setTranslateX(0.0);
            detailNodeBox.getDetail_toggle_pane_complete().setTranslateY(0.0);
            this.resetChildrenTransforms(detailNodeBox.getDetail_toggle_pane_complete());

         }
         else {
            nodeBox.getStackPane().setLayoutX(nodeBox.getOriginalXLayout());
            nodeBox.getStackPane().setLayoutY(nodeBox.getOriginalYLayout());
            nodeBox.getStackPane().setTranslateX(0.0D);
            nodeBox.getStackPane().setTranslateY(0.0D);
         }
      }
      
      //setzt die Last-Shifts zur Berechnung der Verschiebung zurück, bei der Überlappung zurück
      for(NodeBox nodeBox: this.allNodeBoxes) {
         nodeBox.setLastShift(0.0);
      }
      
      //aktualisiert Linien
      this.updateLines();
   }
   
   //Rekrusive Fuktion die die Koordianten aller Kinder zurücksetzt
   private void resetChildrenTransforms(Pane parent) {

      for(Node child: parent.getChildren()) {
    	  
          child.setTranslateX(0);
          child.setTranslateY(0);
          
         if (child instanceof Pane) {
            this.resetChildrenTransforms((Pane)child);
         }
      }

   }
}