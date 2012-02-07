package jobs;

import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import pubnub.Pubnub;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 29/11/11
 * Time: 16:02
 */
@Every("180s")
public class SyncVentas extends Job {
    public void doJob() {
        Clientes clientes = new Clientes();
        clientes.publicarComando("getVentas");
    }

}
