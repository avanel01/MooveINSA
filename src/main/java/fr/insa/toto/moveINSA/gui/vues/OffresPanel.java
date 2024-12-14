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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ColumnDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.GridDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.Etudiant;

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

    private ResultSetGrid gOffres;
    private Button bPostule;
    private Etudiant etudiantConnecte;

    // Classe interne pour représenter des icônes en fonction d'un nombre
    public static class IntAsIcon extends HorizontalLayout {
        public IntAsIcon(int nbr) {
            for (int i = 0; i < nbr; i++) {
                this.add(new Icon(VaadinIcon.EXIT));
            }
        }
    }

    public OffresPanel() {
        // Initialisation de l'étudiant connecté
        etudiantConnecte = VaadinSession.getCurrent().getAttribute(Etudiant.class);
        if (etudiantConnecte == null) {
            Notification.show("Erreur : Aucun étudiant connecté. Veuillez vous connecter.");
            return;
        }

        try (Connection con = ConnectionPool.getConnection()) {
            // Mise en page globale
            configureLayout();

            // Titre principal
            H2 titre = new H2("Affichage des offres de mobilité");
            titre.getStyle().set("text-align", "center");
            this.add(titre);

            // Configuration de la grille principale
            configureOffresGrid(con);

            // Ajout du bouton "Postuler"
            configurePostulerButton();

            // Ajout de la section "Offres groupées par partenaires"
            configureGroupByPartnerGrid(con);

        } catch (SQLException ex) {
            logAndNotifyError(ex, "Erreur lors du chargement des données");
        }
    }

    private void configureLayout() {
        this.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("padding", "20px")
            .set("background-color", "#f9f9f9");
    }

    private void configureOffresGrid(Connection con) throws SQLException {
        PreparedStatement offresAvecPart = con.prepareStatement(
            "SELECT OffreMobilite.idOffre AS idOffre, " +
            "       Partenaire.refPartenaire AS refPartenaire, " +
            "       OffreMobilite.nbrPlaces AS nbrPlaces, " +
            "       Partenaire.idPartenaire AS idPartenaire, " +
            "       OffreMobilite.nomOffre AS nomOffre, " +
            "       OffreMobilite.specialiteAssocie AS spe " +
            "FROM OffreMobilite " +
            "JOIN Partenaire ON OffreMobilite.proposepar = Partenaire.idPartenaire"
        );

        this.gOffres = new ResultSetGrid(offresAvecPart, new GridDescription(List.of(
            new ColumnDescription().colData(0).visible(false), // ID de l'offre
            new ColumnDescription().colData(1).headerString("Partenaire"), // refPartenaire
            new ColumnDescription().colData(4).headerString("Intitulé de l'offre"), // nomOffre
            new ColumnDescription().colDataCompo(2, (nbrPlaces) -> 
                new IntAsIcon((Integer) nbrPlaces) // Composant graphique pour le nombre de places
            ).headerString("Places disponibles"),
            new ColumnDescription().colData(5).headerString("Spécialité") // specialiteAssocie
        )));

        this.gOffres.getStyle()
            .set("width", "80%")
            .set("margin", "20px auto")
            .set("border", "1px solid #ccc")
            .set("border-radius", "5px")
            .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
            .set("background-color", "white");

        this.add(new H3("Offres de mobilité avec mise en forme"));
        this.add(this.gOffres);
    }

    private void configurePostulerButton() {
        bPostule = new Button("Postuler", e -> handlePostulerClick());
        bPostule.getStyle()
            .set("margin-top", "20px")
            .set("padding", "10px 20px")
            .set("background-color", "#FF0000")
            .set("color", "white")
            .set("font-size", "16px")
            .set("font-weight", "bold")
            .set("border", "none")
            .set("border-radius", "5px")
            .set("cursor", "pointer");

        this.add(bPostule);
    }

    private void configureGroupByPartnerGrid(Connection con) throws SQLException {
        PreparedStatement offresParPartenaire = con.prepareStatement(
            "SELECT Partenaire.idPartenaire AS idPartenaire, " +
            "       Partenaire.refPartenaire AS refPartenaire, " +
            "       SUM(OffreMobilite.nbrplaces) AS placesPartenaire, " +
            "       (SELECT SUM(nbrplaces) FROM OffreMobilite) AS totPlaces " +
            "FROM OffreMobilite " +
            "JOIN Partenaire ON OffreMobilite.proposepar = Partenaire.idPartenaire " +
            "GROUP BY Partenaire.idPartenaire, Partenaire.refPartenaire"
        );

        ResultSetGrid parPart = new ResultSetGrid(offresParPartenaire, new GridDescription(List.of(
            new ColumnDescription().colData(0).visible(false), // ID du partenaire
            new ColumnDescription().colData(1).headerString("Partenaire"), // refPartenaire
            new ColumnDescription().colData(2).headerString("Total places offertes"), // placesPartenaire
            new ColumnDescription().colCalculatedObject((t) -> {
                int nbrPart = Integer.parseInt("" + t.get(2));
                int nbrTot = Integer.parseInt("" + t.get(3));
                double percent = ((double) nbrPart) / nbrTot * 100;
                return String.format("%.0f%%", percent);
            }).headerString("Pourcentage")
        )));

        parPart.getStyle()
            .set("width", "80%")
            .set("margin", "20px auto")
            .set("border", "1px solid #ccc")
            .set("border-radius", "5px")
            .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
            .set("background-color", "white");

        this.add(new H3("Offres groupées par partenaires"));
        this.add(parPart);
    }

    private void handlePostulerClick() {
        Set<List<Object>> lignesSelected = this.gOffres.getSelectedItems();
        if (lignesSelected.isEmpty()) {
            Notification.show("Veuillez sélectionner une offre.");
            return;
        }

        List<Object> ligne = lignesSelected.iterator().next();
        Integer idOffre = (Integer) ligne.get(0); // L'ID de l'offre
        UI.getCurrent().navigate("candidature/" + idOffre);
    }

    private void logAndNotifyError(Exception ex, String message) {
        System.err.println(message + ": " + ex.getLocalizedMessage());
        ex.printStackTrace();
        Notification.show(message + ". Consultez les logs pour plus de détails.");
    }
}
