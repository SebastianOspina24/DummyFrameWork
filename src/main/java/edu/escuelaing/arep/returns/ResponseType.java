package edu.escuelaing.arep.returns;

import java.io.*;
import java.net.Socket;

import edu.escuelaing.arep.NanoServerException;

public class ResponseType {

    private static ResponseType instance = null;

    private ResponseType() {
    }

    public static ResponseType getInstance() {
        if (instance == null)
            instance = new ResponseType();
        return instance;
    }

    /**
     * Va a buscar el recurso pedido y devuvelve lo que encuentre sea texto o una imagen
     * @param url ubicacion del archivo solicitado
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
            System.out.println(url);
                String pagina = "HTTP/1.1";
                BufferedReader br;
                try {
                    br = getBufferedReaderFromlocation(url);

                    pagina += "200 OK\r\n Content-Type: text/" + devo + "\r\n\r\n";
                    pagina += toString(br);
                } catch (Exception e) {
                    pagina += "404 Not Found \r\n\r\n";
                    ;
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
                binaryOut.writeBytes("Content-Length: " + bytes.length+"\r\n\r\n");
                binaryOut.write(bytes,0,(int)new File(url).length());
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
     * va a obtener la imagen en la ruta dada y obtiene el arreglo de bytes que la componen
     * @param url ubicacion de la imagen    
     * @return Devuelve un arreglo de bytes de la imagen
     * @throws NanoServerException en caso de no encontrar la imagen
     */

    private byte[] toBytes(String url) throws NanoServerException {
        try {
            File graphicResource= new File(url);
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
        } else if (url.contains(".jpg") || url.contains(".png")|| url.contains(".ico")) {
            a = Tipo.BIN;
        } else {
            a = Tipo.NOSOPORTADO;
        }
        return a;
    }
}

/**
 * Enum para los tipos de respuesta que se dan.
 */
enum Tipo {
    TXT, BIN, NOSOPORTADO
}