package CTL_Backend;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

//Klasse die eine Zustand repräsentiert
public class Zustand {
	
	//Zustand besteht aus seiner Bezeichnung z.B z1 und einer Liste aus Übergängen verknüpft mit dem Zielzustand
    private String name;
    private LinkedList<ZweiTuple<Übergang, Zustand>> relationen_abgehend = new LinkedList<>();


    public Zustand(String name, LinkedList<ZweiTuple<Übergang, Zustand>> relationen) {
            this.name = name;
            this.relationen_abgehend = relationen;
    }

    public String getName() {
        return this.name;
    }

    public void addAbgehendeRelation(ZweiTuple<Übergang, Zustand> abgehendeRelation) {

        this.relationen_abgehend.add(abgehendeRelation);
    }

    public LinkedList<ZweiTuple<Übergang, Zustand>> getRelationen_abgehend() {
        return this.relationen_abgehend;
    }

    public void setRelationen_abgehend(LinkedList<ZweiTuple<Übergang, Zustand>> relationen_abgehend) {
        this.relationen_abgehend = relationen_abgehend;
    }
    
    //Zum Debuggen: Schreibt Zustand formatiert auf Konsole
    public void printZustand() {
        System.out.println("Zustand: " + this.name);
        if (this.relationen_abgehend != null && !this.relationen_abgehend.isEmpty()) {
            Iterator<ZweiTuple<Übergang, Zustand>> iterator = this.relationen_abgehend.iterator();
            while (iterator.hasNext()) {
                ZweiTuple<Übergang, Zustand> relation = iterator.next();
                System.out.println("  Übergang: " + relation.getFirst().getZeichen() + 
                                   " -> Zustand: " + relation.getSecond().getName());
            }
        } else {
            System.out.println("  Keine abgehenden Relationen.");
        }
    }
}




