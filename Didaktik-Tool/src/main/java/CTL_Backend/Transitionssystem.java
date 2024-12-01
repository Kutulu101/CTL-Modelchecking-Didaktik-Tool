package CTL_Backend;

import GUI.Relation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Transitionssystem {
    private HashSet<Zustand> zustände = new HashSet<>();
    private HashMap<String, Zustand> zustandsMap = new HashMap<>();

    public Transitionssystem(HashSet<Relation> relationen) {
        Iterator<Relation> var2 = relationen.iterator();

        while (var2.hasNext()) {
            Relation relation = var2.next();
            String detailsString = relation.getDetailsString();
            String[] parts = detailsString.split(" ");
            if (parts.length < 4) {
                System.err.println("Ungültiges Format für Relation: " + detailsString);
            } else {
                String ersterZustandName = parts[1];
                Übergang transition = new Übergang(parts[2]);
                String zweiterZustandName = parts[3];
                Zustand ersterZustand = this.getOrCreateZustand(ersterZustandName);
                Zustand zweiterZustand = this.getOrCreateZustand(zweiterZustandName);
                ZweiTuple<Übergang, Zustand> abgehendeRelation = new ZweiTuple<>(transition, zweiterZustand);
                ersterZustand.addAbgehendeRelation(abgehendeRelation);
            }
        }

        this.zustände.addAll(this.zustandsMap.values());
    }

    private Zustand getOrCreateZustand(String name) {
        if (!this.zustandsMap.containsKey(name)) {
            Zustand neuerZustand = new Zustand(name, new LinkedList<>(), true);
            this.zustandsMap.put(name, neuerZustand);
        }

        return this.zustandsMap.get(name);
    }

    public HashSet<Zustand> getZustände() {
        return this.zustände;
    }

    public Set<String> getZuständeasStrings() {
        Set<String> namenSet = new HashSet<>();
        Iterator<Zustand> var2 = this.zustände.iterator();

        while (var2.hasNext()) {
            Zustand zustand = var2.next();
            namenSet.add(zustand.getName());
        }

        return namenSet;
    }

    public void printAllZustände() {
        Iterator<Zustand> var1 = this.zustände.iterator();

        while (var1.hasNext()) {
            Zustand zustand = var1.next();
            zustand.printZustand();
        }
    }
}

