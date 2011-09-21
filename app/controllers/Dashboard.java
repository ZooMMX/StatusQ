
package controllers;

import models.EstadoSucursal;
import models.Sucursal;
import models.VentaPorDia;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * Usuario: octavioruizcastillo
 * Date: 27/08/11
 * Time: 00:12
 */

@With(Secure.class)
public class Dashboard extends Application {

    public static void overall() {

        BigDecimal        ventaGeneral = VentaPorDia.find("SELECT sum(venta) FROM VentaPorDia group by fecha  order by fecha desc").first();
        List<VentaPorDia> sucursales   = VentaPorDia.find("fecha = current_date order by venta desc").fetch();

        renderArgs.put("ventaGeneral", ventaGeneral);
        renderArgs.put("sucursales"  , sucursales);
        renderArgs.put("titulo"      , "Resumen de hoy");
        render();
    }

    public static void show(Long id) {
        SimpleDateFormat  anioMes     = new SimpleDateFormat("yyyyMM");
        List<VentaPorDia> ventas      = VentaPorDia.find("sucursal_id = ? order by fecha desc", id).fetch(7);
        List<VentaPorDia> anios       = VentaPorDia.find("select fecha from VentaPorDia WHERE sucursal_id = ? group by year(fecha)", id).fetch();
        List<VentaPorDia> meses       = VentaPorDia.find("select fecha from VentaPorDia WHERE sucursal_id = ? group by month(fecha)", id).fetch();
        List<Object[]>    ventaPorMes = VentaPorDia.find("SELECT fecha, sum(venta) FROM VentaPorDia WHERE sucursal_id = ? group by year(fecha), month(fecha)", id).query.getResultList();

        if(ventas.size() > 0) {

            Sucursal sucursal = Sucursal.findById(id);
            Boolean  online   = sucursal.estado == EstadoSucursal.ONLINE;
            HashMap<String, BigDecimal> hashVMes = new HashMap<String, BigDecimal>();

            for(Object[] mes : ventaPorMes) {
                final Date       mesFecha = (Date) mes[0];
                final BigDecimal mesVenta = (BigDecimal) mes[1];
                hashVMes.put(anioMes.format(mesFecha), mesVenta);
            }

            renderArgs.put("ventaHoy"    , ventas.get(0));
            renderArgs.put("ventasSemana", ventas       );
            renderArgs.put("anios"       , anios        );
            renderArgs.put("meses"       , meses        );
            renderArgs.put("ventaPorMes" , hashVMes     );
            renderArgs.put("online"      , online       );

            renderArgs.put("tab"         , sucursal.nombre);
            renderArgs.put("id"          , id);
            renderArgs.put("titulo"      , "Sucursal "+sucursal.nombre);

            render();
        } else {
            renderText("Aún no hay ventas registradas");
        }
    }


}
