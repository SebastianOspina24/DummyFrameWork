package edu.escuelaing.arep;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ResponseType {

    private static ResponseType instance = new ResponseType();
    private HashMap<String, Method> method = new HashMap<String, Method>();

    private ResponseType() {
        mapping(findComponents());
    }

    public static ResponseType getInstance() {
        if (instance == null)
            instance = new ResponseType();
        return instance;
    }

    public void getRecurso(String url, Socket clienteSocket)throws IOException, IllegalArgumentException, URISyntaxException{
        if(url.contains(".")){
            recursoToString(url, clienteSocket);
        }else{
            mapp( url,  clienteSocket);
        }
    }

    /**
     * Va a buscar el recurso pedido y devuvelve lo que encuentre sea texto o una
     * imagen
     * 
     * @param url          ubicacion del archivo solicitado
     * @param clientSocket Socket de respuesta al cliente que lo solicita
     * @throws IOException en caso de errores
     */

    public void recursoToString(String url, Socket clienteSocket) throws IOException {
        String devo = getExtension(url);
        Tipo type = getType(url);
        PrintWriter out = new PrintWriter(
                clienteSocket.getOutputStream(), true);
        switch (type) {
            case TXT:
                String pagina = "HTTP/1.1";
                BufferedReader br;
                try {
                    br = getBufferedReaderFromlocation(url);

                    pagina += "200 OK\r\n Content-Type: text/" + devo + "\r\n\r\n";
                    pagina += toString(br);
                } catch (Exception e) {
                    pagina += "404 Not Found \r\n\r\n";
                }
                out = new PrintWriter(clienteSocket.getOutputStream(), true);
                out.println(pagina);
                out.close();
                break;

            case BIN:
                DataOutputStream binaryOut = new DataOutputStream(clienteSocket.getOutputStream());
                try {
                    byte[] bytes = toBytes(url);
                    binaryOut.writeBytes("HTTP/1.1 200 OK \r\n");
                    binaryOut.writeBytes("Content-Type: image/" + devo + "\r\n");
                    binaryOut.writeBytes("Content-Length: " + bytes.length + "\r\n\r\n");
                    binaryOut.write(bytes, 0, (int) new File(url).length());
                } catch (Exception e) {
                    binaryOut.writeBytes("HTTP/1.1 404 Not Found\r\n\r\n");
                }
                binaryOut.close();
                break;

            case NOSOPORTADO:
                out = new PrintWriter(clienteSocket.getOutputStream(), true);
                out.println("HTTP/1.1 501 Not Implemented\r\n\r\n");
                out.close();
                break;
        }

    }

    /**
     * va a obtener la imagen en la ruta dada y obtiene el arreglo de bytes que la
     * componen
     * 
     * @param url ubicacion de la imagen
     * @return Devuelve un arreglo de bytes de la imagen
     * @throws NanoServerException en caso de no encontrar la imagen
     */

    private byte[] toBytes(String url) throws NanoServerException {
        try {
            File graphicResource = new File(url);
            FileInputStream inputImage = new FileInputStream(graphicResource);
            byte[] bytes = new byte[(int) graphicResource.length()];
            inputImage.read(bytes);
            inputImage.close();
            return bytes;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Devuelve un String apartir de un BufferedReader que se obtiene
     * 
     * @param r BufferedReader del archivo
     * @return contenido del archivo en String para devolver
     */
    private String toString(BufferedReader r) throws NanoServerException {
        String temp, convertion = "";
        try {
            while ((temp = r.readLine()) != null) {
                convertion = convertion.concat(temp);
            }
        } catch (IOException e) {
            throw new NanoServerException(NanoServerException.STRINGCONVERTION);
        }
        return convertion;
    }

    /**
     * Obtiene El Buffer con el archivo que se desea devolver
     * 
     * @param url Ruta donde se va a buscar el archivo a leer
     * @return BufferedReader de lo que se econtro
     * @throws NanoServerException En caso de no encontrar el archivo
     */
    private BufferedReader getBufferedReaderFromlocation(String url) throws NanoServerException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(url)));
        } catch (FileNotFoundException ex) {
            throw new NanoServerException(NanoServerException.NOFOUND);
        }
        return br;
    }

    /**
     * Determina la extension del archivo para posteriormente tratarlo y poder dar
     * una respuesta adecuada
     * 
     * @param url String del archivo
     * @return Tipo del archivo que se va a devolver
     */
    private String getExtension(String url) {
        String[] temp = url.split("\\.");
        return temp[temp.length - 1];
    }

    private void mapp(String url, Socket clienteSocket) throws IOException, IllegalArgumentException, URISyntaxException{
        String res = "HTTP/1.1 200 OK\r\n Content-Type: text/html \r\n\r\n";
        Method m = method.get("/"+url);
        if(m == null){
            res ="HTTP/1.1 404 Not Found \r\n\r\n";
        }
        try {
            res += m.invoke(null,"");
        } catch (IllegalAccessException |InvocationTargetException e) {
            Logger.getLogger(ResponseType.class.getName()).log(Level.SEVERE, "Component not found", e);
        }
        PrintWriter out = new PrintWriter(
                clienteSocket.getOutputStream(), true);
        out.println(res);
        out.close();        
    }

    /**
     * Determina la extension del archivo para posteriormente tratarlo y poder dar
     * una respuesta adecuada
     * 
     * @param url String del archivo
     * @return Tipo del archivo que se va a devolver
     */
    private Tipo getType(String url) {
        Tipo a = null;
        if (url.contains(".css") || url.contains(".js") || url.contains(".html")) {
            a = Tipo.TXT;
        } else if (url.contains(".jpg") || url.contains(".png") || url.contains(".ico")) {
            a = Tipo.BIN;
        } else {
            a = Tipo.NOSOPORTADO;
        }
        return a;
    }

    private void mapping(List<String> componentsList) {
        for (String component : componentsList) {
            Class<?> c = null;
            try {
                c = Class.forName(component);
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(GetMapping.class)) {
                        String uri = m.getAnnotation(GetMapping.class).value();
                        method.put(uri, m);
                    }
                }
            } catch (ClassNotFoundException e) {
                Logger.getLogger(ResponseType.class.getName()).log(Level.SEVERE, "Component not found", e);
            }
        }
    }

    public static List<String> findComponents() {
        List<String> javaFiles = new ArrayList<String>();
        List<String> components = new ArrayList<String>();
        String path = "./src/main/java/edu/escuelaing/arep";
        try {
            javaFiles = Files.walk(Paths.get(path)).map(Path::getFileName).map(Path::toString)
                    .filter(n -> n.endsWith(".java")).collect(Collectors.toList());

            for (String name : javaFiles) {
                Class<?> cls = Class.forName("edu.escuelaing.arep." + name.substring(0, name.length() - 5));
                if (cls.isAnnotationPresent(Componente.class)) {
                    components.add("edu.escuelaing.arep." + name.substring(0, name.length() - 5));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ResponseType.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return components;
    }

}

/**
 * Enum para los tipos de respuesta que se dan.
 */
enum Tipo {
    TXT, BIN, NOSOPORTADO
}