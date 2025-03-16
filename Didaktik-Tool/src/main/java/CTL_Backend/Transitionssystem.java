package CTL_Backend;

import GUI.Relation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


//Klasse die Transitionsysteme bestehend aus Zustanden repräsentiert
public class Transitionssystem {
	
	//Set mir allen Zuständen
    private HashSet<Zustand> zustände = new HashSet<>();
    private HashSet<Zustand> Antizustände = new HashSet<>();
    //Set mit namen des Zustandes und dem Zustands-Objekt, um redundante Zustandsobjekte zu verhindern
    private HashMap<String, Zustand> zustandsMap = new HashMap<>();
	private HashMap<String, Zustand> antizustandsMap= new HashMap<>();
    
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
        this.Antizustände.addAll(this.antizustandsMap.values());
    }

    private Zustand getOrCreateZustand(String name) {
        if (!this.zustandsMap.containsKey(name)) {
            Zustand neuerZustand = new  KonkreterZustand(name, new LinkedList<>());
            Zustand neuerAntiZustand = new  AntiZustand(name, new LinkedList<>());
            this.zustandsMap.put(name, neuerZustand);
            this.antizustandsMap.put(name, neuerAntiZustand);
        }

        return this.zustandsMap.get(name);
    }

    public HashSet<Zustand> getZustände() {
        return this.zustände;
    }
    
    public HashSet<Zustand> getAntiZustände() {
        return this.Antizustände;
    }
    
    //Zum Debugging gibt Zustände auf Konsole aus
    public void printAllZustände() {
       for(Zustand zustand : this.zustände) {
            zustand.printZustand();
        }
    }
    
 // Gibt alle Zustände als String zurück
    public String Alle_Zustände() {
        StringBuilder result = new StringBuilder();
        
        for (Zustand zustand : this.zustände) {
            // Holen des Namens des Zustands und Hinzufügen zum Ergebnis
            result.append(zustand.getName()).append(", ");
        }
        
        // Wenn der String nicht leer ist, entferne das letzte Komma und Leerzeichen
        if (result.length() > 0) {
            result.delete(result.length() - 2, result.length());
        }
        
        return result.toString();
    }
}

