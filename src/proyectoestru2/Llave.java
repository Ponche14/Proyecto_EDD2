package proyectoestru2;

import java.io.Serializable;

/**
 *
 * @author ghasb
 */
public class Llave implements Serializable {

    private Long offset;
    private Comparable key;
    private static final long SerialVersionUID = 777;

    public Llave() {
    }

    public Llave(Comparable key, long offset) {
        this.key = key;
        this.offset = offset;
    }

    public Comparable getKey() {
        return key;
    }

    public void setKey(Comparable key) {
        this.key = key;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "Llave{" + "offset=" + offset + ", key=" + key + '}';
    }
}
