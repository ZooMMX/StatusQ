package controllers;

import play.mvc.With;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * Usuario: octavioruizcastillo
 * Date: 07/09/11
 * Time: 13:27
 */
@With(Secure.class)
@Check("isAdmin")
public class Usuarios extends CRUD {
}
