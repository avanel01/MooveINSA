package fr.insa.beuvron.vaadin.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestion d'un pool de connexions pour MySQL avec HikariCP.
 */
public class ConnectionPool {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        // Configuration pour MySQL
        config.setJdbcUrl("jdbc:mysql://92.222.25.165:3306/m3_mhirschy01"); // Remplacez par votre URL MySQL
        config.setUsername("m3_mhirschy01"); // Remplacez par votre utilisateur
        config.setPassword("be111c10"); // Remplacez par votre mot de passe
        config.setMaximumPoolSize(50); // Taille maximale du pool

        // Optimisations pour MySQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);
    }

    /**
     * Retourne une connexion depuis le pool.
     * @return Une instance de connexion.
     * @throws SQLException Si une connexion ne peut pas être obtenue.
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     * Ferme le pool de connexions.
     */
    public static void close() {
        if (ds != null) {
            ds.close();
        }
    }
}
