
package proyectoestru2;

import java.util.ArrayList;

public class Metadata {
    private long headAvaiList;
    private long rootArbolB;
    private int posLlavePrimaria;
    private int posLlaveSecundaria;
    private ArrayList<Campo> campos;
    private long offsetInicio;
    public static final long TAMANIO_RESERVADO = 500;

    public Metadata() {
        this.headAvaiList = -1;
        this.rootArbolB = -1;
        this.posLlavePrimaria = -1;
        this.posLlaveSecundaria = -1;
        this.campos = new ArrayList<>();
        this.offsetInicio = -1;
    }
    
    public void guardar(java.io.RandomAccessFile file) throws java.io.IOException{
        file.seek(0);
        file.writeLong(headAvaiList);
        file.writeLong(rootArbolB);
        file.writeInt(posLlavePrimaria);
        file.writeInt(posLlaveSecundaria);
        file.writeInt(campos.size());
        for (Campo c : campos) {
            file.writeUTF(c.getNombre());
            file.writeChar(c.getTipo());
            file.writeInt(c.getLongitud());
            file.writeBoolean(c.isEsPrimaria());
            file.writeBoolean(c.isEsSecundaria());
        }
        
        long bytesEscritos = file.getFilePointer();
        if (bytesEscritos > TAMANIO_RESERVADO) {
            throw new java.io.IOException("La metadata excede el espacio reservado "  + TAMANIO_RESERVADO);
        }
        
        // Rellenar el resto del bloque reservado para no dejar basura del registro anterior
        for (long i = bytesEscritos; i < TAMANIO_RESERVADO; i++) {
            file.writeByte('#');
        }
        
        this.offsetInicio = TAMANIO_RESERVADO;
    }
    
    public void cargar(java.io.RandomAccessFile file) throws java.io.IOException{
        file.seek(0);
        this.headAvaiList = file.readLong();
        this.rootArbolB = file.readLong();
        this.posLlavePrimaria = file.readInt();
        this.posLlaveSecundaria = file.readInt();
        
        int totalCampos = file.readInt();
        this.campos.clear();
        for (int i = 0; i < totalCampos; i++) {
            String nombre = file.readUTF();
            char tipo = file.readChar();
            int longitud = file.readInt();
            boolean esPrimaria = file.readBoolean();
            boolean esSecundaria = file.readBoolean();
            this.campos.add(new Campo(nombre, tipo, longitud, esPrimaria, esSecundaria));
        }
        
        this.offsetInicio = TAMANIO_RESERVADO;
    }

    public long getOffsetInicio() {
        return offsetInicio;
    }
    
    public long getHeadAvaiList() {
        return headAvaiList;
    }
    
    public long getRootArbolB() {
        return rootArbolB;
    }

    public int getPosLlavePrimaria() {
        return posLlavePrimaria;
    }

    public int getPosLlaveSecundaria() {
        return posLlaveSecundaria;
    }

    public ArrayList<Campo> getCampos() {
        return campos;
    }

    public void setHeadAvaiList(long headAvaiList) {
        this.headAvaiList = headAvaiList;
    }
    
    public void setRootArbolB(long rootArbolB) {
        this.rootArbolB = rootArbolB;
    }

    public void setPosLlavePrimaria(int posLlavePrimaria) {
        this.posLlavePrimaria = posLlavePrimaria;
    }

    public void setPosLlaveSecundaria(int posLlaveSecundaria) {
        this.posLlaveSecundaria = posLlaveSecundaria;
    }

    public void setCampos(ArrayList<Campo> campos) {
        this.campos = campos;
    }
    
}
