package jobs;

import models.Producto;
import models.ProductoId;
import models.Sucursal;
import models.VentaPorDia;
import org.json.JSONObject;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import pubnub.Callback;
import pubnub.Pubnub;

import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 26/11/11
 * Time: 23:13
 */
@OnApplicationStart(async = true)
public class ClientListener extends Job {
    public void doJob() {
        final Pubnub pubnub  = new Pubnub( "pub-24eb74f7-b8f9-485a-86bb-a08f05c7cb89", "sub-b3f1fd2e-1897-11e1-8b36-c5b5280f91f0" );
        final String channel = "servidor";

        class Receiver extends Job implements Callback {
            public boolean execute(JSONObject message) {

                Logger.info("Mensaje recibido");
                EntityTransaction et = VentaPorDia.em().getTransaction();

                try {
                    ClientMessage msg = new ClientMessage(message);
                    if( msg.command == ClientMessage.Command.setVentas ) {
                        setVentas(et, msg);
                    }
                    if( msg.command == ClientMessage.Command.setProductos) {
                        setProductos(et, msg);
                    }

                } catch (Exception e) {
                    Logger.error("Error extrayendo información de JSON", e);
                    e.printStackTrace();
                }

                // Continue Listening?
                return true;
            }

            private synchronized void setProductos(EntityTransaction et, ClientMessage msg) {
                if(!JPA.em().getTransaction().isActive()) { JPA.em().getTransaction().begin(); }
                Sucursal sucursal = Sucursal.findById(msg.idSucursal);

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
                    System.out.println("Guardando producto: " + productoToSave + ",idsuc:" + productoToSave.sucursalId);
                    productoToSave.save();

                }
                JPA.em().flush();
                JPA.em().getTransaction().commit();
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

        Receiver message_receiver = new Receiver();

        pubnub.subscribe( channel, message_receiver );
    }
}
