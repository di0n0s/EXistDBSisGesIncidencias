/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EXistDBSisGesIncidencias;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;

/**
 *
 * @author sfcar
 */
public class IncidenciasXND {

    private Database database;
    private final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc"; //Ruta del servidor
    private final String USER = "admin";
    private final String PASS = "admin";
    private final String COLECCEMPLEADOS = "/db/sis_ges_incidencias/empleados"; //Ruta de las colecciones
    private final String COLECCINCIDENCIAS = "/db/sis_ges_incidencias/incidencias";
    private final String COLECCHISTORIAL = "/db/sis_ges_incidencias/historial";

    public IncidenciasXND() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {
        String driver = "org.exist.xmldb.DatabaseImpl";
        Class c; //La manera de sacar la clase de ese driver
        c = Class.forName(driver);
        database = (Database) c.newInstance();
        DatabaseManager.registerDatabase(database); //Registramos la BBDD

    }
    
    //4A. Insertar un empleado nuevo en la B.D.
    public boolean insertarEmpleado(Empleado e1) throws XMLDBException {
        String insert = "update insert <Empleado>"
                + "<username>" + e1.getUsername() + "</username>"
                + "<password>" + e1.getPassword() + "</password>"
                + "<nombre_completo>" + e1.getNombreCompleto() + "</nombre_completo>"
                + "<telefono>" + e1.getTelefono() + "</telefono>"
                + "</Empleado> into /Empleados";
        ejecutarUpdate(COLECCEMPLEADOS, insert);
        return true;
    }
    
  
    
    //4C. Modificar perfil de un empleado existente (FUNCIONA)
    public boolean modificarEmpleado (Empleado e1, Empleado e2) throws XMLDBException{
        String updatePass = "update replace /Empleados/Empleado "
                + "[username=\'"+e1.getUsername()+"'] "
                + "/password with <password>"+e2.getPassword()+"</password>";
        ejecutarUpdate(COLECCEMPLEADOS, updatePass);
        String updateNombre = "update replace /Empleados/Empleado "
                + "[username=\'"+e1.getUsername()+"'] "
                + "/nombre_completo with <nombre_completo>"+e2.getNombreCompleto()+"</nombre_completo>";
        ejecutarUpdate(COLECCEMPLEADOS, updateNombre);
        String updateTelefono = "update replace /Empleados/Empleado "
                + "[username=\'"+e1.getUsername()+"'] "
                + "/telefono with <telefono>"+e2.getTelefono()+"</telefono>";
        ejecutarUpdate(COLECCEMPLEADOS, updateTelefono);      
        return true;
    }
    
    //4D. Cambiar contraseña de un empleado existente (FUNCIONA)
    public boolean modificarPassword(Empleado e1, Empleado e2) throws XMLDBException{
        String updatePass = "update replace /Empleados/Empleado "
        + "[username=\'"+e1.getUsername()+"'] "
        + "/password with <password>"+e2.getPassword()+"</password>";
        ejecutarUpdate(COLECCEMPLEADOS, updatePass);
        return true;
    }
    
    //4B. Validar la entrada de un empleado (suministrando usuario y contraseña) (FUNCIONA)(CON SUBMÉTODO)
    public boolean validarEmpleado (Empleado e) throws XMLDBException{
        String consulta = "for $e in //Empleados/Empleado "
                + "let $user := $e/username let $pass := $e/password "
                + "where $user ='"+e.getUsername()+"' and $pass ='"+e.getPassword()+"' "
                + "return $e";
        ResourceSet resultado = ejecutarQuery(COLECCEMPLEADOS, consulta);
        if(resultado.getSize()>0){
            System.out.println("Empleado validado");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss:SS"); //Indicamos como queremos que se muestre la fecha y hora
            Date ahoramismo = new Date(); //Creamos un new Date para la fecha y hora actual
            String fechaHoraenTexto = sdf.format(ahoramismo); //Creamos un string indicando al sdf que mofique el formato del dato
            Historial h = new Historial("I", fechaHoraenTexto, e);   
            String insert = "update insert <historial>"
                + "<tipo_evento>" + h.getTipoEvento()+ "</tipo_evento>"
                + "<fecha_hora>" + h.getFechaHora() + "</fecha_hora>"
                + "<username>" + h.getUsername().getUsername() + "</username>"
                + "</historial> into /Historial";
            ejecutarUpdate(COLECCHISTORIAL, insert);
            System.out.println("--Se ha insertado en el historial ESTA validación del usuario: '"+e.getUsername()+"'--");            
            return true;
        } else {
            System.out.println("El empleado "+e.getUsername()+" no existe en la BDOO");
            return false;  
        }
            
    }
    
