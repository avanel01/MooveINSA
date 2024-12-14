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
 * Classe "miroir" de la table Etudiant.
 * Pour interfacer facilement un programme Java avec une base de données relationnelle,
 * il est souvent pratique de définir des classes correspondant aux tables d'entités.
 * Nous éviterons l'utilisation d'un ORM pour rester dans l'esprit pédagogique.
 *
 * @author francois
 */
public class Etudiant {

    private String INE;
    private String nomEtudiant;
    private String prenom;
    private String classe;
    private int annee;
    private int classement;
    private String mdp;

    /**
     * Constructeur minimaliste.
     *
     * @param INE Identifiant de l'étudiant
     */
    public Etudiant(String INE) {
        if (!INE.matches("^[0-9]{9}[A-Z]{2}$")) {
        throw new IllegalArgumentException("INE invalide : " + INE);
    }
        this.INE = INE;
    }

    /**
     * Constructeur complet.
     */
    public Etudiant(String INE, String nomEtudiant, String prenom, String classe, int annee, int classement, String mdp) {
    if (!INE.matches("^[0-9]{9}[A-Z]{2}$")) {
        throw new IllegalArgumentException("INE invalide : " + INE);
    }
    this.INE = INE;
    this.nomEtudiant = nomEtudiant;
    this.prenom = prenom;
    this.classe = classe;
    this.annee = annee;
    this.classement = classement;
    this.mdp = mdp;
}

   /**
 * Récupère un étudiant à partir de son INE.
 *
 * @param con Connexion à la base de données
 * @param INE Identifiant unique de l'étudiant
 * @return Un Optional contenant l'étudiant s'il existe, ou un Optional vide sinon
 * @throws SQLException En cas de problème avec la base de données
 */
public static Optional<Etudiant> getEtudiantByINE(Connection con, String INE) throws SQLException {
    // Mise à jour de la requête SQL pour utiliser nometudiant au lieu de nom
    String sql = "SELECT INE, nometudiant, prenom, classe, annee, classement, mdp FROM Etudiant WHERE INE = ?";
    
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, INE); // Remplace le paramètre INE dans la requête SQL
        
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // Crée un objet Etudiant avec les données trouvées
                Etudiant etudiant = new Etudiant(
                    rs.getString("INE"),
                    rs.getString("nometudiant"),  // Correspondance avec la colonne nometudiant
                    rs.getString("prenom"),
                    rs.getString("classe"),
                    rs.getInt("annee"),
                    rs.getInt("classement"),
                    rs.getString("mdp")
                );
                return Optional.of(etudiant);
            } else {
                return Optional.empty(); // Aucun étudiant trouvé pour cet INE
            }
        }
    }
}

    // Getters et setters
    public String getINE() {
        return INE;
    }

    public void setINE(String INE) {
        this.INE = INE;
    }

    public String getNomEtudiant() {
        return nomEtudiant;  // Récupère la valeur de la colonne 'nometudiant'
    }

    
    public String getPrenom() {
        return prenom;
    }

    

    public String getClasse() {
        return classe;
    }

    

    public int getAnnee() {
        return annee;
    }

    
    public int getClassement() {
        return classement;
    }

    

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    /**
     * Calcule un score basé sur le classement et l'effectif de la classe.
     *
     * @param con
     * @param effectif Effectif total de la classe
     * @return Score de l'étudiant
     * @throws IllegalStateException Si l'étudiant n'est associé à aucune classe
     */
   public double score(Connection con) {
    if (classe == null) {
        throw new IllegalStateException("La classe n'est pas définie.");
    }

    try {
        // Récupère l'objet Classe à partir du nom de la classe
        Classe classeObjet = Classe.recupererParNomClasse(con, classe);
        if (classeObjet == null || classeObjet.getEffectifClasse() <= 0) {
            throw new IllegalStateException("Classe introuvable ou effectif invalide.");
        }

        // Calcul du score
        return (double) classement / classeObjet.getEffectifClasse();
    } catch (SQLException e) {
        throw new IllegalStateException("Erreur lors de la récupération de la classe.", e);
    }
}


    @Override
    public String toString() {
        return "Etudiant{" +
                "INE='" + INE + '\'' +
                ", nom='" + nomEtudiant + '\'' +
                ", prenom='" + prenom + '\'' +
                ", classe='" + classe + '\'' +
                ", annee=" + annee +
                ", classement=" + classement +
                ", mdp='" + mdp + '\'' +
                '}';
    }

   /**
 * Sauvegarde un étudiant dans la base de données. Si un étudiant avec le même
 * INE existe déjà, une exception est levée.
 *
 * @param con Connexion à la base de données
 * @return L'INE de l'étudiant après insertion
 * @throws EntiteDejaSauvegardee Si un étudiant avec cet INE existe déjà
 * @throws SQLException En cas de problème avec la base de données
 */
