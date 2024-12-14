package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ColumnDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.GridDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.moveINSA.gui.MainLayout;

import com.vaadin.flow.component.UI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Classe pour afficher les offres de mobilité.
 * 
 * @author francois
 */
@PageTitle("MoveINSA")
@Route(value = "offres/liste", layout = MainLayout.class)
public class OffresPanel extends VerticalLayout {

    // Classe interne pour représenter des icônes en fonction d'un nombre
    public static class IntAsIcon extends HorizontalLayout {
        public IntAsIcon(int nbr) {
            for (int i = 0; i < nbr; i++) {
                this.add(new Icon(VaadinIcon.EXIT));
            }
        }
    }

    private ResultSetGrid gOffres;
    private Button bPostule;

    public OffresPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            this.add(new H2("Affichage des offres de mobilité (tables formatées)"));

            // Création de la table pour afficher les offres
            PreparedStatement offresAvecPart = con.prepareStatement(
                    "SELECT offremobilite.idOffre AS idOffre, " +
                    "       partenaire.refPartenaire AS refPartenaire, " +
                    "       offremobilite.nbrplaces AS nbrPlaces, " +
                    "       partenaire.id AS idPartenaire " +
                    "FROM OffreMobilite " +
                    "JOIN Partenaire ON OffreMobilite.proposepar = partenaire.id");

            this.gOffres = new ResultSetGrid(offresAvecPart, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false), // ID de l'offre (non affichée)
                    new ColumnDescription().colData(1).headerString("Partenaire"),
                    new ColumnDescription().colData(2).headerString("Nombre de places"),
                    new ColumnDescription().colDataCompo(2, (t) -> new IntAsIcon((Integer) t)).headerString("Places"),
                    new ColumnDescription().colCalculatedObject((t) -> t.get(1) + " : " + t.get(2)).headerString("Résumé"),
                    new ColumnDescription().colData(3).visible(false) // ID du partenaire (non affichée)
            )));
            this.add(new H3("Offres de mobilité avec mise en forme"));
            this.add(this.gOffres);

            // Ajout du bouton "Postuler"
            bPostule = new Button("Postuler");
            bPostule.addClickListener(e -> {
                handlePostulerClick();
            });
            this.add(this.bPostule);

            // Création de la table groupée par partenaires
            PreparedStatement offresParPartenaire = con.prepareStatement(
                    "SELECT partenaire.id AS idPartenaire, " +
                    "       partenaire.refPartenaire AS refPartenaire, " +
                    "       SUM(offremobilite.nbrplaces) AS placesPartenaire, " +
                    "       (SELECT SUM(nbrplaces) FROM offremobilite) AS totPlaces " +
                    "FROM OffreMobilite " +
                    "JOIN Partenaire ON offremobilite.proposepar = partenaire.id " +
                    "GROUP BY partenaire.id");

            ResultSetGrid parPart = new ResultSetGrid(offresParPartenaire, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false), // ID du partenaire (non affichée)
                    new ColumnDescription().colData(1).headerString("Partenaire"),
                    new ColumnDescription().colData(2).headerString("Total places offertes"),
                    new ColumnDescription().colCalculatedObject((t) -> {
                        int nbrPart = Integer.parseInt("" + t.get(2));
                        int nbrTot = Integer.parseInt("" + t.get(3));
                        double percent = ((double) nbrPart) / nbrTot * 100;
                        return String.format("%.0f%%", percent);
                    }).headerString("Pourcentage")
            )));
            this.add(new H3("Offres groupées par partenaires"));
            this.add(parPart);

        } catch (SQLException ex) {
            logAndNotifyError(ex, "Erreur lors du chargement des données");
        }
    }

    /**
     * Gère le clic sur le bouton "Postuler".
     */
    private void handlePostulerClick() {
        Set<List<Object>> lignesSelected = this.gOffres.getSelectedItems();
        if (lignesSelected.isEmpty()) {
            Notification.show("Veuillez sélectionner une offre.");
        } else {
            try {
                List<Object> ligne = lignesSelected.iterator().next();
                Integer idOffre = (Integer) ligne.get(0); // L'ID de l'offre est en première position
                UI.getCurrent().navigate("candidature/" + idOffre);
            } catch (Exception ex) {
                logAndNotifyError(ex, "Erreur lors de la navigation vers la candidature");
            }
        }
    }

    /**
     * Affiche un message d'erreur et loggue les détails.
     * 
     * @param ex Exception levée
     * @param message Message utilisateur
     */
    private void logAndNotifyError(Exception ex, String message) {
        System.err.println(message + ": " + ex.getLocalizedMessage());
        ex.printStackTrace();
        Notification.show(message + ". Consultez les logs pour plus de détails.");
    }
}
