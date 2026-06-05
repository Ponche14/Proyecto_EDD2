
package proyectoestru2;

public class Campo {
    private String nombre;
    private char tipo; //String 'S', Entero 'E', Float 'F', Char 'C'
    private int longitud;
    private boolean esPrimaria;
    private boolean esSecundaria;

    public Campo(String nombre, char tipo, int longitud, boolean esPrimaria, boolean esSecundaria) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.longitud = longitud;
        this.esPrimaria = esPrimaria;
        this.esSecundaria = esSecundaria;
    }

    public String getNombre() {
        return nombre;
    }

    public char getTipo() {
        return tipo;
    }

    public int getLongitud() {
        return longitud;
    }

    public boolean isEsPrimaria() {
        return esPrimaria;
    }

    public boolean isEsSecundaria() {
        return esSecundaria;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    public void setEsPrimaria(boolean esPrimaria) {
        this.esPrimaria = esPrimaria;
    }

    public void setEsSecundaria(boolean esSecundaria) {
        this.esSecundaria = esSecundaria;
    }
    
}
