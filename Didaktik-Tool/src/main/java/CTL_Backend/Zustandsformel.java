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

//Klasse dien ganze Zustandsformel repräsentiert und verwaltet
public class Zustandsformel {
	
	//Zuszandsformel als String
	private String formel_string = "";
	//Zustandsformel in Normalform als String
	private String formel_string_normal_form = "";
	
	//Zustandsformel ist rekursiv über die erfüllenden Mengen definiert
	private erfüllende_Mengen Start_der_rekursiven_Definition;
	
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
	        }
	        
	     // Regel 2: Wenn ∀psiUgamma enthalten ist, ersetze mit ¬∃□¬psi∧¬∃□¬psiU(¬psi∧¬gamma)
	        if (this.formel_string_normal_form.contains("U")) {
	            int indexU = this.formel_string_normal_form.indexOf("U");

	            // Suche den Teil links von U (psi), beginnend mit ∀
	            String psi = "";
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

	            // Wenn ∀ gefunden wurde, speichere psi
	            if (foundForAll) {
	                psi = this.formel_string_normal_form.substring(leftIndex + 1, indexU).trim();
	             // Suche den Teil rechts von U (gamma), gehe bis zum Ende der Formel
		            String gamma = this.formel_string_normal_form.substring(indexU + 1).trim();

		            // Ersetze ∀psiUgamma durch ¬∃□¬psi∧¬∃□¬psiU(¬psi∧¬gamma)
		            String ersatz = "¬∃□¬" + psi + "∧¬∃□¬" + psi + "U(¬" + psi + "∧¬" + gamma + ")";

		            // Aktualisiere die Formel mit dem ersetzten Ausdruck
		            this.formel_string_normal_form = this.formel_string_normal_form.substring(0, leftIndex) + ersatz;
		            changed = true;
		            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, leftIndex, indexU + gamma.length(), "Regel 2: Ersetze ∀psiUgamma mit ¬∃□¬psi∧¬∃□¬psiU(¬psi∧¬gamma)"));
	            }       
	        }
	        
	        //Regel 3: Wenn ∀□ enthalten ist, ersetze mit ¬∃1U¬
	        if (this.formel_string_normal_form.contains("∀□")) {
	            int index = this.formel_string_normal_form.indexOf("∀□");
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("∀□", "¬∃1U¬");
	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Regel 3: Ersetze ∀□ mit ¬∃1U¬"));
	        }
	
	        //Regel 4: Wenn ∃◇ enthalten ist, ersetze mit ∃1U
	        if (this.formel_string_normal_form.contains("∃◇")) {
	            int index = this.formel_string_normal_form.indexOf("∃◇");
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("∃◇", "∃1U");
	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Regel 4: Ersetze ∃◇ mit ∃1U"));
	        }
	
	        //Regel 5: Wenn ∀◇ enthalten ist, ersetze mit ¬∃□¬
	        if (this.formel_string_normal_form.contains("∀◇")) {
	            int index = this.formel_string_normal_form.indexOf("∀◇");
	            this.formel_string_normal_form = this.formel_string_normal_form.replace("∀◇", "¬∃□¬");
	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Regel 5: Ersetze ∀◇ mit ¬∃□¬"));
	        }
	        
	     // Regel 6: Wenn psi∨gamma enthalten ist, ersetze mit ¬(¬psi∧¬gamma) 
	        if (this.formel_string_normal_form.contains("∨")) {
	            int index = this.formel_string_normal_form.indexOf("∨");

	            // Suche den Teil links von ∨ (psi)
	            String leftPart = "";
	            int leftIndex = index - 1;

	            // Zähle die Klammern im linken Teil
	            int openCount = 0;
	            int closeCount = 0;

	            // Durchlaufe den linken Teil bis zum Anfang des Strings
	            while (leftIndex >= 0) {
	                char currentChar = this.formel_string_normal_form.charAt(leftIndex);
	                if (currentChar == '(') {
	                    openCount++;
	                } else if (currentChar == ')') {
	                    closeCount++;
	                }
	                leftIndex--;
	            }

	            // Setze den leftIndex wieder auf den ursprünglichen Wert
	            leftIndex = index - 1;

	            // Prüfe die Anzahl der Klammern und bestimme, wo ¬(¬ eingefügt werden soll
	            if (openCount > closeCount) {
	                // Es gibt mehr öffnende als schließende Klammern
	                // Suche die letzte öffnende Klammer im linken Teil
	                int lastOpenParenIndex = this.formel_string_normal_form.lastIndexOf("(", index);
	                leftPart = this.formel_string_normal_form.substring(0, lastOpenParenIndex + 1) + "¬(¬" + this.formel_string_normal_form.substring(lastOpenParenIndex + 1, index);
	            } else {
	                // Es gibt gleich viele oder mehr schließende Klammern
	                leftPart = "¬(¬" + this.formel_string_normal_form.substring(0, index);
	            }

	            // Suche den Teil rechts von ∨ (gamma)
	            String rightPart = "";
	            int rightIndex = index + 1;
	            if (this.formel_string_normal_form.charAt(rightIndex) == '(') {
	                // Finde die zugehörige schließende Klammer
	                int closeParenIndex = this.formel_string_normal_form.indexOf(")", rightIndex);
	                rightPart = this.formel_string_normal_form.substring(index + 1, closeParenIndex + 1);
	                // Ergänze ) nach gamma
	                rightPart = "¬" + rightPart + ")";
	            } else {
	                // Wenn keine Klammer, dann alles rechts von ∨ als gamma betrachten
	                rightPart = "¬" + this.formel_string_normal_form.substring(index + 1) + ")";
	            }

	            // Ersetze ∨ durch ∧ und verbinde psi und gamma
	            this.formel_string_normal_form = leftPart + "∧" + rightPart;
	            changed = true;
	            ersetzungen.add(new Umformung(original, this.formel_string_normal_form, index, index, "Regel 6: Ersetze psi∨gamma mit ¬(¬psi∧¬gamma)"));
	        }
	        
	    } while (changed);  // Wiederhole, solange Änderungen vorgenommen wurden
	 }
	 
	 //Methode die aus dem String die rekursive Definiton über erfüllende Mengen zusammenbaut
	// Idee: Zustandsformel in kleinere Formeln  aufteilen z.B an den Stellen 1 oder 0 oder E.....U
	 //--> erst alle Formel die auf 0 und 1 enden zusammenbauen, dannach die Formel mit den Verzwiegungen kombinieren
	 public void turn_string_into_recursive_ctl() {
		 
	    // Wenn formel_string_normal_form leer ist, zuerst turn_to_normal_form() aufrufen
	    if (formel_string_normal_form.equals("")) {
	        this.turn_to_normal_form();
	    }
	    
	    //letzte erfüllende Menge muss gepseichert und übergeben werden
	    erfüllende_Mengen letzte_erfüllende_Menge = null;

	    
	    // Ersetze "Formelende" mit einer leeren Zeichenfolge
	    String bereinigterFormelString = formel_string_normal_form.replace("Formelende", "");
	    
	    //entferne geschweifte Klammern
	    bereinigterFormelString = bereinigterFormelString.replace("{", "");
	    bereinigterFormelString = bereinigterFormelString.replace("}", "");
	    
        // Splitte den String in 1 und 0 und ∃ (aber nur wenn ∃ zu einem U gehört) und ( auf
	    List<String> liste_zustandformeln = new ArrayList<>(Arrays.asList(
	    	    bereinigterFormelString.split("(?<=1)|(?<=0)|(?<=∃)(?![○□])|(?=\\()")
	    	));
	    
	    
		 //Workaround wenn ein E oder ) oder ( alleine stehen würde kommt es zum nächsten String dazu
	    for (int i = 0; i < liste_zustandformeln.size() - 1; i++) {
	        String current = liste_zustandformeln.get(i).trim();
	        String next = liste_zustandformeln.get(i + 1).trim();

	        // Prüfen ob das aktuelle Element nur aus '∃', ')', 'U' oder '(' besteht
	        // oder ob das nächste Element nur ")" enthält
	        if (current.matches("[∃()U]+") || next.matches("[)]+")) {
	            // Verbinde das aktuelle Element mit dem nächsten Eintrag
	            liste_zustandformeln.set(i, current + next);
	            liste_zustandformeln.remove(i + 1);  // Entferne den nächsten Eintrag, da er jetzt zusammengeführt wurde
	            i--; // Reduziere den Index um 1, um sicherzustellen, dass die Liste korrekt weiter verarbeitet wird
	        }
	    }
	    
	    //abspeichern der Zeichen nach dem Split, sollte U oder UND , E oder Klammer sein um nachher leichter darauf zugreifen zu können
	    List<Character> ersteZeichenNachSplit = new ArrayList<>(Collections.nCopies(liste_zustandformeln.size(), null));
	   
	    //Liste zum abspeichern der einzelnen Startpunkte der gespiltteten erfüllenden Menge, gleiche Länge wie liste_zustandsformeln
	    List<erfüllende_Mengen> startpunkte_erfüllende_mengen = new ArrayList<>(Collections.nCopies(liste_zustandformeln.size(), null)); // Liste vorinitialisieren

	    
	    // Durchlaufe jede Zustandsformel in der Liste um diese in erfüllende Mengen umzuwandeln
	    for (int j = liste_zustandformeln.size() - 1; j >= 0; j--) {
	    	
	        String zustandsformel = liste_zustandformeln.get(j);
	        
	    	//Speichere das erste Zeichen ab, außer es ist eine schließende Kalmmer dann das zweite
	        if (zustandsformel.length() >= 2 && zustandsformel.charAt(0) == ')') {
	        	String zustandsformelOhneKlammern = zustandsformel.replaceFirst("^\\)+", "");
	        	ersteZeichenNachSplit.set(j,zustandsformelOhneKlammern.charAt(0));
	        }else ersteZeichenNachSplit.set(j,zustandsformel.charAt(0));
	    		
	        letzte_erfüllende_Menge = null;
	         
	        
	        // Durchlaufe die aktuelle Zustandsformel von rechts nach links, starte bei length-2 weil 0 oder 1 bei behalten wird, außerdem läuft nur bis 1 weil die erste Stelle ja den Split anzeigt
	        for (int i = zustandsformel.length() - 1; i >= 0; i--) {
	            char currentChar = zustandsformel.charAt(i);
	            
	            if (currentChar == '1') {
	                letzte_erfüllende_Menge = new one();
	                continue;
	            } 
	            
	            if (currentChar == '0') {
	                letzte_erfüllende_Menge = new null_();
	                continue;
	            } 

	            
	            
	            // 1. Wenn der aktuelle Char eine schließende Klammer `〉` ist
	            if (currentChar == '〉') {
	                // Finde die passende öffnende Klammer `〈`
	                int matchingBracketIndex = findMatchingOpenBracket(zustandsformel.substring(0, i),'〈','〉',i-1);
	                if (matchingBracketIndex != -1) {
	                    // Extrahiere den Inhalt zwischen den Klammern
	                    String zwischen_den_klammern = zustandsformel.substring(matchingBracketIndex + 1, i);
	                    
	                    // Teile den Inhalt durch Komma und erstelle ein HashSet mit Übergangsobjekten
	                    String[] übergangsZeichen = zwischen_den_klammern.split(",");
	                    HashSet<Übergang> übergänge = new HashSet<>();
	                    for (String zeichen : übergangsZeichen) {
	                        übergänge.add(new Übergang(zeichen));
	                    }

	                    // Erstelle ein neues `ein_übergang`-Objekt, das auf die vorherige Menge verweist
	                    letzte_erfüllende_Menge = new ein_übergang(letzte_erfüllende_Menge, übergänge);

	                    // Springe zum Anfang der Klammer `〈`
	                    i = matchingBracketIndex;
	                    continue;
	                } 
	            }
	             // 2. Wenn der aktuelle Char eine schließende Klammer `]` ist
	                if (currentChar == ']') {
	                    // Finde die passende öffnende Klammer `[`
	                    int matchingBracketIndex = findMatchingOpenBracket(zustandsformel.substring(0, i), '[', ']',i-1);
	                    if (matchingBracketIndex != -1) {
	                        // Extrahiere den Inhalt zwischen den Klammern
	                        String zwischen_den_klammern = zustandsformel.substring(matchingBracketIndex + 1, i);
	                        
	                        // Teile den Inhalt durch Komma und erstelle ein HashSet mit Übergangsobjekten
	                        String[] übergangsZeichen = zwischen_den_klammern.split(",");
	                        HashSet<Übergang> übergänge = new HashSet<>();
	                        for (String zeichen : übergangsZeichen) {
	                            übergänge.add(new Übergang(zeichen));
	                        }

	                        // Erstelle ein neues `alle_übergänge`-Objekt, das auf die vorherige Menge verweist
	                        letzte_erfüllende_Menge = new alle_übergänge(übergänge,letzte_erfüllende_Menge);

	                        // Springe zum Anfang der Klammer `[`
	                        i = matchingBracketIndex;
	                        continue;
	                    }
	                }
	             // 3. Wenn der aktuelle Char eine Negation `¬` ist
	                if (currentChar == '¬') {
	                    // Erstelle ein neues `Negation`-Objekt, das auf die vorherige Menge verweist
	                    letzte_erfüllende_Menge = new Negation(letzte_erfüllende_Menge);
	                    
	                    // Springe weiter vor die Negation
	                    continue;
	                }
	             // 4. Wenn der aktuelle Char ein `○` ist
	                if (currentChar == '○') {
	                    // Prüfe, ob der nächste Char `∃` ist
	                    if (i > 0 && zustandsformel.charAt(i - 1) == '∃') {
	                        // Erstelle ein neues `in_einem_nächsten_zustand_gilt`-Objekt, das auf die vorherige Menge verweist
	                        letzte_erfüllende_Menge = new in_einem_nächsten_zustand_gilt(letzte_erfüllende_Menge);

	                        // Springe vor das Zeichen `∃`
	                        i--;
	                        continue;
	                    }
	                }
	             // 5. Wenn der aktuelle Char ein `□` ist
	                if (currentChar == '□') {
	                    // Prüfe, ob der nächste Char `□` ist
	                    if (i > 0 && zustandsformel.charAt(i - 1) == '∃') {
	                        // Erstelle ein neues `ein_pfad_auf_dem_immer_gilt`-Objekt, das auf die vorherige Menge verweist
	                        letzte_erfüllende_Menge = new ein_pfad_auf_dem_immer_gilt(letzte_erfüllende_Menge);

	                        // Springe vor das Zeichen ∃
	                        i--;
	                        continue;
	                    }
	                }
	                
	            }
	        	//füge den Startpunkt zur Liste hinzu
	        	startpunkte_erfüllende_mengen.set(j,letzte_erfüllende_Menge);
	        }
	    //Zusammenbauen der bis jetzt extrhaieren erfüllenden Mengen
	    //1. Geklammerte Ausdrücke müssen zuerst verbunden werden
	    //Idee: Klammer Tiefe bestimmen, Therme nach Kalmmertiefe sortieren, Therm mit der größten Tiefe mit dem näcshten Verknüpfen und neu berechenen
	    boolean haelt = true;
        
        while (haelt && liste_zustandformeln.size()>1) {
            haelt = false;
            
            // 1. Tiefe der Klammern bestimmen
            List<Integer> klammernTiefe = new ArrayList<>();
            for (int i = 0; i < liste_zustandformeln.size(); i++) {
                String term = liste_zustandformeln.get(i);
                int tiefe = berechneKlammerTiefe(term);
                if(i>0) {
                klammernTiefe.add(tiefe+klammernTiefe.get(i-1));
                }else{klammernTiefe.add(tiefe);}
            }
            
            // 2. Paare aus (Index, Term, Tiefe) erstellen
            List<Triple<Integer, String, Integer>> termTiefePaare = new ArrayList<>();
            for (int i = 0; i < liste_zustandformeln.size(); i++) {
                termTiefePaare.add(new Triple<>(i, liste_zustandformeln.get(i), klammernTiefe.get(i)));
            }
	            
            // 3. Terme nach Tiefe sortieren
            termTiefePaare.sort(Comparator.comparingInt(triple -> (Integer) ((Triple<Integer, String, Integer>) triple).getThird()).reversed());
	        
            for (Triple<Integer, String, Integer> triple : termTiefePaare) {
                System.out.println("Key: " + triple.getKey() + ", Value: " + triple.getValue() + ", Depth: " + triple.getThird());
            }

            
	       //den Therm mit der größten Tiefe finden und Verknüpfen
            int i = termTiefePaare.get(0).getKey();
            
	        // Verknüpfungszeichen an der aktuellen Position holen
	        char verknüpfungsChar = ersteZeichenNachSplit.get(i+1);

	        // Nimm die beiden Mengen an den Positionen `i` und `i+1`
	        erfüllende_Mengen erste_menge = startpunkte_erfüllende_mengen.get(i);
	        erfüllende_Mengen zweite_menge = startpunkte_erfüllende_mengen.get(i+1);
	        

	        // Kombiniere die beiden Mengen basierend auf dem Verknüpfungscharakter
	        erfüllende_Mengen kombinierte_menge;
	        
	        if (verknüpfungsChar == 'U') {
	            // Erstelle ein `psi_Until_gamma`-Objekt
	            kombinierte_menge = new psi_Until_gamma(erste_menge, zweite_menge);
	        } else if (verknüpfungsChar == '∧') {
	            // Erstelle ein `And`-Objekt
	            kombinierte_menge = new And(erste_menge, zweite_menge);
	        }else {
	            // Wenn kein passendes Verknüpfungszeichen vorhanden ist, überspringe die aktuelle Menge
	            continue;
	        }
            //Kombiniere die Stringtherme
	        
	        String aktueller_term = liste_zustandformeln.get(i) + liste_zustandformeln.get(i+1);
	        
            //füge die kombiniertre Therme hinzu und entferne die alten
	        startpunkte_erfüllende_mengen.set(i, kombinierte_menge);
            startpunkte_erfüllende_mengen.remove(i + 1);
            liste_zustandformeln.remove(i + 1);
            liste_zustandformeln.set(i, aktueller_term);
            //entferne das Verknüpfungszeichen
            ersteZeichenNachSplit.remove(i+1);
            
            haelt = true;
	            
        }
	    
	    
	    //2. Verknüpfe die Mengen mit Verknüpfungszeichen UND oder UNTIL mit einander von links nach rechts, 
	    for(int i = 1; i< startpunkte_erfüllende_mengen.size();i++) {
	        // Verknüpfungszeichen an der aktuellen Position holen
	        char verknüpfungsChar = ersteZeichenNachSplit.get(i);

	        // Nimm die beiden Mengen an den Positionen `i` und `i-1`
	        erfüllende_Mengen erste_menge = startpunkte_erfüllende_mengen.get(i-1);
	        erfüllende_Mengen zweite_menge = startpunkte_erfüllende_mengen.get(i);

	        // Kombiniere die beiden Mengen basierend auf dem Verknüpfungscharakter
	        erfüllende_Mengen kombinierte_menge;
	        
	        if (verknüpfungsChar == 'U') {
	            // Erstelle ein `psi_Until_gamma`-Objekt
	            kombinierte_menge = new psi_Until_gamma(erste_menge, zweite_menge);
	        } else if (verknüpfungsChar == '∧') {
	            // Erstelle ein `And`-Objekt
	            kombinierte_menge = new And(erste_menge, zweite_menge);
	        }else {
	            // Wenn kein passendes Verknüpfungszeichen vorhanden ist, überspringe die aktuelle Menge
	            continue;
	        }

	        // 1.4 Füge die kombinierte Menge an der Position `index` ein
	        startpunkte_erfüllende_mengen.set(i-1, kombinierte_menge);

	        // 1.5 Entferne die zweite Menge und das Verknüpfungszeichen
	        startpunkte_erfüllende_mengen.remove(i);
	        ersteZeichenNachSplit.remove(i);
	        // Beachte: Da eine Verknüpfung durchgeführt wurde, bleibt `index` gleich, da die Liste kleiner wird
	        i--;
	    	}
	    
	    	//3.Baue die verbleidenden unverknüpften erfüllenden Mengen, also Mengen die auf Null zeigen von hinten nach vorne zusammen
	    while(startpunkte_erfüllende_mengen.size() > 1) {//Solange es mehr erfüllende Mengen gibt als 1 
	    	
	        int letzteIndex = startpunkte_erfüllende_mengen.size() - 1;
	        erfüllende_Mengen aktuelle_menge_aus_liste = startpunkte_erfüllende_mengen.get(letzteIndex);

	        // Finde die tiefste nicht verknüpfte Menge (die auf null zeigt)
	        erfüllende_Mengen tiefste_menge = findeTiefsteMengeMitNull(startpunkte_erfüllende_mengen.get(letzteIndex - 1));
	        
	        //Unterscheidung ob ein oder zwei erfüllenden Mengen in der Definition vorkommen
	        if (tiefste_menge instanceof Ast) {
	            Ast ast_menge = (Ast) tiefste_menge;
	            ast_menge.setInnere_Menge(aktuelle_menge_aus_liste); // Setze die innere Menge
	            startpunkte_erfüllende_mengen.remove(aktuelle_menge_aus_liste);
	        } else if (tiefste_menge instanceof Verzweigung) {//Setzte die Rechte Menge
	            Verzweigung verzweigung_menge = (Verzweigung) tiefste_menge;
	            verzweigung_menge.setRechte_Seite(aktuelle_menge_aus_liste);
	            startpunkte_erfüllende_mengen.remove(aktuelle_menge_aus_liste); 
	        } else {
	            throw new IllegalStateException("Verknüpfungsproblem bei der Schachtelung.");
	        }
	    }
	        
	        this.Start_der_rekursiven_Definition = startpunkte_erfüllende_mengen.get(0);
	    }
	 
	// Hilfsmethode, um die tiefste Menge zu finden, die noch nicht vollständig verknüpft ist
	 private erfüllende_Mengen findeTiefsteMengeMitNull(erfüllende_Mengen menge) {
	     
		 if (menge instanceof Verzweigung) {
	         Verzweigung verzweigung_menge = (Verzweigung) menge;
	         
	         // Überprüfe, ob die rechte Seite noch nicht gesetzt ist
	         if (verzweigung_menge.getRechte_Seite() == null) {
	             return verzweigung_menge;
	         } else {
	             // Rekursiv weitersuchen in der rechten Seite
	             return findeTiefsteMengeMitNull(verzweigung_menge.getRechte_Seite());
	         }
	     } else if (menge instanceof Ast) {
	         Ast ast_menge = (Ast) menge;

	         // Überprüfe, ob die innere Menge noch nicht gesetzt ist
	         if (ast_menge.getInnere_Menge() == null) {
	             return ast_menge;
	         } else {
	             // Rekursiv weitersuchen in der inneren Menge
	             return findeTiefsteMengeMitNull(ast_menge.getInnere_Menge());
	         }
	     }
	     
	     // Wenn es kein Verzweigung oder Ast ist, einfach zurückgeben
	     return menge;
	 }	 
	 
	 //Methode zur Ausgabe der Lösungsmenge der CTL-Formel
	public  Set<Zustand> get_Lösungsmenge(Transitionssystem ts){
		if (this.Start_der_rekursiven_Definition == null){
			this.turn_string_into_recursive_ctl();
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
	    final String UND_UND_ODER_NUR_NACH_ZUSTANDSFORMEL = "UND oder ODER können nur nach einer Zustandsformel eingelesen werden nicht nach einer Pfadformel";
	    final String FORMELENDE_NUR_WENN_ZUSTANDSFORMEL = "Die Formeleingabe kann nur mit einer korrekten Zustandsformel beendet werden";
	    final String FORMELENDE_NUR_WENN_KLAMMERN_OFFEN = "Die Formeleingabe kann nur mit beendet werden, wenn glecih viele öffnende und schließende Klammern vorhanden sind"+ "\n\n";
	    final String UNTIL_NUR_WENN_ZUSTANDSFORMEL = "U kann nur nach einer korrekten Zustandsformel eingesetzt werden";
	    String UNTIL_NUR_MIT_QUANTOR = "U kann nur eingelesen, wenn vor der Zustandsformel ein Quantor eingelesen wurde, da man sonst eine Pfadformel und keine Zustandsformel erhalten würde\n\n";
	    
	    // Ursprungszustand herstellen
	    for (String symbol : this.all_symbols) {
	        this.gruendeFuerNichteinlesbareSymbole.put(symbol, "");
	    }

	    // 1. Man kann nicht mit schließenden Klammern beginnen
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
	    
	    // Kein UND bzw. ODER nach nach Pfadformel
	        if (!(this.ist_Zustandsformel(this.formel_string, 0))) {
	            this.gruendeFuerNichteinlesbareSymbole.put("∧", UND_UND_ODER_NUR_NACH_ZUSTANDSFORMEL + " " +last_checked_Formular+ "\n\n");
	            this.gruendeFuerNichteinlesbareSymbole.put("∨", UND_UND_ODER_NUR_NACH_ZUSTANDSFORMEL+ " "+ last_checked_Formular + "\n\n");
	        }

	    // Transitionen müssen nach einem "〈" oder "[" stehen
	    if (this.counter_eckige_klammern == 0 && this.counter_spitze_klammern == 0) {
	        this.gruendeFuerNichteinlesbareSymbole.put("Transition eingeben", TRANSITION_NUR_ZWISCHEN_KLAMMERN);
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
          this.gruendeFuerNichteinlesbareSymbole.put("U", "U kann nur eingelesen, wenn vor der Zustandsformel ein Quantor eingelesen wurde, da man sonst eine Pfadformel und keine Zustandsformel erhalten würde\n\n");
       } else {
          int indexEoA = Math.max(this.formel_string.indexOf("∃"), this.formel_string.indexOf("∀"));
          System.out.println("EoA at: " + indexEoA);
          if (indexEoA != -1) {
             String zustandsformelTeil = this.formel_string.substring(indexEoA + 1).trim();
             if (!this.ist_Zustandsformel(zustandsformelTeil, 0)) {
                this.gruendeFuerNichteinlesbareSymbole.put("U", "U kann nur nach einer korrekten Zustandsformel eingelesen werden\n\n " + this.last_checked_Formular + "\n\n");
             }
          } else {
             this.gruendeFuerNichteinlesbareSymbole.put("U", "U kann nur eingelesen, wenn vor der Zustandsformel ein Quantor eingelesen wurde, da man sonst eine Pfadformel und keine Zustandsformel erhalten würde\n\n");
          }
       }
      
      //Formelende nur wenn gültige CTL-Formel
       if (!this.ist_Zustandsformel(this.formel_string, 0)) {
          this.gruendeFuerNichteinlesbareSymbole.put("Formelende", "Die Formeleingabe kann nur mit einer korrekten Zustandsformel beendet werden " + this.fehlerbeschreibung + "\n\n");
          this.fehlerbeschreibung = "";
       }
       //Formelende nur wenn alle Klammern geschlossen sind
       if (this.counter_normale_klammern != 0) {
          this.gruendeFuerNichteinlesbareSymbole.put("Formelende", "Die Formeleingabe kann nur mit beendet werden, wenn glecih viele öffnende und schließende Klammern vorhanden sind\n\n");
       }

       return this.gruendeFuerNichteinlesbareSymbole;
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
		    	 if(eingelesenesSymbol == "Formelende") {
		    		 this.turn_to_normal_form();
		    	 }
			}
	 }
	 	
	 //Methode die prüft ob übergebner String eine Zustandsformel ist
	 public boolean ist_Zustandsformel(String zu_prüfende_Formel, int tiefe) {
		 
		 //Schaltet Debugging Ausgaben ein oder aus
		 boolean debug = false;
		    // Ausgaben zur Nachverfolgung des aktuellen Aufrufs
		    if (debug) printAufruf(tiefe, "Prüfe Formel: " + zu_prüfende_Formel);

		    // Entferne führende und nachfolgende Leerzeichen
		    zu_prüfende_Formel = zu_prüfende_Formel.trim();

		    // 0. Basisfall: Wenn die Formel "1" oder "0" ist, gib true zurück
		    if (zu_prüfende_Formel.equals("1") || zu_prüfende_Formel.equals("0")) {
		        if (debug) printAufruf(tiefe, "Erfolgreich: Basisfall erreicht mit " + zu_prüfende_Formel);
		        return true;
		    }

		    // 1. Wenn "∃" oder "∀" gelesen wird, muss das folgende eine Pfadformel sein
		    if (zu_prüfende_Formel.startsWith("∃") || zu_prüfende_Formel.startsWith("∀")) {
		        
		    	if (debug) printAufruf(tiefe, "Gefunden: Quantor " + zu_prüfende_Formel.charAt(0));
		        String rest = zu_prüfende_Formel.substring(1).trim();
		        //NAch diesen Symbolen kommt wieder eine Zustandsformel die rekrusiv geprüft werden kann
		        if (rest.startsWith("◇") || rest.startsWith("○") || rest.startsWith("□")) {
		            return ist_Zustandsformel(rest.substring(1).trim(), tiefe + 1);
		        }
		        //Oder man findet ein U und prüft den linekn und rechten PArt ob es eine Zustandsformel ist
		        int indexOfU = rest.indexOf("U");
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
		        } else return true;
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

		    // 5. Logische Operatoren prüfen: "∧" und "∨", linke und rechte Seite prüfen dabei auf Klammern achten
		    int andIndex = zu_prüfende_Formel.indexOf("∧");
		    int orIndex = zu_prüfende_Formel.indexOf("∨");

		    int offset = 0;
		    if (!zu_prüfende_Formel.isEmpty() && zu_prüfende_Formel.charAt(0) == '(') {
		        offset = 1;
		    }

		    if (andIndex != -1) {
		        String leftPart = zu_prüfende_Formel.substring(0 + offset, andIndex).trim();
		        String rightPart = zu_prüfende_Formel.substring(andIndex + 1).trim();
		        return ist_Zustandsformel(leftPart, tiefe + 1) && ist_Zustandsformel(rightPart, tiefe + 1);
		    }

		    if (orIndex != -1) {
		        String leftPart = zu_prüfende_Formel.substring(0, orIndex).trim();
		        String rightPart = zu_prüfende_Formel.substring(orIndex + 1).trim();
		        return ist_Zustandsformel(leftPart, tiefe + 1) && ist_Zustandsformel(rightPart, tiefe + 1);
		    }
		    
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
		    System.out.println(prefix + message);
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
	public void entferneLetztenChar() {
	    // Wenn der formel_string leer ist, gibt es nichts zu entfernen
	    if (this.formel_string.isEmpty()) {
	        return;
	    }
	    
	    String formelende = "Formelende";
	    if (this.formel_string.endsWith(formelende)) {
	        // Entferne den gesamten "Formelende"-String
	        this.formel_string = this.formel_string.substring(0, this.formel_string.length() - formelende.length());
	    }else {
	    
	
		    // Das letzte Zeichen im formel_string ermitteln
		    String letztesZeichen = this.formel_string.substring(this.formel_string.length() - 1);
	
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
		    if (letztesZeichen.equals("〉")) { // <Fall {a,b}> zu <{a,b,
		        // Entferne die letzten zwei Zeichen und füge "," an
		        this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 2);
		        this.formel_string = this.formel_string + ",";
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
		    } else {this.formel_string = this.formel_string.substring(0, this.formel_string.length() - 1);}
	    }
	}
	
	public void print_erfüllende_zustände(Transitionssystem ts) {
		turn_string_into_recursive_ctl();
		Set<Zustand> lösungsmenge = this.Start_der_rekursiven_Definition.berechne(ts);
		System.out.println("###############Lösungsmenge##################");
		for(Zustand lösung:lösungsmenge) {
			System.out.println(lösung.getName());
		}
	}

	public erfüllende_Mengen getStart_der_rekursiven_Definition() {
		if (this.Start_der_rekursiven_Definition == null){
			this.turn_string_into_recursive_ctl();
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
}


	


