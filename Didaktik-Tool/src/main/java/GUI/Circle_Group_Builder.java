    package GUI;

import CTL_Backend.Zustand;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Circle_Group_Builder {
   private int circle_counter = 0;
   private List<Group> list_of_circle_groups = new ArrayList();
   Application app;
   private Arrow_Builder arrow_builder;

   Circle_Group_Builder(Arrow_Builder arrow_builder) {
      this.arrow_builder = arrow_builder;
   }

   public List<Group> getList_of_circle_groups() {
      return this.list_of_circle_groups;
   }

   public void clearCircleGroups() {
      this.list_of_circle_groups.clear();
      this.circle_counter = 0;
   }

   public Group create_circle_with_text(BooleanProperty draw_relations) {
      Circle circle = new Circle(50.0D, Color.BLUE);
      ++this.circle_counter;
      Text text = new Text("z" + this.circle_counter);
      Random random = new Random();
      double offsetX = random.nextDouble() * 40.0D;
      double offsetY = random.nextDouble() * 40.0D;
      circle.setCenterX(100.0D + offsetX);
      circle.setCenterY(100.0D + offsetY);
      text.setX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2.0D);
      text.setY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4.0D);
      Group circleWithText = new Group();
      circleWithText.getChildren().addAll(circle, text);
      this.enableDragAndDrop(circleWithText, draw_relations);
      circleWithText.getStyleClass().add("circle_with_text");
      this.list_of_circle_groups.add(circleWithText);
      return circleWithText;
   }

   private void enableDragAndDrop(Group circleWithText, BooleanProperty draw_relations) {
      Circle circle = (Circle)circleWithText.getChildren().get(0);
      Text text = (Text)circleWithText.getChildren().get(1);
      double[] offsetX = new double[1];
      double[] offsetY = new double[1];
      circle.setOnMousePressed((event) -> {
         circle.setFill(Color.RED);
         offsetX[0] = event.getSceneX() - circle.getCenterX();
         offsetY[0] = event.getSceneY() - circle.getCenterY();
      });
      circle.setOnMouseDragged((event) -> {
         if (!draw_relations.get()) {
            circle.setCenterX(event.getSceneX() - offsetX[0]);
            circle.setCenterY(event.getSceneY() - offsetY[0]);
            text.setX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2.0D);
            text.setY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4.0D);
            this.arrow_builder.updateArrows(circle);
         }

      });
      circle.setOnMouseReleased((event) -> {
         if (!draw_relations.get()) {
            circle.setFill(Color.BLUE);
         } else {
            circle.setFill(Color.YELLOW);
         }

      });
   }

   public void färbeKreiseNachZustand(Set<Zustand> lösungsmenge, boolean is_colored) {
      Iterator var3 = this.list_of_circle_groups.iterator();

      while(true) {
         while(true) {
            Node kreisNode;
            Node textNode;
            do {
               do {
                  Group group;
                  do {
                     if (!var3.hasNext()) {
                        return;
                     }

                     group = (Group)var3.next();
                  } while(group.getChildren().size() < 2);

                  kreisNode = (Node)group.getChildren().get(0);
                  textNode = (Node)group.getChildren().get(1);
               } while(!(kreisNode instanceof Circle));
            } while(!(textNode instanceof Text));

            Circle kreis = (Circle)kreisNode;
            Text text = (Text)textNode;
            String textName = text.getText();
            boolean gefunden = lösungsmenge.stream().map(Zustand::getName).anyMatch((zustandsName) -> {
               return zustandsName.equals(textName);
            });
            if (gefunden && is_colored) {
               kreis.setFill(Color.GREEN);
            } else if (is_colored) {
               kreis.setFill(Color.RED);
            } else {
               kreis.setFill(Color.BLUE);
            }
         }
      }
   }

   public void colorAllCircles(Node node) {
      if (node instanceof Circle) {
         ((Circle)node).setFill(Color.BLUE);
      } else {
         Iterator var2;
         Node child;
         if (node instanceof Pane) {
            var2 = ((Pane)node).getChildren().iterator();

            while(var2.hasNext()) {
               child = (Node)var2.next();
               this.colorAllCircles(child);
            }
         } else if (node instanceof Group) {
            var2 = ((Group)node).getChildren().iterator();

            while(var2.hasNext()) {
               child = (Node)var2.next();
               this.colorAllCircles(child);
            }
         }
      }

   }

   public void schliesseAlleEingabefelder(Parent parent) {
      Iterator var2 = parent.getChildrenUnmodifiable().iterator();

      while(var2.hasNext()) {
         Object node = var2.next();
         if (node instanceof TextField) {
            TextField textField = (TextField)node;
            if (textField.getOnAction() != null) {
               textField.getOnAction().handle(new ActionEvent());
            }
         } else if (node instanceof Parent) {
            this.schliesseAlleEingabefelder((Parent)node);
         }
      }

   }
}