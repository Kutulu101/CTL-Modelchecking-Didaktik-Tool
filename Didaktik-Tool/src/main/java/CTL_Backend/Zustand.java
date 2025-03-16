package CTL_Backend;

import java.util.Iterator;
import java.util.LinkedList;

// Abstrakte Klasse für Zustand
public abstract class Zustand {
    protected String name;
    protected LinkedList<ZweiTuple<Übergang, Zustand>> relationen_abgehend = new LinkedList<>();

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

    public void printZustand() {
        System.out.println(getZustandBezeichnung() + ": " + this.name);
        if (!this.relationen_abgehend.isEmpty()) {
            for (ZweiTuple<Übergang, Zustand> relation : this.relationen_abgehend) {
                System.out.println("  Übergang: " + relation.getFirst().getZeichen() + 
                                   " -> Zustand: " + relation.getSecond().getName());
            }
        } else {
            System.out.println("  Keine abgehenden Relationen.");
        }
    }

    // Abstrakte Methode, um die konkrete Bezeichnung des Zustands zu erhalten
    protected abstract String getZustandBezeichnung();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Zustand) {
            Zustand otherZustand = (Zustand) obj;
            return this.name.equals(otherZustand.getName()); // Vergleiche nur den Namen
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode(); // Nur der Name beeinflusst den HashCode
    }
}

// Konkrete Implementierung des Zustands
class KonkreterZustand extends Zustand {
    public KonkreterZustand(String name, LinkedList<ZweiTuple<Übergang, Zustand>> relationen) {
        super(name, relationen);
    }

    @Override
    protected String getZustandBezeichnung() {
        return "Zustand";
    }
}

// AntiZustand implementiert ebenfalls Zustand
class AntiZustand extends Zustand {
    public AntiZustand(String name, LinkedList<ZweiTuple<Übergang, Zustand>> relationen) {
        super(name, relationen);
    }

    @Override
    protected String getZustandBezeichnung() {
        return "AntiZustand";
    }
}
