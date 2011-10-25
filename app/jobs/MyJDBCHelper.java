package jobs;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
     * Just for tests or very simple non-important utilities. Just for be clear, not intended for production.
     * Author: Octavio Ruiz
     * Date: 20 Ago 11
     */
    public class MyJDBCHelper {
        public Connection con  = null;
        public Statement stmt = null;
        public ResultSet rs   = null;
        private String      url  = null;
        private String      user = null;
        private String      pass = null;


        public MyJDBCHelper(String url, String user, String pass) throws SQLException {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(url, user, pass);
                stmt = con.createStatement();

            } catch (ClassNotFoundException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            } catch (SQLException e) {
                throw e;
            }
        }

        public MyJDBCHelper(Connection connection) throws SQLException {
            con  = connection;
            stmt = con.createStatement();
        }

        public ResultSet execute(String query) {
            try {
                System.out.println("-"+query);
                rs = stmt.executeQuery(query);
                return rs;
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return null;
        }

        public boolean next() {
            try {
                return rs.next();
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return false;
        }

        public void close() {
            try {
                if(rs != null) { rs.close(); }
                stmt.close();
                con.close();
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }

        }
    }
