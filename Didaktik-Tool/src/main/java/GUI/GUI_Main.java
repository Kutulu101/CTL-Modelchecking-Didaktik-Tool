   package GUI;

import java.util.Objects;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GUI_Main extends Application {
   public void start(Stage primaryStage) {
      VBox root = new VBox(15.0D);
      root.setPadding(new Insets(20.0D));
      root.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)}));
      Button btnFreierModus = this.createButton("Freier Modus", () -> {
         this.openFreierModus();
         primaryStage.close();
      });
      Button btnZeichenModus = this.createButton("Zeichenmodus", () -> {
         this.openZeichenModus();
         primaryStage.close();
      });
      Button btnInequivalenzen = this.createButton("CTL-Inäquivalenzen ", () -> {
         this.openInequiModus();
         primaryStage.close();
      });
      Objects.requireNonNull(primaryStage);
      Button btnBeenden = this.createButton("Programm Beenden", primaryStage::close);
      root.getChildren().addAll(btnFreierModus, btnZeichenModus, btnInequivalenzen, btnBeenden);
      Scene scene = new Scene(root, 300.0D, 200.0D);
      primaryStage.setTitle("Main Menü");
      primaryStage.setScene(scene);
      primaryStage.show();
   }

   private Button createButton(String text, Runnable action) {
      Button button = new Button(text);
      button.setPrefWidth(250.0D);
      button.setStyle("-fx-border-color: black; -fx-background-radius: 10;");
      button.setOnAction((e) -> {
         action.run();
      });
      return button;
   }

   private void openFreierModus() {
      Stage stage = new Stage();
      GUI_freier_Modus freierModus = new GUI_freier_Modus(true);
      freierModus.start(stage);
   }

   private void openZeichenModus() {
      Stage stage = new Stage();
      GUI_zeichen_modus zeichenModus = new GUI_zeichen_modus(true);
      zeichenModus.start(stage);
   }

   private void openInequiModus() {
      Stage stage = new Stage();
      GUI_Inequiv inequivModus = new GUI_Inequiv(true);
      inequivModus.start(stage);
   }

   public static void main(String[] args) {
      launch(args);
   }
}