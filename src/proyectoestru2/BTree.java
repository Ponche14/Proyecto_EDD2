/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectoestru2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author ghasb
 */
public class BTree implements Serializable {

    private static final long SerialVersionUID = 777;

    private String Father_filepath = "";
    private BTreeNode root;
    private int t;

    public BTree(int t) {
        this.t = t;
        this.root = new BTreeNode(t, true);
    }

    public String getFather_filepath() {
        return Father_filepath;
    }

    public void setFather_filepath(String Father_filepath) {
        this.Father_filepath = Father_filepath;
    }

    public BTree() {

    }

    public BTreeNode getRoot() {
        return root;
    }

    public void setRoot(BTreeNode root) {
        this.root = root;
    }

    public int getT() {
        return (int) t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public BTreeNode search(Object key) {
        return (root == null) ? null : searchInNode(root, key);
    }

    private BTreeNode searchInNode(BTreeNode node, Object key) {
        int index = node.binarySearch(key);

        // Si la clave existe en el nodo
        if (index >= 0 && index < node.getNumKeys() && node.getKeys()[index].getKey().equals(key)) {
            return node;
        }

        // Si es hoja y no está, devolver null
        if (node.isLeaf()) {
            return null;
        }

        // Buscar en el hijo correspondiente
        int childIndex = (index >= 0) ? index : -(index + 1);
        return searchInNode(node.getChildren()[childIndex], key);
    }

    public void insert(Llave key) {
        BTreeNode r = root;
        if (r.getNumKeys() == t - 1) { // Nodo lleno (t - 1 claves)
            BTreeNode newNode = new BTreeNode(t, false);
            newNode.getChildren()[0] = r;
            splitChild(newNode, 0);
            this.root = newNode;
        }
        insertNonFull(root, key);
    }

    // Método para insertar una clave en un nodo que no está lleno
    private void insertNonFull(BTreeNode node, Llave key) {
        int i = node.getNumKeys() - 1;

        if (node.isLeaf()) {
            // Buscar el lugar donde insertar la clave usando búsqueda binaria
            int position = node.binarySearch(key.getKey());

            // Asegúrate de que la posición esté dentro del rango
            if (position < 0) {
                position = -position - 1;  // Si no encontramos la clave, obtenemos la posición correcta
            }

            // Insertar la clave en la posición correcta
            for (int j = node.getNumKeys(); j > position; j--) {
                node.getKeys()[j] = node.getKeys()[j - 1];
            }
            node.getKeys()[position] = key;
            node.setNumKeys(node.getNumKeys() + 1);

        } else {
            // Si el nodo no es hoja, buscamos el hijo adecuado
            int position = node.binarySearch(key.getKey());

            // Asegúrate de que la posición esté dentro del rango
            if (position < 0) {
                position = -position - 1;  // Si no encontramos la clave, obtenemos la posición correcta
            }

            // Si el hijo está lleno, lo dividimos antes de insertar
            if (node.getChildren()[position] != null && node.getChildren()[position].getNumKeys() == t - 1) {
                splitChild(node, position);

                // Después de dividir, el hijo medio se mueve a la posición i del nodo actual
                if (key.getKey().compareTo(node.getKeys()[position].getKey()) > 0) {
                    position++;
                }
            }

            // Llamamos recursivamente en el hijo adecuado
            if (node.getChildren()[position] != null) {
                insertNonFull(node.getChildren()[position], key);  // Insertar en el hijo adecuado
            }
        }
    }

    // Método para dividir un hijo cuando está lleno
    private void splitChild(BTreeNode parent, int index) {
        BTreeNode fullChild = parent.getChildren()[index];
        BTreeNode newChild = new BTreeNode(t, fullChild.isLeaf());

        int splitPoint = (t - 1) / 2; // Punto de división

        // Transfiere las claves y los hijos al nuevo nodo
        for (int i = 0; i < t - 1 - splitPoint - 1; i++) {
            newChild.getKeys()[i] = fullChild.getKeys()[i + splitPoint + 1];
        }

        if (!fullChild.isLeaf()) {
            for (int i = 0; i < t - splitPoint - 1; i++) {
                newChild.getChildren()[i] = fullChild.getChildren()[i + splitPoint + 1];
            }
        }

        // Ajusta el número de claves en el nodo dividido
        fullChild.setNumKeys(splitPoint);
        newChild.setNumKeys(t - 1 - splitPoint - 1);

        // Mueve la clave del medio al padre
        for (int i = parent.getNumKeys(); i > index; i--) {
            parent.getKeys()[i] = parent.getKeys()[i - 1];
        }
        parent.getKeys()[index] = fullChild.getKeys()[splitPoint];

        // Inserta el nuevo hijo en el padre
        for (int i = parent.getNumKeys() + 1; i > index + 1; i--) {
            parent.getChildren()[i] = parent.getChildren()[i - 1];
        }
        parent.getChildren()[index + 1] = newChild;

        parent.setNumKeys(parent.getNumKeys() + 1);
    }

    public void CrossTree(BTree Btree2, File file, Archivo archivo1, Archivo archivo2, int[] campo1, int[] campo2) {
        if (root != null && Btree2.getRoot() != null) {
            CrossTreeNode(root, file, Btree2, archivo1, archivo2, campo1, campo2);
        } else {
            System.out.println("El árbol está vacío.");
        }
    }

    private void CrossTreeNode(BTreeNode node, File file, BTree Btree2, Archivo archivo1, Archivo archivo2, int[] campo1, int[] campo2) {
        if (node != null) {
            try {
                // Imprimir las claves del nodo
                try (BufferedWriter bf = new BufferedWriter(new FileWriter(file, true))) {
                    for (Llave key : node.getKeys()) {
                        if (key != null) {
                            Registro registro1 = archivo1.LoadRegistro(key.getRRN());
                            if (!registro1.isBorrado()) {
                                BTreeNode temp = Btree2.search(key.getKey());
                                if (temp != null) {
                                    Llave llave_temp = temp.getKeys()[temp.binarySearch(key.getKey())];
                                    Registro registro2 = archivo2.LoadRegistro(llave_temp.getRRN());
                                    if (!registro2.isBorrado()) {
                                        StringBuilder write = new StringBuilder();

                                        for (int i : campo1) {
                                            write.append(registro1.getData().get(i).toString()).append(" ");
                                        }
                                        write.append("- ");
                                        for (int i : campo2) {
                                            write.append(registro2.getData().get(i).toString()).append(" ");
                                        }
                                        bf.write(write.toString() + "\n");
                                        bf.flush();
                                    }
                                }
                            }

                        }
                    }
                    bf.close();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error in cross tree: ");
                Logger.getLogger(BTree.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Imprimir los hijos de este nodo
            for (int i = 0; i <= node.getNumKeys(); i++) {
                if (node.getChildren()[i] != null) {
                    CrossTreeNode(node.getChildren()[i], file, Btree2, archivo1, archivo2, campo1, campo2);
                }
            }
        }
    }

    public void printTree() {
        if (root != null) {
            printTreeNode(root, "", true);
        } else {
            System.out.println("El árbol está vacío.");
        }
    }

    private void printTreeNode(BTreeNode node, String indent, boolean isLeft) {
        if (node != null) {
            // Imprimir las claves del nodo
            System.out.println(indent + (isLeft ? "├── " : "└── ") + node.toString());

            // Imprimir los hijos de este nodo
            for (int i = 0; i <= node.getNumKeys(); i++) {
                if (node.getChildren()[i] != null) {
                    printTreeNode(node.getChildren()[i], indent + (isLeft ? "│   " : "    "), i < node.getNumKeys());
                }
            }
        }
    }

    public void delete(Comparable key) {
        if (root == null) {
            System.out.println("El árbol está vacío.");
            return;
        }

        deleteKey(root, key);

        // Si la raíz se queda sin claves y no es hoja, actualizamos la raíz
        if (root.getNumKeys() == 0 && !root.isLeaf()) {
            root = root.getChildren()[0];
        }
    }

// Método principal para eliminar una clave en un nodo
    ///binary search no sirve
    private void deleteKey(BTreeNode node, Comparable key) {
        int position = node.binarySearch(key);

        // Case 1: The key is in the current node
        if (position >= 0 && position < node.getNumKeys()
                && node.getKeys()[position].getKey().compareTo(key) == 0) {

            if (node.isLeaf()) {
                // Directly remove the key from the leaf node
                removeKey(node, position);
            } else {
                // If the node is internal, handle deletion via predecessors/successors
                if (node.getChildren()[position].getNumKeys() >= getMinKeys()) {
                    Llave predecessorKey = findPredecessorKey(node, position);
                    node.getKeys()[position] = predecessorKey;
                    deleteKey(node.getChildren()[position], predecessorKey.getKey());
                } else if (node.getChildren()[position + 1].getNumKeys() >= getMinKeys()) {
                    Llave successorKey = findSuccessorKey(node, position);
                    node.getKeys()[position] = successorKey;
                    deleteKey(node.getChildren()[position + 1], successorKey.getKey());
                } else {
                    mergeNodes(node, position);
                    deleteKey(node.getChildren()[position], key);
                }
            }
        } else {
            // Case 2: The key is not in this node
            if (node.isLeaf()) {
                System.out.println("The key " + key + " is not in the tree.");
                return;
            }

            // Interpret position if it's negative
            position = -(position + 1);

            boolean lastChild = (position == node.getNumKeys());
            BTreeNode child = node.getChildren()[position];

            // Ensure the child has at least the minimum number of keys
            if (child.getNumKeys() < getMinKeys()) {
                fixChild(node, position);
            }

            // Recursive call on the correct child
            if (lastChild && position > node.getNumKeys()) {
                deleteKey(node.getChildren()[position - 1], key);
            } else {
                deleteKey(node.getChildren()[position], key);
            }
        }
    }

// Elimina una clave de un nodo hoja
    private void removeKey(BTreeNode node, int index) {
        for (int i = index; i < node.getNumKeys() - 1; i++) {
            node.getKeys()[i] = node.getKeys()[i + 1];
        }
        node.setNumKeys(node.getNumKeys() - 1);
    }

// Encuentra el predecesor de una clave en un nodo
    // Encuentra el predecesor de una clave en un nodo
    private Llave findPredecessorKey(BTreeNode node, int index) {
        BTreeNode child = node.getChildren()[index];
        while (!child.isLeaf()) {
            child = child.getChildren()[child.getNumKeys()];
        }
        return child.getKeys()[child.getNumKeys() - 1];
    }

// Encuentra el sucesor de una clave en un nodo
    private Llave findSuccessorKey(BTreeNode node, int index) {
        BTreeNode child = node.getChildren()[index + 1];
        while (!child.isLeaf()) {
            child = child.getChildren()[0];
        }
        return child.getKeys()[0];
    }

// Fusiona dos nodos en el índice dado
    // Fusiona dos nodos en el índice dado
    private void mergeNodes(BTreeNode parent, int index) {
        BTreeNode leftChild = parent.getChildren()[index];
        BTreeNode rightChild = parent.getChildren()[index + 1];

        // Mueve la clave del padre al nodo izquierdo
        leftChild.getKeys()[leftChild.getNumKeys()] = parent.getKeys()[index];
        for (int i = 0; i < rightChild.getNumKeys(); i++) {
            leftChild.getKeys()[leftChild.getNumKeys() + 1 + i] = rightChild.getKeys()[i];
        }

        if (!leftChild.isLeaf()) {
            for (int i = 0; i <= rightChild.getNumKeys(); i++) {
                leftChild.getChildren()[leftChild.getNumKeys() + 1 + i] = rightChild.getChildren()[i];
            }
        }

        // Ajusta el número de claves
        leftChild.setNumKeys(leftChild.getNumKeys() + 1 + rightChild.getNumKeys());

        // Mueve las claves del padre para llenar el vacío
        for (int i = index; i < parent.getNumKeys() - 1; i++) {
            parent.getKeys()[i] = parent.getKeys()[i + 1];
        }

        // Mueve los hijos del padre para llenar el vacío
        for (int i = index + 1; i < parent.getNumKeys(); i++) {
            parent.getChildren()[i] = parent.getChildren()[i + 1];
        }

        parent.setNumKeys(parent.getNumKeys() - 1);
    }

// Arregla un hijo para que tenga al menos el mínimo de claves
    private void fixChild(BTreeNode parent, int index) {
        BTreeNode child = parent.getChildren()[index];

        if (index > 0 && parent.getChildren()[index - 1].getNumKeys() >= getMinKeys()) {
            moveKey(parent, index - 1, index);
        } else if (index < parent.getNumKeys() && parent.getChildren()[index + 1].getNumKeys() >= getMinKeys()) {
            moveKey(parent, index + 1, index);
        } else {
            if (index < parent.getNumKeys()) {
                mergeNodes(parent, index);
            } else {
                mergeNodes(parent, index - 1);
            }
        }
    }

// Mueve una clave entre nodos adyacentes
    private void moveKey(BTreeNode parent, int fromIndex, int toIndex) {
        BTreeNode fromChild = parent.getChildren()[fromIndex];
        BTreeNode toChild = parent.getChildren()[toIndex];

        if (fromIndex < toIndex) {
            toChild.getKeys()[toChild.getNumKeys()] = parent.getKeys()[fromIndex];
            parent.getKeys()[fromIndex] = fromChild.getKeys()[fromChild.getNumKeys() - 1];
            fromChild.setNumKeys(fromChild.getNumKeys() - 1);
        } else {
            for (int i = toChild.getNumKeys(); i > 0; i--) {
                toChild.getKeys()[i] = toChild.getKeys()[i - 1];
            }
            toChild.getKeys()[0] = parent.getKeys()[fromIndex];
            parent.getKeys()[fromIndex] = fromChild.getKeys()[fromChild.getNumKeys() - 1];
            fromChild.setNumKeys(fromChild.getNumKeys() - 1);
        }
    }

// Método que define el nuevo número mínimo de claves por nodo
    private int getMinKeys() {
        return (int) Math.floor((t - 1) / 2.0);
    }

// Método que define el número máximo de claves por nodo
    private int getMaxKeys() {
        return t - 1;
    }
