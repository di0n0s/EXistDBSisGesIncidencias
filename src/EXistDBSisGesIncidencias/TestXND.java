package EXistDBSisGesIncidencias;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmldb.api.base.XMLDBException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sfcar
 */
public class TestXND {
    public static void main (String[] args){
        Empleado e1 = new Empleado("mnavarro","nat174","Mario Navarro López","+34687358998");
        Empleado e2 = new Empleado("ppujol","nyt287","Pablo Pujol Marín","+34645222174");
        Empleado e3 = new Empleado("mnavarro", "mab918", "Manuel Navarro León", "+34632384574");
        Empleado e4 = new Empleado("llugo", "ags634" , "Laura Lugo Manzaneda", "+34698777454");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss:SS"); //Indicamos como queremos que se muestre la fecha y hora
        Date ahoramismo = new Date(); //Creamos un new Date para la fecha y hora actual
        String fechaHoraenTexto = sdf.format(ahoramismo); //Creamos un string indicando al sdf que mofique el formato del dato                    
        Incidencia i1 = new Incidencia(1, fechaHoraenTexto, e1, e2, "Urgente", "La impresora no tiene tóner");
        Incidencia i2 = new Incidencia(2, sdf.format(new Date()), e2, e1, "Urgente", "La impresora no tiene tóner");
        
        Historial h1 = new Historial("I");

        try {
            //Creamos el gestor y establecemos conexión
            IncidenciasXND gestor = new IncidenciasXND();
            System.out.println("Conexión establecida");
            
            //4A. Insertar empleado
            System.out.println("---------------------");
            System.out.println("4A. Insertar un empleado nuevo en la B.D.");
            System.out.println("Insertando Empleado "+e1.getUsername()+" ...");
            if (gestor.insertarEmpleado(e1)){
                System.out.println("Empleado insertado!");
            } else System.out.println("El empleado no se ha podido insertar");
            System.out.println("---------------------");
        
            gestor.insertarEmpleado(e2);
            
            //4B. Validar empleado
            System.out.println("---------------------");
            System.out.println("4B. Validar la entrada de un empleado (suministrando usuario y contraseña)");
            System.out.println("Validando Empleado "+e1.getUsername()+" ...");
            gestor.validarEmpleado(e1);
            System.out.println("---------------------");
            
            //4C. Prueba de modificar empleado existente
            System.out.println("-----------------------");
            System.out.println("4C. Modificar empleado existente");
            System.out.println("Modificando Empleado "+e1.getUsername()+" ...");
            if(gestor.modificarEmpleado(e1, e3)){
                System.out.println("Empleado Modificado");
                System.out.println("Los datos del Empleado se han actualizado a: "+e3);
            } else System.out.println("El Empleado no se ha modificado");
            System.out.println("-----------------------");
            
            //Prueba de modificar password
            System.out.println("-----------------------");
            System.out.println("4D. Cambiar la contraseña de un Empleado existente");
            System.out.println("Modificando Password de "+e1.getUsername()+" ...");
            if(gestor.modificarPassword(e3, e1)){
                System.out.println("Password Modificado");
                System.out.println("El Password de "+e3.getUsername()+" se han actualizado a: "+e1.getPassword());
            } else System.out.println("El Password no se ha modificado");
            System.out.println("-----------------------");
            
            gestor.insertarEmpleado(e4);
            
            //Eliminar empleado
            System.out.println("---------------------");
            System.out.println("4E. Eliminar un Empleado existente");
            System.out.println("Eliminando Empleado "+e4.getUsername()+" ...");
            if (gestor.eliminarEmpleado(e4)){
                System.out.println("Empleado Eliminado!");
            } else System.out.println("El empleado no se ha podido eliminar");
            System.out.println("---------------------");    
            
            gestor.insertarIncidencia(i2);
            
            //5A. Obtener un objeto Incidencia a partir de su Id.
            System.out.println("--------------------");
            System.out.println("5A. Obtener un objeto incidencia a partir de su ID");
            System.out.println("Consultando incidencia por su ID...");
            List<Incidencia> laIncidencia = gestor.getIncidenciaByID(2);
            System.out.println(laIncidencia);
            System.out.println("--------------------");
            
            //5B. Obtener todas las incidencias
            System.out.println("--------------------");
            System.out.println("5B. Obtener la lista de todas las incidencias");
            System.out.println("Listado de Incidencias");            
            List<Incidencia> incidencias = gestor.getAllIncidencias();
            for (Incidencia incidenciaActual : incidencias){
                System.out.println(incidenciaActual);
            }
            System.out.println("--------------------");
            
            //5C. Insertar una incidencia a partir de un objeto de clase Incidencia
            System.out.println("---------------------");
            System.out.println("5C. Insertar una incidencia a partir de un objeto de clase Incidencia definido adecuadamente según los campos que presenta (incluido el empleado que la origina y el empleado destino).");
            System.out.println("Insertando Incidencia "+i1.getIdIncidencia()+" ...");
            if (gestor.insertarIncidencia(i1)){
                System.out.println("Incidencia insertada!");
            } else System.out.println("La incidencia no se ha podido insertar");
            System.out.println("---------------------");

            //5D. Consultar todas las incidencias para un empleado
            System.out.println("--------------------");
            System.out.println("5D. Obtener las incidencias para un empleado a partir de un objeto de clase Empleado.");
            System.out.println("Listado de Incidencias para el empleado "+e2.getUsername());            
            List<Incidencia> todasIncidenciasPara = gestor.getAllIncidenciasParaEmpleado(e2);
            for (Incidencia incidenciaActual : todasIncidenciasPara){
                System.out.println(incidenciaActual);
            }            
            System.out.println("--------------------");
            
            //5E. Obtener las incidencias creadas por un empleado concreto.
            System.out.println("--------------------");
            System.out.println("5E. Obtener las incidencias creadas por un empleado concreto.");
            System.out.println("Listado de Incidencias creadas el empleado "+e1.getUsername());
            List<Incidencia> todasLasIncidenciasPorEmpleado = gestor.getAllIncidenciasByOrigen(e1);
            for (Incidencia incidenciaActual : todasLasIncidenciasPorEmpleado){
                System.out.println(incidenciaActual);
            }       
            System.out.println("--------------------");
            
            //6B. Obtener un listado de todos los inicios de sesión que ha habido.
            System.out.println("--------------------");
            System.out.println("6B. Obtener un listado de todos los inicios de sesión que ha habido.");
            System.out.println("Obteniendo listado...");
            List<Historial> allLogins = gestor.getAllLogins();
            for (Historial loginActual : allLogins){
                System.out.println(loginActual);
            }       
            System.out.println("--------------------");
            
            //System.out.println(gestor.getNumeroIncidencias());
            

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            System.out.println("Error con la BBDD: " +ex.getMessage());
        }
        
    }
}
