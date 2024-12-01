package CTL_Backend;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class Zustand {
    private String name;
    private LinkedList<ZweiTuple<Übergang, Zustand>> relationen_abgehend;
    private LinkedList<ZweiTuple<Übergang, Zustand>> relationen_zugehend;

    public Zustand(String name, LinkedList<ZweiTuple<Übergang, Zustand>> relationen, boolean relationen_abgehend) {
        if (relationen_abgehend) {
            this.name = name;
            this.relationen_abgehend = relationen;
        } else {
            this.name = name;
            this.relationen_zugehend = relationen;
        }
    }

    public String getName() {
        return this.name;
    }

    public void addAbgehendeRelation(ZweiTuple<Übergang, Zustand> abgehendeRelation) {
        if (this.relationen_abgehend == null) {
            this.relationen_abgehend = new LinkedList<>();
        }
        this.relationen_abgehend.add(abgehendeRelation);
    }

    public LinkedList<ZweiTuple<Übergang, Zustand>> getRelationen_abgehend() {
        return this.relationen_abgehend;
    }

    public void setRelationen_abgehend(LinkedList<ZweiTuple<Übergang, Zustand>> relationen_abgehend) {
        this.relationen_abgehend = relationen_abgehend;
    }

    public LinkedList<ZweiTuple<Übergang, Zustand>> getRelationen_zugehend() {
        return this.relationen_zugehend;
    }

    public void setRelationen_zugehend(LinkedList<ZweiTuple<Übergang, Zustand>> relationen_zugehend) {
        this.relationen_zugehend = relationen_zugehend;
    }

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

class ZweiTuple<A, B> {
    private A first;
    private B second;

    public ZweiTuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return this.first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return this.second;
    }

    public void setSecond(B second) {
        this.second = second;
    }
}

class Übergang {
    private String Zeichen;

    public Übergang(String zeichen) {
        this.Zeichen = zeichen;
    }

    public String getZeichen() {
        return this.Zeichen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && this.getClass() == o.getClass()) {
            Übergang übergang = (Übergang) o;
            return this.Zeichen.equals(übergang.Zeichen);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.Zeichen);
    }
}
