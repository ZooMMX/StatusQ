package controllers.websockets;

import models.*;
import org.json.JSONObject;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.mvc.Http;
import play.mvc.WebSocketController;

import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Hashtable;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 13/05/12
 * Time: 16:33
 */
public class ClientController {
    private Http.Outbound outbound;

    public ClientController(Http.Outbound outbound) {
        this.outbound = outbound;
    }

    public boolean mensajeEntrante(JSONObject message) {

            Logger.info("Mensaje recibido");
            final EntityTransaction et = VentaPorDia.em().getTransaction();

            try {
                final ClientMessage msg = new ClientMessage(message);
                if( msg.command == ClientMessage.Command.setVentas ) {
                    setVentas(et, msg);
                }
                if( msg.command == ClientMessage.Command.setProductos) {
                    new SetProductosJob(msg).now();
                    Logger.info("Paso 1");
                }
                if( msg.command == ClientMessage.Command.ping ) {
                    JSONObject pong = new JSONObject();
                    pong.put("command", "pong");
                    outbound.send(pong.toString());
                    Sucursal actual = ((Sucursal)Sucursal.findById(msg.idSucursal));
                    actual.estado = EstadoSucursal.ONLINE;
                    actual.save();
                }

            } catch (Exception e) {
                Logger.error("Error extrayendo información de JSON", e);
                e.printStackTrace();
            }

            // Continue Listening?
            return true;
        }

        public class SetProductosJob extends Job {
            ClientMessage msg;

            public SetProductosJob(ClientMessage msg) { this.msg = msg; }

            public void doJob() {
                setProductos(msg);
            }

            private void setProductos(ClientMessage msg) {
                //if(!Producto.em().getTransaction().isActive()) { Producto.em().getTransaction().begin(); }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                Logger.info("Paso 2");

                Sucursal sucursal = Sucursal.findById(msg.idSucursal);

                Logger.info("Guardando productos. Sucursal: "+sucursal.nombre);;

                int i = 0;
                for (Producto producto : msg.getProductos()) {
                    ProductoId productoId = new ProductoId();
                    productoId.id = producto.id;
                    productoId.sucursalId = producto.sucursalId;

                    Producto productoToSave = Producto.findById(productoId);
                    if(productoToSave != null) {
                        productoToSave.precio      = producto.precio;
                        productoToSave.costo       = producto.costo;
                        productoToSave.codigo      = producto.codigo;
                        productoToSave.existencias = producto.existencias;
                        productoToSave.nombre      = producto.nombre;
                        productoToSave.utilidad    = producto.utilidad;

                    } else {
                        productoToSave = producto;
                    }
                    producto.sucursal = sucursal;
                    productoToSave.save();
                    if(++i % 1000 == 0) { Producto.em().flush(); Producto.em().clear(); }

                }
                Logger.info("Finalizo la carga de productos("+i+") de la sucursal: "+sucursal.nombre);
                Producto.em().getTransaction().commit();
            }
        }

        private void setVentas(EntityTransaction et, ClientMessage msg) {
            if(!et.isActive()) { et.begin(); }

            BigDecimal importe    = msg.getVentaSucursalImporte();
            Date fecha            = msg.getVentaSucursalFecha();
            Sucursal suc          = Sucursal.findById(msg.idSucursal);

            VentaPorDia vpd       = VentaPorDia.find("byFechaAndSucursal_id", fecha, suc.id).first();

            if(vpd == null)  {
               vpd = new VentaPorDia(fecha, importe);
               vpd.sucursal = suc;
            } else {
              vpd.venta = importe;
            }
            vpd.save();
            et.commit();
            Logger.info("Ventas solicitadas fueron recibidas. S: %s, $: %s", suc.nombre, importe.toString());
        }
}
