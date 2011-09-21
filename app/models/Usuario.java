package models;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.*;
import play.libs.Codec;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * Usuario: octavioruizcastillo
 * Date: 07/09/11
 * Time: 13:14
 */


@javax.persistence.Entity
public class Usuario extends Model {

    @Required
    public String username;

    @Required
    public String password;

    @Email
    public String email;

    public boolean isAdmin;

    public Usuario(String username, String password, String mail, boolean isAdmin) {
        this.username = username;
        setPassword(password);
        this.email    = mail;
        this.isAdmin  = isAdmin;
        create();
    }

    public void setPassword(String pass) {
        this.password = Codec.hexMD5(pass);
    }

}
