package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

import java.text.*;

@javax.persistence.Entity
public class Producto extends Model {

	public String   nombre;
	public BigDecimal precio;
	public BigDecimal costo;
	public BigDecimal utilidad;
	public BigDecimal existencias;
    public String codigo;

    @ManyToOne
    public Sucursal sucursal;

    /**
     *
     * @param nombre
     * @param precio
     * @param costo
     * @param utilidad
     * @param existencias
     * @param codigo
     */
    public Producto(String nombre, BigDecimal precio, BigDecimal costo, BigDecimal utilidad, BigDecimal existencias, String codigo) {
		this.nombre = nombre;
		this.precio  = precio;
		this.costo   = costo;
		this.utilidad = utilidad;
		this.existencias = existencias;
        this.codigo = codigo;
	}
	public String toString() {
		return nombre;
	}
}