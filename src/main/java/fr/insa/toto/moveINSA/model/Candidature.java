package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une candidature soumise par un étudiant pour une offre de mobilité.
 */
public class Candidature {

    private int idCandidature;
    private int idOffre;
    private String idEtudiant;
    private int ordre;
    private int classementEtudiant;
    private String date;

    // Constructeur
    public Candidature(int idCandidature, int idOffre, String idEtudiant, int ordre, int classementEtudiant, String date) {
        this.idCandidature = idCandidature;
        this.idOffre = idOffre;
        this.idEtudiant = idEtudiant;
        this.ordre = ordre;
        this.classementEtudiant = classementEtudiant;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "idCandidature=" + idCandidature +
                ", idOffre=" + idOffre +
                ", idEtudiant=" + idEtudiant +
                ", ordre=" + ordre +
                ", classementEtudiant=" + classementEtudiant +
                ", date='" + date + '\'' +
                '}';
    }

    /**
     * Sauvegarde une candidature dans la base de données et retourne l'ID généré.
     */
    public int saveInDB(Connection con) throws SQLException {
        String query = "INSERT INTO Candidature (idOffre, idEtudiant, ordre, classementEtudiant, date) VALUES (?,?,?,?,?)";
        try (PreparedStatement insert = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setInt(1, this.idOffre);
            insert.setString(2, this.idEtudiant);
            insert.setInt(3, this.ordre);
            insert.setInt(4, this.classementEtudiant);
            insert.setString(5, this.date);
            insert.executeUpdate();

            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idCandidature = rid.getInt(1);
                    return this.idCandidature;
                } else {
                    throw new SQLException("Échec de la création de la candidature : aucune clé générée.");
                }
            }
        }
    }

    public static int nombreCandidaturesEtudiant(Connection con, String idEtudiant) throws SQLException {
        String query = "SELECT COUNT(*) FROM Candidature WHERE idEtudiant = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, idEtudiant);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0; // Si aucune candidature n'existe
    }

    /**
     * Récupère toutes les candidatures de la base de données.
     */
    public static List<Candidature> toutesLesCandidatures(Connection con) throws SQLException {
        String query = "SELECT idCandidature, idOffre, idEtudiant, ordre, classementEtudiant, date FROM Candidature";
        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            List<Candidature> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Candidature(
                        rs.getInt("idCandidature"),
                        rs.getInt("idOffre"),
                        rs.getString("idEtudiant"),
                        rs.getInt("ordre"),
                        rs.getInt("classementEtudiant"),
                        rs.getString("date")
                ));
            }
            return res;
        }
    }

    public static List<Candidature> candidatureEtudiant(Connection con, String idEtudiant) throws SQLException {
        String query = "SELECT idCandidature, idOffre, ordre, classementEtudiant, date FROM Candidature WHERE idEtudiant = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, idEtudiant);

            try (ResultSet rs = pst.executeQuery()) {
                List<Candidature> res = new ArrayList<>();
                while (rs.next()) {
                    res.add(new Candidature(
                            rs.getInt("idCandidature"),
                            rs.getInt("idOffre"),
                            idEtudiant,
                            rs.getInt("ordre"),
                            rs.getInt("classementEtudiant"),
                            rs.getString("date")
                    ));
                }
                return res;
            }
        }
    }

    public static int creeConsole(Connection con) throws SQLException {
        String idEtudiant = ConsoleFdB.entreeString("ID de l'étudiant : ");

        int nombreCandidatures = Candidature.nombreCandidaturesEtudiant(con, idEtudiant);
        if (nombreCandidatures >= 5) {
            System.out.println("Cet étudiant a déjà soumis 5 candidatures. Impossible d'en soumettre davantage.");
            return -1;
        }

        int ordre = ConsoleFdB.entreeInt("Ordre de préférence (entre 1 et 5) : ");
        while (ordre < 1 || ordre > 5) {
            System.out.println("L'ordre de préférence doit être compris entre 1 et 5.");
            ordre = ConsoleFdB.entreeInt("Ordre de préférence (entre 1 et 5) : ");
        }

        if (Candidature.existeOrdrePourEtudiant(con, idEtudiant, ordre)) {
            System.out.println("Cet ordre de préférence est déjà utilisé pour une autre candidature.");
            return -1;
        }

        int idOffre = ConsoleFdB.entreeInt("ID de l'offre de mobilité : ");
        int classement = ConsoleFdB.entreeInt("Classement de l'étudiant : ");
        String date = ConsoleFdB.entreeString("Date de la candidature (YYYY-MM-DD) : ");

        Candidature nouvelle = new Candidature(-1, idOffre, idEtudiant, ordre, classement, date);
        return nouvelle.saveInDB(con);
    }

    public static boolean existeOrdrePourEtudiant(Connection con, String idEtudiant, int ordre) throws SQLException {
        String query = "SELECT COUNT(*) FROM candidature WHERE idEtudiant = ? AND ordre = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, idEtudiant);
            stmt.setInt(2, ordre);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Méthodes set
    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public void setIdEtudiant(String idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public void setClassement(int classementEtudiant) {
        this.classementEtudiant = classementEtudiant;
    }

    public void setDate(String dateSejour) {
        // Conversion de la chaîne en un objet Date (format attendu : YYYY-MM-DD)
        this.date = dateSejour;
    }
    
    public void setOrdre(int Ordre) {
        this.ordre = Ordre;
    }

    // Méthodes get (si nécessaires)
    public int getIdOffre() {
        return idOffre;
    }

    public String getIdEtudiant() {
        return idEtudiant;
    }

    public int getClassement() {
        return classementEtudiant;
    }

    public String getDate() {
        return date;
    }
    
    public int getOrdre() {
        return ordre;
    }
}
