package controllers;

import play.mvc.*;
import models.*;

@With(Secure.class)
public class Application extends Controller {

    @Before
    static void addDefaults() {
        String userName = Seguridad.connected();

        renderArgs.put("tab"      , "Home"               );
        renderArgs.put("sucursals", Sucursal.findAll()   );
        renderArgs.put("usuario"  , userName             );
        renderArgs.put("isAdmin"  , Usuario.find("byUsername", userName).<Usuario>first().isAdmin);

    }

    public static void index() {
        //render();
        Dashboard.overall();
    }

}