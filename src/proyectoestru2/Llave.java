/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectoestru2;

import java.io.Serializable;

/**
 *
 * @author ghasb
 */
public class Llave implements Serializable {

    private Long RRN;
    private Comparable key;
    private static final long SerialVersionUID = 777;

    public Llave() {
    }

    public Llave(Comparable key, long RRN) {
        this.key = key;
        this.RRN = RRN;
    }

    public Comparable getKey() {
        return key;
    }

    public void setKey(Comparable key) {
        this.key = key;
    }

    public Long getRRN() {
        return RRN;
    }

    public void setRRN(Long RRN) {
        this.RRN = RRN;
    }

    @Override
    public String toString() {
        return "Llave{" + "RRN=" + RRN + ", key=" + key + '}';
    }
}
