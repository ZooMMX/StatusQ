package jobs;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 21/08/11
 * Time: 11:55
 */

import models.Producto;
import models.Sucursal;
import models.VentaPorDia;
import play.db.helper.JdbcHelper;
import play.jobs.Every;
import play.jobs.Job;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Every("60s")
public class SyncVentas extends Job {

    public void doJob() {
        System.out.println("Ejecutando SyncVentas;");
        List<Sucursal> sucursalList = Sucursal.findAll();
        for (Sucursal suc : sucursalList) {

            MyJDBCHelper bd = null;

            try {
                bd = new MyJDBCHelper(suc.bdURL, suc.bdUser, suc.bdPass);
                bd.execute("select curdate() as hoy, sum(total) as total from omoikane.ventas where fecha_hora between curdate() and DATE_ADD(curdate(), INTERVAL 1 DAY)");

                bd.next();

                final Date fecha       = bd.rs.getDate("hoy");
                final BigDecimal total = bd.rs.getBigDecimal("total");

                VentaPorDia vpd = VentaPorDia.find("byFechaAndSucursal_id", fecha, suc.getId()).first();

                if(vpd == null)  {
                   vpd = new VentaPorDia(fecha, total);
                   vpd.sucursal = suc;
                } else {
                  vpd.venta = total;
                }
                vpd.save();

            } catch (SQLException e) {
                Logger.getLogger(SyncVentas.class.getName()).log(Level.SEVERE, null, e);
            }
            if(bd != null) { bd.close(); }

        }
    }


}