package GUI;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import CTL_Backend.ErrorDialog;
import CTL_Backend.NormalFormException;
import CTL_Backend.Zustandsformel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;


//Klasse die die Eingaben in der Combobox handelt und die Zustandsformel verwaltet
public class Combobox_Handler {
	
    // Stack zum Speichern der Labels und ComboBoxen (beide sind vom Typ Node)
    private Stack<Node> historyStack = new Stack<>();
    int i;
    
    //zum ersetzten der ComboBox
    private AtomicReference<ComboBox<String>> eingabeComboboxRef = new AtomicReference<>();
    
    //Zustandsformel
    private Zustandsformel zustandsformel = new Zustandsformel ("");
    private Definition_Reader defintion_reader = new Definition_Reader();
    
    //Hbox für die Aufnahme der Comboboxen und der Labels
    HBox formelbox;
    
    //Lsite mit globalen Transitionen
    List<String> vorauswahl_transitionen = null;
	
    //Konstruktor kennt HBox die in Eingaben in GUI anzeigt
	public Combobox_Handler(HBox formelbox){
		this.formelbox = formelbox;
	}
	
    public Zustandsformel getZustandsformel() {
		return zustandsformel;
	}
    
    //Gibt die Zustandsformel gewandelt in Rekursive Form zurück
    public Zustandsformel get_transformed_Zustandsformel() {
    	zustandsformel.turn_to_normal_form();
    	zustandsformel.turn_string_into_recursive_ctl_rekursiv();
		return zustandsformel;
	}
    
    //Methode die den Umgang mit der ersten Combobox auf der GUI beschreibt
    public void handle_first_combobox(BorderPane root) {
        // Erstelle ein Label für den Text
        Label textLabel = new Label("Formel eingeben:");

        // Füge das Label zur HBox hinzu
        this.formelbox.getChildren().add(textLabel);
        
        //erzeugen und einfügen der Combobox
        ComboBox<String> eingabeCombobox = new ComboBox<>();
        
        //zustandformel berechnet aus dem bereits eingelsenen String welche Symbole verwendet werden können
        this.configureComboBox(eingabeCombobox,this.getZustandsformel().einlesbare_Symbole());
        historyStack.push(eingabeCombobox);
        this.formelbox.getChildren().add(eingabeCombobox);
        this.formelbox.setPadding(new Insets(10.0));
        
        // Ereignisbehandlung für die ComboBox
        eingabeCombobox.setOnAction(this::handleComboBoxAction);
        
        //AtomicRef auf die ComboBox
        eingabeComboboxRef.set(eingabeCombobox);
        
        //Formelbox einfügen
        root.setBottom(this.formelbox);
    }
    
    //MEthode die getriggert wird wenn eine Eingabe in ComboBox bemacht wird
	public void handleComboBoxAction(Event event) {
    	
    	ComboBox<String> sourceComboBox = (ComboBox<String>) event.getSource();
    	String entered_symbol;
    	
        // Überprüfe, ob die ausgewählte Option "Transition eingeben" ist
        if ("Transition eingeben".equals(sourceComboBox.getValue())) {
        	//Öffnet Eingabefeld
        	entered_symbol = Character.toString(read_transition("Bitte die gewünschte Transition eingeben"));
        }
        else {entered_symbol = (String) sourceComboBox.getValue();}
        
        //Tooltip für das ausgewählte Item extrahieren
        Tooltip selectedTooltip = new Tooltip(zustandsformel.einlesbare_Symbole().get(entered_symbol)+defintion_reader.getDefinitionForSymbol(entered_symbol,"CTL-Definitionen.txt","Definition nicht gefunden") +"\n\n"+ defintion_reader.getDefinitionForSymbol(entered_symbol,"CTL-Symboleumgangsprachlich.txt","" ));
        
        //NAch dem Einlesen der Eingabe soll Label erzeugt werden und 
        this.replaceComboBoxWithTextField(entered_symbol,selectedTooltip);
        //this.zustandsformel.ein_char_einlesen(entered_symbol);
    }

