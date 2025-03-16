package CTL_Backend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorDialog {

    /**
     * Zeigt einen Fehlerdialog an.
     * 
     * @param title   Der Titel des Dialogs.
     * @param message Die Nachricht, die im Dialog angezeigt werden soll.
     */
    public static void show(String title, String message) {
        // Erstellen eines Dialogs mit JavaFX
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Modal, blockiert andere Fenster
        dialogStage.setTitle(title);

        // Layout des Dialogs
        VBox dialogVBox = new VBox();
        dialogVBox.setSpacing(10);
        dialogVBox.setPadding(new Insets(15));
        dialogVBox.setAlignment(Pos.CENTER);

        // Nachricht und Button hinzufügen
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true); // Text umbrechen, falls er zu lang ist

        Button closeButton = new Button("OK");
        closeButton.setOnAction(event -> dialogStage.close());

        // Elemente zur VBox hinzufügen
        dialogVBox.getChildren().addAll(messageLabel, closeButton);

        // Scene und Stage setzen
        Scene dialogScene = new Scene(dialogVBox, 400, 200);
        dialogStage.setScene(dialogScene);

        // Dialog anzeigen
        dialogStage.showAndWait();
    }
}
