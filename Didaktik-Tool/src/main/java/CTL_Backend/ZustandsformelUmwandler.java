package CTL_Backend;

import java.util.HashSet;

public class ZustandsformelUmwandler {
	
	//Zum Verwalten von AND, da diese keinen "einleitenden Operator haben
	private static ErfüllendeMenge aktuellesObjekt = null;
	private static String bereinigterFormelString;

    // Startmethode: Ruft die rekursive Methode auf
    public static ErfüllendeMenge parseZustandsformel(String zustandsformel) {
    	
    	 if (zustandsformel == null || zustandsformel.isEmpty()) {
             throw new IllegalArgumentException("Die Zustandsformel darf nicht null oder leer sein.");
         }
    	
	    // Ersetze "Formelende" mit einer leeren Zeichenfolge
	    String bereinigterFormelString = zustandsformel.replace("Formelende", "");
	    
	    //entferne geschweifte Klammern
	    bereinigterFormelString = bereinigterFormelString.replace("{", "");
	    bereinigterFormelString = bereinigterFormelString.replace("}", "");
	    bereinigterFormelString = bereinigterFormelString.replace(" ", "");
	    
	    ZustandsformelUmwandler.bereinigterFormelString = bereinigterFormelString;
       
	    // Parse die Formel zu einem verschachtelten Objekt
	    ErfüllendeMenge wurzel = parse(bereinigterFormelString);
	    	    
	    return wurzel;
        
        
    }

    private static ErfüllendeMenge parse(String zustandsformel) {
    	
    	
        // Basisfall: Wenn der String leer ist, gibt es nichts mehr zu parsen
        if (zustandsformel == null || zustandsformel.isEmpty()) {
            return null;
        }

        // Überprüfen, ob ein ∧ enthalten ist
        int indexOfAnd = zustandsformel.indexOf('∧');
        while (indexOfAnd != -1) {
            String leftPart = zustandsformel.substring(0, indexOfAnd);
            String rightPart = zustandsformel.substring(indexOfAnd + 1);

            // Überprüfen, ob die Klammeranzahl übereinstimmt
            if (checkBalancedParentheses(leftPart) && checkBalancedParentheses(rightPart)) {
                ErfüllendeMenge left = parse(leftPart);
                ErfüllendeMenge right = parse(rightPart);

                return new And(left, right);
            }

            // Nächstes Vorkommen von ∧ suchen
            indexOfAnd = zustandsformel.indexOf('∧', indexOfAnd + 1);
        }

        char currentChar = zustandsformel.charAt(0);

        // 1. Wenn der aktuelle Char eine `1` ist
        if (currentChar == '1') {
            return new one();
        }

        // 2. Wenn der aktuelle Char eine `0` ist
        if (currentChar == '0') {
            return new null_();
        }

        // 3. Wenn der aktuelle Char eine öffnende Klammer `〈` ist
        if (currentChar == '〈') {
            int matchingBracketIndex = findMatchingCloseBracket(zustandsformel, '〈', '〉', 1);
            if (matchingBracketIndex != -1) {
                String zwischenDenKlammern = zustandsformel.substring(1, matchingBracketIndex);
                String[] übergangsZeichen = zwischenDenKlammern.split(",");
                HashSet<Übergang> übergänge = new HashSet<>();
                for (String zeichen : übergangsZeichen) {
                    übergänge.add(new Übergang(zeichen));
                }

                // Rekursiver Aufruf für den restlichen String nach der Klammer
                ErfüllendeMenge nächsteMenge = parse(zustandsformel.substring(matchingBracketIndex + 1));
                return new ein_übergang(nächsteMenge, übergänge);
            }
        }

        // 4. Wenn der aktuelle Char eine öffnende Klammer `[` ist
        if (currentChar == '[') {
            int matchingBracketIndex = findMatchingCloseBracket(zustandsformel, '[', ']', 1);
            if (matchingBracketIndex != -1) {
                String zwischenDenKlammern = zustandsformel.substring(1, matchingBracketIndex);
                String[] übergangsZeichen = zwischenDenKlammern.split(",");
                HashSet<Übergang> übergänge = new HashSet<>();
                for (String zeichen : übergangsZeichen) {
                    übergänge.add(new Übergang(zeichen));
                }

                // Rekursiver Aufruf für den restlichen String nach der Klammer
                ErfüllendeMenge nächsteMenge = parse(zustandsformel.substring(matchingBracketIndex + 1));
                return new alle_übergänge(übergänge, nächsteMenge);
            }
        }

        // 5. Wenn der aktuelle Char eine Negation `¬` ist
        if (currentChar == '¬') {
            // Rekursiver Aufruf für den restlichen String nach der Negation
            ErfüllendeMenge nächsteMenge = parse(zustandsformel.substring(1));
            return new Negation(nächsteMenge);
        }

        // 6. Wenn der aktuelle Char ein `∃` ist
        if (currentChar == '∃') {
        	
        	//6.1 ∃○
            if (zustandsformel.length() > 1 && zustandsformel.charAt(1) == '○') {
                // Rekursiver Aufruf für den restlichen String nach `○`
                ErfüllendeMenge nächsteMenge = parse(zustandsformel.substring(2));
                return new in_einem_nächsten_zustand_gilt(nächsteMenge);
            }
            //6.2 ∃□
            if (zustandsformel.length() > 1 && zustandsformel.charAt(1) == '□') {
                // Rekursiver Aufruf für den restlichen String nach `□`
                ErfüllendeMenge nächsteMenge = parse(zustandsformel.substring(2));
                return new ein_pfad_auf_dem_immer_gilt(nächsteMenge);
            }
          //6.3 ∃psiUgamma
            else {
                int matchingUIndex = findMatchingU(zustandsformel, 1);
                if (matchingUIndex != -1) {
                    // Inhalt zwischen `E` und `U` extrahieren
                    String zwischenEundU = zustandsformel.substring(1, matchingUIndex);

                    // Rekursiver Aufruf für die erste Menge (zwischen `E` und `U`)
                    ErfüllendeMenge ersteMenge = parse(zwischenEundU);

                    // Rekursiver Aufruf für die zweite Menge (alles nach dem `U`)
                    ErfüllendeMenge zweiteMenge = parse(zustandsformel.substring(matchingUIndex + 1));

                    // Rückgabe des neuen psi_Until_gamma Objekts
                    return new psi_Until_gamma(ersteMenge, zweiteMenge);
                }
            }
        }
        
     // 7. Wenn der aktuelle Char eine öffnende Klammer `(` ist
        if (currentChar == '(') {
            int matchingBracketIndex = findMatchingCloseBracket(zustandsformel, '(', ')', 1);
            if (matchingBracketIndex != -1) {
                // Inhalt zwischen den Klammern extrahieren
                String zwischenDenKlammern = zustandsformel.substring(1, matchingBracketIndex);
                // Klammerterm + UND in bereinigterFormelString prüfen
                String klammerTermMitKlammern = "(" + zwischenDenKlammern + ")";
                
                
                	return parse(zwischenDenKlammern);    
            }
        }
        
     // 8. Wenn der aktuelle Char eine öffnende Klammer `)` ist; einfahc zum näcshten gehen
        if (currentChar == ')') {
        	return parse(zustandsformel.substring(1));   
        }
        
        

        // Wenn kein spezieller Fall zutrifft, gehe zum nächsten Zeichen
        return parse(zustandsformel.substring(1));
    }
    
