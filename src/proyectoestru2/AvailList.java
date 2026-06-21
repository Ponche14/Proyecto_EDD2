
package proyectoestru2;

public class AvailList {
    private NodoAvail head;
    
    public AvailList() {
        this.head = null;
    }
    //Insercion ordenada de menor a mayor
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
        while(actual != null){
            if (actual.getTamano() >= tamanoRequerido) {
                desvincular(actual);
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null; // No hay un espacio donde quepa el registro
    }
    //Remueve el nodo de la lista en memoria cuando va a ser reutilizado
    public void desvincular(NodoAvail nodo){
        if (nodo == head) {
            head = nodo.getSiguiente();
            if (head != null) {
                head.setAnterior(null);
            }
        }else{
            if (nodo.getAnterior() != null) {
                nodo.getAnterior().setSiguiente(nodo.getSiguiente());
            }
            if (nodo.getSiguiente() != null) {
                nodo.getSiguiente().setAnterior(nodo.getAnterior());
            }
        }
        nodo.setSiguiente(null);
        nodo.setAnterior(null);
    }
    
    public void guardarEnArchivo(java.io.RandomAccessFile file, Metadata meta) throws java.io.IOException {
        if (head == null) {
            meta.setHeadAvaiList(-1);
            return;
        }
        meta.setHeadAvaiList(head.getOffset());
        NodoAvail actual = head;
        while(actual != null){
            file.seek(actual.getOffset());
            long sigOffset = (actual.getSiguiente() != null ? actual.getSiguiente().getOffset() : -1);
            //*[SiguienteOffset] [Tamaño]
            String infoEliminada = "*" + sigOffset  + "," + actual.getTamano();
            file.writeBytes(infoEliminada);
            
            //Calcular cuantos bytes quedan libres en el slot
            int bytesEscritos = infoEliminada.length();
            int restante = actual.getTamano() - bytesEscritos;
            
            // El tamano del slot incluye el salto de linea final, asi que dejamos
            // un byte reservado para el "\n" y solo rellenamos con espacios el resto
            // (igual que ya se hace en escribirRegistro). Antes nunca se escribia
            // el "\n" y eso corrompia la lectura secuencial de todo lo que viniera
            // despues de un espacio liberado.
            if (restante > 0) {
                for (int i = 0; i < restante - 1; i++) {
                    file.writeByte(' ');
                }
                file.writeBytes("\n");
            }
            actual = actual.getSiguiente();
        }
    }
    
    public void cargarDesdeArchivo(java.io.RandomAccessFile file, Metadata meta) throws java.io.IOException {
        this.head = null;
        long siguienteOffset = meta.getHeadAvaiList();
        
        while (siguienteOffset != -1){
            file.seek(siguienteOffset);
            String linea = file.readLine();
            if (linea != null && linea.startsWith("*")) {
                //Se eliminan los espacios finales que se usaron de relleno
                String limpia = linea.trim();
                String contenido = limpia.substring(1);//Elimina el *
                String[] partes = contenido.split(",");
                long apuntadorSiguiente = Long.parseLong(partes[0]);
                int tamanoEspacio = Integer.parseInt(partes[1]);
                
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
