package jobs;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.ConnectionHandle;
import com.jolbox.bonecp.StatementHandle;
import com.jolbox.bonecp.hooks.AbstractConnectionHook;
import com.jolbox.bonecp.hooks.AcquireFailConfig;
import com.jolbox.bonecp.hooks.ConnectionHook;
import com.jolbox.bonecp.hooks.ConnectionState;
import models.Sucursal;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 19/10/11
 * Time: 18:37
 */
@OnApplicationStart(async = true)
public class ConfigPools extends Job {
    public static HashMap<Long, BoneCP> pools;
    public void doJob() {
        Logger.info("Llenado de pool de conexiones a sucursales iniciado");
        List<Sucursal> sucursales = Sucursal.findAll();
        ConfigPools.pools = new HashMap<Long, BoneCP>();

        for( final Sucursal suc : sucursales) {
            new Thread() {
                @Override
                public void run() {
                    setupSucursalPool(suc);
                }
            }.start();

        }
    }

    private void setupSucursalPool(Sucursal suc) {
        try {
            final BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(suc.bdURL); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
            config.setUsername(suc.bdUser);
            config.setPassword(suc.bdPass);
            config.setMinConnectionsPerPartition(2);
            config.setMaxConnectionsPerPartition(4);
            config.setPartitionCount(1);
            config.setDisableJMX(true);
            config.setTransactionRecoveryEnabled(true);
            config.setAcquireRetryAttempts(2);
            config.setAcquireRetryDelayInMs(2000);
            config.setLogStatementsEnabled(true);
            config.setLazyInit(false);

            final BoneCP pool = new BoneCP(config);

            pools.put(suc.id, pool);

        } catch (SQLException e) {
            Logger.error(e, "Error desconocido iniciando pools de conexiones a sucursales");
        }
    }

    protected void finalize() throws Throwable {
      try {
          if(pools != null) {
            for(Map.Entry<Long, BoneCP> pool : pools.entrySet()) {
                pool.getValue().shutdown();
            }
          }
      } finally {
          super.finalize();
      }
  }
}
