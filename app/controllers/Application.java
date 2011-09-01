package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    @Before
    static void addDefaults() {
        renderArgs.put("tab", "Home");
        renderArgs.put("sucursals", Sucursal.findAll());
    }

    public static void index() {
        render();
    }

}