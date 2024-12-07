   package CTL_Backend;

 //Klasse zum speichern der unternommen Umformungsschritte bei der Umformung einer CTL-Gleichung in Normalform
public class Umformung {
   private String vor_der_Ersetzung;
   private String nach_der_Ersetzung;
   private int start_index_vor_der_Ersetzung;
   private int start_nach_der_Ersetzung;
   private String ersetzt_mit_regel_nummer;

   public Umformung(String vor_der_Ersetzung, String nach_der_Ersetzung, int start_index_vor_der_Ersetzung, int start_nach_der_Ersetzung, String ersetzt_mit_regel_nummer) {
      this.vor_der_Ersetzung = vor_der_Ersetzung;
      this.nach_der_Ersetzung = nach_der_Ersetzung;
      this.start_index_vor_der_Ersetzung = start_index_vor_der_Ersetzung;
      this.start_nach_der_Ersetzung = start_nach_der_Ersetzung;
      this.ersetzt_mit_regel_nummer = ersetzt_mit_regel_nummer;
   }

   public String getVor_der_Ersetzung() {
      return this.vor_der_Ersetzung;
   }

   public String getNach_der_Ersetzung() {
      return this.nach_der_Ersetzung;
   }

   public int getStart_index_vor_der_Ersetzung() {
      return this.start_index_vor_der_Ersetzung;
   }

   public int getStart_nach_der_Ersetzung() {
      return this.start_nach_der_Ersetzung;
   }

   public String getErsetzt_mit_regel_nummer() {
      return this.ersetzt_mit_regel_nummer;
   }
}