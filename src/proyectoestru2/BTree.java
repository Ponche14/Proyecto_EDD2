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
            if (r.getNumKeys() == t) {//si la raiz esta llena o en overflow entonces se hace un split 
                BTreeNode s = new BTreeNode(t, false);
                s.getChildren()[0] = r;//asignamos la raiz como el hijo de el nuevo nodo
                splitChild(s, 0);// split para que s quede como el padre de ambos
                root = s;
            }
            insertNonFull(root, key);//llamamos el metodo para insertar en un nodo que no esta lleno
        }

        private void insertNonFull(BTreeNode node, Llave key) {
            int indice = node.getNumKeys()-1;//creamos el indice para uso en el metodo
            if (node.isLeaf()) {// si el nodo es hoja empezamos con la insercion normal
                // codigo asisitdo por ai, no lograba encontrar la solucion a porque no podia comparar la llave con otra llave
                // utilize gemini y el prompt fue: "porque esta linea me esta tirando error si los dos valores retornados por los metodos son comparable"
                // de gemini solo saque las condiciones para los whiles
                while (indice >= 0 && key.getKey().compareTo(node.getKeys()[indice].getKey()) < 0) {//recorremos las llaves del nodo para encontrar
                    node.getKeys()[indice+1] = node.getKeys()[indice];// la posicion correcta de la nueva llave
                    indice--;
                }
                node.getKeys()[indice+1] = key;//asignamos la nueva llave una vez que encontremos la posicion correcta
                node.setNumKeys(node.getNumKeys()+1);//incrementamos la cantidad de llaves del nodo hoja
            } else {//si no es hoja entonces recorremos el nodo para encontrar el indice de los hijos a donde vamos a meter el nodo
                while (indice >= 0 && key.getKey().compareTo(node.getKeys()[indice].getKey())< 0) {
                    indice--;
                }
                indice++;//se asegura de que empezamos en el primer nodo
                if (node.getChildren()[indice].getNumKeys()==t) {
                    splitChild(node, indice);// si el nodo esta en overflow entonces se hace un split
                    if (key.getKey().compareTo(node.getKeys()[indice].getKey())>0) {//Si el hijo en el que estamos no es el correcto, recorremos el
                        indice++;//el nodo de nuevo para encontrar una posicion mas optima
                    }
                }
                insertNonFull(node.getChildren()[indice], key);//se vuelve a llamar el metodo hasta encontrar una hoja adonde iria la nueva llave
            }
            // codigo creado con ia
//            int position = node.binarySearch(key.getKey());
//            if (position < 0) {
//                position = -position - 1;
//            }
//
//            if (node.isLeaf()) {
//                for (int j = node.getNumKeys(); j > position; j--) {
//                    node.getKeys()[j] = node.getKeys()[j - 1];
//                }
//                node.getKeys()[position] = key;
//                node.setNumKeys(node.getNumKeys() + 1);
//            } else {
//                if (node.getChildren()[position] != null && node.getChildren()[position].getNumKeys() == t) {
//                    splitChild(node, position);
//                    if (key.getKey().compareTo(node.getKeys()[position].getKey()) > 0) {
//                        position++;
//                    }
//                }
//                if (node.getChildren()[position] != null) {
//                    insertNonFull(node.getChildren()[position], key);
//                }
//            }
        }

        // -------------------------------------------------------------------------
        // Division de nodo
        // -------------------------------------------------------------------------

        private void splitChild(BTreeNode parent, int index) {
            BTreeNode fullChild = parent.getChildren()[index]; // agarramos el hijo al que se le va a hacer split
            BTreeNode newChild = new BTreeNode(t, fullChild.isLeaf());//creamos un nuevo hijo hoja para ingresar los valores que se van a separar

            int splitPoint = t/2;// el lugar donde se haria el split seria la mitad del nodo
            
            Llave promotedKey = fullChild.getKeys()[splitPoint];// la llave que seria promovida al padre
            int rightKeys = fullChild.getNumKeys() - splitPoint -1;//La cantidad de llaves que vamos a mover para el hijo nuevo
            
            for (int i = 0; i <= rightKeys; i++) {
                newChild.getKeys()[i] = fullChild.getKeys()[splitPoint+1+i];//movemos las llaves de el hijo lleno a la nueva llave
                fullChild.getKeys()[i+splitPoint+1] = null;//se eliminan las llaves de el nodo que estaba lleno
            }
            
            if (!fullChild.isLeaf()) {//verifica si el nodo que estaba lleno es hoja, si no lo es
                for (int i = 0; i <= rightKeys;i++) {
                    newChild.getChildren()[i] = fullChild.getChildren()[splitPoint+1+i];//movemos tambien los hijos como se hizo con las llaves
                    fullChild.getChildren()[i+splitPoint+1] = null;
                }
            }
            newChild.setNumKeys(rightKeys);//asignacion de la cantidad de llaves
            fullChild.setNumKeys(splitPoint);//asignacion de la cantidad de llaves que le quedan
            for (int i = parent.getNumKeys(); i > index; i--) {//recorremos el padre para movilizar las llaves de forma que el espacio para
                parent.getKeys()[i]=parent.getKeys()[i-1];//que la nueva posicion de la llave quede vacia
            }
            parent.getKeys()[index]=promotedKey;//asignamos la llave que se promovio al padre
            for (int i = parent.getNumKeys()+1;i>index+1;i--) {//recorremos el padre para organizar los hijos de manera
                parent.getChildren()[i]=parent.getChildren()[i-1];//que el espacio para el newChild quede en su lugar correcto
            }
            parent.getChildren()[index+1] = newChild;//se asigna el newChild a su posicion
            parent.setNumKeys(parent.getNumKeys()+1);//incrementamos la cantidad de llaves que tiene el arbol
// El codigo a continuacion era codigo que se habia hecho con el 2*t-1 con la ia, dejandolo aqui como evidencia de que se cambio
//            for (int i = 0; i < t - 1 - splitPoint - 1; i++) {
//                newChild.getKeys()[i] = fullChild.getKeys()[i + splitPoint + 1];
//            }
//
//            if (!fullChild.isLeaf()) {
//                for (int i = 0; i < t - splitPoint - 1; i++) {
//                    newChild.getChildren()[i] = fullChild.getChildren()[i + splitPoint + 1];
//                }
//            }
//
//            fullChild.setNumKeys(splitPoint);
//            newChild.setNumKeys(t - 1 - splitPoint - 1);
//
//            for (int i = parent.getNumKeys(); i > index; i--) {
//                parent.getKeys()[i] = parent.getKeys()[i - 1];
//            }
//            parent.getKeys()[index] = fullChild.getKeys()[splitPoint];
//
//            for (int i = parent.getNumKeys() + 1; i > index + 1; i--) {
//                parent.getChildren()[i] = parent.getChildren()[i - 1];
//            }
//            parent.getChildren()[index + 1] = newChild;
//
//            parent.setNumKeys(parent.getNumKeys() + 1);
        }

        public void crossTree(BTree btree2, File file) {
            if (root != null && btree2.getRoot() != null) {
                crossTreeNode(root, file, btree2);
            } else {
                System.out.println("Uno o ambos arboles estan vacios.");
            }
        }

        private void crossTreeNode(BTreeNode node, File file, BTree btree2) {
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
            Logger.getLogger(BTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i <= node.getNumKeys(); i++) {
            if (node.getChildren()[i] != null) {
                crossTreeNode(node.getChildren()[i], file, btree2);
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
            if (root == null) {//valida que exista un arbol
                System.out.println("El arbol esta vacio.");
                return;
            }

            deleteKey(root, key);//en caso de que si exista un arbol se llama el metodo para borrar una llave

            if (root.getNumKeys() == 0 && !root.isLeaf()) {//si la llave se encuentra en la raiz y queda vacia el primer hijo toma su lugar
                root = root.getChildren()[0];
            }
        }

        @SuppressWarnings("unchecked")
        private void deleteKey(BTreeNode node, Comparable key) {
            int position = node.binarySearch(key);//se llama el metodo de busqueda binaria para encontrar la posicion de la lalve

            if (position >= 0 && position < node.getNumKeys() && node.getKeys()[position].getKey().compareTo(key) == 0) {
                //si la posicion esta adentro del arreglo de llaves
                if (node.isLeaf()) {//validamos is es hoja
                    removeKey(node, position);// si es hoja se llama el metodo que borra la llave
                } else {
                    if (node.getChildren()[position].getNumKeys() >= getMinKeys()) {//si el nodo no es hoja y nos tenemos una posicion valida entonces
                        Llave predecessorKey = findPredecessorKey(node, position);//verificamos que el nodo anterior no este en underflow para
                        node.getKeys()[position] = predecessorKey;//pedir una llave de el, -]
                        deleteKey(node.getChildren()[position], predecessorKey.getKey());//se vuelve a llamar el metodo para buscar en el hijo
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
        return (int) Math.floor((t) / 2.0);
    }

    // Método que define el número máximo de claves por nodo
    private int getMaxKeys() {
        return t;
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
            child = child.getChildren()[child.getNumKeys()];//recorre un lado entero del arbol hasta encontrar un nodo hoja
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
        BTreeNode left = parent.getChildren()[index];
        BTreeNode right = parent.getChildren()[index+1];
        int leftNumKeys = left.getNumKeys();
        left.getKeys()[leftNumKeys] = parent.getKeys()[index];
        parent.getKeys()[index] = null;
        for (int i = 0; i < right.getNumKeys();i++) {
            left.getKeys()[leftNumKeys+i+1] = right.getKeys()[i];
        }
        if (!left.isLeaf()) {
            for (int i = 0; i <= right.getNumKeys();i++) {
                left.getChildren()[leftNumKeys+i+1] = right.getChildren()[i];
            }
        }
        left.setNumKeys(leftNumKeys+1+right.getNumKeys());
        for (int i = index; i < parent.getNumKeys(); i++){
            parent.getKeys()[i] = parent.getKeys()[i+1];
        }
        for (int i = index+1; i < parent.getNumKeys(); i++) {
            parent.getChildren()[i] = parent.getChildren()[i+1];
        }
        parent.setNumKeys(parent.getNumKeys()-1);
        // mismo caso que en split, codigo hecho con ia que fue reemplazado
//        BTreeNode leftChild = parent.getChildren()[index];
//        BTreeNode rightChild = parent.getChildren()[index + 1];
//
//        // Mueve la clave del padre al nodo izquierdo
//        leftChild.getKeys()[leftChild.getNumKeys()] = parent.getKeys()[index];
//        for (int i = 0; i < rightChild.getNumKeys(); i++) {
//            leftChild.getKeys()[leftChild.getNumKeys() + 1 + i] = rightChild.getKeys()[i];
//        }
//
//        if (!leftChild.isLeaf()) {
//            for (int i = 0; i <= rightChild.getNumKeys(); i++) {
//                leftChild.getChildren()[leftChild.getNumKeys() + 1 + i] = rightChild.getChildren()[i];
//            }
//        }
//
//        // Ajusta el número de claves
//        leftChild.setNumKeys(leftChild.getNumKeys() + 1 + rightChild.getNumKeys());
//
//        // Mueve las claves del padre para llenar el vacío
//        for (int i = index; i < parent.getNumKeys() - 1; i++) {
//            parent.getKeys()[i] = parent.getKeys()[i + 1];
//        }
//
//        // Mueve los hijos del padre para llenar el vacío
//        for (int i = index + 1; i < parent.getNumKeys(); i++) {
//            parent.getChildren()[i] = parent.getChildren()[i + 1];
//        }
//
//        parent.setNumKeys(parent.getNumKeys() - 1);
    }

// Arregla un hijo para que tenga al menos el mínimo de claves
    private void fixChild(BTreeNode parent, int index) {
        BTreeNode child = parent.getChildren()[index];
        if (child.getNumKeys() >= getMinKeys()) {
            return;
        }
        if (index > 0 && parent.getChildren()[index-1].getNumKeys() > getMinKeys()) {//getprev
            BTreeNode sibling = parent.getChildren()[index-1];
            for (int i = child.getNumKeys(); i > 0; i--) {
                child.getKeys()[i] = child.getKeys()[i-1];
            }
            child.getKeys()[0] = parent.getKeys()[index-1];
            if (!child.isLeaf()) {
                for (int i = child.getNumKeys()+1;i>0;i--) {
                    child.getChildren()[i] = child.getChildren()[i-1];
                }
                child.getChildren()[0] = sibling.getChildren()[sibling.getNumKeys()];
            }
            parent.getKeys()[index-1] = sibling.getKeys()[sibling.getNumKeys()-1];
            sibling.setNumKeys(sibling.getNumKeys()-1);
            child.setNumKeys(child.getNumKeys()+1);
            return;
        }
        if (index < parent.getNumKeys() && parent.getChildren()[index+1].getNumKeys()>getMinKeys()) {//getnext
            BTreeNode sibling = parent.getChildren()[index+1];
            child.getKeys()[child.getNumKeys()] = parent.getKeys()[index];
            if (!child.isLeaf()) {
                child.getChildren()[child.getNumKeys()+1] = sibling.getChildren()[0];
            }
            parent.getKeys()[index] = sibling.getKeys()[0];
            for (int i = 0; i < sibling.getNumKeys();i++) {
                sibling.getKeys()[i] = sibling.getKeys()[i+1];
            }
            if (!sibling.isLeaf()) {
                for (int i = 0; i < sibling.getNumKeys();i++) {
                    sibling.getChildren()[i] = sibling.getChildren()[i+1];
                }
            }
            sibling.setNumKeys(sibling.getNumKeys()-1);
            child.setNumKeys(child.getNumKeys()+1);
            return;
        }
        if (index < parent.getNumKeys()) {
            mergeNodes(parent, index);
        } else {
            mergeNodes(parent, index-1);
        }
//        BTreeNode child = parent.getChildren()[index];
//
//        if (index > 0 && parent.getChildren()[index - 1].getNumKeys() >= getMinKeys()) {
//            moveKey(parent, index - 1, index);
//        } else if (index < parent.getNumKeys() && parent.getChildren()[index + 1].getNumKeys() >= getMinKeys()) {
//            moveKey(parent, index + 1, index);
//        } else {
//            if (index < parent.getNumKeys()) {
//                mergeNodes(parent, index);
//            } else {
//                mergeNodes(parent, index - 1);
//            }
//        }
        }
    }