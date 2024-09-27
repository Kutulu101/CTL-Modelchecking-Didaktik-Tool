package CTL_Backend;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import GUI.Relation;

public class Transitionssystem {
	
	private HashSet<Zustand> zustände;
	private HashMap<String, Zustand> zustandsMap = new HashMap<>(); // Um bereits erstellte Zustände zu speichern
	
    public Transitionssystem(HashSet<Relation> relationen) {
        
    	zustände = new HashSet<>();
        
        for (Relation relation : relationen) {
            // Extrahiere die Details aus dem String der Relation
            String detailsString = relation.getDetailsString();
            // "Relation: erster_Zustand transition zweiter_Zustand"
            String[] parts = detailsString.split(" ");
            
            if (parts.length < 4) {
                // Wenn der String nicht das erwartete Format hat
                System.err.println("Ungültiges Format für Relation: " + detailsString);
                continue;
            }

            // Der erste Zustand ist an der 1. Stelle (parts[1])
            String ersterZustandName = parts[1];
            // Der Übergang ist an der 2. Stelle (parts[2])
            Übergang transition = new Übergang(parts[2]); // Erstelle ein Übergangsobjekt
            // Der zweite Zustand ist an der 3. Stelle (parts[3])
            String zweiterZustandName = parts[3];

            // Stelle sicher, dass der erste Zustand existiert
            Zustand ersterZustand = getOrCreateZustand(ersterZustandName);

            // Stelle sicher, dass der zweite Zustand existiert
            Zustand zweiterZustand = getOrCreateZustand(zweiterZustandName);

            // Erstelle ein ZweiTuple mit dem Übergang und dem zweiten Zustand
            ZweiTuple<Übergang, Zustand> abgehendeRelation = new ZweiTuple<>(transition, zweiterZustand);

            // Füge die Relation zum ersten Zustand hinzu (abgehende Relationen)
            ersterZustand.addAbgehendeRelation(abgehendeRelation);
        }

        // Füge alle Zustände der HashSet-Sammlung hinzu
        zustände.addAll(zustandsMap.values());
    }
    

    // Hilfsmethode, um einen Zustand zu bekommen oder zu erstellen
    private Zustand getOrCreateZustand(String name) {
        if (!zustandsMap.containsKey(name)) {
            // Falls der Zustand noch nicht existiert, erstelle ihn
            Zustand neuerZustand = new Zustand(name, new LinkedList<>(), true);
            zustandsMap.put(name, neuerZustand);
        }
        return zustandsMap.get(name);
    }
	
	public HashSet<Zustand> getZustände() {
		return zustände;
	}
	
	public Set<String> getZuständeasStrings(){
	    
		Set<String> namenSet = new HashSet<>();
	    

	    // Für jeden Zustand den Namen abrufen und zum Set hinzufügen
	    for (Zustand zustand : this.zustände) {
	        namenSet.add(zustand.getName());
	    }
	    
	    return namenSet;
	}
	
    // Neue Methode zum Auflisten aller Zustände und deren abgehender Relationen
    public void printAllZustände() {
        for (Zustand zustand : zustände) {
            zustand.printZustand();  // Ruft die Methode von Zustand auf, um den Zustand zu drucken
        }
    }
	
}


