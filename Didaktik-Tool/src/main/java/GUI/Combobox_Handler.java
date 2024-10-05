package GUI;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import CTL_Backend.Zustandsformel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    
    //Hbox 
    HBox formelbox;
	
	public Combobox_Handler(HBox formelbox){
		this.formelbox = formelbox;
	}
	
    public Zustandsformel getZustandsformel() {
		return zustandsformel;
	}
    
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
        
        // Ereignisbehandlung für die ComboBox
        eingabeCombobox.setOnAction(this::handleComboBoxAction);
        
        //AtomicRef auf die ComboBox
        eingabeComboboxRef.set(eingabeCombobox);
        
        //Formelbox einfügen
        root.setBottom(this.formelbox);
    }
    
	public void  handleComboBoxAction(javafx.event.ActionEvent event) {
    	
    	ComboBox<String> sourceComboBox = (ComboBox<String>) event.getSource();
    	String entered_symbol;
    	
        // Überprüfe, ob die ausgewählte Option "Transition eingeben" ist
        if ("Transition eingeben".equals(sourceComboBox.getValue())) {
        	entered_symbol = Character.toString(read_transition("Bitte die gewünschte Transition eingeben"));
        }
        else {entered_symbol = (String) sourceComboBox.getValue();}
        
     // Tooltip für das ausgewählte Item extrahieren
        Tooltip selectedTooltip = new Tooltip(zustandsformel.einlesbare_Symbole().get(entered_symbol));
        
        zustandsformel.ein_char_einlesen(entered_symbol);
        System.out.println(zustandsformel.getFormel_string());
        replaceComboBoxWithTextField(entered_symbol,selectedTooltip);        
    }
    
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
            if (input.length() == 1) {
                // Wenn ein einzelner Charakter eingegeben wurde, verarbeite ihn
                char enteredChar = input.charAt(0);
                System.out.println("Eingegebener Charakter: " + enteredChar);
                return enteredChar; // Rückgabe des gültigen Zeichens
            } else {
                // Falls der Benutzer mehr als einen Charakter eingibt
                System.out.println("Bitte nur einen einzelnen Charakter eingeben.");
                return read_transition("Bitte nur einen Charakter eingeben!"); // Rekursiver Aufruf mit neuer Nachricht
            }
        } else {
            // Falls der Benutzer den Dialog abbricht
            System.out.println("Eingabe abgebrochen.");
            return '\0'; // Rückgabewert, der anzeigt, dass keine gültige Eingabe gemacht wurde
        }
    }

    private void replaceComboBoxWithTextField(String enteredSymbol, Tooltip tooltip) {

        ComboBox<String> oldComboBox = eingabeComboboxRef.get();
        if (oldComboBox != null) {
        	historyStack.pop();
            this.formelbox.getChildren().remove(oldComboBox); // Entferne die alte ComboBox
        }else {System.out.println("Combobox nicht gefunden");};
    	
        Label textField = new Label();
        textField.setText(enteredSymbol);
     
        // Tooltip für das Label erstellen
        Tooltip.install(textField, tooltip); // Tooltip auf das Label anwenden
        
        //neue Combobox nur wenn nicht Formelende gelesen wurde 
        if(!(enteredSymbol.contains("Formelende"))){
	        ComboBox<String> newComboBox = new ComboBox<>();
	        configureComboBox(newComboBox,zustandsformel.einlesbare_Symbole());
	        
	        formelbox.getChildren().addAll(textField, newComboBox);
	        
	        // Füge das neue Label und die ComboBox zum Stack hinzu (beide sind vom Typ Node)
	        historyStack.push(textField);
	        historyStack.push(newComboBox);
	        
	        
	        // neue Combobox an Event anhängen
	        newComboBox.setOnAction(this::handleComboBoxAction);
	
	        // Update der Referenz auf die neue ComboBox
	        eingabeComboboxRef.set(newComboBox);
	        //System.out.println("Nach der " + i +  ". Eingabe: " +  eingabeComboboxRef);
        }
    }
    

      
    
    private void configureComboBox(ComboBox<String> comboBox, Map<String, String> gruende) {
        
        // Die Keys der Map als ObservableList für die ComboBox-Items verwenden
        ObservableList<String> items = FXCollections.observableArrayList(gruende.keySet());
        comboBox.setItems(items);
    	
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

                            // Wenn Symbol in der Map der nicht einlesbaren Symbole ist und der Grund nicht "" ist
                            if (gruende.containsKey(symbol) && !"".equals(gruende.get(symbol))) {
                                // Text und Hintergrund grau färben
                                setStyle("-fx-text-fill: gray; -fx-background-color: lightgray;");
                                
                                // Tooltip mit dem Grund für die Ablehnung hinzufügen
                                Tooltip tooltip = new Tooltip(zustandsformel.einlesbare_Symbole().get(symbol)+defintion_reader.getDefinitionForSymbol(symbol,"CTL-Definitionen.txt","Definition nicht gefunden") +"\n\n"+ defintion_reader.getDefinitionForSymbol(symbol,"CTL-Symboleumgangsprachlich.txt","" ));
                                
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
      
 // Methode zum Rückgängig machen der letzten Eingabe, nur wenn mindestens 2 Elemente im Stack sind
    public void undo_combobox() {
        // Prüfen, ob genug Elemente im Stack vorhanden sind
        if (historyStack.size() >= 2 || (historyStack.size() == 1 && historyStack.peek() instanceof Label)) {
            
            // Prüfen, ob das oberste Element im Stack eine ComboBox ist
            if (historyStack.peek() instanceof ComboBox) {
                // Entferne die letzte ComboBox vom Stack und aus der GUI
                Node lastComboBox = historyStack.pop();
                formelbox.getChildren().remove(lastComboBox);

                // Entferne das letzte Label vom Stack und aus der GUI
                Node lastLabel = historyStack.pop();
                formelbox.getChildren().remove(lastLabel);
            }
            //##########Entferne das letzte Symbol aus der Zustandsformel, funktioniert noch nicht bei Transitionen
            zustandsformel.entferneLetztenChar();
            System.out.println("######### " + zustandsformel.getFormel_string());

            // Neue leere ComboBox einfügen, um den Zustand vor der letzten Aktion wiederherzustellen
            ComboBox<String> newComboBox = new ComboBox<>();
            configureComboBox(newComboBox, zustandsformel.einlesbare_Symbole());

            formelbox.getChildren().add(newComboBox);
            historyStack.push(newComboBox);
            newComboBox.setOnAction(this::handleComboBoxAction);

            // Update der Referenz auf die neue ComboBox
            eingabeComboboxRef.set(newComboBox);
        } 

        // Fallback für den Fall, dass nicht genug Elemente im Stack sind
        else {
            System.out.println("Nicht genug Elemente, um eine Rückgängig-Aktion durchzuführen.");
        }
       
    }
    
    public void clear_combobox_handler() {
        zustandsformel = new Zustandsformel("");
        historyStack.clear();
        eingabeComboboxRef.set(null);
        this.formelbox.getChildren().clear();
    }
}
