package GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;


public class GUI_Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Hintergrundfarbe hellgrau
        VBox root = new VBox(15); // Abstand zwischen Buttons
        root.setPadding(new Insets(20));
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Buttons erstellen und formatieren
        Button btnFreierModus = createButton("Freier Modus", () -> {
            openFreierModus(); // Fenster für Freier Modus öffnen
            primaryStage.close();
        });

        Button btnZeichenModus = createButton("Zeichenmodus", () -> {
            openZeichenModus(); // Fenster für Zeichen Modus öffnen
            primaryStage.close();
        });
        
        Button btnInequivalenzen = createButton("CTL-Inäquivalenzen ", () -> {
            openInequiModus(); // Fenster für Zeichen Modus öffnen
            primaryStage.close();
        });

        Button btnBeenden = createButton("Programm Beenden", primaryStage::close);

        // Alle Buttons zur VBox hinzufügen
        root.getChildren().addAll(btnFreierModus, btnZeichenModus,btnInequivalenzen, btnBeenden);

        // Szene erstellen und Stage konfigurieren
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Main Menü");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Hilfsmethode zum Erstellen von Buttons mit Design und Aktionen
    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(250); // Breite festlegen
        button.setStyle("-fx-border-color: black; -fx-background-radius: 10;"); // Schwarze Umrandung und runde Ecken
        button.setOnAction(e -> action.run());
        return button;
    }

    // Methode zum Öffnen des Fensters für den Freien Modus
    private void openFreierModus() {
        Stage stage = new Stage();
        GUI_freier_Modus freierModus = new GUI_freier_Modus(true);
        freierModus.start(stage);
    }

    // Methode zum Öffnen des Fensters für den Zeichen Modus
    private void openZeichenModus() {
        Stage stage = new Stage();
        GUI_zeichen_modus zeichenModus = new GUI_zeichen_modus(true);
        zeichenModus.start(stage);
    }
    
    // Methode zum Öffnen des Fensters für den CTL-Inäquivalenzen Modus
    private void openInequiModus() {
        Stage stage = new Stage();
        GUI_Inequiv inequivModus = new GUI_Inequiv(true);
        inequivModus.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

