
package proyectoestru2;

import java.util.ArrayList;

public class Metadata {
    private long headAvaiList;
    private long rootArbolB;
    private int posLlavePrimaria;
    private int posLlaveSecundaria;
    private ArrayList<Campo> campos;
    private long offsetInicio;

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
        file.writeLong(headAvaiList); //8
        file.writeLong(rootArbolB); //8 
        file.writeInt(posLlavePrimaria); //4
        file.writeInt(posLlaveSecundaria); //4
        file.writeInt(campos.size()); //4
        
        for (Campo c : campos) {
            file.writeUTF(c.getNombre());
            file.writeChar(c.getTipo());
            file.writeInt(c.getLongitud());
            file.writeBoolean(c.isEsPrimaria());
            file.writeBoolean(c.isEsSecundaria());
        }
        this.offsetInicio = file.getFilePointer();
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
            
            Campo c = new Campo(nombre, tipo, longitud, esPrimaria, esSecundaria);
            this.campos.add(c);
        }
        this.offsetInicio = file.getFilePointer();
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
