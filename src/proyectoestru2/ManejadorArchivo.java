
package proyectoestru2;

import java.io.File;
import java.io.RandomAccessFile;

public class ManejadorArchivo {
    private RandomAccessFile archivoActual;
    private Metadata metadataActual;
    private java.util.ArrayList<Campo> listaCampos = new java.util.ArrayList<>();

    public ManejadorArchivo() {
        this.archivoActual = null;
        this.metadataActual = null;
    }
    
    public boolean crearArchivo(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            this.archivoActual = new RandomAccessFile(f, "rw");
            this.metadataActual = new Metadata();
            this.metadataActual.guardar(archivoActual);
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean abrirArchivo(String path) {
        try {
            if (archivoActual != null) {
                cerrarArchivo();
            }
            
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
            this.archivoActual = new RandomAccessFile(f, "rw");
            this.metadataActual = new Metadata();
            this.metadataActual.cargar(archivoActual);
            return true;
    
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean cerrarArchivo(){
        try {
            if (archivoActual != null) {
                if (metadataActual != null) {
                    metadataActual.guardar(archivoActual);
                }
                archivoActual.close();
                archivoActual = null;
                metadataActual = null;
                listaCampos.clear();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean escribirRegistro(String registro) {
        try {
            if (this.archivoActual != null) {
                this.archivoActual.seek(this.archivoActual.length());
                this.archivoActual.writeBytes(registro + "\n");
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean borrarRegistroLogico(String llaveABorrar) {
        try {
            if (this.archivoActual == null) return false;

            this.archivoActual.seek(0); 
            String linea;
            long posicionRegistro;

            while ((linea = this.archivoActual.readLine()) != null) {
                posicionRegistro = this.archivoActual.getFilePointer() - (linea.length() + 1);

                if (!linea.startsWith("*") && linea.startsWith(llaveABorrar)) {
                    this.archivoActual.seek(posicionRegistro);
                    this.archivoActual.writeBytes("*"); 
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean modificarRegistroFisico(String llaveAModificar, String nuevoRegistroCompleto) {
        try {
            if (this.archivoActual == null) return false;

            this.archivoActual.seek(0); 
            String linea;
            long posicionRegistro;

            while ((linea = this.archivoActual.readLine()) != null) {
                posicionRegistro = this.archivoActual.getFilePointer() - (linea.length() + 1);

                // Buscamos que no esté borrado y que coincida con la llave primaria
                if (!linea.startsWith("*") && linea.startsWith(llaveAModificar)) {
                    this.archivoActual.seek(posicionRegistro);

                    // Agregamos un salto de línea al final para mantener la estructura limpia
                    this.archivoActual.writeBytes(nuevoRegistroCompleto + "\n"); 
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public RandomAccessFile getArchivoActual() {
        return archivoActual;
    }

    public Metadata getMetadataActual() {
        return metadataActual;
    }
    
    public boolean isArchivoAbierto() {
        return archivoActual != null;
    }

    public java.util.ArrayList<Campo> getListaCampos() {
        return listaCampos;
    }
}
