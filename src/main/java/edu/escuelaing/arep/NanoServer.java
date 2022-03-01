package edu.escuelaing.arep;

import java.net.*;

import edu.escuelaing.arep.returns.PetitionResponse;

import java.io.*;

public class NanoServer {
    public static void main(String[] args) throws IOException {
        int port = getPort();
        ServerSocket serverSocket = null;
        boolean funcionando = true;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("No es posible escuchar por el puerto  " + port);
            System.exit(1);
        }
        System.out.println("El servidor ya esta listo");
        while (funcionando) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PetitionResponse.getentry(clientSocket);
        }
        serverSocket.close();
    }

    /**
     * Obtiene el puerto si esta asignado en el servidor de lo contrario asigna uno
     * por default
     * 
     * @return devuelve el puerto por el cual se va a realizar la comunicacion
     */
    public static int getPort() {
        return (System.getenv("PORT") != null) ? Integer.parseInt(System.getenv("PORT")) : 4567;
    }
}