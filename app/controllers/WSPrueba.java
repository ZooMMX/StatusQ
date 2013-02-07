package controllers;

import play.Logger;
import play.mvc.WebSocketController;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 10/08/12
 * Time: 13:40
 */
public class WSPrueba extends WebSocketController {

    public static void hello () {
        Logger.error("Método hello ejecutado");
    }
}
