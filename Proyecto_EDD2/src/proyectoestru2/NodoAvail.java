
package proyectoestru2;

public class NodoAvail {
    private long offset;
    private int tamano;
    private NodoAvail siguiente;
    private NodoAvail anterior;

    public NodoAvail(long offset, int tamano) {
        this.offset = offset;
        this.tamano = tamano;
        this.siguiente = null;
        this.anterior = null;
    }

    public long getOffset() {
        return offset;
    }

    public int getTamano() {
        return tamano;
    }

    public NodoAvail getSiguiente() {
        return siguiente;
    }

    public NodoAvail getAnterior() {
        return anterior;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public void setSiguiente(NodoAvail siguiente) {
        this.siguiente = siguiente;
    }

    public void setAnterior(NodoAvail anterior) {
        this.anterior = anterior;
    }
    
}