	//Methode die InputBox öffnet und das Einlesen einer Transition ermöglicht
	 private char read_transition(String headline){
		
	        // Öffne eine Input-Box (TextInputDialog) für die Eingabe eines Charakters
	        TextInputDialog dialog = new TextInputDialog();
	        dialog.setTitle("Transition Eingabe");
	        dialog.setHeaderText(headline); // Verwende die headline hier
	        dialog.setContentText("Charakter:");
	
	        // Zeige den Dialog und warte auf die Eingabe des Benutzers
	        Optional<String> result = dialog.showAndWait();
	
	        if (result.isPresent()) {
	            String input = result.get();
	            if (input.length() == 1 && input !="") {
	                // Wenn ein einzelner Charakter eingegeben wurde, verarbeite ihn
	                char enteredChar = input.charAt(0);
	                return enteredChar; // Rückgabe des gültigen Zeichens
	            } else {
	                // Falls der Benutzer mehr als einen Charakter eingibt
	                return read_transition("Bitte Genau einen Charakter eingeben!"); // Rekursiver Aufruf mit neuer Nachricht
	            }
	        } else {
	            // Falls der Benutzer den Dialog abbricht
	            System.out.println("Eingabe abgebrochen.");
	            return '\0'; // Rückgabewert, der anzeigt, dass keine gültige Eingabe gemacht wurde
	        }
    }
	 
