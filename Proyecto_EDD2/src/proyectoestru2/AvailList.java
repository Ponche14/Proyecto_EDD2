
package proyectoestru2;

public class AvailList {
    private NodoAvail head;

    public AvailList() {
        this.head = null;
    }
    //Aun falta el metodo de best fit, eliminar, y completar insercion
    public void Insertar(long offset, int tamano){
        NodoAvail nodo = new NodoAvail(offset, tamano);
        // Si el archivo estaba vacio
        if (head == null) {
            head = nodo;
            return;
        }
        
        //El nodo es mas pequeño que la cabeza
        if (tamano < head.getTamano()) {
            nodo.setSiguiente(head);
            head.setAnterior(nodo);
            head = nodo;
            return;
        }
        
        //Buscar la posicion correcta en medio o final
        NodoAvail actual = head;
        while (actual.getSiguiente() != null && actual.getSiguiente().getTamano() < tamano) {
            actual = actual.getSiguiente();
        }
        
        nodo.setSiguiente(actual.getSiguiente());
        if (actual.getSiguiente() != null) {
            actual.getSiguiente().setAnterior(nodo);
        }
        actual.setSiguiente(nodo);
        nodo.setAnterior(actual);
    }
    
    public NodoAvail obtenerBestFit(int tamanoRequerido){
        NodoAvail actual = head;
        //La lista ya esta ordenada a menor a mayor, el primer nodo que sea mayor o igual es el best fit
        while(actual != null){
            if (actual.getTamano() >= tamanoRequerido) {
                //Desvincular el nodo
                if (actual == head) {
                    head = actual.getSiguiente();
                    if (head != null) {
                        head.setAnterior(null);
                    }
                }else{
                    //Si esta en medio o final
                    if (actual.getAnterior() != null) {
                        actual.getAnterior().setSiguiente(actual.getSiguiente());
                    }
                    if (actual.getSiguiente() != null) {
                        actual.getSiguiente().setAnterior(actual.getAnterior());
                    }
                }
                //Aislar el nodo
                actual.setSiguiente(null);
                actual.setAnterior(null);
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }
    
    //RECONSTRUCCION
    public void guardarEnArchivo(java.io.RandomAccessFile file, Metadata meta) throws java.io.IOException {
        if (head == null) {
            meta.setHeadAvaiList(-1);
            return;
        }
        meta.setHeadAvaiList(head.getOffset());
        NodoAvail actual = head;
        while(actual != null){
            file.seek(actual.getOffset());
            
            file.writeChar('*');
            if (actual.getSiguiente() != null) {
                file.writeLong(actual.getSiguiente().getOffset());
            }else{
                file.writeLong(-1);
            }
            file.writeInt(actual.getTamano());
            actual = actual.getSiguiente();
        }
    }
    
    public void cargarDesdeArchivo(java.io.RandomAccessFile file, Metadata meta) throws java.io.IOException {
        this.head = null;
        long siguienteOffset = meta.getHeadAvaiList();
        
        while (siguienteOffset != -1){
            file.seek(siguienteOffset);
            
            char borrado = file.readChar();
            if (borrado == '*') {
                long apuntadorSiguiente = file.readLong();
                int tamanoEspacio = file.readInt();
                
                this.Insertar(siguienteOffset, tamanoEspacio);
                siguienteOffset = apuntadorSiguiente;
            }else{
                break;
            }
        }
    }
 
    public NodoAvail getHead() {
        return head;
    }

    public void setHead(NodoAvail head) {
        this.head = head;
    }
    
    
}
