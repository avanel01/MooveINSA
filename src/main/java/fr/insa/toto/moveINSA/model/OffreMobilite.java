package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe "miroir" de la table offre_mobilite.
 * Représente une offre de mobilité enregistrée dans la base de données.
 */
public class OffreMobilite {

    private int idOffre;
    private int nbrPlaces;
    private int proposePar;
    private int semestre; // Semestre principal de l'offre
    private int niveauScolaire;
    private String dispositif;
    private String nomOffre;
    private String specialiteAssocie;
    private List<Integer> semestres; // Liste des semestres associés

    /**
     * Constructeur pour une nouvelle Offre en mémoire, non existante dans la base de données.
     */
    public OffreMobilite(int nbrPlaces, int proposePar, int semestre, int niveauScolaire, String dispositif, String nomOffre, String specialiteAssocie) {
        this(-1, nbrPlaces, proposePar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie);
    }

    /**
     * Constructeur pour une Offre retrouvée dans la base de données.
     */
    public OffreMobilite(int id, int nbrPlaces, int proposePar, int semestre, int niveauScolaire, String dispositif, String nomOffre, String specialiteAssocie) {
        this.idOffre = id;
        this.nbrPlaces = nbrPlaces;
        this.proposePar = proposePar;
        this.semestre = semestre;
        this.niveauScolaire = niveauScolaire;
        this.dispositif = dispositif;
        this.nomOffre = nomOffre;
        this.specialiteAssocie = specialiteAssocie;
        this.semestres = new ArrayList<>(); // Initialisation de la liste des semestres
    }

    @Override
    public String toString() {
        return "OffreMobilite{" +
                "idOffre=" + idOffre +
                ", nbrPlaces=" + nbrPlaces +
                ", proposePar=" + proposePar +
                ", semestre=" + semestre +
                ", niveauScolaire=" + niveauScolaire +
                ", dispositif='" + dispositif + '\'' +
                ", nomOffre='" + nomOffre + '\'' +
                ", specialiteAssocie='" + specialiteAssocie + '\'' +
                ", semestres=" + semestres +
                '}';
    }

    /**
     * Sauvegarde une nouvelle Offre dans la base de données.
     */
    public int saveInDB(Connection con) throws SQLException {
        if (this.idOffre != -1) {
            throw new IllegalStateException("Cette offre a déjà été sauvegardée.");
        }
        String sql = "INSERT INTO OffreMobilite (nbrplaces, proposepar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement insert = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setInt(1, this.nbrPlaces);
            insert.setInt(2, this.proposePar);
            insert.setInt(3, this.semestre);
            insert.setInt(4, this.niveauScolaire);
            insert.setString(5, this.dispositif);
            insert.setString(6, this.nomOffre);
            insert.setString(7, this.specialiteAssocie);
            insert.executeUpdate();

            try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.idOffre = generatedKeys.getInt(1);
                    return this.idOffre;
                } else {
                    throw new SQLException("Échec de la sauvegarde de l'offre, aucune clé générée.");
                }
            }
        }
    }

    /**
     * Récupère toutes les offres de mobilité depuis la base de données.
     */
    public static List<OffreMobilite> toutesLesOffres(Connection con) throws SQLException {
        String query = "SELECT idOffre, nbrplaces, proposepar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie FROM OffreMobilite";
        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            List<OffreMobilite> offres = new ArrayList<>();
            while (rs.next()) {
                OffreMobilite offre = new OffreMobilite(
                        rs.getInt("idOffre"),
                        rs.getInt("nbrplaces"),
                        rs.getInt("proposepar"),
                        rs.getInt("semestre"),
                        rs.getInt("niveauScolaire"),
                        rs.getString("dispositif"),
                        rs.getString("nomOffre"),
                        rs.getString("specialiteAssocie")
                );
                offres.add(offre);
            }
            return offres;
        }
    }

    /**
     * Crée une offre via une saisie console.
     */
    public static int creeConsole(Connection con) throws SQLException {
        int nbr = ConsoleFdB.entreeInt("Nombre de places : ");
        int par = ConsoleFdB.entreeInt("Proposé par : ");
        int s = ConsoleFdB.entreeInt("Semestre proposé : ");
        int niv = ConsoleFdB.entreeInt("Niveau scolaire : ");
        String dispositif = ConsoleFdB.entreeString("Type de dispositif : ");
        String nom = ConsoleFdB.entreeString("Nom de l'offre : ");
        String spe = ConsoleFdB.entreeString("Pour quelle spécialité : ");

        OffreMobilite offre = new OffreMobilite(nbr, par, s, niv, dispositif, nom, spe);
        return offre.saveInDB(con);
    }

    /**
     * Récupère une offre par son ID.
     */
    public static Optional<OffreMobilite> getOffreById(Connection con, int idOffre) throws SQLException {
        String query = "SELECT * FROM OffreMobilite WHERE idOffre = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, idOffre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OffreMobilite offre = new OffreMobilite(
                            rs.getInt("idOffre"),
                            rs.getInt("nbrplaces"),
                            rs.getInt("proposepar"),
                            rs.getInt("semestre"),
                            rs.getInt("niveauScolaire"),
                            rs.getString("dispositif"),
                            rs.getString("nomOffre"),
                            rs.getString("specialiteAssocie")
                    );
                    offre.setSemestres(getSemestresForOffre(con, idOffre));
                    return Optional.of(offre);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Récupère les semestres associés à une offre.
     */
    private static List<Integer> getSemestresForOffre(Connection con, int idOffre) throws SQLException {
        String query = "SELECT semestre FROM SemestresOffre WHERE idOffre = ?";
        List<Integer> semestres = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, idOffre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    semestres.add(rs.getInt("semestre"));
                }
            }
        }
        return semestres;
    }

    // Getters et Setters
    public int getId() {
        return idOffre;
    }

    public void setId(int id) {
        this.idOffre = id;
    }

    public List<Integer> getSemestres() {
        return semestres;
    }

    public void setSemestres(List<Integer> semestres) {
        this.semestres = semestres;
    }

    public int getNbrPlaces() {
        return nbrPlaces;
    }

    public void setNbrPlaces(int nbrPlaces) {
        this.nbrPlaces = nbrPlaces;
    }

    public int getProposePar() {
        return proposePar;
    }

    public void setProposePar(int proposePar) {
        this.proposePar = proposePar;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public int getNiveauScolaire() {
        return niveauScolaire;
    }

    public void setNiveauScolaire(int niveauScolaire) {
        this.niveauScolaire = niveauScolaire;
    }

    public String getDispositif() {
        return dispositif;
    }

    public void setDispositif(String dispositif) {
        this.dispositif = dispositif;
    }

    public String getNomOffre() {
        return nomOffre;
    }

    public void setNomOffre(String nomOffre) {
        this.nomOffre = nomOffre;
    }

    public String getSpecialiteAssocie() {
        return specialiteAssocie;
    }

    public void setSpecialiteAssocie(String specialiteAssocie) {
        this.specialiteAssocie = specialiteAssocie;
    }
}
