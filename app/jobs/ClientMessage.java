package jobs;

import models.Producto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 29/11/11
 * Time: 15:39
 */
public class ClientMessage {
    public Long idSucursal;
    public enum Command {
        setVentas, setProductos
    }
    public Command       command;
    private VentaScurusal ventaScurusal;
    private List<Producto> productoList;

    public ClientMessage(JSONObject message) throws JSONException {

        String comando = message.getString("command");
        idSucursal = message.getLong("idSucursal");


        if(comando.equals("setVentas"))    {
            command = Command.setVentas;

            ventaScurusal = new VentaScurusal();
            ventaScurusal.setFecha(Date.valueOf(message.getString("fecha")));
            ventaScurusal.setImporte(new BigDecimal(message.getString("importe")));
        }
        else if(comando.equals("setProductos")) {
            command = Command.setProductos;

            productoList = new ArrayList<Producto>();
            JSONArray    productosJSON = message.getJSONArray("productos");

            for(int i = 0 ; i < productosJSON.length() ; i++) {
                final JSONObject productoJSON = (JSONObject) productosJSON.get(i);

                final Long       id          = productoJSON.getLong("id");
                final String     nombre      = productoJSON.getString("descripcion");
                final BigDecimal precio      = new BigDecimal(productoJSON.getString("precio"));
                final BigDecimal costo       = new BigDecimal(productoJSON.getString("costo"));
                final BigDecimal utilidad    = new BigDecimal(productoJSON.getString("utilidad"));
                final BigDecimal existencias = new BigDecimal(productoJSON.getString("existencias"));
                final String     codigo      = productoJSON.getString("codigo");
                final Producto producto      = new Producto(nombre, precio, costo, utilidad, existencias, codigo);

                producto.id = id;
                producto.sucursalId = idSucursal;

                productoList.add(producto);
            }

        }

    }
    public Date getVentaSucursalFecha() {
        return ventaScurusal.getFecha();
    }

    public BigDecimal getVentaSucursalImporte() {
        return ventaScurusal.getImporte();
    }

    public List<Producto> getProductos() {
        return productoList;
    }

    class VentaScurusal {
        private Date       fecha;
        private BigDecimal importe;

        public Date getFecha() {
            return fecha;
        }

        public void setFecha(Date fecha) {
            this.fecha = fecha;
        }

        public BigDecimal getImporte() {
            return importe;
        }

        public void setImporte(BigDecimal importe) {
            this.importe = importe;
        }
    }

}
