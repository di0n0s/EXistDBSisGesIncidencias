/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EXistDBSisGesIncidencias;

/**
 *
 * @author sfcar
 */
public class Historial {
    
    private String tipoEvento;
    private String fechaHora;
    private Empleado username;

    public Historial(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
    
    public Historial() {
        tipoEvento = "";
        fechaHora = "";
        username = new Empleado();
    }

    public Historial(String tipoEvento, String fechaHora, Empleado username) {
        this.tipoEvento = tipoEvento;
        this.fechaHora = fechaHora;
        this.username = username;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Empleado getUsername() {
        return username;
    }

    public void setUsername(Empleado username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Historial{" + "tipoEvento=" + tipoEvento + ", fechaHora=" + fechaHora + ", username=" + username + '}';
    }





    
}
