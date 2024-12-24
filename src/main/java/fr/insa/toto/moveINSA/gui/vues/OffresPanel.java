package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.server.VaadinSession;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ColumnDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.GridDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.Etudiant;
import fr.insa.toto.moveINSA.model.OffreMobilite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@PageTitle("MoveINSA")
@Route(value = "offres/liste", layout = MainLayout.class)
public class OffresPanel extends VerticalLayout {

    public static class IntAsIcon extends HorizontalLayout {
        public IntAsIcon(int nbr) {
            for (int i = 0; i < nbr; i++) {
                this.add(new Icon(VaadinIcon.EXIT));
            }
        }
    }

    private ResultSetGrid gOffres;
    private Button bPostule;
    private Etudiant etudiantConnecte;

    public OffresPanel() {
        Object user = VaadinSession.getCurrent().getAttribute("user");

        if (user instanceof Etudiant) {
            this.etudiantConnecte = (Etudiant) user;
        } else {
            this.etudiantConnecte = null;
        }

        configureLayout();
        addTitle();
        configureGrid();
        configurePostulerButton();
        addGroupedOffersSection();
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

    private void addTitle() {
        H2 titre = new H2("Affichage des offres de mobilité");
        titre.getStyle()
            .set("text-align", "center")
            .set("color", "#333")
            .set("margin-bottom", "20px");
        this.add(titre);
    }

    private void configureGrid() {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement offresAvecPart = con.prepareStatement(
                "SELECT o.idOffre AS idOffre, " +
                "       p.refPartenaire AS refPartenaire, " +
                "       o.nbrPlaces AS nbrPlaces, " +
                "       p.idPartenaire AS idPartenaire, " +
                "       o.nomOffre AS nomOffre, " +
                "       o.specialiteAssocie AS spe, " +
                "       GROUP_CONCAT(DISTINCT so.semestre ORDER BY so.semestre) AS semestres " +
                "FROM OffreMobilite o " +
                "JOIN Partenaire p ON o.proposepar = p.idPartenaire " +
                "LEFT JOIN SemestresOffre so ON o.idOffre = so.idOffre " +
                "GROUP BY o.idOffre, p.refPartenaire, o.nbrPlaces, p.idPartenaire, o.nomOffre, o.specialiteAssocie"
            );

            this.gOffres = new ResultSetGrid(offresAvecPart, new GridDescription(List.of(
                new ColumnDescription().colData(0).visible(false), // ID de l'offre (non affichée)
                new ColumnDescription().colData(1).headerString("Partenaire"), // refPartenaire
                new ColumnDescription().colData(4).headerString("Intitulé de l'offre"), // nomOffre
                new ColumnDescription().colDataCompo(2, (nbrPlaces) ->
                    new IntAsIcon((Integer) nbrPlaces) // Composant pour afficher le nombre de places
                ).headerString("Places disponibles"),
                new ColumnDescription().colData(5).headerString("Spécialité"), // specialiteAssocie
                new ColumnDescription().colData(6).headerString("Semestres") // Liste des semestres
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
        } catch (SQLException ex) {
            logAndNotifyError(ex, "Erreur lors du chargement des données");
        }
    }

    private void configurePostulerButton() {
        bPostule = new Button("Postuler", e -> handlePostulerClick());

        if (etudiantConnecte == null) {
            bPostule.setEnabled(false);
            bPostule.setText("Connectez-vous pour postuler");
            bPostule.getStyle().set("background-color", "#ccc");
        } else {
            bPostule.getStyle().set("background-color", "#FF0000");
        }

        bPostule.getStyle()
            .set("margin-top", "20px")
            .set("padding", "10px 20px")
            .set("color", "white")
            .set("font-size", "16px")
            .set("font-weight", "bold")
            .set("border", "none")
            .set("border-radius", "5px")
            .set("cursor", "pointer");

        this.add(bPostule);
    }

    private void addGroupedOffersSection() {
        // Pas de modification ici
    }

    private void handlePostulerClick() {
        Set<List<Object>> lignesSelected = this.gOffres.getSelectedItems();
        if (lignesSelected.isEmpty()) {
            Notification.show("Veuillez sélectionner une offre.");
        } else {
            try {
                List<Object> ligne = lignesSelected.iterator().next();
                Integer idOffre = (Integer) ligne.get(0);
                UI.getCurrent().navigate("candidature/" + idOffre);
            } catch (Exception ex) {
                logAndNotifyError(ex, "Erreur lors de la navigation vers la candidature");
            }
        }
    }

    private void logAndNotifyError(Exception ex, String message) {
        System.err.println(message + ": " + ex.getLocalizedMessage());
        ex.printStackTrace();
        Notification.show(message + ". Consultez les logs pour plus de détails.");
    }
}