    //4E. Eliminar un empleado (FUNCIONA)
    public boolean eliminarEmpleado (Empleado e) throws XMLDBException{
        String delete = "update delete /Empleados/Empleado "
        + "[username=\'"+e.getUsername()+"']";
        ejecutarUpdate(COLECCEMPLEADOS, delete);
        return true;
    }

    //5A. Obtener un objeto Incidencia a partir de su Id. (FUNCIONA)
    public List<Incidencia> getIncidenciaByID (int id) throws XMLDBException{
        String consulta = "for $i in //Incidencias/Incidencia "
                + "let $incidencia := $i/id_incidencia "
                + "where $incidencia ='"+id+"' "
                + "return $i";
        ResourceSet resultado = ejecutarQuery(COLECCINCIDENCIAS, consulta);
        ResourceIterator it = resultado.getIterator(); //Recorrer la consulta
        List<Incidencia> laIncidencia = new ArrayList<>();
        while(it.hasMoreResources()){
            XMLResource resource = (XMLResource) it.nextResource();
            Node nodo = (Node) resource.getContentAsDOM(); //Tenemos que leer el resultado con DOM
            NodeList barra = nodo.getChildNodes(); //Leemos la lista de los hijos
            NodeList hijo = barra.item(0).getChildNodes(); //3 niveles(nodo/hijo) para leer la entidad
            Incidencia i = leerDomIncidencia(hijo);
            laIncidencia.add(i);
        }
        return laIncidencia;
        
    }
    
    //5B. Obtener la lista de todas las incidencias (FUNCIONA)
    public List<Incidencia> getAllIncidencias() throws XMLDBException{
        String consulta = "for $i in //Incidencias/Incidencia "
                + "return $i";
        ResourceSet resultado = ejecutarQuery(COLECCINCIDENCIAS, consulta);
        ResourceIterator it = resultado.getIterator(); //Recorrer la consulta
        List<Incidencia> todasLasIncidencias = new ArrayList<>();
        while(it.hasMoreResources()){
            XMLResource resource = (XMLResource) it.nextResource();
            Node nodo = (Node) resource.getContentAsDOM(); //Tenemos que leer el resultado con DOM
            NodeList barra = nodo.getChildNodes(); //Leemos la lista de los hijos
            NodeList hijo = barra.item(0).getChildNodes(); //3 niveles(nodo/hijo) para leer la entidad
            Incidencia i = leerDomIncidencia(hijo);
            todasLasIncidencias.add(i);
        }
        return todasLasIncidencias;
        
    }    
    
    //5C. Insertar una incidencia a partir de un objeto de clase Incidencia (FUNCIONA)(CON SUBMÉTODO)
    public boolean insertarIncidencia(Incidencia i) throws XMLDBException {
        String insert = "update insert <Incidencia>"
                + "<id_incidencia>" + i.getIdIncidencia()+ "</id_incidencia>"
                + "<fecha_hora>" + i.getFechaHora() + "</fecha_hora>"
                + "<origen>" + i.getOrigen().getUsername() + "</origen>"
                + "<destino>" + i.getDestino().getUsername() + "</destino>"
                + "<tipo>"+i.getTipo()+"</tipo>"
                + "<detalle>"+i.getDetalle()+"</detalle>"
                + "</Incidencia> into /Incidencias";
        if(i.getTipo()=="Urgente"){
            System.out.println("Se ha insertado una Incidencia Urgente!");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss:SS"); //Indicamos como queremos que se muestre la fecha y hora
            Date ahoramismo = new Date(); //Creamos un new Date para la fecha y hora actual
            String fechaHoraenTexto = sdf.format(ahoramismo); //Creamos un string indicando al sdf que mofique el formato del dato
            Historial h = new Historial("U", fechaHoraenTexto, i.getOrigen());
            String insertH = "update insert <historial>"
                + "<tipo_evento>" + h.getTipoEvento()+ "</tipo_evento>"
                + "<fecha_hora>" + h.getFechaHora() + "</fecha_hora>"
                + "<username>" + h.getUsername().getUsername() + "</username>"
                + "</historial> into /Historial";
            ejecutarUpdate(COLECCHISTORIAL, insertH);
            System.out.println("--Se ha insertado en el historial ESTA incidencia Urgente: '"+i+"'--");                
        }
        ejecutarUpdate(COLECCINCIDENCIAS, insert);
        return true;
    }  
    
