package jobs;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Date;

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

    public ClientMessage(JSONObject message) throws JSONException {
        String comando = message.getString("command");
        if(comando.equals("setVentas"))    { command = Command.setVentas; }
        if(comando.equals("setProductos")) { command = Command.setProductos; }
        idSucursal = message.getLong("idSucursal");

        ventaScurusal = new VentaScurusal();
        ventaScurusal.setFecha(Date.valueOf(message.getString("fecha")));
        ventaScurusal.setImporte(new BigDecimal(message.getString("importe")));
    }
    public Date getVentaSucursalFecha() {
        return ventaScurusal.getFecha();
    }

    public BigDecimal getVentaSucursalImporte() {
        return ventaScurusal.getImporte();
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
