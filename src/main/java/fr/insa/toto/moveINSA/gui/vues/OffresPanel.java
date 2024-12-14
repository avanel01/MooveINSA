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
import java.sql.ResultSet;

import com.vaadin.flow.component.UI;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
            
            // Création de la table pour afficher les offres
            PreparedStatement offresAvecPart = con.prepareStatement(
                    "SELECT OffreMobilite.idOffre AS idOffre, " +
                    "       Partenaire.refPartenaire AS refPartenaire, " +
                    "       OffreMobilite.nbrplaces AS nbrPlaces, " +
                    "       Partenaire.idPartenaire AS idPartenaire " +
                    "FROM OffreMobilite " +
                    "JOIN Partenaire ON OffreMobilite.proposepar = Partenaire.idPartenaire"
            );
            
            // Centrage global du contenu
            this.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("padding", "20px")
                .set("background-color", "#f9f9f9");

            // Titre principal
            H2 titre = new H2("Affichage des offres de mobilité (tables formatées)");
            titre.getStyle()
                .set("text-align", "center")
                .set("color", "#333")
                .set("margin-bottom", "20px");
            this.add(titre);

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
            this.gOffres.getStyle()
                .set("width", "80%")
                .set("margin", "20px auto")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
                .set("background-color", "white");
            this.add(gOffres);

            // Ajout du bouton "Postuler"
            bPostule = new Button("Postuler");
            bPostule.getStyle()
                .set("margin-top", "20px")
                .set("padding", "10px 20px")
                .set("background-color", "#FF0000") // Couleur bleue
                .set("color", "white")
                .set("font-size", "16px")
                .set("font-weight", "bold")
                .set("border", "none")
                .set("border-radius", "5px")
                .set("cursor", "pointer");
            bPostule.addClickListener(e -> handlePostulerClick());
            this.add(this.bPostule);
            
            // Titre pour les offres groupées
            H3 titreParPartenaire = new H3("Offres groupées par partenaires");
            titreParPartenaire.getStyle()
                .set("margin-top", "30px")
                .set("text-align", "center")
                .set("color", "#555");
            this.add(titreParPartenaire);
            
            // Création de la table groupée par partenaires // Préparation de la requête SQL pour les offres groupées
            PreparedStatement offresParPartenaire = con.prepareStatement(
                    "SELECT Partenaire.idPartenaire AS idPartenaire, " +
                    "       Partenaire.refPartenaire AS refPartenaire, " +
                    "       SUM(OffreMobilite.nbrplaces) AS placesPartenaire, " +
                    "       (SELECT SUM(nbrplaces) FROM OffreMobilite) AS totPlaces " +
                    "FROM OffreMobilite " +
                    "JOIN Partenaire ON OffreMobilite.proposepar = Partenaire.idPartenaire " +
                    "GROUP BY Partenaire.idPartenaire, Partenaire.refPartenaire"
            );
            
            // Exécution de la requête pour récupérer les résultats
            ResultSet resultSetParPart = offresParPartenaire.executeQuery();

            // Table groupée par partenaire
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
