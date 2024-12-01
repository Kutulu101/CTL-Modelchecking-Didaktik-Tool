 package CTL_Backend;

import java.util.ArrayList;
import java.util.Iterator;
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
   private erfüllende_Mengen startpunkt = null;
   private Transitionssystem ts;
   private boolean lösungsmengen_anzeigen;
   private List<NodeBox> allNodeBoxes = new ArrayList();
   private List<Line> lines = new ArrayList();
   private Pane zeichenPane;
   private boolean isSatisfyingSetsVisible;

   public boolean isLösungsmengen_anzeigen() {
      return this.lösungsmengen_anzeigen;
   }

   public void setLösungsmengen_anzeigen(boolean lösungsmengen_anzeigen) {
      this.lösungsmengen_anzeigen = lösungsmengen_anzeigen;
      Iterator var2 = this.allNodeBoxes.iterator();

      while(var2.hasNext()) {
         NodeBox node = (NodeBox)var2.next();
         if (lösungsmengen_anzeigen) {
            node.makeToggleButtonVisible();
            if (node instanceof NodeBox_with_detail_solution) {
               ((NodeBox_with_detail_solution)node).make_detail_toggle_button_visible();
            }
         } else {
            node.makeToggleButtonInvisible();
            if (node instanceof NodeBox_with_detail_solution) {
               ((NodeBox_with_detail_solution)node).make_detail_toggle_button_unvisible();
            }
         }
      }

   }

   public CTL_Formel_Baum(erfüllende_Mengen startpunkt, Transitionssystem ts) {
      this.startpunkt = startpunkt;
      this.ts = ts;
   }

   public Stage zeichneBaum(double startwidht, double startHeight, Button shared_button) {
      this.zeichenPane = new Pane();
      if (this.startpunkt != null) {
         this.startpunkt.berechne(this.ts);
         this.drawTree(this.zeichenPane, this.startpunkt, startwidht / 2.0D, 100.0D, 200.0D, 80.0D, (NodeBox)null);
         this.updateLines();
      }

      Group group = new Group();
      group.getChildren().add(this.zeichenPane);
      HBox buttonBox = new HBox(10.0D);
      Button copiedButton = new Button("Formelbaum ausblenden");
      copiedButton.setOnAction(shared_button.getOnAction());
      Button resetToCenterButton = new Button("Formelbaum zentrieren");
      resetToCenterButton.setOnAction((event) -> {
         double centerX = (this.zeichenPane.getWidth() - this.zeichenPane.getLayoutBounds().getWidth()) / 2.0D;
         double centerY = (this.zeichenPane.getHeight() - this.zeichenPane.getLayoutBounds().getHeight()) / 2.0D;
         group.setTranslateX(centerX);
         group.setTranslateY(centerY);
         group.setScaleX(1.0D);
         group.setScaleY(1.0D);
         group.setTranslateX(0.0D);
         group.setTranslateY(0.0D);
      });
      Button entzerrenButton = new Button("Baum entzerren");
      entzerrenButton.setOnAction((event) -> {
         this.resetLayout();
         this.checkOverlappingNodeBoxes(0);
         this.updateLines();
      });
      Button mache_sichtbar = new Button("Erfüllende Mengen sichtbar machen");
      mache_sichtbar.setOnAction((event) -> {
         this.isSatisfyingSetsVisible = !this.isSatisfyingSetsVisible;
         if (this.isSatisfyingSetsVisible) {
            mache_sichtbar.setText("Erfüllende Mengen ausblenden");
         } else {
            mache_sichtbar.setText("Erfüllende Mengen sichtbar machen");
         }

         this.setLösungsmengen_anzeigen(this.isSatisfyingSetsVisible);
      });
      buttonBox.getChildren().addAll(copiedButton, mache_sichtbar, entzerrenButton, resetToCenterButton);
      buttonBox.setStyle("-fx-padding: 10;");
      VBox vbutton_box = new VBox(10.0D);
      vbutton_box.setPadding(new Insets(5.0D));
      vbutton_box.getChildren().add(buttonBox);
      vbutton_box.setStyle("-fx-background-color: #D3D3D3;");
      VBox gesamt_box = new VBox();
      StackPane.setAlignment(vbutton_box, Pos.TOP_LEFT);
      gesamt_box.getChildren().addAll(vbutton_box, group);
      VBox.setVgrow(vbutton_box, Priority.ALWAYS);
      StackPane.setAlignment(vbutton_box, Pos.TOP_LEFT);
      this.zeichenPane.setPickOnBounds(false);
      Scene scene = new Scene(gesamt_box, startwidht, startHeight);
      Stage stage = new Stage();
      stage.setScene(scene);
      this.zeichenPane.prefWidthProperty().bind(scene.widthProperty());
      this.zeichenPane.prefHeightProperty().bind(scene.heightProperty());
      scene.setOnScroll((event) -> {
         double zoomFactor = 1.1D;
         if (event.getDeltaY() < 0.0D) {
            zoomFactor = 0.9D;
         }

         double mouseX = event.getSceneX();
         double mouseY = event.getSceneY();
         group.setScaleX(group.getScaleX() * zoomFactor);
         group.setScaleY(group.getScaleY() * zoomFactor);
         group.setTranslateX(group.getTranslateX() - (mouseX - group.getTranslateX()) * (zoomFactor - 1.0D));
         group.setTranslateY(group.getTranslateY() - (mouseY - group.getTranslateY()) * (zoomFactor - 1.0D));
      });
      group.setOnMousePressed((event) -> {
         if (event.isSecondaryButtonDown()) {
            group.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
         }

      });
      group.setOnMouseDragged((event) -> {
         if (event.isSecondaryButtonDown()) {
            double[] data = (double[])group.getUserData();
            double deltaX = event.getSceneX() - data[0];
            double deltaY = event.getSceneY() - data[1];
            group.setTranslateX(group.getTranslateX() + deltaX);
            group.setTranslateY(group.getTranslateY() + deltaY);
            group.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
         }

      });
      group.setOnMouseReleased((event) -> {
         this.updateLines();
      });
      group.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
         group.setTranslateX((scene.getWidth() - newBounds.getWidth()) / 2.0D);
         group.setTranslateY((scene.getHeight() - newBounds.getHeight()) / 2.0D);
      });
      stage.setOnCloseRequest((event) -> {
         stage.hide();
         event.consume();
         copiedButton.fire();
      });
      stage.show();
      return stage;
   }

   private void drawTree(Pane pane, erfüllende_Mengen node, double x, double y, double xOffset, double yOffset, NodeBox parent) {
      if (node != null) {
         String symbol;
         if (node instanceof HatÜbergang && node.get_symbol().contains("übergänge")) {
            HatÜbergang hatÜbergang = (HatÜbergang)node;
            Set<Übergang> übergänge = hatÜbergang.getÜbergänge();
            String übergängeString = (String)übergänge.stream().map(Übergang::getZeichen).collect(Collectors.joining(", "));
            symbol = node.get_symbol().replace("übergänge", übergängeString);
         } else {
            symbol = node.get_symbol();
         }

         NodeBox combo_at_node;
         if (node instanceof detail_lösung) {
            combo_at_node = new NodeBox_with_detail_solution(node, symbol, this.ts, parent);
            Pane nodeBox = ((NodeBox_with_detail_solution)combo_at_node).getDetail_toggle_pane_complete();
            nodeBox.setLayoutX(x);
            nodeBox.setLayoutY(y);
            ((NodeBox)combo_at_node).setOriginalXLayout(x);
            ((NodeBox)combo_at_node).setOriginalYLayout(y);
            pane.getChildren().add(nodeBox);
         } else {
            combo_at_node = new NodeBox(symbol, node, this.ts, parent);
            StackPane nodeBox = ((NodeBox)combo_at_node).getStackPane();
            nodeBox.setLayoutX(x);
            nodeBox.setLayoutY(y);
            ((NodeBox)combo_at_node).setOriginalXLayout(x);
            ((NodeBox)combo_at_node).setOriginalYLayout(y);
            pane.getChildren().add(nodeBox);
         }

         this.allNodeBoxes.add(combo_at_node);
         erfüllende_Mengen leftNode;
         if (node instanceof Ast) {
            leftNode = ((Ast)node).getInnere_Menge();
            double childX = x - xOffset;
            double childY = y + yOffset;
            this.drawTree(pane, leftNode, childX, childY, xOffset / 2.0D, yOffset, (NodeBox)combo_at_node);
         } else if (node instanceof Verzweigung) {
            leftNode = ((Verzweigung)node).getLinke_Seite();
            erfüllende_Mengen rightNode = ((Verzweigung)node).getRechte_Seite();
            double leftChildX = x - xOffset;
            double rightChildX = x + xOffset;
            double childY = y + yOffset;
            this.drawTree(pane, leftNode, leftChildX, childY, xOffset / 2.0D, yOffset, (NodeBox)combo_at_node);
            this.drawTree(pane, rightNode, rightChildX, childY, xOffset / 2.0D, yOffset, (NodeBox)combo_at_node);
         } else if (node instanceof Blatt) {
            return;
         }

      }
   }

   private void checkOverlappingNodeBoxes(int depth) {
      if (depth <= 20) {
         for(int i = 0; i < this.allNodeBoxes.size(); ++i) {
            for(int j = i + 1; j < this.allNodeBoxes.size(); ++j) {
               NodeBox first = (NodeBox)this.allNodeBoxes.get(i);
               NodeBox second = (NodeBox)this.allNodeBoxes.get(j);
               Bounds boundsFirst = first.getStackPane().localToScene(first.getRectangleBounds());
               Bounds boundsSecond = second.getStackPane().localToScene(second.getRectangleBounds());
               if (!this.hasMinimumDistance(boundsFirst, boundsSecond, 100.0D)) {
                  Iterator var8 = this.allNodeBoxes.iterator();

                  while(var8.hasNext()) {
                     NodeBox box = (NodeBox)var8.next();
                     if (box.getParentNodeBox() != null) {
                        double parentX = box.getParentNodeBox().getStackPane().localToScene(0.0D, 0.0D).getX();
                        double parent_X_before_shift = parentX - box.getParentNodeBox().getLastShift();
                        double boxX = box.getStackPane().localToScene(0.0D, 0.0D).getX();
                        if (box.getParentNodeBox().getParentNodeBox() == null) {
                           if (boxX < parent_X_before_shift) {
                              box.moveNodeBy(-20.0D);
                           } else if (boxX > parent_X_before_shift) {
                              box.moveNodeBy(20.0D);
                           }
                        }

                        if (box.getParentNodeBox().getParentNodeBox() != null) {
                           if (box.getParentNodeBox().getErfuellendeMenge() instanceof Verzweigung) {
                              if (box.getParentNodeBox().getLastShift() > 0.0D) {
                                 if (boxX > parent_X_before_shift) {
                                    box.moveNodeBy(1.5D * box.getParentNodeBox().getLastShift());
                                 } else if (boxX < parent_X_before_shift) {
                                    box.moveNodeBy(0.5D * box.getParentNodeBox().getLastShift());
                                 }
                              } else if (boxX < parent_X_before_shift) {
                                 box.moveNodeBy(1.5D * box.getParentNodeBox().getLastShift());
                              } else if (boxX > parent_X_before_shift) {
                                 box.moveNodeBy(0.5D * box.getParentNodeBox().getLastShift());
                              }
                           } else {
                              box.moveNodeBy(1.2D * box.getParentNodeBox().getLastShift());
                           }
                        }
                     }
                  }

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
      Iterator var1 = this.lines.iterator();

      while(var1.hasNext()) {
         Line line = (Line)var1.next();
         this.zeichenPane.getChildren().remove(line);
      }

      this.lines.clear();
      var1 = this.allNodeBoxes.iterator();

      while(var1.hasNext()) {
         NodeBox nodeBox = (NodeBox)var1.next();
         NodeBox parentNode = nodeBox.getParentNodeBox();
         if (parentNode != null) {
            Bounds nodeBounds = nodeBox.getRectangleBounds();
            Bounds parentBounds = parentNode.getRectangleBounds();
            double EndX = nodeBounds.getCenterX();
            double EndY = nodeBounds.getMinY();
            double StartX = parentBounds.getCenterX();
            double StartY = parentBounds.getMaxY();
            Line line = new Line(StartX, StartY, EndX, EndY);
            this.zeichenPane.getChildren().add(line);
            this.lines.add(line);
         }
      }

   }

   public void resetLayout() {
      Iterator var1 = this.allNodeBoxes.iterator();

      NodeBox nodeBox;
      while(var1.hasNext()) {
         nodeBox = (NodeBox)var1.next();
         if (nodeBox instanceof NodeBox_with_detail_solution) {
            NodeBox_with_detail_solution detailNodeBox = (NodeBox_with_detail_solution)nodeBox;
            detailNodeBox.getDetail_toggle_pane_complete().setLayoutX(nodeBox.getOriginalXLayout());
            detailNodeBox.getDetail_toggle_pane_complete().setLayoutY(nodeBox.getOriginalXLayout());
            detailNodeBox.getDetail_toggle_pane_complete().setTranslateX(0.0D);
            detailNodeBox.getDetail_toggle_pane_complete().setTranslateY(0.0D);
            this.resetChildrenTransforms(detailNodeBox.getDetail_toggle_pane_complete());
            detailNodeBox.getStackPane().setTranslateX(detailNodeBox.getStackPane().getTranslateX());
         } else {
            nodeBox.getStackPane().setLayoutX(nodeBox.getOriginalXLayout());
            nodeBox.getStackPane().setLayoutY(nodeBox.getOriginalXLayout());
            nodeBox.getStackPane().setTranslateX(0.0D);
            nodeBox.getStackPane().setTranslateY(0.0D);
         }
      }

      var1 = this.allNodeBoxes.iterator();

      while(var1.hasNext()) {
         nodeBox = (NodeBox)var1.next();
         nodeBox.setLastShift(0.0D);
      }

      this.updateLines();
   }

   private void resetChildrenTransforms(Pane parent) {
      Iterator var2 = parent.getChildren().iterator();

      while(var2.hasNext()) {
         Node child = (Node)var2.next();
         if (child instanceof Pane) {
            this.resetChildrenTransforms((Pane)child);
         }
      }

   }
}