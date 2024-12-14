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
 * <p>
 * Pour un commentaire plus détaillé sur ces classes "miroir", voir dans la
 * classe Partenaire.
 * </p>
 *
 * @author francois
 */
public class OffreMobilite {

    private int idOffre;
    private int nbrPlaces;
    private int proposePar;
    private int semestre;
    private int niveauScolaire;
    private String dispositif;
    private String nomOffre;
    private String specialiteAssocie;
    private List<Integer> semestres; // Liste des semestres associés à l'offre, corrigée en List<Integer>

    /**
     * Création d'une nouvelle Offre en mémoire, non existante dans la base de
     * données.
     * @param nbrPlaces
     * @param proposePar
     * @param semestre
     * @param niveauScolaire
     * @param dispositif
     * @param nomOffre
     * @param specialiteAssocie
     */
    public OffreMobilite(int nbrPlaces, int proposePar, int semestre, int niveauScolaire, String dispositif, String nomOffre, String specialiteAssocie) {
        this(-1, nbrPlaces, proposePar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie);
    }

    /**
     * Création d'une Offre retrouvée dans la base de données.
     * @param id
     * @param nbrPlaces
     * @param proposePar
     * @param semestre
     * @param niveauScolaire
     * @param dispositif
     * @param nomOffre
     * @param specialiteAssocie
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
                '}';
    }

    /**
     * Sauvegarde une nouvelle entité et retourne la clé affectée automatiquement
     * par le SGBD.
     * <p>
     * La clé est également sauvegardée dans l'attribut id.
     * </p>
     *
     * @param con
     * @return la clé de la nouvelle entité dans la table de la BdD
     * @throws SQLException si autre problème avec la BdD
     */
    public int saveInDB(Connection con) throws SQLException {
        if (this.getId() != -1) {
            throw new EntiteDejaSauvegardee();
        }
        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO OffreMobilite (nbrplaces, proposepar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie) VALUES (?,?,?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setInt(1, this.nbrPlaces);
            insert.setInt(2, this.proposePar);
            insert.setInt(3, this.semestre);
            insert.setInt(4, this.niveauScolaire);
            insert.setString(5, this.dispositif);
            insert.setString(6, this.nomOffre);
            insert.setString(7, this.specialiteAssocie);
            insert.executeUpdate();
            
            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idOffre = rid.getInt(1);
                    return this.getId();
                } else {
                    throw new SQLException("Échec de la création de l'offre, aucune clé générée.");
                }
            }
        }
    }

    /**
     * Récupère toutes les offres de mobilité de la base de données.
     *
     * @param con la connexion à la base de données
     * @return une liste de toutes les offres de mobilité
     * @throws SQLException si un problème survient lors de la récupération
     */
    public static List<OffreMobilite> toutesLesOffres(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT idOffre, nbrplaces, proposepar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie FROM OffreMobilite")) {
            ResultSet rs = pst.executeQuery();
            List<OffreMobilite> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new OffreMobilite(
                        rs.getInt("idOffre"), 
                        rs.getInt("nbrplaces"), 
                        rs.getInt("proposepar"), 
                        rs.getInt("semestre"), 
                        rs.getInt("niveauScolaire"), 
                        rs.getString("dispositif"), 
                        rs.getString("nomOffre"), 
                        rs.getString("specialiteAssocie")
                ));
            }
            return res;
        }
    }

    /**
     * Crée une offre de mobilité en demandant les informations via la console.
     *
     * @param con la connexion à la base de données
     * @return l'ID de l'offre créée
     * @throws SQLException si un problème survient lors de l'enregistrement
     */
    public static int creeConsole(Connection con) throws SQLException {
        int nbr = ConsoleFdB.entreeInt("Nombre de places : ");
        int par = ConsoleFdB.entreeInt("Proposé par : ");
        int s = ConsoleFdB.entreeInt("Semestre proposé : ");
        int niv = ConsoleFdB.entreeInt("Niveau scolaire : ");
        String dispositif = ConsoleFdB.entreeString("Type de dispositif : ");
        String nom = ConsoleFdB.entreeString("Nom de l'offre : ");
        String spe = ConsoleFdB.entreeString("Pour quelle spécialité : ");
        
        OffreMobilite nouveau = new OffreMobilite(nbr, par, s, niv, dispositif, nom, spe);
        return nouveau.saveInDB(con);
    }

    /**
     * Récupère une offre de mobilité par son ID.
     * 
     * @param con la connexion à la base de données
     * @param idOffre l'ID de l'offre
     * @return une instance d'OffreMobilite ou un Optional vide si non trouvé
     */
    public static Optional<OffreMobilite> getOffreById(Connection con, int idOffre) {
        String query = "SELECT * FROM OffreMobilite WHERE idOffre = ?"; // Requête SQL

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, idOffre);  // Paramétrage de la requête avec l'ID de l'offre

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

                    // Récupération des semestres associés
                    offre.setSemestres(getSemestresForOffre(con, idOffre));

                    return Optional.of(offre); // Retourne l'offre trouvée
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Optional.empty(); // Retourne un Optional vide si l'offre n'a pas été trouvée
    }

    /**
     * Récupère les semestres associés à une offre de mobilité.
     * 
     * @param con la connexion à la base de données
     * @param idOffre l'ID de l'offre
     * @return une liste de semestres associés à l'offre
     */
    private static List<Integer> getSemestresForOffre(Connection con, int idOffre) {
        List<Integer> semestres = new ArrayList<>();
        String query = "SELECT semestre FROM SemestresOffre WHERE idOffre = ?"; // Exemple de requête

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, idOffre);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    semestres.add(rs.getInt("semestre")); // Ajout des semestres en tant qu'entiers
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return semestres;
    }

    // Getters et Setters
    public int getId() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
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
