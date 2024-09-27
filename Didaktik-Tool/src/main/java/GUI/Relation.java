package GUI;

import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Label;

public class Relation {

    private final Circle firstCircle;
    private final Circle secondCircle;
    private final Text firstCircleLabel;
    private final Text secondCircleLabel;
    private final Shape line;
    private final Polygon arrowHead;
    private final Label arrowLabel;
    private final String detailsString;
    private int offset_counter;

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

	public void increaseOffset_counter(boolean is_reversed) {
		
		// Primzahl als Basis für den Offset
	    int step = 23;
		
		if (is_reversed && offset_counter < 0) {
			this.offset_counter = - this.offset_counter;
		}else if(is_reversed && offset_counter > 0) {
			this.offset_counter = - this.offset_counter -step;
		}

	
		    // Wenn der Offset 0 ist, setze den ersten Wert auf step
		    if (this.offset_counter == 0) {
		        this.offset_counter = step;
		    } 
		    // Wenn der Offset positiv ist, mache ihn negativ
		    else if (this.offset_counter > 0) {
		        this.offset_counter = -this.offset_counter;
		    } 
		    // Wenn der Offset negativ ist, erhöhe ihn um step
		    else {
		        this.offset_counter = -this.offset_counter + step;
		    }
	}
}