	 //Methode die ComboBox entfernt, eingelesenen Char in Label darstellt, und neue Combobox erzeugt
    private void replaceComboBoxWithTextField(String enteredSymbol, Tooltip tooltip) {

    	//entfernt alte Combobox über AtomicRef
        ComboBox<String> oldComboBox = eingabeComboboxRef.get();
        if (oldComboBox != null) {
        	historyStack.pop();
            this.formelbox.getChildren().remove(oldComboBox); // Entferne die alte ComboBox
        }else {System.out.println("Combobox nicht gefunden");};
    	
        //erzeugt Label
        Label textField = new Label();
        textField.setText(enteredSymbol);
     
        //Färbe zu unrecht eingelesene Symbole rot
        if (this.zustandsformel.einlesbare_Symbole().get(enteredSymbol) != "" && this.zustandsformel.getAll_symbols().contains(enteredSymbol)) {  	
           textField.setStyle("-fx-background-color: red; -fx-text-fill: white;");
           //Wenn Formelende falsch war dann wird letztes Label rot gefärbt
           if (enteredSymbol == "Formelende" && !this.historyStack.isEmpty()) {
              Node topElement = (Node)this.historyStack.peek();
              if (topElement instanceof Label) {
                 Label topTextLabel = (Label)topElement;
                 topTextLabel.setStyle("-fx-background-color: red; -fx-text-fill: white;");
              }
           }
        }
        
        //Tooltip auf Textfeld übertragen
        Tooltip.install(textField, tooltip);
        
        //einlesen des Zeichend in die Zustandsformel
        this.zustandsformel.ein_char_einlesen(enteredSymbol);
        
        //neue Combobox nur wenn nicht Formelende gelesen wurde 
        if(!(enteredSymbol.contains("Formelende"))){
        	
        	//erstelle neue ComboBox
	        ComboBox<String> newComboBox = new ComboBox<>();
	        configureComboBox(newComboBox,zustandsformel.einlesbare_Symbole());
	        
	        //Füge Label und ComboBox hinzu
	        formelbox.getChildren().addAll(textField, newComboBox);
	        
	        // Füge das neue Label und die ComboBox zum Stack hinzu (beide sind vom Typ Node)
	        historyStack.push(textField);
	        historyStack.push(newComboBox);
	        
	        // neue Combobox an Event anhängen
	        newComboBox.setOnAction(this::handleComboBoxAction);
	
	        // Update der Referenz auf die neue ComboBox
	        eingabeComboboxRef.set(newComboBox);
        }
    }
    

      
    //Methode die ComboBox befüllt und mit ToolTips hinzufügt
    private void configureComboBox(ComboBox<String> comboBox, Map<String, String> gruende) {
        
        // Die Keys der Map als ObservableList für die ComboBox-Items verwenden
        ObservableList<String> items = FXCollections.observableArrayList(gruende.keySet());
        comboBox.setItems(items);

     // Prüfen, ob this.vorauswahl_transitionen nicht null und nicht leer ist
        if (this.vorauswahl_transitionen != null && !this.vorauswahl_transitionen.isEmpty()&& "".equals(gruende.get("Transition eingeben"))) {
            // "Transition auswählen" entfernen, falls vorhanden
            items.remove("Transition eingeben");

            // Alle Strings aus this.vorauswahl_transitionen zu den Items hinzufügen
            items.addAll(this.vorauswahl_transitionen);
        }


    	// Benutzerdefinierte ListCell für ComboBox
        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String symbol, boolean empty) {
                        super.updateItem(symbol, empty);
                        if (symbol != null && !empty) {
                            setText(symbol);

                            // Wenn Symbol in der Map der Symbole ist und der Grund nicht "" ist, man es also nicht einlesen darf -> ausgrauen
                            if (gruende.containsKey(symbol) && !"".equals(gruende.get(symbol))) {
                                // Text und Hintergrund grau färben
                                setStyle("-fx-text-fill: gray; -fx-background-color: lightgray;");
                                String grund_nicht_einlesbar = zustandsformel.einlesbare_Symbole().get(symbol);
                                // Tooltip mit dem Grund für die Ablehnung hinzufügen
                                Tooltip tooltip = new Tooltip(grund_nicht_einlesbar+defintion_reader.getDefinitionForSymbol(symbol,"CTL-Definitionen.txt","Definition nicht gefunden") +"\n\n"+ defintion_reader.getDefinitionForSymbol(symbol,"CTL-Symboleumgangsprachlich.txt","" ));
                                
                                //TollTip hinzufügen
                                setTooltip(tooltip);
                                

                            } else {
                                // Standardtextfarbe für einlesbare Symbole
                                setStyle("-fx-text-fill: black;");
                                setTooltip(new Tooltip(defintion_reader.getDefinitionForSymbol(symbol,"CTL-Definitionen.txt","Definition nicht gefunden")+"\n\n"+ defintion_reader.getDefinitionForSymbol(symbol,"CTL-Symboleumgangsprachlich.txt","" ))); 
                            }
                        } else {
                            setText(null);
                            setTooltip(null);
                        }
                    }
                };
            }
        });
    }
      
 // Methode zum Rückgängig machen der letzten Eingabe, entfernt letze Elemente und erzeugt neue Combobox
    public void undo_combobox() {
    	//neue Combobox
        ComboBox newComboBox;
        
        String test= this.zustandsformel.getFormel_string();
        
        //Wenn Formelende letztes Label ist, kann keine Combobox oder Label entfernt werden
        if (this.historyStack.size() < 2 && (this.historyStack.size() != 1 || !(this.historyStack.peek() instanceof Label))) {
        	
        	
           if (this.zustandsformel.getFormel_string().endsWith("Formelende")) {
        	  
        	   //letzen char aus Formelentfernen
              this.zustandsformel.entferneLetztenChar(checkIfRed());
              
              //neue Combobox erzeugen
              newComboBox = new ComboBox();
              this.configureComboBox(newComboBox, this.zustandsformel.einlesbare_Symbole());
              this.formelbox.getChildren().add(newComboBox);
              this.historyStack.push(newComboBox);
              newComboBox.setOnAction(this::handleComboBoxAction);
              this.eingabeComboboxRef.set(newComboBox);  
           } 
           //Ansonsten müssen Combobox und Label entfernt werden
        } else {
           if (this.historyStack.peek() instanceof ComboBox) {
              Node lastComboBox = (Node)this.historyStack.pop();
              this.formelbox.getChildren().remove(lastComboBox);
              Node lastLabel = (Node)this.historyStack.pop();
              this.formelbox.getChildren().remove(lastLabel);
           }
           
           //entferne den lezten Char aus der Zustandsformel
           this.zustandsformel.entferneLetztenChar(checkIfRed());
           
           //erzeuge neue ComboBox
           newComboBox = new ComboBox();
           this.configureComboBox(newComboBox, this.zustandsformel.einlesbare_Symbole());
           this.formelbox.getChildren().add(newComboBox);
           this.historyStack.push(newComboBox);
           newComboBox.setOnAction(this::handleComboBoxAction);
           this.eingabeComboboxRef.set(newComboBox);
        }
     }
    
    public void clear_combobox_handler() {
        zustandsformel = new Zustandsformel("");
        historyStack.clear();
        eingabeComboboxRef.set(null);
        this.formelbox.getChildren().clear();
    }
    
    //prüft ob ein Label auf Rot steht, also ob ein CTL-Symbol zu unrecht eingelesen wurde
    public boolean checkIfRed() {
        for (Node node : this.historyStack) {
            String style = node.getStyle();
            if (style != null && style.contains("-fx-background-color: red;")) {
                return true;
            }
        }
        return false;
    }

    //prüft ob Stack mit Labels und Combobox leer ist
     public boolean isStackEmpty() {
        return this.historyStack.isEmpty();
     }

	public void setVorauswahl_transitionen(List<String> vorauswahl_transitionen) {
		this.vorauswahl_transitionen = vorauswahl_transitionen;
	}
}
