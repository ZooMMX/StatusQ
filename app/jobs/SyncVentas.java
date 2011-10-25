package jobs;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * Usuario: octavioruizcastillo
 * Date: 21/08/11
 * Time: 11:55
 */

import models.EstadoSucursal;
import models.Producto;
import models.Sucursal;
import models.VentaPorDia;
import play.Logger;
import play.db.helper.JdbcHelper;
import play.jobs.Every;
import play.jobs.Job;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;


@Every("60s")
public class SyncVentas extends Job {

    public void doJob() {
        try {
            System.out.println("Ejecutando SyncVentas;");
            List<Sucursal> sucursalList = Sucursal.findAll();
            for (Sucursal suc : sucursalList) {

                if(ConfigPools.pools==null) { throw new Exception("Pool de conexiones nulo"); }
                if(suc.estado == EstadoSucursal.OFFLINE) { continue; }
                Connection connection = ConfigPools.pools.get(suc.id).getConnection();
                ResultSet  rs         = null;
                Statement stmt        = null;

                try {
                    System.out.println("- Consultando ventas de "+suc.nombre);

                    stmt = connection.createStatement();
                    rs   = stmt.executeQuery("select curdate() as hoy, sum(total) as total from omoikane.ventas where fecha_hora between curdate() and DATE_ADD(curdate(), INTERVAL 1 DAY)");

                    rs.next();

                    final Date fecha       = rs.getDate("hoy");
                    final BigDecimal total = rs.getBigDecimal("total");

                    VentaPorDia vpd = VentaPorDia.find("byFechaAndSucursal_id", fecha, suc.getId()).first();

                    if(vpd == null)  {
                       vpd = new VentaPorDia(fecha, total);
                       vpd.sucursal = suc;
                    } else {
                      vpd.venta = total;
                    }

                    vpd.save();

                } catch (Exception ex) {
                    Logger.error("Error consultando ventas de "+suc.nombre, ex);
                    ex.printStackTrace();
                } finally {
                    if(rs         != null) rs.close();
                    if(stmt       != null) stmt.close();
                    if(connection != null) connection.close();
                }

            }
        } catch (Exception e) {
            Logger.error(e, "Error al consultar ventas del día");
        }
    }


}