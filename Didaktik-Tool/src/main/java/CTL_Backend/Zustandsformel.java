package CTL_Backend;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

//Klasse dien ganze Zustandsformel repräsentiert und verwaltet
public class Zustandsformel {
	
	//Zuszandsformel als String
	private String formel_string = "";
	//Zustandsformel in Normalform als String
	private String formel_string_normal_form = "";
	
	//Zustandsformel ist rekursiv über die erfüllenden Mengen definiert
	private ErfüllendeMenge Start_der_rekursiven_Definition;
	
	//efüllende Menge der gesamten Gleichung
	private Set<Zustand> lösungsmenge = new HashSet<>();
	
	//Map die verwaltet welche Symbole aktuell über GUI zur CTL hinzugefügt werden können und welche warum ausgeschlossen werden
	private Map<String, String> gruendeFuerNichteinlesbareSymbole;
	
	//Für das rekursive überprüfen ob CTL-Formel gültig ist
	private String fehlerbeschreibung = "";
	private String last_checked_Formular = "";
	
	//Counter zum überwachen ob die eingegebne Formel gültig ist
    int counter_normale_klammern = 0;
    int counter_spitze_klammern = 0;
    int counter_eckige_klammern = 0;
    
    //Liste die alle unternommenen Umformungen zur Normalform hin speichert
    private List<Umformung> ersetzungen;
    
    //alle im Kurstext definierten Symbole
    HashSet<String> all_symbols = new HashSet<>(Arrays.asList(
            "∃", "∀", "◇", "○", "□", "U",
            "[", "]", "(", ")", "〈", "〉",
            "∧", "∨", "¬", "1","0", "Transition eingeben","Formelende"
        ));
    
    //Konstruktor beginnt mit eingelesener Stringformel
    
	public Zustandsformel(String formel_string) {
	
		gruendeFuerNichteinlesbareSymbole = new HashMap<>();
		
	    //Map mit den Symbolen setze den Value auf ""
        for (String symbol : this.all_symbols) {
            this.gruendeFuerNichteinlesbareSymbole.put(symbol, "");
        }
        
        // Lese alle Zeichen aus dem formel_string ein
        for (char ch : formel_string.toCharArray()) {
            this.ein_char_einlesen(String.valueOf(ch));  // Cast von char zu String
        }
        
        //String bereinigen für bessere Lesbarkeit
        this.formel_string = this.formel_string.replace(",", "");
        this.formel_string = this.formel_string.replace(" ", "");
       
	}
	//Methode die CTL-Formel in Normalform bringt,
	 public void turn_to_normal_form() {
		 
	    // Initialisiere formel_string_normal_form als formel_string
	    this.formel_string_normal_form = this.formel_string;
	    
	    this.formel_string_normal_form .replace("Formelende", "");
	    
	    // Liste zur Speicherung aller Ersetzungen
	    this.ersetzungen = new ArrayList<>();
	    
	    boolean changed;
	    
	    //er wird geprüft ob ein nicht erlaubtes Symbol enthalten ist, falls ja wird dieses nach ersetzt und der Vorgang wiederholt bis kein unerlaubtes Symbol mehr enthalten ist
	    do {
	        changed = false;  // Schleifenbedingung: keine Änderungen zu Beginn
	        
	        String original = this.formel_string_normal_form;  // Original-String vor der Ersetzung
	        
	        //Regel 1: Wenn ∀○ enthalten ist, ersetze es durch ¬∃○¬
	        if (this.formel_string_normal_form.contains("∀○")) {
	            int index = this.formel_string_normal_form.indexOf("∀○");
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("∀○", "¬∃○¬");
	            changed = true;
	            // Erzeuge Ersetzungs-Objekt und füge es der Liste hinzu
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Regel 1: Ersetze ∀○ mit ¬∃○¬"));
	            continue;
	        }
	        
        
	        //Regel 3: Wenn ∀□ enthalten ist, ersetze mit ¬∃1U¬
	        if (this.formel_string_normal_form.contains("∀□")) {
	            int index = this.formel_string_normal_form.indexOf("∀□");
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("∀□", "¬∃1U¬");
	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Regel 3: Ersetze ∀□ mit ¬∃1U¬"));
	            continue;
	        }
	
	        //Regel 4: Wenn ∃◇ enthalten ist, ersetze mit ∃1U
	        if (this.formel_string_normal_form.contains("∃◇")) {
	            int index = this.formel_string_normal_form.indexOf("∃◇");
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("∃◇", "∃1U");
	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Regel 4: Ersetze ∃◇ mit ∃1U"));
	            continue;
	        }
	
	        //Regel 5: Wenn ∀◇ enthalten ist, ersetze mit ¬∃□¬
	        if (this.formel_string_normal_form.contains("∀◇")) {
	            int index = this.formel_string_normal_form.indexOf("∀◇");
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("∀◇", "¬∃□¬");
	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Regel 5: Ersetze ∀◇ mit ¬∃□¬"));
	            continue;
	        }
	        
	        //Diese Regel hat, den Nachzug nach den anderen Regeln für Quantoren, weil ansonsten z.B bei ∀◇∃1U1 das ∃ Übergangen werden würde
		     // Regel 2: Wenn ∀ϕUψ enthalten ist, ersetze mit ¬∃□¬ϕ∧¬∃□¬ϕU(¬ϕ∧¬ψ)
	        if (this.formel_string_normal_form.contains("U")) {
	            int indexU = this.formel_string_normal_form.indexOf("U");

	            // Suche den Teil links von U (ϕ), beginnend mit ∀
	            String ϕ = "";
	            int leftIndex = indexU - 1;
	            boolean foundForAll = false;

	            // Gehe nach links, um ∀ zu finden
	            while (leftIndex >= 0) {
	                if (this.formel_string_normal_form.charAt(leftIndex) == '∀') {
	                    foundForAll = true;
	                    break;
	                }
	                leftIndex--;
	            }

	            // Wenn ∀ gefunden wurde, speichere ϕ
	            if (foundForAll) {
	            	
	            	//extrahiere ϕ
	                ϕ = "("+this.formel_string_normal_form.substring(leftIndex + 1, indexU).trim()+")";

	                // Suche den Teil rechts von U (psi), mit Beachtung der Klammerung
	                String ψ = "";
	                int rightIndex = indexU + 1;
	                int parenthesesCounter = 0;
	                boolean boundaryFound = false;

	                while (rightIndex < this.formel_string_normal_form.length()) {
	                    char currentChar = this.formel_string_normal_form.charAt(rightIndex);

	                    // Klammerzähler erhöhen oder verringern
	                    if (currentChar == '(') {
	                        parenthesesCounter++;
	                    } else if (currentChar == ')') {
	                        parenthesesCounter--;
	                    }

	                    // Grenze finden, wenn Klammerzähler 0 ist und ein ∧ oder ∨ auftritt
	                    if ((currentChar == '∧' || currentChar == '∨') && parenthesesCounter == 0) {
	                        boundaryFound = true;
	                        break;
	                    }

	                    rightIndex++;
	                }

	                // Bestimme psi basierend auf der gefundenen Grenze
	                if (boundaryFound) {
	                    ψ = "("+this.formel_string_normal_form.substring(indexU + 1, rightIndex).trim()+")";
	                } else {
	                    ψ = "("+this.formel_string_normal_form.substring(indexU + 1).trim()+")";
	                }


		            // Ersetze ∀ϕUψ durch ¬∃□¬ϕ∧¬∃¬ϕU(¬ϕ∧¬ψ)
		            String ersatz = "¬∃□¬" + ψ + "∧¬∃¬" + ψ + "U(¬" + ϕ + "∧¬" + ψ + ")";

		            // Aktualisiere die Formel mit dem ersetzten Ausdruck
		            this.formel_string_normal_form = this.formel_string_normal_form.substring(0, leftIndex) + ersatz + this.formel_string_normal_form.substring(rightIndex);
		            changed = true;
		            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, leftIndex, indexU + ψ.length(), "Regel 2: Ersetze ∀ϕUψ mit ¬∃□¬ϕ∧¬∃□¬ϕU(¬ϕ∧¬ψ)"));
		            continue;
	            } 
	        }
	        
