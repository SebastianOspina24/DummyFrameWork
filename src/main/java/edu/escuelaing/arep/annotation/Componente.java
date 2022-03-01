package edu.escuelaing.arep.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Esta clase es una anotacion para poder denotar los componentes de la aplicacion, como lo hace 
 * el framework Spring
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Componente {

}
