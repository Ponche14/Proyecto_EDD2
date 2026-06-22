    package proyectoestru2;

    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.io.Serializable;
    import java.util.LinkedList;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * 
     * author ghasb
     */
    public class BTree implements Serializable {

        private static final long serialVersionUID = 777L;

        private String fatherFilepath = "";
        private BTreeNode root;
        private int t;

        // -------------------------------------------------------------------------
        // Constructores
        // -------------------------------------------------------------------------

        public BTree(int t) {
            this.t = t;
            this.root = new BTreeNode(t, true);
        }

        public BTree() {
        }

        // -------------------------------------------------------------------------
        // Getters / Setters
        // -------------------------------------------------------------------------

        public String getFatherFilepath() {
            return fatherFilepath;
        }

        public void setFatherFilepath(String fatherFilepath) {
            this.fatherFilepath = fatherFilepath;
        }

        public BTreeNode getRoot() {
            return root;
        }

        public void setRoot(BTreeNode root) {
            this.root = root;
        }

        public int getT() {
            return t;
        }

        public void setT(int t) {
            this.t = t;
        }

        // -------------------------------------------------------------------------
        // Busqueda
        // -------------------------------------------------------------------------

        public BTreeNode search(Object key) {
            return (root == null) ? null : searchInNode(root, key);
        }
        
        // Igual que search(), pero devuelve la Llave completa (con su offset real
        // en el archivo) en vez del nodo. Esto es lo que permite que "Buscar Registro"
        // use el indice del Arbol B en vez de la tabla en memoria.
        public Llave searchLlave(Object key) {
            if (root == null) {
                return null;
            }
            BTreeNode node = searchInNode(root, key);
            if (node == null) {
                return null;
            }
            int index = node.binarySearch(key);
            if (index >= 0 && index < node.getNumKeys()) {
                return node.getKeys()[index];
            }
            return null;
        }

        private BTreeNode searchInNode(BTreeNode node, Object key) {
            int index = node.binarySearch(key);

            if (index >= 0 && index < node.getNumKeys() && node.getKeys()[index].getKey().equals(key)) { // valida si el nodo se ha encontrado
                return node;
            }

            if (node.isLeaf()) { // si el nodo no se encontro y es hoja entonces retorna null
                return null;
            }

            int childIndex = (index >= 0) ? index : -(index + 1); 
            return searchInNode(node.getChildren()[childIndex], key);// si la posicion del nodo deberia de existir pero no existe entonces esta validacion se asegura de que se va a buscar en un nodo hijo, de no ser un nodo hoja claro
        }

        // -------------------------------------------------------------------------
        // Insercion
        // -------------------------------------------------------------------------

        public void insert(Llave key) {
            BTreeNode r = root;
            if (r.getNumKeys() == 2 * t - 1) {
                BTreeNode s = new BTreeNode(t, false);
                s.getChildren()[0] = r;
                splitChild(s, 0);
                root = s;
            }
            insertNonFull(root, key);
        }

        private void insertNonFull(BTreeNode node, Llave key) {
            int position = node.binarySearch(key.getKey());
            if (position < 0) {
                position = -position - 1;
            }

            if (node.isLeaf()) {
                for (int j = node.getNumKeys(); j > position; j--) {
                    node.getKeys()[j] = node.getKeys()[j - 1];
                }
                node.getKeys()[position] = key;
                node.setNumKeys(node.getNumKeys() + 1);
            } else {
                if (node.getChildren()[position] != null
                        && node.getChildren()[position].getNumKeys() == t - 1) {
                    splitChild(node, position);
                    if (key.getKey().compareTo(node.getKeys()[position].getKey()) > 0) {
                        position++;
                    }
                }
                if (node.getChildren()[position] != null) {
                    insertNonFull(node.getChildren()[position], key);
                }
            }
        }

        // -------------------------------------------------------------------------
        // Division de nodo
        // -------------------------------------------------------------------------

        private void splitChild(BTreeNode parent, int index) {
            BTreeNode fullChild = parent.getChildren()[index];
            BTreeNode newChild = new BTreeNode(t, fullChild.isLeaf());

            int splitPoint = (t - 1) / 2;

            for (int i = 0; i < t - 1 - splitPoint - 1; i++) {
                newChild.getKeys()[i] = fullChild.getKeys()[i + splitPoint + 1];
            }

            if (!fullChild.isLeaf()) {
                for (int i = 0; i < t - splitPoint - 1; i++) {
                    newChild.getChildren()[i] = fullChild.getChildren()[i + splitPoint + 1];
                }
            }

            fullChild.setNumKeys(splitPoint);
            newChild.setNumKeys(t - 1 - splitPoint - 1);

            for (int i = parent.getNumKeys(); i > index; i--) {
                parent.getKeys()[i] = parent.getKeys()[i - 1];
            }
            parent.getKeys()[index] = fullChild.getKeys()[splitPoint];

            for (int i = parent.getNumKeys() + 1; i > index + 1; i--) {
                parent.getChildren()[i] = parent.getChildren()[i - 1];
            }
            parent.getChildren()[index + 1] = newChild;

            parent.setNumKeys(parent.getNumKeys() + 1);
        }

        public void crossTree(BTree btree2, File file, int[] campo1, int[] campo2) {
            if (root != null && btree2.getRoot() != null) {
                crossTreeNode(root, file, btree2, campo1, campo2);
            } else {
                System.out.println("Uno o ambos arboles estan vacios.");
            }
        }

        private void crossTreeNode(BTreeNode node,
                               File file,
                               BTree btree2,
                               int[] campo1,
                               int[] campo2) {

        if (node == null) {
            return;
        }

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file, true))) {

            for (int i = 0; i < node.getNumKeys(); i++) {

                Llave llave1 = node.getKeys()[i];

                if (llave1 == null) {
                    continue;
                }

                BTreeNode encontrado = btree2.search(llave1.getKey());

                if (encontrado == null) {
                    continue;
                }

                int pos = encontrado.binarySearch(llave1.getKey());

                if (pos < 0) {
                    continue;
                }

                Llave llave2 = encontrado.getKeys()[pos];

                bf.write(
                        llave1.getKey() + " | offset1="
                        + llave1.getOffset()
                        + " | offset2="
                        + llave2.getOffset());

                bf.newLine();
            }

        } catch (IOException ex) {

            Logger.getLogger(BTree.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i <= node.getNumKeys(); i++) {

            if (node.getChildren()[i] != null) {

                crossTreeNode(
                        node.getChildren()[i],
                        file,
                        btree2,
                        campo1,
                        campo2);
            }
        }
    }

        // -------------------------------------------------------------------------
        // Impresion
        // -------------------------------------------------------------------------

        public void printTree() {
            if (root != null) {
                printTreeNode(root, "", true);
            } else {
                System.out.println("El arbol esta vacio.");
            }
        }

        private void printTreeNode(BTreeNode node, String indent, boolean isLeft) {
            if (node == null) return;

            System.out.println(indent + (isLeft ? "+-- " : "\\-- ") + node.toString());

            for (int i = 0; i <= node.getNumKeys(); i++) {
                if (node.getChildren()[i] != null) {
                    printTreeNode(node.getChildren()[i],
                            indent + (isLeft ? "|   " : "    "),
                            i < node.getNumKeys());
                }
            }
        }

        // -------------------------------------------------------------------------
        // Eliminacion
        // -------------------------------------------------------------------------

        public void delete(Comparable key) {
            if (root == null) {
                System.out.println("El arbol esta vacio.");
                return;
            }

            deleteKey(root, key);

            if (root.getNumKeys() == 0 && !root.isLeaf()) {
                root = root.getChildren()[0];
            }
        }

        @SuppressWarnings("unchecked")
        private void deleteKey(BTreeNode node, Comparable key) {
            int position = node.binarySearch(key);

            if (position >= 0 && position < node.getNumKeys()
                    && node.getKeys()[position].getKey().compareTo(key) == 0) {

                if (node.isLeaf()) {
                    removeKey(node, position);
                } else {
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
                if (node.isLeaf()) {
                    System.out.println("La clave " + key + " no existe en el arbol.");
                    return;
                }

                if (position < 0) {
                    position = -(position + 1);
                }

                boolean lastChild = (position == node.getNumKeys());
                BTreeNode child = node.getChildren()[position];

                if (child.getNumKeys() < getMinKeys()) {
                    fixChild(node, position);
                }

                if (lastChild && position > node.getNumKeys()) {
                    deleteKey(node.getChildren()[position - 1], key);
                } else {
                    deleteKey(node.getChildren()[position], key);
                }
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
}
