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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss:SS"); //Indicamos como queremos que se muestre la fecha y hora
        Date ahoramismo = new Date(); //Creamos un new Date para la fecha y hora actual
        String fechaHoraenTexto = sdf.format(ahoramismo); //Creamos un string indicando al sdf que mofique el formato del dato                    
        Incidencia i = new Incidencia(1, fechaHoraenTexto, e1, e2, "Normal", "La impresora no tiene tóner");
        try {
            //Creamos el gestor y establecemos conexión
            IncidenciasXND gestor = new IncidenciasXND();
            System.out.println("Conexión establecida");
//            //Insertar empleado
//            gestor.insertarEmpleado(e2); 
//            System.out.println("Empleado insertado");
            
//            //Insertar incidencia
//            gestor.insertarIncidencia(i);
//            System.out.println("Incidencia insertada");
            
//            //Consultar todas las incidencias
//            List<Incidencia> incidencias = gestor.getAllIncidenciasParaEmpleado(e2);
//            for (Incidencia incidenciaActual : incidencias){
//                System.out.println(incidenciaActual);
//            }
            
            //Modificar empleado existente
            gestor.modificarEmpleado(e1, e3);
            //List<Empleado> e
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            System.out.println("Error con la BBDD: " +ex.getMessage());
        }
        
    }
}
