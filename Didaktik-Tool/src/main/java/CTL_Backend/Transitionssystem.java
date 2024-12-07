package CTL_Backend;

import GUI.Relation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


//Klasse die Transitionsysteme bestehend aus Zustanden repräsentiert
public class Transitionssystem {
	
	//Set mir allen Zuständen
    private HashSet<Zustand> zustände = new HashSet<>();
    //Set mit namen des Zustandes und dem Zustands-Objekt, um redundante Zustandsobjekte zu verhindern
    private HashMap<String, Zustand> zustandsMap = new HashMap<>();
    
    //Transitionssystem wird aus Relationen im Stringformat erzeugt z.B "z1 a z2"
    public Transitionssystem(HashSet<Relation> relationen) {
    	
    	//durchlaufe alle Übergebenen Relationen
        for(Relation relation :relationen) {
        	//Zerteile String
            String detailsString = relation.getDetailsString();
            String[] parts = detailsString.split(" ");
            if (parts.length < 4) {
                System.err.println("Ungültiges Format für Relation: " + detailsString);
            } else {
            	
            	//extrahiert die Namen der Zustände und erzeugt Übergangsobjekt
                String ersterZustandName = parts[1];
                Übergang transition = new Übergang(parts[2]);
                String zweiterZustandName = parts[3];
                
                //erzeugt neuen Zustand oder sucht den Bestehenden Zustand aus HashMap über den Namen heraus
                Zustand ersterZustand = this.getOrCreateZustand(ersterZustandName);
                Zustand zweiterZustand = this.getOrCreateZustand(zweiterZustandName);
                
                //erzeugt die Relation zwischen den Zuständen als Objekt und speichert sie im ersten Zustand (abgehende Speicherung)
                ZweiTuple<Übergang, Zustand> abgehendeRelation = new ZweiTuple<>(transition, zweiterZustand);
                ersterZustand.addAbgehendeRelation(abgehendeRelation);
            }
        }
        //Fügt alle Zusände zur Map hinzu
        this.zustände.addAll(this.zustandsMap.values());
    }

    private Zustand getOrCreateZustand(String name) {
        if (!this.zustandsMap.containsKey(name)) {
            Zustand neuerZustand = new Zustand(name, new LinkedList<>());
            this.zustandsMap.put(name, neuerZustand);
        }

        return this.zustandsMap.get(name);
    }

    public HashSet<Zustand> getZustände() {
        return this.zustände;
    }
    
    //Zum Debugging gibt Zustände auf Konsole aus
    public void printAllZustände() {
       for(Zustand zustand : this.zustände) {
            zustand.printZustand();
        }
    }
}

