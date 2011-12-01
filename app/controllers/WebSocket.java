package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.WebSocketController;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 24/11/11
 * Time: 01:41
 */
public class WebSocket extends WebSocketController {
    public static void hola () {
        System.out.println("conexión abierta");
        while(inbound.isOpen()) {
            Http.WebSocketEvent e = await(inbound.nextEvent());

            outbound.send("hola ");
            System.out.println("enviado hola");
        }
        System.out.println("conexión cerrada");
    }
}
