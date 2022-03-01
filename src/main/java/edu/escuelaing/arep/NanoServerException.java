package edu.escuelaing.arep;

public class NanoServerException extends Exception {

    public static String NOFOUND = "La ubicacion del archivo no fue encontrada";
    public static String STRINGCONVERTION = "Occurion un Error al convertir a texto el archivo";
    public static String IMAGENCONVERTION = "Error occurres on Imagen convertion";

    public NanoServerException(String a){
        super(a);
    }
}
