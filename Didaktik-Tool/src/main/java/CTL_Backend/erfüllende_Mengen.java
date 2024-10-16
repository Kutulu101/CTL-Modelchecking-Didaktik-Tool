package CTL_Backend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class erfüllende_Mengen {
    
    protected String symbol;
    
    public abstract Set<Zustand> berechne(Transitionssystem ts);

    public String get_symbol() {
        return symbol;
    }
    
	protected String print_Lösungsmenge(Set<Zustand> menge) {
	    if (menge.isEmpty()) {
	        return "∅"; // Zeichen für die leere Menge
	    } else {
	        // Generiere die Einträge des HashSets als kommaseparierte Werte in {}
	        return "{" + menge.stream()
	            .map(Zustand::getName)  // Rufe getName() für jeden Zustand auf
	            .collect(Collectors.joining(", ")) + "}";
	    }
	}
}

//Interface für Funktion der Detail-Ausgabe bei ∃ϕUψ und ∃□ϕ

interface detail_lösung{
	public String get_schritt_weise_lösung();
}

//##################Wird nochmal aufgeteilt, entweder eine Lösungsmenge deffiniert sich über keine,1 oder zwei vorhergehende Lösungsmengen

// Verzweigung: Für Klassen mit zwei vorherigen Lösungsmengen linker und rechter Seite

abstract class Verzweigung extends erfüllende_Mengen {
    protected erfüllende_Mengen linke_Seite;
    protected erfüllende_Mengen rechte_Seite;

    public erfüllende_Mengen getLinke_Seite() {
        return linke_Seite;
    }

    public erfüllende_Mengen getRechte_Seite() {
        return rechte_Seite;
    }

	public void setLinke_Seite(erfüllende_Mengen linke_Seite) {
		this.linke_Seite = linke_Seite;
	}

	public void setRechte_Seite(erfüllende_Mengen rechte_Seite) {
		this.rechte_Seite = rechte_Seite;
	}
    
    
}

//damit ist nicht das klassiche Until gemeint sondern ein Ausdruck nahc der Art psi-U-gamma
class psi_Until_gamma extends Verzweigung implements detail_lösung{
	
	String detail_lösung;
	
	public String get_schritt_weise_lösung(){
		//System.out.println(this.detail_lösung);
		return this.detail_lösung;
	}
	
	
	public psi_Until_gamma(erfüllende_Mengen linke_seite,erfüllende_Mengen rechte_seite) {
		this.linke_Seite = linke_seite;
		this.rechte_Seite = rechte_seite;
		this.symbol = "∃{linke_seite}U{rechte_seite}";
	}
	
  public Set<Zustand> berechne(Transitionssystem ts) {
	  
	  int n =0;
      // 1. Berechne die Lösungen der linken und rechten Seite
      Set<Zustand> lösungsmenge_links = linke_Seite.berechne(ts);
      Set<Zustand> lösungsmenge_rechts = rechte_Seite.berechne(ts);

      // 2. Fixpunkt-Iteration, beginnend bei der leeren Menge
      HashSet<Zustand> returnSet = new HashSet<>();
      HashSet<Zustand> last_ReturnSet;
      
      //Startpunkt speichern für Detailausgabe
	    this.detail_lösung = "allgemeine Lösung: " + "Sat(ψ)" +"∪" + "{z ∈ Sat(ϕ)|∃a ∈ A,z' ∈ Z': z ↦ z'} \n";

      do {
    	  
    	//String anhängen
    	  n = n +1;
    	  this.detail_lösung = this.detail_lösung+ "● "+ n+ ". Itterationsschritt: "+"(" + this.print_Lösungsmenge(lösungsmenge_rechts) + ") ∪" + "{z ∈ " + this.print_Lösungsmenge(returnSet) +"|∃a ∈ A,z' ∈ "+ this.print_Lösungsmenge(returnSet)+": z ↦ z'}";
          // Letzte Iteration speichern
          last_ReturnSet = new HashSet<>(returnSet);

          // Erstelle ein neues Set für die aktuelle Iteration
          HashSet<Zustand> neuesReturnSet = new HashSet<>(lösungsmenge_rechts);  // Beginne mit der Lösung der rechten Seite

          // Iteriere über alle Zustände des Transitionssystems
          for (Zustand zustand : ts.getZustände()) {

              // Prüfe, ob der Zustand in der Lösungsmenge der linken Seite ist
              if (lösungsmenge_links.contains(zustand)) {

                  // Hole alle abgehenden Relationen für diesen Zustand
                  List<ZweiTuple<Übergang, Zustand>> relation_abgehend = zustand.getRelationen_abgehend();

                  // Prüfe, ob einer der Zielzustände im letzten ReturnSet liegt
                  for (ZweiTuple<Übergang, Zustand> relation : relation_abgehend) {
                      Zustand zielzustand = relation.getSecond();
                      
                      // Falls der Zielzustand im letzten ReturnSet liegt, füge den Zustand zur neuen Menge hinzu
                      if (last_ReturnSet.contains(zielzustand)) {
                          neuesReturnSet.add(zustand);
                          break;  // Keine weiteren Relationen für diesen Zustand prüfen
                      }
                  }
              }
          }
          
          //an String anfügen
	      this.detail_lösung = this.detail_lösung + "= " + this.print_Lösungsmenge(neuesReturnSet)+"\n";
	        
          // Setze das aktuelle ReturnSet auf das neue Ergebnis
          returnSet = neuesReturnSet;
          
      } while (!returnSet.equals(last_ReturnSet));  // Wiederhole, bis sich das ReturnSet nicht mehr ändert

      // Gib das finale ReturnSet zurück
	  this.detail_lösung = this.detail_lösung + "→ Fixpunkt gefunden";
      return returnSet;
  }
}

