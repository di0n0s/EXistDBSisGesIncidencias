/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EXistDBSisGesIncidencias;

import java.util.ArrayList;
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
    private final String PASS = "samson180387";
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
    
    //Insertar empleado
    public void insertarEmpleado(Empleado e1) throws XMLDBException {
        String insert = "update insert <Empleado>"
                + "<username>" + e1.getUsername() + "</username>"
                + "<password>" + e1.getPassword() + "</password>"
                + "<nombre_completo>" + e1.getNombreCompleto() + "</nombre_completo>"
                + "<telefono>" + e1.getTelefono() + "</telefono>"
                + "</Empleado> into /Empleados";
        ejecutarUpdate(COLECCEMPLEADOS, insert);

    }
    
    //Insertar una incidencia a partir de un objeto Incidencia
    public void insertarIncidencia(Incidencia i) throws XMLDBException {
        String insert = "update insert <Incidencia>"
                + "<id_incidencia>" + i.getIdIncidencia()+ "</id_incidencia>"
                + "<fecha_hora>" + i.getFechaHora() + "</fecha_hora>"
                + "<origen>" + i.getOrigen().getUsername() + "</origen>"
                + "<destino>" + i.getDestino().getUsername() + "</destino>"
                + "<tipo>"+i.getTipo()+"</tipo>"
                + "<detalle>"+i.getDetalle()+"</detalle>"
                + "</Incidencia> into /Incidencias";
        ejecutarUpdate(COLECCINCIDENCIAS, insert);

    }    
    
    //Modificar perfil de un empleado existente
    public void modificarEmpleado (Empleado e1, Empleado e2) throws XMLDBException{
        String updatePass = "update replace /Empleados/Empleado"
                + "[username=\'"+e1.getUsername()+"']"
                + "/password with <password>"+e2.getPassword()+"</password>";
        ejecutarUpdate(COLECCEMPLEADOS, updatePass);
        String updateNombre = "update replace /Empleados/Empleado"
                + "[username=\'"+e1.getUsername()+"']"
                + "/nombre_completo with <nombre_completo>"+e2.getNombreCompleto()+"</nombre_completo>";
        ejecutarUpdate(COLECCEMPLEADOS, updateNombre);
        String updateTelefono = "update replace /Empleados/Empleado"
                + "[username=\'"+e1.getUsername()+"']"
                + "/telefono with <telefono>"+e2.getTelefono()+"</telefono>";
        ejecutarUpdate(COLECCEMPLEADOS, updateTelefono);
    }
    
    //Validar empleado
    public void validarEmpleado (Empleado e){
        //String consulta = "let"
    }

    //Consultar todas las incidencias para un empleado concreto
    public List<Incidencia> getAllIncidenciasParaEmpleado(Empleado e) throws XMLDBException{
        String consulta = "for $i in //Incidencias/Incidencia return $i";
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
    
    
    //Método auxiliar que lee los datos de un Libro
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
