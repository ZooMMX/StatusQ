package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

import java.text.*;

@javax.persistence.Entity

public class VentaPorDia extends Model {

	@javax.persistence.Temporal(javax.persistence.TemporalType.DATE)
	public Date fecha;
	public BigDecimal venta;

    @ManyToOne
    public Sucursal sucursal;
	
	public VentaPorDia(Date fecha, BigDecimal venta) {
		this.fecha = fecha;
		this.venta = venta;
	}
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(fecha);
		//return String.valueOf(venta);
	}

    public void setVenta(BigDecimal venta) {
        if(venta == null) {
            venta = new BigDecimal(0);
        }
        this.venta = venta;
    }
}