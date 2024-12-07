package CTL_Backend;

import java.util.Objects;

//Klasse zum repräsentieren von einzelnen Übergängen  
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
