package CTL_Backend;
import java.util.LinkedList;
import java.util.Objects;

public class Zustand {
    private String name;
    //Speichert redundant die Relationen die Zugehend sind und die die Abgehend sind
    private LinkedList<ZweiTuple<Übergang, Zustand>> relationen_abgehend;
    private LinkedList<ZweiTuple<Übergang, Zustand>> relationen_zugehend;

    public Zustand(String name, LinkedList<ZweiTuple<Übergang, Zustand>> relationen, boolean relationen_abgehend) {
	  if (relationen_abgehend){     
		  	this.name = name;
	        this.relationen_abgehend = relationen;
	  } else {
		  	this.name = name;
	        this.relationen_zugehend = relationen;
	  }
    }

    public String getName() {
        return name;
    }
    
    // Methode zum Hinzufügen einer abgehenden Relation
    public void addAbgehendeRelation(ZweiTuple<Übergang, Zustand> abgehendeRelation) {
        if (relationen_abgehend == null) {
            relationen_abgehend = new LinkedList<>();
        }
        relationen_abgehend.add(abgehendeRelation);
    }

	public LinkedList<ZweiTuple<Übergang, Zustand>> getRelationen_abgehend() {
		return relationen_abgehend;
	}

	public void setRelationen_abgehend(LinkedList<ZweiTuple<Übergang, Zustand>> relationen_abgehend) {
		this.relationen_abgehend = relationen_abgehend;
	}

	public LinkedList<ZweiTuple<Übergang, Zustand>> getRelationen_zugehend() {
		return relationen_zugehend;
	}

	public void setRelationen_zugehend(LinkedList<ZweiTuple<Übergang, Zustand>> relationen_zugehend) {
		this.relationen_zugehend = relationen_zugehend;
	}
	
    public void printZustand() {
        System.out.println("Zustand: " + name);
        if (relationen_abgehend != null && !relationen_abgehend.isEmpty()) {
            for (ZweiTuple<Übergang, Zustand> relation : relationen_abgehend) {
                System.out.println("  Übergang: " + relation.getFirst().getZeichen() + " -> Zustand: " + relation.getSecond().getName());
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
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

}

class Übergang {
    String Zeichen;
    
    public Übergang(String zeichen) {
        this.Zeichen = zeichen;
    }

    public String getZeichen() {
        return Zeichen;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Übergang übergang = (Übergang) o;
        return Zeichen.equals(übergang.Zeichen); // Vergleich der Zeichen
    }

    @Override
    public int hashCode() {
        return Objects.hash(Zeichen); // HashCode basierend auf den Zeichen
    }
}
