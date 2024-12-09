package fr.insa.toto.moveINSA.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestion simple de connexions MySQL.
 */
public class ConnectionSimpleSGBD {

    /**
     * Crée une connexion MySQL avec les paramètres donnés.
     * @param host L'adresse de l'hôte (ex. : 92.222.25.165).
     * @param port Le port MySQL (par défaut : 3306).
     * @param database Le nom de la base de données.
     * @param user L'utilisateur MySQL.
     * @param pass Le mot de passe MySQL.
     * @return Une connexion MySQL.
     * @throws SQLException Si une erreur de connexion se produit.
     */
    public static Connection connectMySQL(String host, int port, String database, String user, String pass) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Driver MySQL non trouvé", ex);
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC";
        Connection con = DriverManager.getConnection(url, user, pass);

        // Assure un isolement transactionnel élevé
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }

    /**
     * Connexion par défaut pour le module M3.
     * @return Une connexion à la base MySQL par défaut.
     * @throws SQLException Si une erreur de connexion se produit.
     */
    public static Connection mysqlServeurPourM3() throws SQLException {
        return connectMySQL("92.222.25.165", 3306, "m3_mhirschy01", "m3_mhirschy01", "be111c10");
    }
    
     public static String sqlForGeneratedKeys(Connection con, String nomColonne) throws SQLException {
        String sgbdName = con.getMetaData().getDatabaseProductName();
        if (sgbdName.equals("MySQL")) {
            return nomColonne + "  INT AUTO_INCREMENT PRIMARY KEY";
        } else {
            return nomColonne + " INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY";
        }
    }
     
     /**
     * permet de basculer facilement entre plusieurs SGBD.
     * <p>
     * il suffit de commenter/decommenter pour avoir la ligne correspondant au
     * sgbd que vous souhaitez.
     * </p>
     * <p>
     * ATTENTION : chaque appel à defautCon crée une nouvelle connection à la
     * BdD. Vous ne devez pas l'utiliser à chaque fois que vous voulez accéder à
     * la base de donnée. Vous devez conserver la connection renvoyée par
     * defaultCon pour la ré-utiliser.
     * </p>
     *
     * @return
     */
    public static Connection defaultCon() throws SQLException {
        return mysqlServeurPourM3();
//        return h2InMemory("test");
//        return h2InFile("bdd");
    }

}
