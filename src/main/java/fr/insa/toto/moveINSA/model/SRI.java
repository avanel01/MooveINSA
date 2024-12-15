package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.list.ListUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.Optional;

public class SRI {
    
    private int idPersonnel;
    private String login;
    private String motDePasse;
    
    // Liste des attributions effectuées
    private List<Attribution> attributions = new ArrayList<>();

    public SRI(int idPersonnel, String login, String motDePasse) {
        this.idPersonnel = idPersonnel;
        this.login = login;
        this.motDePasse = motDePasse;
    }
    
    @Override
    public String toString() {
        return "SRI{" 
            + "idPersonnel=" + this.getIdPersonnel()
            + ", login='" + login + '\'' 
            + ", motDePasse='" + motDePasse + '\'' 
            + '}';
    }

    // Sauvegarde du SRI dans la base de données
    public int saveInDB(Connection con) throws SQLException {
        if (this.getIdPersonnel() != -1) {
            throw new EntiteDejaSauvegardee(); // Exception personnalisée
        }
        try (PreparedStatement insert = con.prepareStatement(
                "insert into SRI (login, motDePasse) values (?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, this.getLogin());
            insert.setString(2, this.getMdp());
            insert.executeUpdate();

            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idPersonnel = rid.getInt(1); // récupération de l'ID généré
                    return this.getIdPersonnel();
                } else {
                    throw new SQLException("Échec de la récupération de la clé générée.");
                }
            }
        }
    }

    // Méthode d'attribution des candidatures aux étudiants
    public void attribuer(List<Candidature> candidatures) {
        // Map pour suivre les offres déjà attribuées
        Map<Integer, Attribution> offresAttribuees = new HashMap<>();
        // Set pour suivre les étudiants déjà attribués
        Set<String> etudiantsAttribues = new HashSet<>();

        // Comparator personnalisé pour trier les candidatures
        candidatures.sort(Comparator
            .comparing(Candidature::getIdOffre)
            .thenComparing(Candidature::getOrdre)
            .thenComparing(Candidature::getClassement)
        );

        int idAttributionCounter = 1;

        // Parcourir toutes les candidatures
        for (Candidature candidature : candidatures) {
            int idOffre = candidature.getIdOffre();
            String idEtudiant = candidature.getIdEtudiant();

            // Vérifier si l'étudiant a déjà une attribution
            if (etudiantsAttribues.contains(idEtudiant)) {
                continue; // L'étudiant a déjà été attribué à une offre
            }

            // Vérifier si l'offre n'a pas encore été attribuée
            if (!offresAttribuees.containsKey(idOffre)) {
                // Créer une nouvelle attribution
                Attribution attribution;
                attribution = new Attribution(idAttributionCounter++, idOffre, idEtudiant, candidature.getDate());
                // Ajouter l'attribution à la liste
                attributions.add(attribution);
                // Marquer l'offre comme attribuée
                offresAttribuees.put(idOffre, attribution);
                // Marquer l'étudiant comme ayant reçu une attribution
                etudiantsAttribues.add(idEtudiant);
            }
        }
    }

    // Méthode pour afficher les attributions
    public void afficherAttributions() {
        for (Attribution attribution : attributions) {
            System.out.println("Attribution ID: " + attribution.getIdAttribution() +
                               ", Offre ID: " + attribution.getIdOffre() +
                               ", Etudiant ID: " + attribution.getIdEtudiant() +
                               ", Date: " + attribution.getDate());
        }
    }
    
    public static Optional<SRI> getSRIByLogin(Connection con, String login) throws SQLException {
    String sql = "SELECT idPersonnel, login, motDePasse FROM SRI WHERE login = ?";
    
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, login); // Remplace le paramètre dans la requête SQL
        
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // Crée un objet SRI avec les données trouvées
                SRI sri = new SRI(
                    rs.getInt("idPersonnel"), // Assurez-vous que la colonne dans la table est bien "idPersonnel"
                    rs.getString("login"),
                    rs.getString("motDePasse")
                );
                return Optional.of(sri);
            } else {
                return Optional.empty(); // Aucun SRI trouvé pour ce login
            }
        }
    }
}
    
    // Getters et setters
    public String getLogin() {
        return login;
    }

    public String getMdp() {
        return motDePasse;
    }

    public void setLogin(String login) {
        this.login = login;
    } 

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public int getIdPersonnel() {
        return idPersonnel;
    }

    public void setIdPersonnel(int idPersonnel) {
        this.idPersonnel = idPersonnel;
    }
}
