package models;

import javax.persistence.*;
import java.sql.*;
import java.sql.Blob;
import javax.sql.*;
import java.math.*;
import java.util.*;
import java.util.Date;

import play.db.jpa.*;

@Entity
@Table(name="Producto")
@IdClass(ProductoId.class)
public class Producto extends GenericModel{

	@Id
	@Column(name="id")
	public Long id;

	@Id
	@Column(name="sucursal_id")
	public Long sucursalId;

	@Column(name="codigo", length=255)
	public String codigo;

	@Column(name="costo")
	public BigDecimal costo;

	@Column(name="existencias")
	public BigDecimal existencias;

	@Column(name="nombre", length=255)
	public String nombre;

    @Column(name="linea", length = 255)
    public String linea;

	@Column(name="precio")
	public BigDecimal precio;

	@Column(name="utilidad")
	public BigDecimal utilidad;

    @JoinColumn(insertable = false, updatable = false)
    @ManyToOne
    public Sucursal sucursal;

    public Producto(String nombre, String linea, BigDecimal precio, BigDecimal costo, BigDecimal utilidad, BigDecimal existencias, String codigo) {
        this.nombre      = nombre;
        this.linea       = linea;
        this.precio      = precio;
        this.costo       = costo;
        this.utilidad    = utilidad;
        this.existencias = existencias;
        this.codigo      = codigo;
    }
}
