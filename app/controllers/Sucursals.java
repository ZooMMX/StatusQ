package controllers;

import java.util.List;
import models.Sucursal;
import play.mvc.Controller;
import play.i18n.Messages;
import play.data.validation.Validation;
import play.data.validation.Valid;
import play.mvc.With;

@With(Secure.class)
@Check("isAdmin")
public class Sucursals extends CRUD {

}
