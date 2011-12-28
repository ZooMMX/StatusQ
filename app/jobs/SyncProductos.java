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
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Every("24h")
public class SyncProductos extends Job {

    public void doJob() {
        Clientes clientes = new Clientes();
        clientes.publicarComando("getProductos");

    }


}