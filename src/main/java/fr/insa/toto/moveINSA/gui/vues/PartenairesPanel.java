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

@PageTitle("MoveINSA - Partenaires")
@Route(value = "partenaires/liste", layout = MainLayout.class)
public class PartenairesPanel extends VerticalLayout {

    private ResultSetGrid gPartenaire;
    private Button bOffre;

    public PartenairesPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            // Ajout du titre principal
            this.add(new H2("Affichage des partenaires"));

            // Configuration de la grille des partenaires
            setupPartenaireGrid(con);

            // Configuration du bouton pour naviguer vers les offres
            setupOffresButton();

            // Configuration de l'affichage des partenaires groupés par pays
            setupPartenairesParPays(con);

        } catch (SQLException ex) {
            handleSQLException(ex);
        }
    }

    /**
     * Configure la grille affichant la liste des partenaires.
     */
    private void setupPartenaireGrid(Connection con) throws SQLException {
        // Requête SQL pour récupérer les partenaires
        String sql = """
            SELECT Partenaire.idPartenaire AS idPartenaire,
                   Partenaire.refPartenaire AS refPartenaire,
                   Partenaire.ville AS ville,
                   Partenaire.pays AS pays
            FROM Partenaire
        """;
        PreparedStatement part = con.prepareStatement(sql);

        // Création de la grille des partenaires
        gPartenaire = new ResultSetGrid(part, new GridDescription(List.of(
            new ColumnDescription().colData(0).visible(false), // ID non affiché
            new ColumnDescription().colData(1).headerString("Nom"),
            new ColumnDescription().colData(2).headerString("Ville"),
            new ColumnDescription().colData(3).headerString("Pays")
        )));

        this.add(new H3("Liste des partenaires"));
        this.add(gPartenaire);
    }

    /**
     * Configure le bouton pour naviguer vers la liste des offres.
     */
    private void setupOffresButton() {
        bOffre = new Button("Voir les offres");
        bOffre.addClickListener(event -> UI.getCurrent().navigate("offres/liste"));
        this.add(bOffre);
    }

    /**
     * Configure l'affichage des partenaires groupés par pays.
     */
    private void setupPartenairesParPays(Connection con) throws SQLException {
        // Requête SQL pour afficher les partenaires groupés par pays
        String sql = """
            SELECT Pays.idPays AS idPays,
                   Pays.nomPays AS nomPays,
                   COUNT(Partenaire.idPartenaire) AS nbrPartenaires,
                   (SELECT COUNT(*) FROM Partenaire) AS totalPartenaires
            FROM Partenaire
            JOIN Pays ON Partenaire.pays = Pays.idPays
            GROUP BY Pays.idPays, Pays.nomPays
        """;
        PreparedStatement partenaireParPays = con.prepareStatement(sql);

        // Création de la grille pour les partenaires groupés par pays
        ResultSetGrid parPart = new ResultSetGrid(partenaireParPays, new GridDescription(List.of(
            new ColumnDescription().colData(0).visible(false), // ID non affiché
            new ColumnDescription().colData(1).headerString("Pays"),
            new ColumnDescription().colData(2).headerString("Nombre de partenaires"),
            new ColumnDescription().colCalculatedObject(t -> {
                int nbrPart = Integer.parseInt("" + t.get(2));
                int nbrTot = Integer.parseInt("" + t.get(3));
                double percent = ((double) nbrPart) / nbrTot * 100;
                return String.format("%.0f%%", percent);
            }).headerString("Pourcentage")
        )));

        this.add(new H3("Partenaires groupés par pays"));
        this.add(parPart);
    }

    /**
     * Gère les erreurs SQL en affichant une notification et en loguant l'exception.
     */
    private void handleSQLException(SQLException ex) {
        System.err.println("Erreur SQL : " + ex.getLocalizedMessage());
        ex.printStackTrace();
        Notification.show("Problème : " + ex.getLocalizedMessage(), 3000, Notification.Position.MIDDLE);
    }
}
