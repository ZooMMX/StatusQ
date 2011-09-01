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
import play.jobs.Every;
import play.jobs.Job;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Every("6h")
public class SyncProductos extends Job {

    public void doJob() {
        System.out.println("Ejecutando SyncProductos;");
        List<Sucursal> sucursalList = Sucursal.findAll();
        for (Sucursal suc : sucursalList) {

            MyJDBCHelper bd = null;


            try {
                bd = new MyJDBCHelper(suc.bdURL, suc.bdUser, suc.bdPass);
                bd.rs = bd.stmt.executeQuery("select id,descripcion,costo,existencias,precio,utilidad,codigo from omoikane.ramcachearticulos");
                while(bd.rs.next()) {
                    System.out.println("Insertando producto");
                    try {
                        final Long   id          = bd.rs.getLong("id");
                        final String descripcion = bd.rs.getString("descripcion");
                        final BigDecimal costo       = bd.rs.getBigDecimal("costo");
                        final BigDecimal existencias = bd.rs.getBigDecimal("existencias");
                        final BigDecimal precio      = bd.rs.getBigDecimal("precio");
                        final BigDecimal utilidad    = bd.rs.getBigDecimal("utilidad");
                        final String codigo      = bd.rs.getString("codigo");

                        //Producto p = Producto.find("byIdAndSucursal_id", i, suc.getId()).first();
                        Producto p;
                        p = new Producto(descripcion, precio, costo, utilidad, existencias, codigo);
                        p.id = id;
                        p.sucursal = suc;
                        p.merge();

                    }  catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("+++Count:" + bd.rs.getFetchSize());
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if(bd != null) { bd.close(); }

        }
    }


}