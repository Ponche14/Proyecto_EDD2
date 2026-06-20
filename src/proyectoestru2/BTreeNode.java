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
public class BTreeNode implements Serializable {

    private Llave[] keys;
    private BTreeNode[] children;
    
    private boolean isLeaf;
    private int t;
    int numKeys;
    private static final long SerialVersionUID = 777;

   public BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.isLeaf = leaf;
        this.keys = new Llave[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.numKeys = 0;
    }   

    public Llave[] getKeys() {
        return keys;
    }

    public BTreeNode[] getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getDegree() {
        return (int) t;
    }

    public int getNumKeys() {
        return numKeys;
    }

    public void setNumKeys(int n) {
        this.numKeys = n;
    }

    public int binarySearch(Object key) {
        int left = 0, right = numKeys - 1;

        while (left <= right) {
            int mid = (left + right) / 2;

            Comparable<Object> midKey = (Comparable<Object>) keys[mid].getKey();
            Comparable<Object> searchKey = (Comparable<Object>) key;

            int cmp = midKey.compareTo(searchKey);

            if (cmp == 0) {
                return mid; // Key found
            } else if (cmp < 0) {
                left = mid + 1; // Search right half
            } else {
                right = mid - 1; // Search left half
            }
        }

        return -(left + 1); // Return insertion point as a negative value
    }

    public String toString() {
        if (numKeys == 0) {
            return "Nodo vacío"; // O cualquier mensaje que te ayude a ver que el nodo está vacío
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numKeys; i++) {
            sb.append(keys[i]).append(" ");
        }
        return sb.toString().trim();  // Devuelve las claves como una cadena separada por espacios
    }
}
