package GUI;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class Tooltips_für_Buttons {

    // Methode für Tooltip freier Modus
    public static void setTooltipFor_freierModus(Button button) {
        final String tooltipText = "Startet den freien Modus: Eine Art Auto-Solver zum selbstständigen Herumprobieren.   ";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    // Methode für Tooltip Zeichenmodus
    public static void setTooltipFor_ZeichenModus(Button button) {
        final String tooltipText = "Startet den Zeichen-Modus: Hier können die Eigenschaften bestimmter Symbole und Gleichungen überprüft werden.\n"
        		+ "Dies geschieht durch das Zeichnen eines passenden und eines unpassenden Transitionssystems.  ";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    
    public static void setTooltipFor_Inquivalenz(Button button) {
        final String tooltipText = "Startet den CTL-Inäquivalenz-Modus: In diesem Modus soll ein Transitionssystem für eine gegebene Ungleichung gezeichnet werden.\n"
        		+ "Ziel ist es, eine Seite der Ungleichung zu erfüllen, während die andere Seite nicht erfüllt wird.";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_neuerZustand(Button button) {
        final String tooltipText = "Fügt einen Zustand zur Zeichenfläche hinzu, diese können via Drag-and-Drop bewegt werden.";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    

    public static void setTooltip_relation_einzeichen(Button button) {
        final String tooltipText = "Ermöglicht das Einzeichnen von Transitionen. Die Zustände färben sich gelb, Drag-and-Drop wird deaktiviert.\n Durch klicken auf zwei Zustände wird eine Transition erzeugt.";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_Ts_loeschen(Button button) {
        final String tooltipText = "Löscht das eingezeichnet Transitionssystem";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_neustart(Button button) {
        final String tooltipText = "Startet den Modus neu";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_beenden_Modus(Button button) {
        final String tooltipText = "Kehrt zum Hauptmenu zurück";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_undo(Button button) {
        final String tooltipText = "Macht die letzte Eingabe zur CTL-Gleichung rückgänig";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_berechen(Button button) {
        final String tooltipText = "Startet die Berechnung der Lösungsmenge";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    public static void setTooltip_prüfen(Button button) {
        final String tooltipText = "Startet die Berechnung der Lösungsmengen und gibt Feedback";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_formelbaum(Button button) {
        final String tooltipText = "Öffnet ein zweites Fenster, dass die CTL-Gleichung als (unechten) Binärbaum darstellt.\nDabei können Teilergebnisse eingeblendet werden.";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_färben(Button button) {
        final String tooltipText = "Färbt die erfüllenden Mengen im Transitinssystem Grün und die nicht-erfüllenden Rot";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_normalform(Button button) {
        final String tooltipText = "Blendet die Schritt für Schritt Umformung in die Normalform ein";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_globale_Transition(Label label) {
        final String tooltipText = "Hier können Zeichen Kommagetrennt eingegeben werden,\n diese erscheinen dann als Vorauswahl beim Einzeichnen neuer Transitionen und bei der Eingabe der CTL-Gleichung";
        Tooltip tooltip = new Tooltip(tooltipText);
        label.setTooltip(tooltip);
    }
   
    
    public static void setTooltip_entzerren(Button button) {
        final String tooltipText = "Versucht Überlappungen der Knoten aufzulösen. Bei Bedarf können einzelne Knoten via Drag-and-Drop entlang der x-Achse verschoben werden.";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_zentrieren(Button button) {
        final String tooltipText = "Zentriert die Wurzel des Binärbaumes auf dem Bildschirm. Der gesamte Baum kann via Drag-and-Drop durch klicken auf einen Knoten mit der rechten Maustaste verschoben werden. Zoom mit dem Mausrad ist ebenfalls möglich.";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    public static void setTooltip_teilergebnisse(Button button) {
        final String tooltipText = "Fügt den Knoten einen Button hinzu mit dem das Ergebnis des Teilbaums, dessen Wurzel der Knoten ist, eingeblendet werden kann.\nDie Detaillösungen für Fixpunktitterationen können ebenfalls eingeblendet werden.";
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }
    
    
    
    
    
    
    
    
}
