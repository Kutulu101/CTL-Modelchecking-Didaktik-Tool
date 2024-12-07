package CTL_Backend;

//Hilfklasse die verwendet wird um Übergang und Zielzustand zu verknüpfen
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