	     // Regel 6: Wenn ϕ∨ψ enthalten ist, ersetze mit ¬(¬ϕ∧¬ψ) 
	        if (this.formel_string_normal_form.contains("∨")) {
	        	
	            int index = this.formel_string_normal_form.indexOf("∨");

	            // Finde den linken und rechten Teil
	            markiereLinkenTeil(index);
	            markiereRechtenTeil(index+5); // +5

	            // Extrahiere die markierten Teile
	            int leftStart = this.formel_string_normal_form.indexOf("links");
	            int leftEnd = this.formel_string_normal_form.indexOf("rechts");
	            
	            index = this.formel_string_normal_form.indexOf("∨");

	            String leftPart = this.formel_string_normal_form.substring(leftStart + 5, index).trim();
	            String rightPart = this.formel_string_normal_form.substring(index + 1, leftEnd).trim();

	            // Entferne die Markierungen "links" und "rechts"
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("links", "").replace("rechts", "");

	            // Teile vor und nach dem Ausdruck beibehalten
	            String prefix = this.formel_string_normal_form.substring(0, leftStart).trim();
	            String suffix = this.formel_string_normal_form.substring(leftEnd-5).trim(); // eglt +5 weil aber links und rechts enfternt werden +5-10
	            
	            // Ersetze ∨ durch die Negationsregel
	            String ersetzterAusdruck = "¬(¬" + "("+leftPart+")" + "∧¬" + "("+rightPart +")"+ ")";
	            this.formel_string_normal_form = (prefix + ersetzterAusdruck+ suffix).trim();

	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index,
	                    "Regel 6: Ersetze ϕ∨ψ mit ¬(¬ϕ∧¬ψ)"));
	            continue;
	        }
	        
	      //Entfernt Doppelte Negation Wenn ¬¬ enthalten ist, ersetze mit ""
	        if (this.formel_string_normal_form.contains("¬¬")) {
	            int index = this.formel_string_normal_form.indexOf("¬¬");
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("¬¬", "");
	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Doppelte Negation löst sich auf ¬¬"));
	            continue;
	        }
	        
	    } while (changed);  // Wiederhole, solange Änderungen vorgenommen wurden
	 }
	 
	 //Hilfsmehthode für das ersetzten von ODER
	 private void markiereLinkenTeil(int index) {
		    int leftIndex = index - 1;
		    int klammerBalance = 0;
		    char prevChar = ' '; // Variable zur Speicherung des vorherigen Zeichens

		    while (leftIndex >= 0) {
		        char currentChar = this.formel_string_normal_form.charAt(leftIndex);

		        if (currentChar == ')') {
		            klammerBalance++;
		        } else if (currentChar == '(') {
		            klammerBalance--;
		        }

		        // Wenn die Klammerbalance negativ wird, haben wir eine Klammerüberspannung über dem Oder,
		        // die überspannende Klammer muss durch -(- ersetzt werden
		        if (klammerBalance < 0) {
		            break;
		        }

		        prevChar = currentChar; // Setze prevChar auf das aktuelle Zeichen für die nächste Iteration
		        leftIndex--;
		    }

		    // Überprüfen, ob leftIndex < 0 geworden ist, und korrekt behandeln
		    int startIndex = Math.max(leftIndex + 1, 0);
		    this.formel_string_normal_form = this.formel_string_normal_form.substring(0, startIndex) + "links" + this.formel_string_normal_form.substring(startIndex);
		}
	 
	 		//Hilfsmehthode für das ersetzten von ODER
		private void markiereRechtenTeil(int index) {
		    int rightIndex = index + 1;
		    int klammerBalance = 0;
		    char currentChar = 0;

		    while (rightIndex < this.formel_string_normal_form.length()) {
		        currentChar = this.formel_string_normal_form.charAt(rightIndex);
		        if (currentChar == '(') {
		            klammerBalance++;
		        } else if (currentChar == ')') {
		            klammerBalance--;
		        }

		        // Wenn die Klammerbalance wieder bei 0 ist, oder man auf ein U stößt das nicht in klammern steht haben wir den rechten Teil gefunden
		        if (klammerBalance <0 ) {
		            break;
		        }

		        rightIndex++;
		    }
		    // Falls keine schließende Klammer gefunden wurde ,markiere den Rest
		    if (rightIndex == this.formel_string_normal_form.length()) {
		        this.formel_string_normal_form = this.formel_string_normal_form.substring(0, rightIndex) + "rechts" + this.formel_string_normal_form.substring(rightIndex);
		    } 
		    else {
		        this.formel_string_normal_form = this.formel_string_normal_form.substring(0, rightIndex + 1) + "rechts" + this.formel_string_normal_form.substring(rightIndex + 1);
		    }
		    }
	 //Methode zur Ausgabe der Lösungsmenge der CTL-Formel
	public  Set<Zustand> get_Lösungsmenge(Transitionssystem ts){
		if (this.Start_der_rekursiven_Definition == null){
			this.turn_to_normal_form();
			this.Start_der_rekursiven_Definition = ZustandsformelUmwandler.parseZustandsformel(this.getFormel_string_normal_form());
			//this.turn_string_into_recursive_ctl();
		}
		this.lösungsmenge = Start_der_rekursiven_Definition.berechne(ts);
		return this.lösungsmenge;
	}
	
	//Methode die Verwaltet welche Symbole pber die GUI einglesen werden können
	public Map<String, String> einlesbare_Symbole() {
		
	    // Konstanten für Fehlermeldungen
	    final String KEINE_OEFFNENDE_KLAMMER = "Kann nicht eingelesen werden, da keine öffnende normale Klammer vorhanden ist." + "\n\n";
	    final String KEINE_OEFFNENDE_SPITZE_KLAMMER = "Kann nicht eingelesen werden, da keine öffnende spitze Klammer vorhanden ist." + "\n\n";
	    final String KEINE_OEFFNENDE_ECKIGE_KLAMMER = "Kann nicht eingelesen werden, da keine öffnende eckige Klammer vorhanden ist."+ "\n\n";
	    final String NICHT_VERSCHACHTELBAR = "Kann nicht eingelesen werden, da man \"[\" und \"〈\" nicht verschachteln kann"+ "\n\n";
	    final String NICHT_LEER_BEGIN = "Man kann nicht mit einem UND oder ODER starten, wenn kein Term eingegeben wurde."+ "\n\n";
	    final String NICHT_NACH_UND_ODER = "Man kann nach einem UND oder ODER nicht direkt ein UND oder ODER einlesen."+ "\n\n";
	    final String NICHT_NACH_OEFFNENDER_KLAMMER = "Man kann nicht direkt nach einer geöffneten Klammer ein UND oder ODER einlesen."+ "\n\n";
	    final String TRANSITION_NUR_ZWISCHEN_KLAMMERN = "Transitionen dürfen nur zwischen einem \"〈\" oder einem \"[\" stehen."+ "\n\n";
	    final String NUR_TRANSITION_NACH_KLAMMER = "Nach einem \"〈\" oder \"[\" kann man nur Transitionen eingeben."+ "\n\n";
	    final String NACH_TRANSITION = "Nach einer Transition kann entweder eine weitere Transition oder die passende schließende Klammer stehen."+ "\n\n";
	    final String VOR_PFADOPERATOR = "Vor den Pfadoperatoren \"◇\", \"○\", \"□\" muss ein Quantor wie \"∃\" oder \"∀\" stehen, da man sonst eine Pfadformel erhält."+ "\n\n";
	    final String UND_UND_ODER_NUR_NACH_ZUSTANDSFORMEL = "UND oder ODER können nur nach einer korrekten Zustandsformel eingelesen werden.";
	    final String FORMELENDE_NUR_WENN_KLAMMERN_OFFEN = "Die Formeleingabe kann nur mit beendet werden, wenn gleich viele öffnende und schließende Klammern vorhanden sind"+ "\n\n";
	    final String UNTIL_NUR_WENN_ZUSTANDSFORMEL = "U kann nur eingelesen werden, wenn nach dem Quantor eine korrekte Zustandsformel eingelesen wurde";
	    final String UNTIL_NUR_MIT_QUANTOR = "U kann nur eingelesen, wenn vor der Zustandsformel ein Quantor eingelesen wurde, da man sonst eine Pfadformel und keine Zustandsformel erhalten würde\n\n";
	    final String ZWISCHEN_KLAMMERN = "Zwischen öffnender und schließender Klammer muss eine gültige Zustandsformel stehen \n\n";
	    final String EINS_NICHT_NACH_ZF = "Man kann keine 1 oder 0 (eine Zustandsformel) an eine Zustandsformel anhängen \n\n";
	    
	    
	    // Ursprungszustand herstellen
	    for (String symbol : this.all_symbols) {
	        this.gruendeFuerNichteinlesbareSymbole.put(symbol, "");
	    }

	    // Man kann nicht mit schließenden Klammern beginnen
	    if (this.counter_normale_klammern <= 0) {
	        this.gruendeFuerNichteinlesbareSymbole.put(")", KEINE_OEFFNENDE_KLAMMER);
	    }

	    if (this.counter_spitze_klammern <= 0) {
	        this.gruendeFuerNichteinlesbareSymbole.put("〉", KEINE_OEFFNENDE_SPITZE_KLAMMER);
	    }

	    if (this.counter_eckige_klammern <= 0) {
	        this.gruendeFuerNichteinlesbareSymbole.put("]", KEINE_OEFFNENDE_ECKIGE_KLAMMER);
	    }

	    // Verschachtelung von eckigen und spitzen Klammern
	    if (this.counter_eckige_klammern > 0 || this.counter_spitze_klammern > 0) {
	        this.gruendeFuerNichteinlesbareSymbole.put("[", NICHT_VERSCHACHTELBAR);
	        this.gruendeFuerNichteinlesbareSymbole.put("〈", NICHT_VERSCHACHTELBAR);
	    }

	    // Kein UND bzw. ODER, wenn die Formel leer ist
	    if (this.formel_string.isEmpty()) {
	        this.gruendeFuerNichteinlesbareSymbole.put("∧", NICHT_LEER_BEGIN);
	        this.gruendeFuerNichteinlesbareSymbole.put("∨", NICHT_LEER_BEGIN);
	    }

	    // Kein UND bzw. ODER nach UND oder ODER
	    if (!this.formel_string.isEmpty()) {
	        char letzterBuchstabe = this.formel_string.charAt(this.formel_string.length() - 1);
	        if (letzterBuchstabe == '∧' || letzterBuchstabe == '∨') {
	            this.gruendeFuerNichteinlesbareSymbole.put("∧", NICHT_NACH_UND_ODER);
	            this.gruendeFuerNichteinlesbareSymbole.put("∨", NICHT_NACH_UND_ODER);
	        }
	    }

	    // Kein UND bzw. ODER nach öffnender Klammer
	    if (!this.formel_string.isEmpty()) {
	        char letzterBuchstabe = this.formel_string.charAt(this.formel_string.length() - 1);
	        if (letzterBuchstabe == '(') {
	            this.gruendeFuerNichteinlesbareSymbole.put("∧", NICHT_NACH_OEFFNENDER_KLAMMER);
	            this.gruendeFuerNichteinlesbareSymbole.put("∨", NICHT_NACH_OEFFNENDER_KLAMMER);
	        }
	    }
	    
	  //Kein UND bzw. ODER nach unkorrekter Zustandsformel
	    int openingBracketIndex = findMatchingOpenBracket(this.formel_string, '(', ')', this.formel_string.length() - 1);
	    String substringToCheck = (openingBracketIndex != -1) 
	        ? this.formel_string.substring(openingBracketIndex) 
	        : this.formel_string;

	    if (!(this.ist_Zustandsformel(substringToCheck, 0))) {
	        this.gruendeFuerNichteinlesbareSymbole.put("∧", UND_UND_ODER_NUR_NACH_ZUSTANDSFORMEL + " bitte prüfe: " + substringToCheck + "\n\n");
	        this.gruendeFuerNichteinlesbareSymbole.put("∨", UND_UND_ODER_NUR_NACH_ZUSTANDSFORMEL + " bitte prüfe: "  + substringToCheck + "\n\n");
	    }

	    // Transitionen müssen nach einem "〈" oder "[" stehen
	    if (this.counter_eckige_klammern == 0 && this.counter_spitze_klammern == 0) {
	        this.gruendeFuerNichteinlesbareSymbole.put("Transition eingeben", TRANSITION_NUR_ZWISCHEN_KLAMMERN);
	    }
	    
	    //keine 1 oder 0 nach einer ZF
	    if (this.ist_Zustandsformel(this.formel_string, 0)) {
	        this.gruendeFuerNichteinlesbareSymbole.put("1", EINS_NICHT_NACH_ZF);
	        this.gruendeFuerNichteinlesbareSymbole.put("0", EINS_NICHT_NACH_ZF);
	    }
	    


	    // Nach einem "〈" oder "[" kann man nur Transitionen eingeben
	    if (!this.formel_string.isEmpty()) {
	        char letzterBuchstabe = this.formel_string.charAt(this.formel_string.length() - 1);
	        if (letzterBuchstabe == '〈' || letzterBuchstabe == '[') {
	            for (String symbol : this.all_symbols) {
	                if (!"Transition eingeben".equals(symbol)) {
	                    this.gruendeFuerNichteinlesbareSymbole.put(symbol, NUR_TRANSITION_NACH_KLAMMER);
	                }
	            }
	        }
	    }

	    // Nach Transition entweder eine weitere Transition oder schließende Klammer
	    if (!this.formel_string.isEmpty()) {
	        char letzterBuchstabe = this.formel_string.charAt(this.formel_string.length() - 1);
	        if (letzterBuchstabe == ',') {
	            for (String symbol : this.all_symbols) {
	                if (!"Transition eingeben".equals(symbol)) {
	                    if (!((this.counter_eckige_klammern > 0 && "]".equals(symbol)) ||
	                            (this.counter_spitze_klammern > 0 && "〉".equals(symbol)))) {
	                        this.gruendeFuerNichteinlesbareSymbole.put(symbol, NACH_TRANSITION);
	                    }
	                }
	            }
	        }
	    }

	    // Vor Pfadoperatoren muss ein Quantor stehen
	    if (this.formel_string.isEmpty() || !(this.formel_string.endsWith("∃") || this.formel_string.endsWith("∀"))) {
	        this.gruendeFuerNichteinlesbareSymbole.put("◇", VOR_PFADOPERATOR);
	        this.gruendeFuerNichteinlesbareSymbole.put("○", VOR_PFADOPERATOR);
	        this.gruendeFuerNichteinlesbareSymbole.put("□", VOR_PFADOPERATOR);
	    }
	    
	    // Man kann U nur einlesen wenn schon ein Quantor eingelsen wurde sonst immer Pfadfromel
      if (!this.formel_string.contains("∃") && !this.formel_string.contains("∀")) {
          this.gruendeFuerNichteinlesbareSymbole.put("U", UNTIL_NUR_MIT_QUANTOR);
       } else {
    	    int indexEoA = findeLetztesFreiesEoA(this.formel_string);
    	    if (indexEoA != -1) {
    	        String zustandsformelTeil = this.formel_string.substring(indexEoA + 1).trim();
    	        if (!this.ist_Zustandsformel(zustandsformelTeil, 0)) {
    	            this.gruendeFuerNichteinlesbareSymbole.put("U", UNTIL_NUR_WENN_ZUSTANDSFORMEL + this.last_checked_Formular + "\n\n");
    	        }
          }
       }
      

      if (counter_normale_klammern>0) { // Es gibt mindestens eine '('
    	// Initialisierung des Stacks
    	  Stack<Integer> klammerStack = new Stack<>();

	    	// Durchlaufe den String
	    	for (int i = 0; i < formel_string.length(); i++) {
	    	    char c = formel_string.charAt(i);
	
	    	    if (c == '(') {
	    	        // Wenn eine öffnende Klammer gefunden wird, speichere den Index im Stack
	    	        klammerStack.add(i);
	    	    } else if (c == ')') {
	    	        // Wenn eine schließende Klammer gefunden wird, entferne den letzten offenen Klammer-Index
	    	        if (!klammerStack.isEmpty()) {
	    	        	//entferne von Stack
	    	            klammerStack.remove(klammerStack.size() - 1);	

	    	            
	    	        }
	    	    }
	    	}
	    	if (!klammerStack.isEmpty()) {
	    	    // Letzten Index aus dem Stack holen
	    	    int letzteOffeneKlammerIndex = klammerStack.peek(); // oder pop() wenn es entfernt werden soll
	    	    
	    	    // Substring ab dem letzten Index + 1
	    	    String nachKlammer = formel_string.substring(letzteOffeneKlammerIndex + 1).trim();
	            // Prüfe, ob der Substring eine gültige Zustandsformel ist
	            if (!this.ist_Zustandsformel(nachKlammer, 0)) {
	                // Falls der Substring keine gültige Zustandsformel ist, füge den Fehlergrund in das Dictionary ein
	                this.gruendeFuerNichteinlesbareSymbole.put(")", ZWISCHEN_KLAMMERN + "überprüfe den Therm: " + nachKlammer);
	            }
	    	}
	   }

      //Formelende nur wenn gültige CTL-Formel
       if (!this.ist_Zustandsformel(this.formel_string, 0)) {
          this.gruendeFuerNichteinlesbareSymbole.put("Formelende", "Die Formeleingabe kann nur mit einer korrekten Zustandsformel beendet werden " + this.fehlerbeschreibung + "\n\n");
          this.fehlerbeschreibung = "";
       }
       //Formelende nur wenn alle Klammern geschlossen sind
       if (this.counter_normale_klammern != 0) {
          this.gruendeFuerNichteinlesbareSymbole.put("Formelende", FORMELENDE_NUR_WENN_KLAMMERN_OFFEN);
       }

       return this.gruendeFuerNichteinlesbareSymbole;
    }
	
	//Hilfmethode für verschaltelte E und A 
	private int findeLetztesFreiesEoA(String formel) {
	    Stack<Character> stack = new Stack<>();
	    for (int i = formel.length() - 1; i >= 0; i--) {
	        char c = formel.charAt(i);

	        if (c == 'U') {
	            // U-Symbol gefunden, hinzufügen, um spätere E/A zu ignorieren
	            stack.push('U');
	        } else if (c == 'E' || c == 'A') {
	            // Ein E oder A gefunden
	            if (!stack.isEmpty()) {
	                // Es wird ignoriert, da ein "U" aktiv ist
	                stack.pop();
	            }
	            else return i;
	        } else if (c == '◇' || c == '○' || c == '□') {
	            // Diese Symbole gehören zu einem vorherigen E/A, nächstes Zeichen überspringen
	            i--; // Überspringe den vorherigen Charakter (E/A)
	        }
	    }
	    // Kein freies E oder A gefunden
	    return -1;
	}
	//Methode die die Einzenlen Chars einliest und an die CTL-Formel anfügt
	 public void ein_char_einlesen(String eingelesenesSymbol) {

		    // Wenn eine öffnende normale Klammer "(" eingelesen wird, erhöhe den Zähler
		    if (eingelesenesSymbol.equals("(")) {
		        this.counter_normale_klammern++;
		    }

		    // Wenn eine schließende normale Klammer ")" eingelesen wird, reduziere den Zähler
		    if (eingelesenesSymbol.equals(")")) {
		        this.counter_normale_klammern--;
		    }

		    // Wenn eine öffnende spitze Klammer "〈" eingelesen wird, erhöhe den Zähler
		    if (eingelesenesSymbol.equals("〈")) {
		        this.counter_spitze_klammern++;
		    }

		    // Wenn eine schließende spitze Klammer "〉" eingelesen wird, reduziere den Zähler
		    if (eingelesenesSymbol.equals("〉")) {
		        this.counter_spitze_klammern--;
		    }
		    
		    // Wenn eine schließende eckige Klammer "]" eingelesen wird, reduziere den Zähler
		    if (eingelesenesSymbol.equals("]")) {
		        this.counter_eckige_klammern--;
		    }
		    // Wenn eine öffnende eckige Klammer "[" eingelesen wird, erhöhe den Zähler
		    if (eingelesenesSymbol.equals("[")) {
		        this.counter_eckige_klammern++;
		    }

		 
		    // Überprüfe, ob das eingelesene Symbol in this.all_symbols enthalten ist, falls nicht dann ist es eine Transition
		    boolean symbolInAllSymbols = this.all_symbols.contains(eingelesenesSymbol);
		    // Überprüfe, ob die letzte Stelle des formel_string ein Komma ist
		    boolean letzteStelleIstKomma = !this.formel_string.isEmpty() && this.formel_string.endsWith(",");

		    // Wenn das Symbol nicht in all_symbols ist und formel_string nicht mit Komma endet, erste eingelsene Transition,
		    if (!symbolInAllSymbols && !letzteStelleIstKomma) {
		        this.formel_string += "{" + eingelesenesSymbol + ",";
		    } 
		    // Wenn das Symbol nicht in all_symbols ist und formel_string mit Komma endet, mittlere Transition
		    else if (!symbolInAllSymbols && letzteStelleIstKomma) {
		        this.formel_string += eingelesenesSymbol + ",";
		    }
		    // Wenn das Symbol in all_symbols ist und formel_string mit Komma endet, letzte Transtion
		    else if (symbolInAllSymbols && letzteStelleIstKomma) {
		    	//entfernt das Komma
		    	this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 1);
		        this.formel_string += "}" + eingelesenesSymbol;
		    } 
		    // Wenn das Symbol in all_symbols ist und formel_string nicht mit Komma endet, normale Eingabe eines Symbols
		    else {
		    	 this.formel_string += eingelesenesSymbol;
			}
	 }
	 	
	 //Methode die prüft ob übergebner String eine Zustandsformel ist
	 public boolean ist_Zustandsformel(String zu_prüfende_Formel, int tiefe) {
		 
		 if(tiefe == 0) {
			 
			 String test = "";
		 }
		 
		 //Schaltet Debugging Ausgaben ein oder aus
		 boolean debug = false;
		    // Ausgaben zur Nachverfolgung des aktuellen Aufrufs
		    if (debug) printAufruf(tiefe, "Prüfe Formel: " + zu_prüfende_Formel);

		    // Entferne führende und nachfolgende Leerzeichen
		    zu_prüfende_Formel = zu_prüfende_Formel.trim();

		    
		    
		    // Zuerst an Logischen Operatoren aufteilen und  seiten einzeln prüfen aber nur wenn gleichviele öffnende und schließende Klammern vorhanden sind
				
		    int andIndex = zu_prüfende_Formel.indexOf("∧");
			int orIndex = zu_prüfende_Formel.indexOf("∨");
		
				
			// Bestimme den linken und rechten Teil basierend auf dem gefundenen logischen Operator
			if (andIndex != -1 || orIndex != -1) {
			    String leftPart;
			    String rightPart;

			    if (andIndex != -1) {
			        leftPart = zu_prüfende_Formel.substring(0, andIndex).trim();
			        rightPart = zu_prüfende_Formel.substring(andIndex + 1).trim();
			    } else { // orIndex != -1
			        leftPart = zu_prüfende_Formel.substring(0, orIndex).trim();
			        rightPart = zu_prüfende_Formel.substring(orIndex + 1).trim();
			    }

			    if (ZustandsformelUmwandler.checkBalancedParentheses(leftPart)) {
			            return ist_Zustandsformel(leftPart, tiefe + 1) && ist_Zustandsformel(rightPart, tiefe + 1);
			    }
			}
		    //###Dann einzelne Zustandsformel prüfen
		    // 1. Wenn "∃" oder "∀" gelesen wird, muss das folgende eine Pfadformel sein
		    if (zu_prüfende_Formel.startsWith("∃") || zu_prüfende_Formel.startsWith("∀")) {
		        
		    	if (debug) printAufruf(tiefe, "Gefunden: Quantor " + zu_prüfende_Formel.charAt(0));
		        String rest = zu_prüfende_Formel.substring(1).trim();
		        //NAch diesen Symbolen kommt wieder eine Zustandsformel die rekrusiv geprüft werden kann
		        if (rest.startsWith("◇") || rest.startsWith("○") || rest.startsWith("□")) {
		            return ist_Zustandsformel(rest.substring(1).trim(), tiefe + 1);
		        }
		        //Oder man findet ein U und prüft den linekn und rechten PArt ob es eine Zustandsformel ist
		        int indexOfU = ZustandsformelUmwandler.findMatchingU(rest,0);		        
		        if (indexOfU != -1) {
		            if (debug) printAufruf(tiefe, "Hinweis: 'U' gefunden nach Quantor.");
		            String leftPart = rest.substring(0, indexOfU).trim();
		            String rightPart = rest.substring(indexOfU + 1).trim();
		            return ist_Zustandsformel(leftPart, tiefe + 1) && ist_Zustandsformel(rightPart, tiefe + 1);
		        }

		        if (zu_prüfende_Formel.endsWith("Formelende")) {
		        	 addFehlerbeschreibung(zu_prüfende_Formel, "Fehler: Kein gültiges 'U' gefunden nach Quantor.");
		            if (debug) printAufruf(tiefe, "Fehler: Kein gültiges 'U' gefunden.");
		            return false;
		        }
		        else return false;
		    }

		    // 2. Klammern prüfen: "[" oder "〈", wenn es eine passende schleißende Klammer gibt, prüfe was nach der Klammer kommt
		    if (zu_prüfende_Formel.startsWith("[") || zu_prüfende_Formel.startsWith("〈")) {
		        char opening = zu_prüfende_Formel.charAt(0);
		        char closing = (opening == '[') ? ']' : '〉';
		        int closingIndex = findMatchingBracket(zu_prüfende_Formel, opening, closing);

		        if (closingIndex == -1) {
		        	 addFehlerbeschreibung(zu_prüfende_Formel, "Fehler: Kein passendes schließende Klammer für " + opening);
		            if (debug) printAufruf(tiefe, "Fehler: Kein passendes schließendes Symbol für " + opening);
		            return false;
		        }

		        String inner = zu_prüfende_Formel.substring(1, closingIndex).trim();
		        if (inner.contains("{") && inner.contains("}")) {
		            return ist_Zustandsformel(zu_prüfende_Formel.substring(closingIndex + 1).trim(), tiefe + 1);
		        }
		        
		        addFehlerbeschreibung(zu_prüfende_Formel, "Fehler: Keine gültige Formel zwischen Klammern.");
		        if (debug) printAufruf(tiefe, "Fehler: Keine gültige Formel zwischen Klammern.");
		        return false;
		    }

		    // 3. Negation prüfen: "¬", prüfen was danach kommt
		    if (zu_prüfende_Formel.startsWith("¬")) {
		        if (debug) printAufruf(tiefe, "Gefunden: Negation ¬");
		        return ist_Zustandsformel(zu_prüfende_Formel.substring(1).trim(), tiefe + 1);
		    }

		    // 4. Klammern prüfen: "(",prüfen was danach kommt
		    if (zu_prüfende_Formel.startsWith("(")) {
		        int closingIndex = findMatchingBracket(zu_prüfende_Formel, '(', ')');
		        if (closingIndex == -1) {
		            if (zu_prüfende_Formel.endsWith("Formelende")) {
		            	addFehlerbeschreibung(zu_prüfende_Formel, "Fehler: Keine passende schließende Klammer.");
		                if (debug) printAufruf(tiefe, "Fehler: Keine passende schließende Klammer.");
		                return false;
		            } else {
		                return ist_Zustandsformel(zu_prüfende_Formel.substring(1).trim(), tiefe + 1);
		            }
		        }
		        return ist_Zustandsformel(zu_prüfende_Formel.substring(1, closingIndex).trim(), tiefe + 1);
		    }


		 // ##############Basisfälle: Wenn die Formel "1" oder "0" ist, gib true zurück##########
		    if (zu_prüfende_Formel.equals("1") || zu_prüfende_Formel.equals("0")) {
		        if (debug) printAufruf(tiefe, "Erfolgreich: Basisfall erreicht mit " + zu_prüfende_Formel);
		        return true;
		    }
		    
		    // Basifall wenn leer dann false
		    if (zu_prüfende_Formel.isEmpty()) {
		        if (debug) printAufruf(tiefe, "Eine leere Eingabe ist keine Zustandsformel");
		        return false;
		    }
		    
		 // Basifall wenn in Zustandsformel "∃" oder "∀", wenn kein U in formel oder der char nach "∃" oder "∀" nicht "◇", "○", "□",-> false 
		    if (zu_prüfende_Formel.contains("∃") || zu_prüfende_Formel.contains("∀")) {
		        boolean gueltigerCharNachQuantor = false;
		        boolean enthaeltU = zu_prüfende_Formel.contains("U");

		        // Prüfen, ob ein gültiger Charakter nach "∃" oder "∀" vorhanden ist
		        int index = Math.max(zu_prüfende_Formel.indexOf("∃"), zu_prüfende_Formel.indexOf("∀"));
		        if (index != -1 && index + 1 < zu_prüfende_Formel.length()) {
		            char nextChar = zu_prüfende_Formel.charAt(index + 1);
		            gueltigerCharNachQuantor = (nextChar == '◇' || nextChar == '○' || nextChar == '□');
		        }

		        // Wenn weder ein gültiger Charakter nach "∃"/"∀" noch ein "U" vorhanden ist, false zurückgeben
		        if (!gueltigerCharNachQuantor && !enthaeltU) {
		            return false;
		        }
		    }
		    
		    // Basisfall Wenn String kein 1 oder 0 enthält, kann es keine Zustandsformel sein
		    if (!(zu_prüfende_Formel.contains("1") || zu_prüfende_Formel.contains("0"))) {
		        if (debug) printAufruf(tiefe, "Dieser Abschnitt enthält weder 1 noch 0 kann also keine Zustandsformel sein: " + zu_prüfende_Formel);
		        return false;
		    }

		    
		    //Wenn nichts gefunden wurde Fehler
		    addFehlerbeschreibung(zu_prüfende_Formel, "Fehler: Keine passende logische Struktur.");
		    if (debug) printAufruf(tiefe, "Fehler: Keine passende logische Struktur.");
		    return false;
		}

		// Hilfsfunktion, um passende Klammern zu finden
		private int findMatchingBracket(String formula, char open, char close) {
		    int depth = 0;
		    for (int i = 0; i < formula.length(); i++) {
		        if (formula.charAt(i) == open) {
		            depth++;
		        } else if (formula.charAt(i) == close) {
		            depth--;
		            if (depth <= 0) {
		                return i;
		            }
		        }
		    }
		    return -1; // Keine passende schließende Klammer gefunden
		}
		
		// Hilfsfunktion, um passende öffnende Klammer zu finden
		private int findMatchingOpenBracket(String formula, char open, char close, int startIndex) {
		    int depth = 0;
		    for (int i = startIndex; i >= 0; i--) {
		        if (formula.charAt(i) == close) {
		            depth++;
		        } else if (formula.charAt(i) == open) {
		            depth--;
		            if (depth <= 0) {
		                return i;
		            }
		        }
		    }
		    return -1; // Keine passende öffnende Klammer gefunden
		}
		

		// Hilfsfunktion, um den aktuellen Aufruf zu protokollieren
		private void printAufruf(int tiefe, String message) {
			
		    // Fügt Leerzeichen entsprechend der Tiefe hinzu, um die Rekursionstiefe zu visualisieren
		    String prefix = "  ".repeat(tiefe);
		    
		    if(!(message.contains("Fehler"))) {
		    	last_checked_Formular = message;
		    }
		}

	public String getFormel_string() {

		return formel_string;
	}

	public HashSet<String> getAll_symbols() {
		return all_symbols;
	}

	public List<Umformung> getErsetzungen() {
		return ersetzungen;
	}

	public String getFormel_string_normal_form() {
		return formel_string_normal_form;
	}
	
    // Öffentliche Methode zum Entfernen von Zeichen am Ende des Strings
	public void entferneLetztenChar(boolean is_red) {
	    // Wenn der formel_string leer ist, gibt es nichts zu entfernen
	    if (this.formel_string.isEmpty()) {
	        return;
	    }
	    
	    String formelende = "Formelende";
	    if (this.formel_string.endsWith(formelende)) {
	        // Entferne den gesamten "Formelende"-String
	        this.formel_string = this.formel_string.substring(0, this.formel_string.length() - formelende.length());
	    }else {
	    
	    	String letztesZeichen= this.formel_string.substring(this.formel_string.length() - 1);

		    // Falls das letzte Zeichen eine öffnende oder schließende Klammer ist, müssen die Zähler angepasst werden
		    if (letztesZeichen.equals("(")) {
		        this.counter_normale_klammern--;
		    } else if (letztesZeichen.equals(")")) {
		        this.counter_normale_klammern++;
		    } else if (letztesZeichen.equals("〈")) {
		        this.counter_spitze_klammern--;
		    } else if (letztesZeichen.equals("〉")) {
		        this.counter_spitze_klammern++;
		    } else if (letztesZeichen.equals("[")) {
		        this.counter_eckige_klammern--;
		    } else if (letztesZeichen.equals("]")) {
		        this.counter_eckige_klammern++;
		    }
	
		 // Wenn das letzte Zeichen eine geschlossene Transition ist
		    if ((letztesZeichen.equals("〉")||letztesZeichen.equals("]")) && !is_red) {
		        char vorletztesZeichen = this.formel_string.charAt(this.formel_string.length() - 2);
		        if (vorletztesZeichen == '}') {// Fall <{a}> soll zu <{a, werden und  <Fall <{a,b}> zu <{a,b, 
		        	this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 2);
		        	this.formel_string = this.formel_string + ",";
			    } else {//Wenn die schließenden Klammern illegal eingelesen wurden
			    	// Entferne nur das letzte Zeichen, füge nichts hinzu
		            this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 1);
			    }
		    } else if (letztesZeichen.equals(",")) { // Falls innerhalb der Eingabe
		        // Prüfe das drittletzte Zeichen
		        char drittLetztesZeichen = this.formel_string.charAt(this.formel_string.length() - 3);
	
		        if (drittLetztesZeichen == '{') { // Wenn drittletztes Zeichen ein "{", müssen die drei letzten Zeichen entfernt werden
		            // Zum Beispiel: "<{a," wird zu "<"
		            this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 3);
		        } else { // Andernfalls nur die letzten beiden Zeichen entfernen
		            // Zum Beispiel: "<{a,b," wird zu "<{a,"
		            this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 2);
		        }
		    //Falls nicht in einer Transition einfach nur letztes Zeichen entfernen
		    } else {
		    	this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 1);
		    	// Prüfen, ob der formel_string mit '}' endet  
		    	if (this.formel_string.endsWith("}")) {
		    	        // Letztes Zeichen entfernen
		    	        this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 1);
		    	        // "," hinzufügen
		    	        this.formel_string += ",";
		    	    }
		    }
	    }
	}
	
	public void print_erfüllende_zustände(Transitionssystem ts) {
		//turn_string_into_recursive_ctl();
		this.turn_to_normal_form();
		this.Start_der_rekursiven_Definition = ZustandsformelUmwandler.parseZustandsformel(this.getFormel_string_normal_form());
		Set<Zustand> lösungsmenge = this.Start_der_rekursiven_Definition.berechne(ts);
		System.out.println("###############Lösungsmenge##################");
		for(Zustand lösung:lösungsmenge) {
			System.out.println(lösung.getName());
		}
	}

	public ErfüllendeMenge getStart_der_rekursiven_Definition() {
		if (this.Start_der_rekursiven_Definition == null){
			//this.turn_string_into_recursive_ctl();
			this.turn_to_normal_form();
			this.Start_der_rekursiven_Definition = ZustandsformelUmwandler.parseZustandsformel(this.getFormel_string_normal_form());
		}	
		return Start_der_rekursiven_Definition;
	}
	
	
	private static int berechneKlammerTiefe(String term) {
        int tiefe = 0;
        for (char c : term.toCharArray()) {
            if (c == '(') {
                tiefe++;
            } else if (c == ')') {
                tiefe--;
            }
        }
        return tiefe;
    }
	
    // Hilfsfunktion zur Fehlerbeschreibung
    private void addFehlerbeschreibung(String formel, String fehler) {
    	
    	String formel_;
    	
    	if(formel=="") {
    		formel_ = "Eingabe: ......";
    	} else formel_  = "Eingabe: " + formel;
    	
    	fehlerbeschreibung = formel_  + " --> " + fehler + "\n"; // Fehlerbeschreibung erstellen
    }
    
    // Getter für die Fehlerbeschreibung
    public String getFehlerbeschreibung() {
        return fehlerbeschreibung;
    }
    
  //Wrapper-Klasse für Rückgabewerte (Ergebnis + fehlerhafter Teil)
    class Prüfungsergebnis {
     boolean istKorrekt;
     String fehlerhafterTeil;

     Prüfungsergebnis(boolean korrekt, String teil) {
         this.istKorrekt = korrekt;
         this.fehlerhafterTeil = teil;
     }
    }
    
    //Triple-Klasse, um Index, Term und Tiefe zu speichern
    static class Triple<K, V, T> {
        private final K key;
        private final V value;
        private final T third;

        public Triple(K key, V value, T third) {
            this.key = key;
            this.value = value;
            this.third = third;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
        public T getThird() { return third; }
    }

	public void turn_string_into_recursive_ctl_rekursiv() {
		this.turn_to_normal_form();
		this.Start_der_rekursiven_Definition = ZustandsformelUmwandler.parseZustandsformel(this.getFormel_string_normal_form());
	}
}


	


