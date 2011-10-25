package jobs;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * Usuario: octavioruizcastillo
 * Date: 21/08/11
 * Time: 11:55
 */

import models.EstadoSucursal;
import models.Sucursal;
import models.VentaPorDia;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;


@Every("60s")
public class CheckStatus extends Job {

    public void doJob() {
        try {
            System.out.println("Ejecutando CheckStatus;");
            List<Sucursal> sucursalList = Sucursal.findAll();
            if(ConfigPools.pools==null) { throw new Exception("Pool de conexiones nulo"); }

            for (Sucursal suc : sucursalList) {
                MyJDBCHelper bd = null;
                try {
                    bd = new MyJDBCHelper(ConfigPools.pools.get(suc.id).getConnection());
                    suc.estado = EstadoSucursal.ONLINE;

                } catch (Exception sqle) {
                    Logger.info(sqle, "Falló conexión con la BD de la sucursal: "+suc.nombre);
                    suc.estado = EstadoSucursal.OFFLINE;

                } finally {
                    suc.save();
                    if(bd!=null) { bd.close(); }
                }

            }
        } catch (Exception e) {
            Logger.error(e, "Excepción verificando estátus de sucursal");
        }
    }


}