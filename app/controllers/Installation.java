package controllers;

import jobs.SyncVentasHistoricas;
import models.Usuario;
import models.VentaPorDia;
import play.mvc.Controller;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 23/09/11
 * Time: 12:23
 */
public class Installation extends Controller {

    public static void install(String user, String pass, String mail) {
        if(Usuario.count()>0) {
            Application.index();
        }
        if(user != null && pass != null && !user.isEmpty() && !pass.isEmpty()) {
            Usuario first = new Usuario(user, pass, mail, true);
            first.save();
            Application.index();
        }
        render();
    }

    public static void fillDatabase() {
        if(VentaPorDia.count()==0) {
            new SyncVentasHistoricas().doJob();
            renderText("Se inició la tarea SyncVentasHistoricas");
        }
        Application.index();
    }
}
