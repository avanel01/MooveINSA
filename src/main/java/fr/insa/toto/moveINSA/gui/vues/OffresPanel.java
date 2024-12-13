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
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.vaadin.flow.component.UI;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author francois
 */
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

    public OffresPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            this.add(new H2("Affichage des offres de mobilité (tables formatées)"));

            // Table avec mise en forme (Offres groupées par partenaires)
            PreparedStatement offresAvecPart = con.prepareStatement(
                    "select offremobilite.id as idOffre, partenaire.refPartenaire, offremobilite.nbrplaces, partenaire.id as idPartenaire " +
                    "from OffreMobilite " +
                    "join Partenaire on offremobilite.proposepar = partenaire.id");

            this.gOffres = new ResultSetGrid(offresAvecPart, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false), // id de l'offre (non affichée)
                    new ColumnDescription().colData(1).headerString("Partenaire"),
                    new ColumnDescription().colData(2).headerString("Nombre de places"),
                    new ColumnDescription().colDataCompo(2, (t) -> new IntAsIcon((Integer) t)).headerString("Places"),
                    new ColumnDescription().colCalculatedObject((t) -> t.get(1) + " : " + t.get(2)).headerString("Résumé"),
                    new ColumnDescription().colData(3).visible(false) // id du partenaire (non affichée)
            )));
            this.add(new H3("Offres de mobilité avec mise en forme"));
            this.add(this.gOffres);

            // Bouton "Postuler"
        bPostule = new Button("Postuler");
        bPostule.addClickListener(e -> {
            // Récupérer l'ID de l'offre sélectionnée
            Set<List<Object>> lignesSelected = this.gOffres.getSelectedItems();
            if (lignesSelected.isEmpty()) {
                Notification.show("Veuillez sélectionner une offre.");
            } else {
                // Récupérer l'ID de l'offre à partir de la ligne sélectionnée
                List<Object> ligne = lignesSelected.iterator().next();
                Integer idOffre = (Integer) ligne.get(0);  // L'ID de l'offre est en première position dans la ligne

                // Naviguer vers la page de candidature en passant l'ID de l'offre
                UI.getCurrent().navigate("candidature/" + idOffre);
            }
        });
            this.add(this.bPostule);

            // Table groupée par partenaires (avec pourcentage)
            PreparedStatement offresParPartenaire = con.prepareStatement(
                    "select partenaire.id, partenaire.refPartenaire, sum(offremobilite.nbrplaces) as placesPartenaire, " +
                    "(select sum(nbrplaces) from offremobilite) as totplaces " +
                    "from offremobilite " +
                    "join partenaire on offremobilite.proposepar = partenaire.id " +
                    "group by partenaire.id");

            ResultSetGrid parPart = new ResultSetGrid(offresParPartenaire, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false), // id du partenaire (non affichée)
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
            System.out.println("Problème : " + ex.getLocalizedMessage());
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
    }
}