    private static int findMatchingCloseBracket(String zustandsformel, char openBracket, char closeBracket, int startIndex) {
        int balance = 1;

        for (int i = startIndex; i < zustandsformel.length(); i++) {
            char currentChar = zustandsformel.charAt(i);

            if (currentChar == openBracket) {
                balance++;
            } else if (currentChar == closeBracket) {
                balance--;
                if (balance == 0) {
                    return i;
                }
            }
        }

        return -1; // Keine passende schließende Klammer gefunden
    }
    
    static int findMatchingU(String zustandsformel, int startIndex) {
        int balance = 1;

        for (int i = startIndex; i < zustandsformel.length(); i++) {
            char currentChar = zustandsformel.charAt(i);

            if (currentChar == '∃') {
                // Prüfen, ob der nächste Charakter ○ oder □ ist
                if (i + 1 < zustandsformel.length()) {
                    char nextChar = zustandsformel.charAt(i + 1);
                    if (nextChar != '○' && nextChar != '□' && nextChar != '◇') {
                        balance++;
                    }
                } else {
                    balance++;
                }
            } else if (currentChar == 'U') {
                balance--;
                if (balance == 0) {
                    return i; // Passendes U gefunden
                }
            }
        }

        return -1; // Kein passendes U gefunden
    }

    

	protected static boolean checkBalancedParentheses(String input) {
	    int count = 0;
	    for (char c : input.toCharArray()) {
	        if (c == '(') count++;
	        if (c == ')') count--;
	        if (count < 0) return false; // Mehr schließende als öffnende Klammern
	    }
	    return count == 0; // Genauso viele öffnende wie schließende Klammern
	}
}
