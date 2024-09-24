package CTL_Backend;

//Objekt, dass sich merkt welche Therme bei der Transformation in die Normalform verändert wurden
public class Umformung {
  private String vor_der_Ersetzung;
  private String nach_der_Ersetzung;
  private int start_index_vor_der_Ersetzung;
  private int start_nach_der_Ersetzung;
  private String ersetzt_mit_regel_nummer;

  // Konstruktor, der alle Werte setzt
  public Umformung(String vor_der_Ersetzung, String nach_der_Ersetzung, int start_index_vor_der_Ersetzung, int start_nach_der_Ersetzung, String ersetzt_mit_regel_nummer) {
      this.vor_der_Ersetzung = vor_der_Ersetzung;
      this.nach_der_Ersetzung = nach_der_Ersetzung;
      this.start_index_vor_der_Ersetzung = start_index_vor_der_Ersetzung;
      this.start_nach_der_Ersetzung = start_nach_der_Ersetzung;
      this.ersetzt_mit_regel_nummer = ersetzt_mit_regel_nummer;
  }

  // Getter-Methoden
  public String getVor_der_Ersetzung() {
      return vor_der_Ersetzung;
  }

  public String getNach_der_Ersetzung() {
      return nach_der_Ersetzung;
  }

  public int getStart_index_vor_der_Ersetzung() {
      return start_index_vor_der_Ersetzung;
  }

  public int getStart_nach_der_Ersetzung() {
      return start_nach_der_Ersetzung;
  }

  public String getErsetzt_mit_regel_nummer() {
      return ersetzt_mit_regel_nummer;
  }
}
