
package proyectoestru2;

import java.io.File;
import java.io.RandomAccessFile;

public class ManejadorArchivo {
    private RandomAccessFile archivoActual;
    private Metadata metadataActual;
    private java.util.ArrayList<Campo> listaCampos = new java.util.ArrayList<>();
    private AvailList availListActual = new AvailList();

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
            
            //Cargar los espacios disponibles en la memoria
            this.availListActual = new AvailList();
            this.availListActual.cargarDesdeArchivo(archivoActual, metadataActual);
            
            return true;
    
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean cerrarArchivo(){
        try {
            if (archivoActual != null) {
                if (metadataActual != null) {
                    availListActual.guardarEnArchivo(archivoActual, metadataActual);
                    metadataActual.guardar(archivoActual);
                }
                archivoActual.close();
                archivoActual = null;
                metadataActual = null;
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    //Escritura con best fit
    public boolean escribirRegistro(String registro) {
        try {
            if (this.archivoActual == null) {
                return false;
            }
            String cadenaEscribir = registro + "\n";
            int tamanoRequerido = cadenaEscribir.length();
            
            //Buscar un espacio vacio en la availlist
            NodoAvail espacioDisponible = availListActual.obtenerBestFit(tamanoRequerido);
            long posicionEscritura;
            
            if (espacioDisponible != null) {
                posicionEscritura = espacioDisponible.getOffset();
                //Fragmentacion interna, si el espacio vacio que se encunetra es mucho mas grande que el registro se divide el espacio sobrante y se vuelve a meter a la availlist
                int sobrante = espacioDisponible.getTamano() - tamanoRequerido;
                if (sobrante > 15) {
                    long nuevoOffsetSobrante = posicionEscritura + tamanoRequerido;
                    availListActual.Insertar(nuevoOffsetSobrante, sobrante);
                    tamanoRequerido = espacioDisponible.getTamano() - sobrante;
                }else{
                    //Si el sobrante es muy pequeno 
                    tamanoRequerido = espacioDisponible.getTamano(); 
                }
            }else{
                posicionEscritura = this.archivoActual.length();
                if (posicionEscritura < metadataActual.getOffsetInicio()) {
                    posicionEscritura = metadataActual.getOffsetInicio();
                }
            }
            this.archivoActual.seek(posicionEscritura);
            this.archivoActual.writeBytes(cadenaEscribir);
            
            //rellenar el sobrante con espacios
            int bytesEscritos = cadenaEscribir.length();
            if (bytesEscritos < tamanoRequerido) {
                this.archivoActual.seek(posicionEscritura + bytesEscritos - 1);
                for (int i = 0; i < (tamanoRequerido - bytesEscritos); i++) {
                    this.archivoActual.writeBytes(" ");
                }
                this.archivoActual.writeBytes("\n");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean borrarRegistroLogico(String llaveABorrar) {
        try {
            if (this.archivoActual == null) {
                return false;
            }
            this.archivoActual.seek(metadataActual.getOffsetInicio()); 
            String linea;
            long posicionRegistro;

            while ((linea = this.archivoActual.readLine()) != null) {
                posicionRegistro = this.archivoActual.getFilePointer() - (linea.length() + 1);

                if (!linea.equals("*") && linea.equals(llaveABorrar)) {
                    int tamanoEspacio = linea.length() + 1;
                    availListActual.Insertar(posicionRegistro, tamanoEspacio);
                    availListActual.guardarEnArchivo(archivoActual, metadataActual);
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

            this.archivoActual.seek(metadataActual.getOffsetInicio()); 
            String linea;
            long posicionRegistro;

            while ((linea = this.archivoActual.readLine()) != null) {
                posicionRegistro = this.archivoActual.getFilePointer() - (linea.length() + 1);

                if (!linea.startsWith("*") && linea.startsWith(llaveAModificar)) {
                    int tamanoMaximoSlot = linea.length();//Espacio sin el \n
                    int tamanoNuevoES = nuevoRegistroCompleto.length();
                    if (tamanoNuevoES <= tamanoMaximoSlot) {
                        this.archivoActual.seek(posicionRegistro);
                        this.archivoActual.writeBytes(nuevoRegistroCompleto);
                        
                        int diferencia = tamanoMaximoSlot - tamanoNuevoES;
                        for (int i = 0; i < diferencia; i++) {
                            this.archivoActual.writeBytes(" ");
                        }
                        this.archivoActual.writeBytes("\n");
                        return true;
                    }else{
                        borrarRegistroLogico(llaveAModificar);
                        escribirRegistro(nuevoRegistroCompleto);
                        return true;
                    }
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
