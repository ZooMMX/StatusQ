package jobs;

import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import pubnub.Pubnub;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 29/11/11
 * Time: 16:59
 */
public class Clientes {
    public void publicarComando(String comando) {
        Logger.info("Comando "+ comando + " publicado");
        Pubnub pubnub  = new Pubnub( "pub-24eb74f7-b8f9-485a-86bb-a08f05c7cb89", "sub-b3f1fd2e-1897-11e1-8b36-c5b5280f91f0" );
        String channel = "clientes";
        JSONObject msg = new JSONObject();

        try {
            msg.put("command", comando);
        } catch (JSONException e) {
            Logger.error("Error solicitando ventas", e);
        }

        pubnub.publish(channel, msg);
    }
}
