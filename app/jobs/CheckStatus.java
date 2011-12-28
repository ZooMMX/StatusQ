package jobs;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * Usuario: octavioruizcastillo
 * Date: 21/08/11
 * Time: 11:55
 */

import models.*;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;
import play.db.jpa.Transactional;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import pubnub.Callback;
import pubnub.Pubnub;

import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnApplicationStart(async = true)
public class CheckStatus extends Job {
    public void doJob() {
        final Pubnub pubnub  = new Pubnub( "pub-24eb74f7-b8f9-485a-86bb-a08f05c7cb89", "sub-b3f1fd2e-1897-11e1-8b36-c5b5280f91f0" );
        final String channel = "servidor";

        class Receiver extends Job implements Callback {

            private Pubnub pubnub;
            HashMap<Long, Boolean> sucursales = new HashMap<Long, Boolean>();

            public Receiver(Pubnub pubnub) {
                this.pubnub = pubnub;
            }

            public boolean execute(JSONObject message) {

                try {
                    Long sucursalid = message.getLong("idSucursal");
                    sucursales.put(sucursalid, true);
                    updateSucursalesStatus();
                } catch (JSONException e) {
                    Logger.error("Error en mecanismo de ping", e);
                    e.printStackTrace();
                }
                // Continue Listening?
                return true;
            }
            public void doJob() {

                Logger.info("Transmitiendo ping");
                if(sucursales != null) { updateSucursalesStatus(); }

                List<Sucursal> sucursals = Sucursal.findAll();
                for (Sucursal sucursal : sucursals) {
                    sucursales.put(sucursal.id, false);
                }

                HashMap<String, String> commandMap = new HashMap<String, String>();
                commandMap.put("command", "ping");
                pubnub.publish("clientes", new JSONObject(commandMap));

            }


            private void updateSucursalesStatus() {
                EntityTransaction et = JPA.em().getTransaction();
                if(!et.isActive()) et.begin();
                for (Map.Entry<Long, Boolean> sucursalStatus : sucursales.entrySet()) {
                    Sucursal suc    = Sucursal.findById(sucursalStatus.getKey());
                    Boolean  status = sucursales.get(sucursalStatus.getKey());
                    suc.estado = status ? EstadoSucursal.ONLINE : EstadoSucursal.OFFLINE;
                    suc.save();
                }
                et.commit();
            }
        }

        final Receiver message_receiver = new Receiver(pubnub);
        message_receiver.every(45);

        pubnub.subscribe( channel, message_receiver );

    }
}
/*
//@Every("30s")
public class CheckStatus extends Job {

    public void doJob() {
        try {
            System.out.println("Ejecutando CheckStatus;");
            List<Sucursal> sucursalList = Sucursal.findAll();
            if(ConfigPools.pools==null) { throw new Exception("Pool de conexiones nulo"); }

            for (Sucursal suc : sucursalList) {
                MyJDBCHelper bd = null;
                try {
                    Connection conn = ConfigPools.pools.get(suc.id).getConnection();
                    if(!conn.isValid(2)) { throw new SQLException("Sin conexión"); }
                    suc.estado = EstadoSucursal.ONLINE;
                    Logger.info("[CheckStatus] "+suc.nombre+ " online");

                } catch (Exception sqle) {
                    Logger.info(sqle, "[CheckStatus] "+suc.nombre + " offline");
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
*/