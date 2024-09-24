package CTL_Backend;
import java.util.HashSet;

import CTL_Backend.Zustandsformel;
import CTL_Backend.erfüllende_Mengen;
import GUI.Relation;

public class test_file {
    public static void main(String[] args) {
    	
    	//zum testen "∃◇〈{a}〉∃□¬〈{b}〉1∨∃(〈{a}〉1)U(〈{b}〉1)"
    	//"(1∧1)∨(1∧1)"
        Zustandsformel zf = new Zustandsformel("(1∧1)∨(1∧1)");
        zf.turn_to_normal_form();
        
        System.out.println("orginalle Formel: " + zf.getFormel_string());
        System.out.println("umgeformte Formel: " + zf.getFormel_string_normal_form());
        zf.print_erfüllende_zustände(new Transitionssystem(new HashSet<Relation>()));

       
    }
}

