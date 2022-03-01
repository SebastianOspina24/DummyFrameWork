package edu.escuelaing.arep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Esta clase se encarga de hacer un mapeo del servicio para cargarlo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
	// URI Del servicio
	public String value();
}