//Alle Zustände aus dem TS erfüllen die Formel, die sowohl die linke als auch die rechte Seite erfüllen
class And extends Verzweigung {
	
    public And(erfüllende_Mengen linke_Seite, erfüllende_Mengen rechte_Seite) {
        this.linke_Seite = linke_Seite;
        this.rechte_Seite = rechte_Seite;
        this.symbol = "{linke_Seite}∧{rechte_Seite}";
    }

    public Set<Zustand> berechne(Transitionssystem ts) {
        // Berechne die Mengen für beide Seiten
        Set<Zustand> setLinks = linke_Seite.berechne(ts);
        Set<Zustand> setRechts = rechte_Seite.berechne(ts);
        
        // Falls eine der Mengen null ist, initialisiere sie als leeres Set
        if (setLinks == null) {
            setLinks = new HashSet<>();
        }
        if (setRechts == null) {
            setRechts = new HashSet<>();
        }

        // Schnittmenge der beiden Mengen bilden
        Set<Zustand> IntersectionSet = new HashSet<>(setLinks);
        IntersectionSet.retainAll(setRechts);

        return IntersectionSet;
    }
}

//######################################Ast: Für Klassen, die eine innere Menge besitzen
abstract class Ast extends erfüllende_Mengen {
 protected erfüllende_Mengen innere_Menge;

	 public erfüllende_Mengen getInnere_Menge() {
	     return innere_Menge;
	 }
	 
	
	public void setInnere_Menge(erfüllende_Mengen innere_Menge) {
		this.innere_Menge = innere_Menge;
	}
}

//#######Interface für die Übergänge
interface HatÜbergang {
	    Set<Übergang> getÜbergänge();
	}


//<A'>psi, Noch mit der Unsicherheit ob die innere Menge erweitert wird oder neu aufgebaut wird
class  ein_übergang extends Ast implements HatÜbergang{
	
	Set<Übergang> übergänge;
	
	public ein_übergang(erfüllende_Mengen inner_Menge,HashSet<Übergang> übergange){
		this.innere_Menge= inner_Menge;
		this.übergänge =übergange;
		this.symbol = "〈{übergänge}〉{innere_Menge}";
		}
	
	public  Set<Zustand> berechne(Transitionssystem ts) {
		
	    Set<Zustand> returnSet;
	    //Unsicher ist Return Set hier ein neues Set oder wird die innere erfüllende Menge erweitert
	    returnSet = new HashSet<Zustand>();
	    //returnSet = this.innere_Menge;
	    
	    //berechnne der inneren_Lösungsmenge
	    Set<Zustand> innere_lösungs_menge = this.innere_Menge.berechne(ts);

	    
	    // Durchlaufe alle Zustände im Transitionssystem
	    for (Zustand zustand : ts.getZustände()) {

	        // Erhalte die abgehenden Relationen für den aktuellen Zustand
	        List<ZweiTuple<Übergang,Zustand>> relation_abgehend = zustand.getRelationen_abgehend();

	        // Durchlaufe alle abgehenden Relationen des Zustands
	        for (ZweiTuple<Übergang,Zustand> relation : relation_abgehend) {

	            // Überprüfen, ob der Zielzustand in der inneren Menge ist
	            // und der Übergang in den erlaubten Übergängen liegt
	        	
	        	//Problem übergangsvergleich wird auf fals gesetzt weil Objekte vergleichen werden, funktioniert weil Equals und hashCode in Übergnagsklasse überschrieben wurden
	            if (innere_lösungs_menge.contains(relation.getSecond()) &&
	                this.übergänge.contains(relation.getFirst())) {
	                
	                // Füge den aktuellen Zustand dem Rückgabeset hinzu
	                returnSet.add(zustand);
	            }
	        }
	    }

	    // Gebe das berechnete Set zurück
	    return returnSet;
	}

	

public Set<Übergang> getÜbergänge() {
	return übergänge;
	}
}