    //5D. Consultar todas las incidencias para un empleado concreto (FUNCIONA) (CON SUBMÉTODO)
    public List<Incidencia> getAllIncidenciasParaEmpleado(Empleado e) throws XMLDBException{
        String consulta = "for $i in //Incidencias/Incidencia "
                + "let $e := $i/destino "
                + "where $e ='"+e.getUsername()+"' "
                + "return $i";
        ResourceSet resultado = ejecutarQuery(COLECCINCIDENCIAS, consulta);
        ResourceIterator it = resultado.getIterator(); //Recorrer la consulta
        List<Incidencia> todasLasIncidencias = new ArrayList<>();
        while(it.hasMoreResources()){
            XMLResource resource = (XMLResource) it.nextResource();
            Node nodo = (Node) resource.getContentAsDOM(); //Tenemos que leer el resultado con DOM
            NodeList barra = nodo.getChildNodes(); //Leemos la lista de los hijos
            NodeList hijo = barra.item(0).getChildNodes(); //3 niveles(nodo/hijo) para leer la entidad
            Incidencia i = leerDomIncidencia(hijo);
            todasLasIncidencias.add(i);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss:SS"); //Indicamos como queremos que se muestre la fecha y hora
        Date ahoramismo = new Date(); //Creamos un new Date para la fecha y hora actual
        String fechaHoraenTexto = sdf.format(ahoramismo); //Creamos un string indicando al sdf que mofique el formato del dato
        Historial h = new Historial("C", fechaHoraenTexto, e);
        String insert = "update insert <historial>"
                + "<tipo_evento>" + h.getTipoEvento()+ "</tipo_evento>"
                + "<fecha_hora>" + h.getFechaHora() + "</fecha_hora>"
                + "<username>" + h.getUsername().getUsername() + "</username>"
                + "</historial> into /Historial";
        ejecutarUpdate(COLECCHISTORIAL, insert);        
        System.out.println("--Se ha insertado en el historial ESTA consulta de las Incidencias PARA el empleado '"+e.getUsername()+"'--");
        return todasLasIncidencias;
    }
    
    //5E. Obtener las incidencias creadas por un empleado concreto. (FUNCIONA)
    public List<Incidencia> getAllIncidenciasByOrigen(Empleado e) throws XMLDBException{
        String consulta = "for $i in //Incidencias/Incidencia "
                + "let $e := $i/origen "
                + "where $e ='"+e.getUsername()+"' "
                + "return $i";
        ResourceSet resultado = ejecutarQuery(COLECCINCIDENCIAS, consulta);
        ResourceIterator it = resultado.getIterator(); //Recorrer la consulta
        List<Incidencia> todasLasIncidencias = new ArrayList<>();
        while(it.hasMoreResources()){
            XMLResource resource = (XMLResource) it.nextResource();
            Node nodo = (Node) resource.getContentAsDOM(); //Tenemos que leer el resultado con DOM
            NodeList barra = nodo.getChildNodes(); //Leemos la lista de los hijos
            NodeList hijo = barra.item(0).getChildNodes(); //3 niveles(nodo/hijo) para leer la entidad
            Incidencia i = leerDomIncidencia(hijo);
            todasLasIncidencias.add(i);
        }
        return todasLasIncidencias;
        
    }
    
    //6B. Obtener un listado de todos los inicios de sesión que ha habido. (FUNCIONA)
    public List<Historial> getAllLogins() throws XMLDBException{
        String consulta = "for $h in //Historial/historial "
                + "let $inicio := $h/tipo_evento "
                + "where $inicio ='I' "
                + "return $h";
        ResourceSet resultado = ejecutarQuery(COLECCHISTORIAL, consulta);
        ResourceIterator it = resultado.getIterator(); //Recorrer la consulta
        List<Historial> todosLosEventos = new ArrayList<>();
        while(it.hasMoreResources()){
            XMLResource resource = (XMLResource) it.nextResource();
            Node nodo = (Node) resource.getContentAsDOM(); //Tenemos que leer el resultado con DOM
            NodeList barra = nodo.getChildNodes(); //Leemos la lista de los hijos
            NodeList hijo = barra.item(0).getChildNodes(); //3 niveles(nodo/hijo) para leer la entidad
            Historial evento = leerDomHistorial(hijo);
            todosLosEventos.add(evento);
        }
        return todosLosEventos;
        
    }
    
    //6C. Obtener un listado de los empleados (nombre de usuario) que han consultado sus incidencias al menos una vez.
//    public List<Historial> getEmpleadosLoginAtLeastOne(){
//        String consulta = "for $h in //Historial/historial[count( "
//                + ""
//    }
    
    //6D. Obtener el número de incidencias que hay en la BBDD
    public ResourceSet getNumeroIncidencias() throws XMLDBException{
        String consulta = "for $i in count(//Incidencias/Incidencia) "
                + "return $i";
        ResourceSet resultado = ejecutarQuery(COLECCINCIDENCIAS, consulta);
        return resultado;
        
    }    
    

    //Método auxiliar que lee los datos de un historial
    private Historial leerDomHistorial(NodeList datos){
        int contador = 1;
        Historial h = new Historial();
        for (int it = 0; it < datos.getLength(); it++){
            Node ntemp = datos.item(it);
            if(ntemp.getNodeType() == Node.ELEMENT_NODE){
                if(contador==1){
                    h.setTipoEvento(ntemp.getChildNodes().item(0).getNodeValue());
                    contador ++;
                } else if (contador == 2){
                    h.setFechaHora(ntemp.getChildNodes().item(0).getNodeValue());
                    contador ++;
                } else if (contador == 3){
                    Empleado e = new Empleado(ntemp.getChildNodes().item(0).getNodeValue());
                    h.setUsername(e);
                    contador ++;
                }
            }

            
        }
        return h;        
    }    
    
    //Método auxiliar que lee los datos de una incidencia
    private Incidencia leerDomIncidencia(NodeList datos){
        int contador = 1;
        Incidencia i = new Incidencia();
        for (int it = 0; it < datos.getLength(); it++){
            Node ntemp = datos.item(it);
            if(ntemp.getNodeType() == Node.ELEMENT_NODE){
                if(contador==1){
                    i.setIdIncidencia(Integer.parseInt(ntemp.getChildNodes().item(0).getNodeValue()));
                    contador ++;
                } else if (contador == 2){
                    i.setFechaHora(ntemp.getChildNodes().item(0).getNodeValue());
                    contador ++;
                } else if (contador == 3){
                    Empleado e = new Empleado(ntemp.getChildNodes().item(0).getNodeValue());
                    i.setOrigen(e);
                    contador ++;
                } else if (contador == 4){
                    Empleado e = new Empleado(ntemp.getChildNodes().item(0).getNodeValue());
                    i.setDestino(e);
                    contador ++;
                } else if (contador == 5){
                    i.setTipo(ntemp.getChildNodes().item(0).getNodeValue());
                    contador ++;
                } else if (contador == 6){
                    i.setDetalle(ntemp.getChildNodes().item(0).getNodeValue());
                    contador ++;
                }
            }

            
        }
        return i;        
    }
    
    
    //Método para preparar consultas
    private XQueryService prepararConsulta(String coleccion) throws XMLDBException {
        Collection col = DatabaseManager.getCollection(URI + coleccion, USER, PASS); //Acceso a la colección
        XQueryService service = (XQueryService) col.getService("XQueryService", "1.0"); //Service para hacer consultas
        service.setProperty(OutputKeys.INDENT, "yes"); //Activar el tabulado en el xml
        service.setProperty(OutputKeys.ENCODING, "UTF-8"); //Carácteres
        return service;
    }
    
    //Método para ejecutar updates
    private void ejecutarUpdate(String coleccion, String consulta) throws XMLDBException {
        XQueryService service = prepararConsulta(coleccion); 
        CompiledExpression consultaCompilada = service.compile(consulta); //Compila la consulta
        service.execute(consultaCompilada); //La ejecuta

    }

    //Método para ejecutar querys
    private ResourceSet ejecutarQuery(String coleccion, String consulta) throws XMLDBException {
        XQueryService service = prepararConsulta(coleccion); 
        ResourceSet resultado = service.query(consulta); //Ejecuta la consulta
        return resultado;
    }

}
