package jobs;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * Usuario: octavioruizcastillo
 * Date: 21/08/11
 * Time: 11:55
 */

import models.Sucursal;
import models.VentaPorDia;
import play.jobs.Every;
import play.jobs.Job;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncVentasHistoricas extends Job {

    public void doJob() {
        System.out.println("Ejecutando SyncVentas Históricas;");
        List<Sucursal> sucursalList = Sucursal.findAll();
        for (Sucursal suc : sucursalList) {

            //if(suc.estado == EstadoSucursal.OFFLINE) { continue; }

            MyJDBCHelper bd = null;

            try {
                System.out.println("- Consultando ventas de "+suc.nombre);

                bd = new MyJDBCHelper(suc.bdURL, suc.bdUser, suc.bdPass);
                bd.execute("select date(fecha_hora) as fecha, sum(total) as total " +
                        "from omoikane.ventas " +
                        "where date(fecha_hora) < date(curdate())" +
                        "group by date(fecha_hora)");
                VentaPorDia.delete("sucursal_id = ?", suc.getId());

                while ( bd.next() ) {

                    final Date fecha       = bd.rs.getDate("fecha");
                    final BigDecimal total = bd.rs.getBigDecimal("total");

                    VentaPorDia vpd = VentaPorDia.find("byFechaAndSucursal_id", fecha, suc.getId()).first();

                    if(vpd == null)  {
                       vpd = new VentaPorDia(fecha, total);
                       vpd.sucursal = suc;
                    } else {
                      vpd.venta = total;
                    }

                    vpd.save();
                    System.out.println("- Ventas consultadas corectamente! de "+suc.nombre+" importe: "+total);
                }


            } catch (SQLException e) {
                System.out.println("- Error al consultar ventas de "+suc.nombre);
                Logger.getLogger(SyncVentasHistoricas.class.getName()).log(Level.SEVERE, null, e);
            }
            if(bd != null) { bd.close(); }

        }
    }


}