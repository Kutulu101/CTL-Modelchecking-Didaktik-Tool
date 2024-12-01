package GUI;

import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Label;


//Klasse zum Speicher von Einzelenne Relationen um dauras TS zu erstellen und die eingezeichenten Kreise zu koordinieren
public class Relation {

    private final Circle firstCircle;
    private final Circle secondCircle;
    private final Text firstCircleLabel;
    private final Text secondCircleLabel;
    private final Shape line;
    private final Polygon arrowHead;
    private final Label arrowLabel;
    private final String detailsString;
    private int offset_counter = 0;
    private int step = 23;  // Schrittweite als Konstante

    public Relation(Circle firstCircle, Circle secondCircle, Text firstCircleLabel, Text secondCircleLabel, 
                    Shape line, Polygon arrowHead, Label arrowLabel, String transition) {
        this.firstCircle = firstCircle;
        this.secondCircle = secondCircle;
        this.firstCircleLabel = firstCircleLabel;
        this.secondCircleLabel = secondCircleLabel;
        this.line = line;
        this.arrowHead = arrowHead;
        this.arrowLabel = arrowLabel;
        this.offset_counter = 0;
        
        // Berechne detailsString basierend auf den übergebenen Parametern
        this.detailsString = "Relation: " + firstCircleLabel.getText() + " " + transition + " " + secondCircleLabel.getText();
    }

    public Circle getFirstCircle() {
        return firstCircle;
    }

    public Circle getSecondCircle() {
        return secondCircle;
    }

    public Text getFirstCircleLabel() {
        return firstCircleLabel;
    }

    public Text getSecondCircleLabel() {
        return secondCircleLabel;
    }

    public Shape getLine() {
        return line;
    }

    public Polygon getArrowHead() {
        return arrowHead;
    }

    public Label getArrowLabel() {
        return arrowLabel;
    }

    public String getDetailsString() {
        return detailsString;
    }

	public int getOffset_counter() {
		return offset_counter;
	}

	public void setOffset_counter(int offset_counter) {
		this.offset_counter = offset_counter;
	}


	public int get_and_increase_Offset() {
	    // Wenn der aktuelle Offset positiv ist
	    if (this.offset_counter > 0) {
	        // Setzen wir ihn auf den negativen Wert und geben diesen zurück
	        this.offset_counter = -this.offset_counter;
	        return this.offset_counter;
	    }
	    // Wenn der Offset 0 ist
	    else if (this.offset_counter == 0) {
	        this.offset_counter = step;  // Setzen auf Schrittwert
	        return this.offset_counter;
	    }
	    // Wenn der Offset negativ ist
	    else {
	        // Berechnen des nächsten Wertes
	        // Hier wird die absolute Anzahl der Schritte ermittelt
	        int counter = Math.abs(this.offset_counter) / step;  // Anzahl der Schritte
	        int nextValue = (counter + 1) * step;  // Nächster Wert in Schritten
	        this.offset_counter = -nextValue;  // Negiere den neuen Wert
	        return this.offset_counter;
	    }
	}
}

