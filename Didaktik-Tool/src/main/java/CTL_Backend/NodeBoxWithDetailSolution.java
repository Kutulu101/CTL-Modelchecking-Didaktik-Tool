package CTL_Backend;

import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

class NodeBox_with_detail_solution extends NodeBox {
    private Pane detail_toggle_pane_complete;
    private Button toggle_button_detail;

    public NodeBox_with_detail_solution(erfüllende_Mengen data, String nodename, Transitionssystem ts, NodeBox parent) {
        super(nodename, data, ts, parent);
        this.detail_toggle_pane_complete = this.createToggleDetailSolution_Pane((detail_lösung) this.erfuellendeMenge);
    }

    private Pane createToggleDetailSolution_Pane(detail_lösung menge) {
        Button toggleButton = new Button("+");
        toggleButton.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-size: 7px; -fx-text-fill: black; -fx-min-width: 5px; -fx-min-height: 5px; -fx-border-color: black; -fx-border-width: 1px;");
        this.toggle_button_detail = toggleButton;
        Label textBox = new Label(menge.get_schritt_weise_lösung());
        textBox.setWrapText(true);
        textBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px; -fx-font-size: 10px;");
        
        ScrollPane scrollPane = new ScrollPane(textBox);
        scrollPane.setPrefWidth(350.0);
        scrollPane.setFitToWidth(true);
        scrollPane.setVisible(false);
        
        toggleButton.setTranslateX(70.0);
        toggleButton.setTranslateY(-10.0);
        scrollPane.setTranslateX(75.0);
        scrollPane.setTranslateY(-5.0);
        
        toggleButton.setOnAction(event -> {
            if (toggleButton.getText().equals("+")) {
                toggleButton.setText("-");
                scrollPane.setVisible(true);
                scrollPane.requestLayout();
            } else {
                toggleButton.setText("+");
                scrollPane.setVisible(false);
            }
        });

        toggleButton.setVisible(false);

        Pane container = new Pane();
        container.setPrefHeight(10.0);
        container.setPrefWidth(10.0);
        container.getChildren().add(this.stackpane);
        container.getChildren().addAll(scrollPane, toggleButton);

        this.stackpane.translateXProperty().addListener((observable, oldValue, newValue) -> {
            toggleButton.setTranslateX(newValue.doubleValue() + 70.0);
            scrollPane.setTranslateX(newValue.doubleValue() + 75.0);
        });

        this.stackpane.translateYProperty().addListener((observable, oldValue, newValue) -> {
            toggleButton.setTranslateY(newValue.doubleValue() - 10.0);
            scrollPane.setTranslateY(newValue.doubleValue() - 5.0);
        });

        return container;
    }

    public void make_detail_toggle_button_visible() {
        this.toggle_button_detail.setVisible(true);
    }

    public void make_detail_toggle_button_unvisible() {
        this.toggle_button_detail.setVisible(false);
    }

    public Pane getDetail_toggle_pane_complete() {
        return this.detail_toggle_pane_complete;
    }

    public Bounds getRectanglebounds() {
        StackPane stackPane = (StackPane) this.rechteck.getParent();
        Pane containerPane = (Pane) stackPane.getParent();
        Pane parentPane = (Pane) containerPane.getParent();
        Bounds boundsInScene = this.rechteck.localToScene(this.rechteck.getBoundsInLocal());
        Bounds boundsInParentPane = parentPane.sceneToLocal(boundsInScene);
        return boundsInParentPane;
    }
}

