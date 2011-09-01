package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@javax.persistence.Entity

public class Camara extends Model {
	public String nombre;
	public String URL;

    @ManyToOne
    Sucursal sucursal;
	
	public Camara(Sucursal sucursal, String nombre, String URL) {
		this.nombre = nombre;
		this.URL = URL;
	}
	public String toString() {
		return nombre;
	}
}