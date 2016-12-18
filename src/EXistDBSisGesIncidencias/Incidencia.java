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
public class Incidencia {
    
    private int idIncidencia;
    private String fechaHora;
    private Empleado origen;
    private Empleado destino;
    private String tipo;
    private String detalle;

    public Incidencia() {
        idIncidencia = 0;
        fechaHora = "";
        origen = new Empleado();
        destino = new Empleado();
        tipo = "";
        detalle = "";
    }

    public Incidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }
    
    

    public Incidencia(int idIncidencia, String fechaHora, Empleado origen, Empleado destino, String tipo, String detalle) {
        this.idIncidencia = idIncidencia;
        this.fechaHora = fechaHora;
        this.origen = origen;
        this.destino = destino;
        this.tipo = tipo;
        this.detalle = detalle;
    }

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }
    
    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Empleado getOrigen() {
        return origen;
    }

    public void setOrigen(Empleado origen) {
        this.origen = origen;
    }

    public Empleado getDestino() {
        return destino;
    }

    public void setDestino(Empleado destino) {
        this.destino = destino;
    }
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "Incidencia{" + "idIncidencia=" + idIncidencia + ", fechaHora=" + fechaHora + ", origen=" + origen + ", destino=" + destino + ", tipo=" + tipo + ", detalle=" + detalle + '}';
    }

    


    
}