public String saveInDB(Connection con) throws SQLException, EntiteDejaSauvegardee {
    // Vérifie si l'INE est déjà défini
    if (this.INE != null) {
        // Vérifie dans la base de données si un étudiant avec le même INE existe
        String sqlCheck = "SELECT INE FROM Etudiant WHERE INE = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(sqlCheck)) {
            checkStmt.setString(1, this.INE);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new EntiteDejaSauvegardee("Un étudiant avec cet INE existe déjà : " + this.INE);
                }
            }
        }
    }

    // Insère un nouvel étudiant dans la base de données
    String sqlInsert = "INSERT INTO Etudiant (INE, nometudiant, prenom, classe, annee, classement, mdp) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement insertStmt = con.prepareStatement(sqlInsert)) {
        insertStmt.setString(1, this.INE);
        insertStmt.setString(2, this.nomEtudiant);
        insertStmt.setString(3, this.prenom);
        insertStmt.setString(4, this.classe);
        insertStmt.setInt(5, this.annee);
        insertStmt.setInt(6, this.classement);
        insertStmt.setString(7, this.mdp);

        int rowsAffected = insertStmt.executeUpdate();
        if (rowsAffected > 0) {
            return this.INE;
        } else {
            throw new SQLException("Erreur lors de l'insertion de l'étudiant.");
        }
    }
}


/**
 * Vérifie si un INE existe déjà dans la base de données.
 *
 * @param con Connexion à la base de données
 * @param ine L'INE à vérifier
 * @return true si l'INE existe déjà, false sinon
 * @throws SQLException En cas de problème avec la base de données
 */
private static boolean ineExisteDeja(Connection con, String ine) throws SQLException {
    String sql = "SELECT COUNT(*) FROM Etudiant WHERE INE = ?";
    try (PreparedStatement check = con.prepareStatement(sql)) {
        check.setString(1, ine);
        try (ResultSet rs = check.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retourne vrai si au moins une correspondance
            }
        }
    }
    return false;
}

    /**
     * Retourne tous les étudiants de la base de données.
     * @param con
     * @return 
     * @throws java.sql.SQLException
     */
    public static List<Etudiant> tousLesEtudiants(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT INE, nom, prenom, classe, annee, classement, mdp FROM Etudiant")) {
            ResultSet rs = pst.executeQuery();
            List<Etudiant> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Etudiant(
                        rs.getString("INE"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("classe"),
                        rs.getInt("annee"),
                        rs.getInt("classement"),
                        rs.getString("mdp")
                 ));
            }
            return res;
        }
    }

    /**
     * Création d'un étudiant via console.
     */
    public static String creeConsole(Connection con) throws SQLException, EntiteDejaSauvegardee {
        String nom = ConsoleFdB.entreeString("Nom : ");
        String prenom = ConsoleFdB.entreeString("Prénom : ");
        String classe = ConsoleFdB.entreeString("Classe : ");
        int annee = ConsoleFdB.entreeInt("Année : ");
        int classement = ConsoleFdB.entreeInt("Classement : ");
        String mdp = ConsoleFdB.entreeString("Mot de passe : ");

        Etudiant nouveau = new Etudiant(null, nom, prenom, classe, annee, classement, mdp);
        return nouveau.saveInDB(con);
    }
}


