package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

import java.text.*;

@javax.persistence.Entity

/**
 * Modelo de sucursal
 */
public class Sucursal extends Model {

	public String nombre;
	public String bdURL;
    public String bdUser;
    public String bdPass;
	public EstadoSucursal estado;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    public List<Camara> camaras;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    public List<VentaPorDia> ventaPorDias;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    public List<Producto> productos;

	
	public Sucursal(String nombre, String url, String user, String pass, EstadoSucursal estado) {
		this.nombre    = nombre;
		this.bdURL     = url;
        this.bdUser    = user;
        this.bdPass    = pass;
		this.estado    = estado;
	}
	public String toString() {
		return nombre;
	}
}