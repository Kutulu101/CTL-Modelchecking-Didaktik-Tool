package GUI;



//Hilfsklasse zur Bündelung der Berechnung der Start und Endpunkte einer Linie, inkl. Anstieg
public class EdgePoints {
   final double startX;
   final double startY;
   final double endX;
   final double endY;
   final double m;

   EdgePoints(double startX, double startY, double endX, double endY, double m) {
      this.startX = startX;
      this.startY = startY;
      this.endX = endX;
      this.endY = endY;
      this.m = m;
   }
   
 //Berechnet Endpunkte der gedachten Linie Zwischen zwei Kreisen beginnen am jeweilgen Rand
   public EdgePoints(double startX, double startY, double endX, double endY, double radius1, double radius2, int offset) {
      
   	   //Anstieg pro Pixel in x- und y Richtung berechnen
   	  double dx = endX - startX;
      double dy = endY - startY;
      double distance = Math.sqrt(dx * dx + dy * dy);
      dx /= distance;
      dy /= distance;
      
      //Anstieg der Normalen berechnen
      double nx = -dy;
      
      //Berechne den Schnittpunkt von ersten Kreis  und der Linie zwischen den Kreismittelpunkten
      double startEdgeX = startX + radius1 * dx + (double)offset * nx;
      double startEdgeY = startY + radius1 * dy + (double)offset * dx;

     //Berechne den Schnittpunkt von zweiten Kreis  und der Linie zwischen den Kreismittelpunkten
      double endEdgeX = endX - radius2 * dx + (double)offset * nx;
      double endEdgeY = endY - radius2 * dy + (double)offset * dx;
      
      //Berechne den Anstieg der Geraden
      double m = (endEdgeY - startEdgeY) / (endEdgeX - startEdgeX);
      
      this.startX = startEdgeX;
      this.startY = startEdgeY;
      this.endX =  endEdgeX;
      this.endY = endEdgeY;
      this.m = m;
   }
} 
