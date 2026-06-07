/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectoestru2;

/**
 *
 * @author ghasb
 */
public class Nodo {
    Campo campo;
    Nodo next;
    Nodo prev;
    public Nodo(Campo campo, Nodo next, Nodo prev){
        this.campo = campo;
        this.next = next;
        this.prev = prev;
    }
    public Campo getCampo(){
        return this.campo;
    }
    public Nodo getNext(){
        return this.next;
    }
    public Nodo getPrev(){
        return this.prev;
    }
    public void setCampo(Campo campo){
        this.campo = campo;
    }
    public void setNext(Nodo next){
        this.next = next;
    }
    public void setPrev(Nodo prev){
        this.prev = prev;
    }
}
