package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.model.Etudiant;
import fr.insa.toto.moveINSA.model.SRI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AttributionPanel : gestion de l'affichage des attributions pour les étudiants et le SRI.
 */
@Route(value = "attribution", layout = MainLayout.class)
@PageTitle("Attribution")
public class AttributionPanel extends VerticalLayout {

    private Grid<AttributionData> gAttribution;

    /**
     * Configuration initiale de la page selon le type d'utilisateur.
     */
    private void configPage() {
        Object user = VaadinSession.getCurrent().getAttribute("user");

        if (user instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) user;
            configureGridEtu(etudiant);
        } else if (user instanceof SRI) {
            SRI sri = (SRI) user;
            configureGridSRI(sri);
        } else {
            Notification.show("Erreur : Veuillez vous connecter.");
            UI.getCurrent().navigate("connexion");
        }
    }

    /**
     * Configuration de la grille pour un étudiant.
     * 
     * @param etudiant L'étudiant connecté.
     */
    private void configureGridEtu(Etudiant etudiant) {
        try (Connection con = ConnectionPool.getConnection()) {
            String ine = etudiant.getINE();

            // Requête SQL corrigée
            PreparedStatement offresAvecPart = con.prepareStatement(
                "SELECT Attribution.idAttribution, " +
                "       Attribution.idOffre, " +
                "       OffreMobilite.nomOffre, " +
                "       Partenaire.refPartenaire, " +
                "       Attribution.date " +
                "FROM Attribution " +
                "JOIN OffreMobilite ON Attribution.idOffre = OffreMobilite.idOffre " +
                "JOIN Partenaire ON OffreMobilite.idPartenaire = Partenaire.idPartenaire " +
                "WHERE Attribution.idEtudiant = ?"
            );
            offresAvecPart.setString(1, ine);

            // Exécuter la requête
            try (ResultSet resultSet = offresAvecPart.executeQuery()) {
                List<AttributionData> attributions = new ArrayList<>();
                while (resultSet.next()) {
                    attributions.add(new AttributionData(
                        resultSet.getInt("idAttribution"),
                        resultSet.getInt("idOffre"),
                        resultSet.getString("nomOffre"),
                        resultSet.getString("refPartenaire"),
                        resultSet.getInt("date")
                    ));
                }

                if (attributions.isEmpty()) {
                    this.add(new H3("Aucune attribution trouvée."));
                    return;
                }

                // Configuration de la grille
                this.gAttribution = new Grid<>(AttributionData.class);
                this.gAttribution.setItems(attributions);
                this.gAttribution.setColumns("nomOffre", "refPartenaire", "date");

                this.gAttribution.getColumnByKey("nomOffre").setHeader("Intitulé de l'offre");
                this.gAttribution.getColumnByKey("refPartenaire").setHeader("Partenaire");
                this.gAttribution.getColumnByKey("date").setHeader("Date d'attribution");

                // Styles de la grille
                styleGrid();

                // Ajout des composants à la vue
                this.add(new H3("Voici votre attribution :"));
                this.add(this.gAttribution);
            }
        } catch (SQLException ex) {
            logAndNotifyError(ex, "Erreur lors du chargement des données.");
        }
    }

    /**
     * Configuration de la grille pour un utilisateur SRI.
     * 
     * @param sri L'utilisateur SRI connecté.
     */
    private void configureGridSRI(SRI sri) {
        try (Connection con = ConnectionPool.getConnection()) {
            // Requête SQL corrigée
            PreparedStatement offresAvecPart = con.prepareStatement(
                "SELECT Attribution.idAttribution, " +
                "       Attribution.idEtudiant, " +
                "       Attribution.idOffre, " +
                "       Etudiant.nomEtudiant, " +
                "       Etudiant.prenom, " +
                "       OffreMobilite.nomOffre, " +
                "       Partenaire.refPartenaire, " +
                "       Attribution.date " +
                "FROM Attribution " +
                "JOIN OffreMobilite ON Attribution.idOffre = OffreMobilite.idOffre " +
                "JOIN Partenaire ON OffreMobilite.idPartenaire = Partenaire.idPartenaire " +
                "JOIN Etudiant ON Attribution.idEtudiant = Etudiant.INE"
            );

            try (ResultSet resultSet = offresAvecPart.executeQuery()) {
                List<AttributionData> attributions = new ArrayList<>();
                while (resultSet.next()) {
                    attributions.add(new AttributionData(
                        resultSet.getInt("idAttribution"),
                        resultSet.getInt("idOffre"),
                        resultSet.getString("nomOffre"),
                        resultSet.getString("refPartenaire"),
                        resultSet.getInt("date")
                    ));
                }

                if (attributions.isEmpty()) {
                    this.add(new H3("Aucune attribution trouvée pour le SRI."));
                    return;
                }

                // Configuration de la grille
                this.gAttribution = new Grid<>(AttributionData.class);
                this.gAttribution.setItems(attributions);
                this.gAttribution.setColumns("nomOffre", "refPartenaire", "date");

                this.gAttribution.getColumnByKey("nomOffre").setHeader("Intitulé de l'offre");
                this.gAttribution.getColumnByKey("refPartenaire").setHeader("Partenaire");
                this.gAttribution.getColumnByKey("date").setHeader("Date d'attribution");

                // Styles de la grille
                styleGrid();

                // Ajout des composants à la vue
                this.add(new H3("Attribution des étudiants :"));
                this.add(this.gAttribution);
            }
        } catch (SQLException ex) {
            logAndNotifyError(ex, "Erreur lors du chargement des données.");
        }
    }

    /**
     * Applique des styles à la grille.
     */
    private void styleGrid() {
        this.gAttribution.getStyle()
            .set("width", "80%")
            .set("margin", "20px auto")
            .set("border", "1px solid #ccc")
            .set("border-radius", "5px")
            .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
            .set("background-color", "white");
    }

    /**
     * Méthode utilitaire pour gérer et notifier les erreurs.
     * 
     * @param ex Exception SQL capturée.
     * @param message Message personnalisé à afficher.
     */
    private void logAndNotifyError(SQLException ex, String message) {
        ex.printStackTrace(); // Log pour le développeur
        Notification.show(message); // Notification utilisateur
    }

    /**
     * Classe représentant une attribution pour la grille.
     */
    public static class AttributionData {
        private int idAttribution;
        private int idOffre;
        private String nomOffre;
        private String refPartenaire;
        private int date;

        public AttributionData(int idAttribution, int idOffre, String nomOffre, String refPartenaire, int date) {
            this.idAttribution = idAttribution;
            this.idOffre = idOffre;
            this.nomOffre = nomOffre;
            this.refPartenaire = refPartenaire;
            this.date = date;
        }

        public String getNomOffre() {
            return nomOffre;
        }

        public String getRefPartenaire() {
            return refPartenaire;
        }

        public int getDate() {
            return date;
        }
    }
}
