
package controllers;

import models.EstadoSucursal;
import models.Sucursal;
import models.VentaPorDia;
import play.db.jpa.JPA;
import play.mvc.Controller;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 27/08/11
 * Time: 00:12
 */

public class Dashboard extends Application {

    public static void overall() {

        renderText("Aquí va el dashboard general con todas las sucursales");
    }

    public static void show(Long id) {
        SimpleDateFormat  anioMes     = new SimpleDateFormat("yyyyMM");
        List<VentaPorDia> ventas      = VentaPorDia.find("sucursal_id = ? order by fecha desc", id).fetch(7);
        List<VentaPorDia> anios       = VentaPorDia.find("select fecha from VentaPorDia group by year(fecha)" ).fetch();
        List<VentaPorDia> meses       = VentaPorDia.find("select fecha from VentaPorDia group by month(fecha)").fetch();
        List<Object[]>    ventaPorMes = VentaPorDia.find("SELECT fecha, sum(venta) FROM VentaPorDia group by year(fecha), month(fecha)").query.getResultList();

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

            render();
        } else {
            renderText("Aún no hay ventas registradas");
        }
    }


}
