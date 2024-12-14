package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ColumnDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.GridDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.moveINSA.gui.MainLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@PageTitle("MoveINSA - Partenaires")
@Route(value = "partenaires/liste", layout = MainLayout.class)
public class PartenairesPanel extends VerticalLayout {

    private ResultSetGrid gPartenaire;
    private Button bOffre;

    public PartenairesPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            this.add(new H2("Affichage de partenaires (mise en forme)"));

            // Requête pour récupérer les partenaires
            PreparedStatement part = con.prepareStatement(
                "SELECT Partenaire.idPartenaire AS idPartenaire, Partenaire.refPartenaire, Partenaire.ville, Partenaire.pays " +
                "FROM Partenaire"
            );

            // Configuration de la grille avec mise en forme
            this.gPartenaire = new ResultSetGrid(part, new GridDescription(List.of(
                new ColumnDescription().colData(0).visible(false), // ID non affiché
                new ColumnDescription().colData(1).headerString("Nom"),
                new ColumnDescription().colData(2).headerString("Ville"),
                new ColumnDescription().colData(3).headerString("Pays")
            )));

            this.add(new H3("Liste des partenaires"));
            this.add(this.gPartenaire);

            // Bouton "Postuler"
            this.bOffre = new Button("les offres !!");
            this.bOffre.addClickListener((event) -> {
                // Récupération de la ligne sélectionnée
                    UI.getCurrent().navigate("offres/liste" );
            });
            this.add(this.bOffre);

        
        
        // Requête pour partenaires groupés par pays
            this.add(new H3("Partenaires groupés par pays"));
            PreparedStatement PartenaireParPays = con.prepareStatement(
                    "SELECT pays.idPays, pays.nomPays, COUNT(partenaire.id) AS nbrPartenaires, " +
                    "       (SELECT COUNT(*) FROM partenaire) AS totalPartenaires " +
                    "FROM Partenaire " +
                    "JOIN Pays ON partenaire.pays = pays.idPays " +
                    "GROUP BY pays.idPays"
            );

            ResultSetGrid parPart = new ResultSetGrid(PartenaireParPays, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false),
                    new ColumnDescription().colData(1).headerString("Pays"),
                    new ColumnDescription().colData(2).headerString("Nombre de partenaires"),
                    new ColumnDescription().colCalculatedObject((t) -> {
                        int nbrPart = Integer.parseInt("" + t.get(2));
                        int nbrTot = Integer.parseInt("" + t.get(3));
                        double percent = ((double) nbrPart) / nbrTot * 100;
                        return String.format("%.0f%%", percent);
                    }).headerString("Pourcentage")
            )));
            this.add(parPart);

        } catch (SQLException ex) {
            System.out.println("Problème : " + ex.getLocalizedMessage());
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
        
    }
    
    
    
}