//[A']psi,Noch mit der Unsicherheit ob die innere Menge erweitert wird oder neu aufgebaut wird
class alle_übergänge  extends Ast implements HatÜbergang{
	
	Set<Übergang> übergänge = new HashSet<>();
	public alle_übergänge(HashSet<Übergang> übergänge, erfüllende_Mengen innere_Menge){
		this.übergänge = übergänge;
		this.innere_Menge = innere_Menge;
		this.symbol = "[{übergänge}]{innere_Menge}";
	}
	
    public Set<Zustand> berechne(Transitionssystem ts) {

        // Neues Set initialisieren (oder bestehende innere Menge nutzen)
        Set<Zustand> returnSet = new HashSet<Zustand>();  // Neu initialisieren
        // returnSet = this.innere_Menge;  // Alternative: Existierende Menge erweitern
        
        //Berehcnen der innerenLösungsmenge
        Set<Zustand> innere_lösungs_menge = this.innere_Menge.berechne(ts);

        // Durchlaufe alle Zustände im Transitionssystem
        for (Zustand zustand : ts.getZustände()) {

            // Erhalte die abgehenden Relationen für den aktuellen Zustand
            List<ZweiTuple<Übergang, Zustand>> relation_abgehend = zustand.getRelationen_abgehend();

            // Prüfe, ob ALLE abgehenden Übergänge erlaubt sind UND alle erreichbaren Zustände in der inneren Menge sind
            boolean alleErreichbarenInMenge = true;
            for (ZweiTuple<Übergang, Zustand> relation : relation_abgehend) {

                // Prüfe, ob der Übergang in den erlaubten Übergängen ist und der Zielzustand in der inneren Menge liegt
                if (!this.übergänge.contains(relation.getFirst()) || 
                    !innere_lösungs_menge.contains(relation.getSecond())) {
                    alleErreichbarenInMenge = false;
                    break;  // Sobald eine Bedingung nicht erfüllt ist, kann der Zustand nicht in das Set aufgenommen werden
                }
            }

            // Wenn alle erreichbaren Zustände in der inneren Menge sind, füge den Zustand dem Rückgabeset hinzu
            if (alleErreichbarenInMenge) {
                returnSet.add(zustand);
            }
        }

        // Rückgabeset zurückgeben
        return returnSet;
    }


	public Set<Übergang> getÜbergänge() {
		return übergänge;
		}
	}



//Negation, Rekursion safe
class Negation extends Ast{
	
	public Negation (erfüllende_Mengen innere_Menge) {
		this.innere_Menge = innere_Menge;
		this.symbol = "¬{innere_Menge}";
	}
	public  Set<Zustand> berechne(Transitionssystem ts) {
		
    Set<Zustand> NegatedSet;
    
    NegatedSet = new HashSet<>(ts.getZustände());
    NegatedSet.removeAll(innere_Menge.berechne(ts));
    
    return NegatedSet;
	}	
}


//damit ist dritt lette Formel gemeint
class in_einem_nächsten_zustand_gilt extends Ast{
	
	public in_einem_nächsten_zustand_gilt (erfüllende_Mengen innere_Menge) {
		this.innere_Menge = innere_Menge;
		this.symbol = "∃○{innere_Menge}";
	}
    public Set<Zustand> berechne(Transitionssystem ts) {

        // Neues Set initialisieren, um die Zustände zu speichern, die die Bedingung erfüllen
        Set<Zustand> returnSet = new HashSet<>();
        
        //erst innere_Lösungsmenge berechnen
        Set<Zustand> innere_Lösungsmenge = innere_Menge.berechne(ts);

        // Durchlaufe alle Zustände die innere Formel erfüllen
        for (Zustand zustand : ts.getZustände()) {

            // Erhalte die abgehenden Relationen für den aktuellen Zustand
            List<ZweiTuple<Übergang, Zustand>> relation_abgehend = zustand.getRelationen_abgehend();

            // Durchlaufe alle abgehenden Relationen des Zustands
            for (ZweiTuple<Übergang, Zustand> relation : relation_abgehend) {

                // Wenn der Zielzustand (relation.getSecond()) in der inneren Menge ist
                if (innere_Lösungsmenge.contains(relation.getSecond())) {
                    // Füge den aktuellen Zustand dem Rückgabeset hinzu
                    returnSet.add(zustand);
                    break; // Wir müssen nicht weiter suchen, da der Zustand schon hinzugefügt wurde
                }
            }
        }

        // Gebe das berechnete Set zurück
        return returnSet;
    }
}



