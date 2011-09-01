package jobs;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 21/08/11
 * Time: 11:55
 */

import models.EstadoSucursal;
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

@Every("10s")
public class CheckStatus extends Job {

    public void doJob() {
        System.out.println("Ejecutando SyncVentas;");
        List<Sucursal> sucursalList = Sucursal.findAll();
        for (Sucursal suc : sucursalList) {

            try {
                MyJDBCHelper bd = new MyJDBCHelper(suc.bdURL, suc.bdUser, suc.bdPass);
                suc.estado = EstadoSucursal.ONLINE;
                bd.close();
            } catch (SQLException e) {
                Logger.getLogger(CheckStatus.class.getName()).log(Level.SEVERE, null, e);
                suc.estado = EstadoSucursal.OFFLINE;
            }
            suc.save();


        }
    }


}