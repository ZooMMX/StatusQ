package controllers.websockets;

import models.EstadoSucursal;
import models.Sucursal;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.mvc.Http;
import play.mvc.WebSocketController;

import static play.libs.F.Matcher.Equals;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 24/11/11
 * Time: 01:41
 */
public class WebSocket extends WebSocketController {

    public static void hola (Long sucursalId) {
        Logger.info("Cliente conectado, id: "+sucursalId);
        sucursalConectada(sucursalId);
        HiloSaliente hiloSaliente = new HiloSaliente(inbound, outbound);
        hiloSaliente.start();

        while(inbound.isOpen()) {

            Http.WebSocketEvent e     = await(inbound.nextEvent());

            for(String quit: e.TextFrame.and(Equals("quit")).match(e)) {
                outbound.send("Bye!");
                disconnect();
            }

            for(String msg: e.TextFrame.match(e)) {
                Http.WebSocketFrame frame = (Http.WebSocketFrame) e;

                try {
                    JSONObject mensajeEntrante = new JSONObject(frame.textData);
                    ClientController clientController = new ClientController(outbound);
                    clientController.mensajeEntrante(mensajeEntrante);
                } catch (JSONException e1) {
                    Logger.error(e1, "Excepción leyendo JSON entrante");
                }

            }

            for(Http.WebSocketClose closed: e.SocketClosed.match(e)) {
                Logger.info("Cliente desconectado");
                inbound.close();
                outbound.close();
            }
        }
        sucursalDesconectada(sucursalId);

    }

    private static void sucursalDesconectada(Long sucursalId) {
        Sucursal actual = ((Sucursal)Sucursal.findById(sucursalId));
        actual.estado = EstadoSucursal.OFFLINE;
        actual.save();
    }

    private static void sucursalConectada(Long sucursalId) {
        Sucursal actual = ((Sucursal)Sucursal.findById(sucursalId));
        actual.estado = EstadoSucursal.ONLINE;
        actual.save();
    }

    public static class HiloSaliente extends Thread {
        private Http.Inbound inbound;
        private Http.Outbound outbound;

        public HiloSaliente(Http.Inbound inbound, Http.Outbound outbound) {
            this.inbound  = inbound;
            this.outbound = outbound;
        }

        public void run() {
            Logger.info("Inicia hilo saliente");
			Every every30sec = new Every(30);
			Every everyHour = new Every(60);
            while(inbound.isOpen()) {
                try {
                    if(every30sec.run()) {
						JSONObject msg = new JSONObject();
						msg.put("command", "getVentas");
						outbound.send(msg.toString());
					}
					if(everyHour.run()) {
						JSONObject msg = new JSONObject();
						msg.put("command", "getProductos");
						outbound.send(msg.toString());
					}
					Thread.sleep(1000);
					
                } catch (InterruptedException e) {
                    Logger.error(e, "Interrupción del hilo saliente");
                } catch (JSONException e) {
                    Logger.error(e, "Excepción fijando comando en cadena JSON");
                }
            }
            Logger.info("Se cierra el hilo saliente");
        }
    }
	public static class Every {
		public int count = 0;
		public int every;
		public Every(int every) {
			this.every = every;
		}
		
		public boolean run() {
			if(count >= every) { count = 0; }
			return count++ == 0;
		}
	}
}