//damit ist die letzte Rekursive Definition aus dem Kurstext gemeint
class ein_pfad_auf_dem_immer_gilt extends Ast implements detail_lösung{
	
	String detail_lösung;
	
	
	public ein_pfad_auf_dem_immer_gilt(erfüllende_Mengen innere_Menge) {
		this.innere_Menge = innere_Menge;
		this.symbol = "∃□{innere_Menge}";
		
	}
	
	public String get_schritt_weise_lösung(){
		//System.out.println(this.detail_lösung);
		return this.detail_lösung;
	}
	
	public Set<Zustand> berechne(Transitionssystem ts) {
	    int n = 0;
		// 1. Berechne rekursive Sat(psi#)
	    Set<Zustand> innere_erfüllende_Menge = innere_Menge.berechne(ts);

	    // 2. Beginne mit Z = Z (alle Zustände)
	    HashSet<Zustand> returnSet = new HashSet<>(ts.getZustände());
	    
	    //Startpunkt speichern für Detailausgabe
	    this.detail_lösung = "allgemeine Lösung: "+"Sat(" + "ϕ" + ") ∩" + "{z ∈ Z'|∃a ∈ A,z' ∈ Z': z ↦ z'} \n";
	    
	    // 3. Berechne mit Sat(psi) die Fixpunktiteration, solange bis sich returnSet nicht mehr ändert
	    HashSet<Zustand> last_ReturnSet;
	    do {
	    	//String anhängen
	    	n = n +1;
	    	this.detail_lösung = this.detail_lösung+ "● "+ n+ ". Itterationsschritt: "+"(" + this.print_Lösungsmenge(innere_erfüllende_Menge) + ") ∩" + "{z ∈ " + this.print_Lösungsmenge(returnSet) +"|∃a ∈ A,z' ∈ "+ this.print_Lösungsmenge(returnSet)+": z ↦ z'}";
	        // Letzte Iteration speichern
	        last_ReturnSet = new HashSet<>(returnSet);
	        
	        // Neues ReturnSet initialisieren
	        HashSet<Zustand> neuesReturnSet = new HashSet<>();

	        // 1. Iteriere über alle Zustände von last_ReturnSet
	        for (Zustand zustand : last_ReturnSet) {

	            // Prüfe, ob der Zustand einen Übergang in last_ReturnSet hat
	            boolean hatÜbergangInReturnSet = false;

	            // Hole alle abgehenden Relationen des Zustands
	            List<ZweiTuple<Übergang, Zustand>> relation_abgehend = zustand.getRelationen_abgehend();

	            for (ZweiTuple<Übergang, Zustand> relation : relation_abgehend) {
	                // Wenn der Zielzustand in last_ReturnSet ist, markiere, dass ein Übergang existiert
	                if (last_ReturnSet.contains(relation.getSecond())) {
	                    hatÜbergangInReturnSet = true;
	                    break;
	                }
	            }

	            // Wenn ein Übergang in last_ReturnSet existiert, füge den Zustand zu neuesReturnSet hinzu
	            if (hatÜbergangInReturnSet) {
	                neuesReturnSet.add(zustand);
	            }
	        }

	        // 2. Schneide neuesReturnSet mit der inneren_erfüllenden_Menge
	        neuesReturnSet.retainAll(innere_erfüllende_Menge);
	        //an String anfügen
	        this.detail_lösung = this.detail_lösung + "= " + this.print_Lösungsmenge(neuesReturnSet)+"\n";

	        // Setze returnSet auf das Ergebnis der aktuellen Iteration
	        returnSet = neuesReturnSet;

	    } while (!returnSet.equals(last_ReturnSet));  // Wiederhole, solange sich returnSet verändert

	    // Gib das berechnete ReturnSet zurück
	    this.detail_lösung = this.detail_lösung + "→ Fixpunkt gefunden";
	    return returnSet;
	}
}

//####################Blatt: Für Klassen ohne innere Menge oder Seiten (keine Setter nötig)
abstract class Blatt extends erfüllende_Mengen {
// Keine spezifischen Attribute oder Methoden notwendig
}
//Alle Zustände aus dem TS erfüllen die Formel hinter dem CTL_Symbol 1
class one extends erfüllende_Mengen{
	
	public one() {
		this.symbol = "{Z}";
	}
	
	public  Set<Zustand> berechne(Transitionssystem ts) {
      return ts.getZustände();
	}
}

//Wenn 0 dann gib die leere Menge zurück, Rekursion safe
class null_ extends erfüllende_Mengen{
	
	null_(){
		this.symbol = "∅";
	}
	public  Set<Zustand> berechne(Transitionssystem ts) {
    return new HashSet<Zustand>();
	}
}