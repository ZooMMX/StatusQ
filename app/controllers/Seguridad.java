package controllers;

import models.Usuario;
import play.libs.Codec;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 09/09/11
 * Time: 13:36
 */
public class Seguridad extends Secure.Security {

    static boolean authenticate(String username, String password) {
        Usuario user = Usuario.find("byUsername", username).first();

        return user != null && user.password.equals(Codec.hexMD5(password));
    }

    static boolean check(String profile) {
        Usuario user = Usuario.find("byUsername", connected()).first();
        if ("isAdmin".equals(profile)) {
            return user.isAdmin;
        }
        else {
            return false;
        }
    }
}