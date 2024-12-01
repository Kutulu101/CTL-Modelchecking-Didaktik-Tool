   package GUI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class Arrow_Builder {
   private HashSet<Relation> list_of_relations = new HashSet();
   private double offsetX;
   private double offsetY;
   private static final double EPSILON = 0.1D;

   private boolean areDoublesEqual(double a, double b) {
      return Math.abs(a - b) < 0.1D;
   }

   public HashSet<Relation> getList_of_relations() {
      return this.list_of_relations;
   }

   public void clearRelations() {
      this.list_of_relations.clear();
   }

   public void add_relation(Relation relation) {
      this.list_of_relations.add(relation);
   }

   public void drawArrow(Pane drawingpane, Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, List<String> vorauswahl_transitionen) {
      int offset = this.off_set_for_existing_relations(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel);
      double startX = firstCircle.getCenterX();
      double startY = firstCircle.getCenterY();
      double endX = secondCircle.getCenterX();
      double endY = secondCircle.getCenterY();
      Polygon arrowHead;
      if (startX != endX && startY != endY) {
         Arrow_Builder.EdgePoints points = this.calculateEdgePoints(startX, startY, endX, endY, firstCircle.getRadius(), secondCircle.getRadius(), offset);
         double var19;
         if (firstCircle.getCenterY() > secondCircle.getCenterY()) {
            var19 = points.m;
         } else {
            var19 = -points.m;
         }

         Arrow_Builder.CustomLine customLine = new Arrow_Builder.CustomLine(points.startX, points.startY, points.endX, points.endY, points.m);
         arrowHead = this.createArrowHead(customLine.getArrow_x(), customLine.getArrow_y(), points.endX, points.endY);
         drawingpane.getChildren().addAll(customLine, customLine.getCurvedPath(), arrowHead);
         customLine.showCurved();
         this.showTextInputField(customLine.getX_textfield_point_x(), customLine.getY_textfield_point_y(), offset, customLine, arrowHead, firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, vorauswahl_transitionen);
      } else {
         Shape line = this.zeichneTeilkreisLinie(startX, startY, firstCircle.getRadius(), (double)(4 * offset), 210.0D);
         arrowHead = this.createArcArrowHead((Arc)line, offset);
         drawingpane.getChildren().addAll(line, arrowHead);
         this.showTextInputField(((Arc)line).getCenterX(), ((Arc)line).getCenterY(), offset, line, arrowHead, firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, vorauswahl_transitionen);
      }

   }

   private Arrow_Builder.EdgePoints calculateEdgePoints(double startX, double startY, double endX, double endY, double radius1, double radius2, int offset) {
      double dx = endX - startX;
      double dy = endY - startY;
      double distance = Math.sqrt(dx * dx + dy * dy);
      dx /= distance;
      dy /= distance;
      double nx = -dy;
      double startEdgeX = startX + radius1 * dx + (double)offset * nx;
      double startEdgeY = startY + radius1 * dy + (double)offset * dx;
      double endEdgeX = endX - radius2 * dx + (double)offset * nx;
      double endEdgeY = endY - radius2 * dy + (double)offset * dx;
      double m = (endEdgeY - startEdgeY) / (endEdgeX - startEdgeX);
      return new Arrow_Builder.EdgePoints(startEdgeX, startEdgeY, endEdgeX, endEdgeY, m);
   }

   private Arc zeichneTeilkreisLinie(double centerX, double centerY, double radius, double startAngle_at_large_circle, double arcAngle) {
      double arcRadius = 28.0D;
      double arcCenterX = centerX + radius * Math.cos(Math.toRadians(startAngle_at_large_circle));
      double arcCenterY = centerY + radius * Math.sin(Math.toRadians(startAngle_at_large_circle));
      double startAngle = -startAngle_at_large_circle - arcAngle / 2.0D;
      Arc arc = new Arc();
      arc.setCenterX(arcCenterX);
      arc.setCenterY(arcCenterY);
      arc.setRadiusX(arcRadius);
      arc.setRadiusY(arcRadius);
      arc.setStartAngle(startAngle);
      arc.setLength(arcAngle);
      arc.setType(ArcType.OPEN);
      arc.setFill(Color.TRANSPARENT);
      arc.setStroke(Color.BLACK);
      return arc;
   }

   private Polygon createArrowHead(double startX, double startY, double endX, double endY) {
      double arrowLength = 10.0D;
      double arrowWidth = 7.0D;
      double angle = Math.atan2(endY - startY, endX - startX) - 3.141592653589793D;
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      double x1 = endX + arrowLength * cos - arrowWidth * sin;
      double y1 = endY + arrowLength * sin + arrowWidth * cos;
      double x2 = endX + arrowLength * cos + arrowWidth * sin;
      double y2 = endY + arrowLength * sin - arrowWidth * cos;
      Polygon arrowHead = new Polygon();
      arrowHead.getPoints().addAll(endX, endY, x1, y1, x2, y2);
      arrowHead.setFill(Color.BLACK);
      return arrowHead;
   }

   private Polygon createArcArrowHead(Arc arc, int offset) {
      double endAngle = Math.toRadians((double)(4 * offset + 105));
      double endXarc = arc.getCenterX() + arc.getRadiusX() * Math.cos(endAngle);
      double endYarc = arc.getCenterY() + arc.getRadiusY() * Math.sin(endAngle);
      Polygon arrowHead = new Polygon();
      double arrowSize = 15.0D;
      double arrowAngle = Math.toRadians(30.0D);
      double finalAngle = endAngle + 1.5707963267948966D - 0.3D;
      arrowHead.getPoints().addAll(endXarc, endYarc, endXarc - arrowSize * Math.cos(finalAngle - arrowAngle), endYarc - arrowSize * Math.sin(finalAngle - arrowAngle), endXarc - arrowSize * Math.cos(finalAngle + arrowAngle), endYarc - arrowSize * Math.sin(finalAngle + arrowAngle));
      arrowHead.setFill(Color.BLACK);
      return arrowHead;
   }

   private void showTextInputField(double x, double y, int offset, Shape line, Polygon arrowHead, Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, List<String> vorauswahl_transitionen) {
      Pane parentPane = (Pane)firstCircle.getParent().getParent();
      Label returnLabel = new Label();
      returnLabel.setMinWidth(50.0D);
      returnLabel.setMinHeight(30.0D);
      returnLabel.setAlignment(Pos.CENTER);
      returnLabel.setStyle("-fx-font-size: 18px;");
      if (vorauswahl_transitionen != null && !vorauswahl_transitionen.isEmpty()) {
         ComboBox<String> comboBox = new ComboBox();
         comboBox.getItems().addAll(vorauswahl_transitionen);
         comboBox.setLayoutX(x);
         comboBox.setLayoutY(y);
         comboBox.setPromptText("Wählen Sie eine Option...");
         parentPane.getChildren().add(comboBox);
         comboBox.setOnAction((event) -> {
            String selectedValue = (String)comboBox.getValue();
            returnLabel.setText(selectedValue);
            parentPane.getChildren().add(returnLabel);
            parentPane.getChildren().remove(comboBox);
            returnLabel.setOnMousePressed((event2) -> {
               returnLabel.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
            });
            returnLabel.setOnMouseDragged((event2) -> {
               double[] initialMousePosition = (double[])returnLabel.getUserData();
               double initialX = initialMousePosition[0];
               double initialY = initialMousePosition[1];
               double deltaX = event2.getSceneX() - initialX;
               double deltaY = event2.getSceneY() - initialY;
               returnLabel.setLayoutX(returnLabel.getLayoutX() + deltaX);
               returnLabel.setLayoutY(returnLabel.getLayoutY() + deltaY);
               returnLabel.setUserData(new double[]{event2.getSceneX(), event2.getSceneY()});
            });
            this.add_relation(new Relation(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, line, arrowHead, returnLabel, selectedValue, offset));
         });
      } else {
         TextField inputField = new TextField();
         inputField.setLayoutX(x);
         inputField.setLayoutY(y);
         inputField.setPromptText("Zeichen Komma getrennt eingeben...");
         parentPane.getChildren().add(inputField);
         inputField.setOnAction((event) -> {
            String userInput = inputField.getText();
            returnLabel.setText(userInput);
            parentPane.getChildren().add(returnLabel);
            parentPane.getChildren().remove(inputField);
            returnLabel.setOnMousePressed((event2) -> {
               this.offsetX = event2.getX();
               this.offsetY = event2.getY();
            });
            returnLabel.setOnMouseDragged((event2) -> {
               returnLabel.setLayoutX(event2.getSceneX() - this.offsetX);
               returnLabel.setLayoutY(event2.getSceneY() - this.offsetY);
            });
            String[] labels = returnLabel.getText().split(",");
            String[] var14 = labels;
            int var15 = labels.length;

            for(int var16 = 0; var16 < var15; ++var16) {
               String label = var14[var16];
               this.add_relation(new Relation(firstCircle, secondCircle, firstCircleLabel, secondCircleLabel, line, arrowHead, returnLabel, label, offset));
            }

         });
      }

      if (line instanceof Arc) {
         Arc arc = (Arc)line;
         double centerX = arc.getCenterX();
         double centerY = arc.getCenterY();
         double radiusX = arc.getRadiusX();
         double radiusY = arc.getRadiusY();
         double startAngle = Math.toRadians(arc.getStartAngle());
         double length = Math.toRadians(arc.getLength());
         double midAngle = startAngle + length / 2.0D;
         double apexX = centerX + radiusX * Math.cos(midAngle);
         double apexY = centerY - radiusY * Math.sin(midAngle);
         positionLabel(apexX, apexY, returnLabel, firstCircle);
      } else {
         returnLabel.setLayoutX(x - 25.0D);
         returnLabel.setLayoutY(y - 15.0D);
      }

   }

   private int off_set_for_existing_relations(Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel) {
      int offset = 0;
      int step = 23;
      int var7 = 20;

      boolean conflictFound;
      do {
         if (var7-- <= 0) {
            return offset;
         }

         conflictFound = false;
         Iterator var9 = this.list_of_relations.iterator();

         while(var9.hasNext()) {
            Relation relation = (Relation)var9.next();
            if (this.containsCircles(relation, firstCircle, secondCircle) && this.isConflict(relation.getLine(), firstCircle, secondCircle, offset)) {
               conflictFound = true;
               offset += step;
               break;
            }
         }
      } while(conflictFound);

      return offset;
   }

   private int extractNumber(String label) {
      String numberPart = label.replaceAll("[^0-9]", "");
      return !numberPart.isEmpty() ? Integer.parseInt(numberPart) : 0;
   }

   private boolean containsCircles(Relation relation, Circle firstCircle, Circle secondCircle) {
      return relation.getFirstCircle().equals(firstCircle) && relation.getSecondCircle().equals(secondCircle) || relation.getFirstCircle().equals(secondCircle) && relation.getSecondCircle().equals(firstCircle);
   }

   private boolean isConflict(Shape shape, Circle firstCircle, Circle secondCircle, int offset) {
      if (shape instanceof Line) {
         return this.isLineConflict((Line)shape, firstCircle, secondCircle, offset);
      } else {
         return shape instanceof Arc ? this.isArcConflict((Arc)shape, offset) : false;
      }
   }

   private boolean isLineConflict(Line line, Circle firstCircle, Circle secondCircle, int offset) {
      Arrow_Builder.EdgePoints points = this.calculateEdgePoints(firstCircle.getCenterX(), firstCircle.getCenterY(), secondCircle.getCenterX(), secondCircle.getCenterY(), firstCircle.getRadius(), secondCircle.getRadius(), offset);
      return this.areDoublesEqual(line.getStartX(), points.startX) && this.areDoublesEqual(line.getStartY(), points.startY) && this.areDoublesEqual(line.getEndX(), points.endX) && this.areDoublesEqual(line.getEndY(), points.endY) || this.areDoublesEqual(line.getStartX(), points.endX) && this.areDoublesEqual(line.getStartY(), points.endY) && this.areDoublesEqual(line.getEndX(), points.startX) && this.areDoublesEqual(line.getEndY(), points.startY);
   }

   private boolean isArcConflict(Arc arc, int offset) {
      double arcRadius = 28.0D;
      double startAngleAtLargeCircle = (double)(4 * offset);
      double arcAngle = 210.0D;
      double centerX = arc.getCenterX();
      double centerY = arc.getCenterY();
      double var10000 = centerX + arcRadius * Math.cos(Math.toRadians(startAngleAtLargeCircle));
      var10000 = centerY + arcRadius * Math.sin(Math.toRadians(startAngleAtLargeCircle));
      double startAngle = -startAngleAtLargeCircle - arcAngle / 2.0D;
      return this.areDoublesEqual(arc.getStartAngle(), startAngle);
   }

   public void updateArrows(Circle movedCircle) {
      Iterator var2 = this.list_of_relations.iterator();

      while(true) {
         while(true) {
            Relation relation;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               relation = (Relation)var2.next();
            } while(!relation.getFirstCircle().equals(movedCircle) && !relation.getSecondCircle().equals(movedCircle));

            int offset = relation.getOffset();
            double startX = relation.getFirstCircle().getCenterX();
            double startY = relation.getFirstCircle().getCenterY();
            double endX = relation.getSecondCircle().getCenterX();
            double endY = relation.getSecondCircle().getCenterY();
            if (endX != startX && startY != endY) {
               Arrow_Builder.EdgePoints points = this.calculateEdgePoints(startX, startY, endX, endY, relation.getFirstCircle().getRadius(), relation.getSecondCircle().getRadius(), offset);
               Arrow_Builder.CustomLine line = (Arrow_Builder.CustomLine)relation.getLine();
               line.updateLineAndCurve(points.startX, points.startY, points.endX, points.endY, points.m);
               line.showCurved();
               this.updateArrowHead(relation.getArrowHead(), line.getArrow_x(), line.getArrow_y(), points.endX, points.endY);
               relation.getArrowLabel().setLayoutX(line.getX_textfield_point_x());
               relation.getArrowLabel().setLayoutY(line.getY_textfield_point_y());
            } else {
               Arc arc = (Arc)relation.getLine();
               double arcRadius = 28.0D;
               double radius = relation.getFirstCircle().getRadius();
               double centerX = relation.getFirstCircle().getCenterX();
               double centerY = relation.getFirstCircle().getCenterY();
               double startAngle_at_large_circle = (double)(4 * relation.getOffset());
               int arcAngle = 210;
               double arcCenterX = centerX + radius * Math.cos(Math.toRadians(startAngle_at_large_circle));
               double arcCenterY = centerY + radius * Math.sin(Math.toRadians(startAngle_at_large_circle));
               double startAngle = -startAngle_at_large_circle - (double)(arcAngle / 2);
               arc.setCenterX(arcCenterX);
               arc.setCenterY(arcCenterY);
               arc.setRadiusX(arcRadius);
               arc.setRadiusY(arcRadius);
               arc.setStartAngle(startAngle);
               arc.setLength((double)arcAngle);
               double arrowSize = 15.0D;
               double angle = Math.toRadians((double)(195 + 4 * offset));
               Polygon arrowHead = relation.getArrowHead();
               double endXarc = arc.getBoundsInLocal().getMinX();
               double endYarc = arc.getBoundsInLocal().getMinY();
               arrowHead.getPoints().setAll(endXarc, endYarc, endXarc - arrowSize * Math.cos(angle - Math.toRadians(30.0D)), endYarc - arrowSize * Math.sin(angle - Math.toRadians(30.0D)), endXarc - arrowSize * Math.cos(angle + Math.toRadians(30.0D)), endYarc - arrowSize * Math.sin(angle + Math.toRadians(30.0D)));
               relation.getArrowLabel().setLayoutX(endXarc);
               relation.getArrowLabel().setLayoutY(endYarc);
            }
         }
      }
   }

   private void updateArrowHead(Polygon arrowHead, double startX, double startY, double endX, double endY) {
      double angle = Math.atan2(endY - startY, endX - startX) + 3.141592653589793D;
      double arrowLength = 10.0D;
      double arrowWidth = 7.0D;
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      double x1 = endX + arrowLength * cos - arrowWidth * sin;
      double y1 = endY + arrowLength * sin + arrowWidth * cos;
      double x2 = endX + arrowLength * cos + arrowWidth * sin;
      double y2 = endY + arrowLength * sin - arrowWidth * cos;
      arrowHead.getPoints().setAll(endX, endY, x1, y1, x2, y2);
   }

   private static void positionLabel(double x, double y, Label label, Circle circle) {
      double circleCenterX = circle.getCenterX();
      double circleCenterY = circle.getCenterY();
      double deltaX = x - circleCenterX;
      double deltaY = y - circleCenterY;
      double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
      double directionX = deltaX / distance;
      double directionY = deltaY / distance;
      double labelDistance = distance + 12.0D;
      double labelCenterX = circleCenterX + directionX * labelDistance;
      double labelCenterY = circleCenterY + directionY * labelDistance;
      label.setLayoutX(labelCenterX - 25.0D);
      label.setLayoutY(labelCenterY - 15.0D);
   }

   private static class EdgePoints {
      final double startX;
      final double startY;
      final double endX;
      final double endY;
      final double m;

      EdgePoints(double startX, double startY, double endX, double endY, double m) {
         this.startX = startX;
         this.startY = startY;
         this.endX = endX;
         this.endY = endY;
         this.m = m;
      }
   }

   private static class CustomLine extends Line {
      private Path curvedPath = new Path();
      private double textfieldX;
      private double textfieldY;
      private double scheitelpunkt_x;
      private double scheitelpunkt_y;
      private double arrow_x;
      private double arrow_y;

      CustomLine(double startX, double startY, double endX, double endY, double m) {
         super(startX, startY, endX, endY);
         this.updateCurve(startX, startY, endX, endY, m);
      }

      private void updateCurve(double startX, double startY, double endX, double endY, double m) {
         double midX = (startX + endX) / 2.0D;
         double midY = (startY + endY) / 2.0D;
         double deltaX = endX - startX;
         double deltaY = endY - startY;
         double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
         double perpX = -deltaY / length;
         double perpY = deltaX / length;
         double controlX = midX + perpX * 0.8D * Math.abs(deltaY);
         double controlY = midY + perpY * 0.8D * Math.abs(deltaX);
         this.scheitelpunkt_x = 0.25D * startX + 0.5D * controlX + 0.25D * endX;
         this.scheitelpunkt_y = 0.25D * startY + 0.5D * controlY + 0.25D * endY;
         this.textfieldX = this.scheitelpunkt_x + perpX * 10.0D;
         this.textfieldY = this.scheitelpunkt_y + perpY * 10.0D;
         double t = 0.99D;
         this.arrow_x = (1.0D - t) * (1.0D - t) * startX + 2.0D * (1.0D - t) * t * controlX + t * t * endX;
         this.arrow_y = (1.0D - t) * (1.0D - t) * startY + 2.0D * (1.0D - t) * t * controlY + t * t * endY;
         this.curvedPath.getElements().clear();
         MoveTo moveTo = new MoveTo(startX, startY);
         QuadCurveTo quadCurveTo = new QuadCurveTo(controlX, controlY, endX, endY);
         this.curvedPath.getElements().addAll(moveTo, quadCurveTo);
         this.curvedPath.setStroke(Color.BLACK);
         this.curvedPath.setFill(Color.TRANSPARENT);
         this.setVisible(true);
         this.curvedPath.setVisible(true);
      }

      public void updateLineAndCurve(double startX, double startY, double endX, double endY, double m) {
         this.setStartX(startX);
         this.setStartY(startY);
         this.setEndX(endX);
         this.setEndY(endY);
         this.updateCurve(startX, startY, endX, endY, m);
      }

      public void showCurved() {
         this.setVisible(false);
         this.curvedPath.setVisible(true);
      }

      public void showStraight() {
         this.setVisible(true);
         this.curvedPath.setVisible(false);
      }

      public Path getCurvedPath() {
         return this.curvedPath;
      }

      public double getX_textfield_point_x() {
         return this.textfieldX;
      }

      public double getY_textfield_point_y() {
         return this.textfieldY;
      }

      public double getScheitelpunkt_x() {
         return this.scheitelpunkt_x;
      }

      public double getScheitelpunkt_y() {
         return this.scheitelpunkt_y;
      }

      public double getArrow_x() {
         return this.arrow_x;
      }

      public double getArrow_y() {
         return this.arrow_y;
      }
   }
}