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
import fr.insa.toto.moveINSA.model.SRI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Classe pour afficher les offres de mobilité.
 * Permet de consulter les offres et de postuler si un étudiant est connecté.
 * Si aucun étudiant n'est connecté, le bouton "Postuler" est désactivé.
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
    private Etudiant etudiantConnecte;

    public OffresPanel() {
        // Récupérer l'utilisateur connecté depuis la session
        Object user = VaadinSession.getCurrent().getAttribute("user");
        
        if (user instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) user;
            this.etudiantConnecte = (Etudiant) VaadinSession.getCurrent().getAttribute("user");
        } else if (user instanceof SRI) {
            SRI sri = (SRI) user;
            this.etudiantConnecte = null;
        } else {
            this.etudiantConnecte = null;
        }
        
        System.out.println("etudiant connecté dans offrespanel : " + this.etudiantConnecte);
                            System.out.println("session courante : " + VaadinSession.getCurrent());
 

        // Configuration globale du panneau
        configureLayout();

        // Ajouter les composants principaux
        addTitle();
        configureGrid();
        configurePostulerButton();
        addGroupedOffersSection();
    }

    /**
     * Configure le style global du panneau.
     */
    private void configureLayout() {
        this.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("padding", "20px")
            .set("background-color", "#f9f9f9");
    }

    /**
     * Ajoute le titre principal de la page.
     */
    private void addTitle() {
        H2 titre = new H2("Affichage des offres de mobilité");
        titre.getStyle()
            .set("text-align", "center")
            .set("color", "#333")
            .set("margin-bottom", "20px");
        this.add(titre);
    }

    /**
     * Configure et ajoute la grille des offres.
     */
    private void configureGrid() {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement offresAvecPart = con.prepareStatement(
                "SELECT OffreMobilite.idOffre AS idOffre, " +
                "       Partenaire.refPartenaire AS refPartenaire, " +
                "       OffreMobilite.nbrPlaces AS nbrPlaces, " +
                "       Partenaire.idPartenaire AS idPartenaire, " +
                "       OffreMobilite.nomOffre AS nomOffre, " +
                "       OffreMobilite.specialiteAssocie AS spe " +
                "       OffreMobilite.semestre AS semestre " +
                "FROM OffreMobilite " +
                "JOIN Partenaire ON OffreMobilite.proposepar = Partenaire.idPartenaire"
            );

            this.gOffres = new ResultSetGrid(offresAvecPart, new GridDescription(List.of(
                new ColumnDescription().colData(0).visible(false), // ID de l'offre (non affichée)
                new ColumnDescription().colData(1).headerString("Partenaire"), // refPartenaire
                new ColumnDescription().colData(4).headerString("Intitulé de l'offre"), // nomOffre
                new ColumnDescription().colDataCompo(2, (nbrPlaces) -> 
                    new IntAsIcon((Integer) nbrPlaces) // Composant pour afficher le nombre de places
                ).headerString("Places disponibles"),
                new ColumnDescription().colData(5).headerString("Spécialité"),// specialiteAssocie
                new ColumnDescription().colData(6).headerString("Semestre")
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

    /**
     * Configure et ajoute le bouton "Postuler".
     */
private void configurePostulerButton() {
    bPostule = new Button("Postuler", e -> handlePostulerClick());

    // Désactiver le bouton si aucun étudiant n'est connecté
    if (etudiantConnecte == null) {
        bPostule.setEnabled(false);
        bPostule.setText("Connectez-vous pour postuler");
        bPostule.getStyle().set("background-color", "#ccc"); // Fond gris quand étudiant déconnecté
    } else {
        bPostule.getStyle().set("background-color", "#FF0000"); // Fond rouge quand étudiant connecté
    }

    // Styles communs pour le bouton
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


    /**
     * Ajoute la section des offres groupées par partenaire.
     */
    private void addGroupedOffersSection() {
        try (Connection con = ConnectionPool.getConnection()) {
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

            parPart.getStyle()
                .set("width", "80%")
                .set("margin", "20px auto")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
                .set("background-color", "white");

            H3 titreParPartenaire = new H3("Offres groupées par partenaires");
            titreParPartenaire.getStyle()
                .set("margin-top", "30px")
                .set("text-align", "center")
                .set("color", "#555");

            this.add(titreParPartenaire, parPart);
        } catch (SQLException ex) {
            logAndNotifyError(ex, "Erreur lors du chargement des données groupées");
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
