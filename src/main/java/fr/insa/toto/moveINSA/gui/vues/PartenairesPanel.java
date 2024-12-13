/*package fr.insa.toto.moveINSA.gui.vues;


import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.GestionBdD;
import fr.insa.toto.moveINSA.model.Partenaire;
import static fr.insa.toto.moveINSA.model.Partenaire.tousLesPartenaires;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOpackage fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.GestionBdD;
import fr.insa.toto.moveINSA.model.Partenaire;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel affichant la liste des partenaires.
 */
/*@PageTitle("MoveINSA - Partenaires")
@Route(value = "partenaires/liste", layout = MainLayout.class)
public class PartenairesPanel extends VerticalLayout {

    public PartenairesPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            // Titre de la page
            this.add(new H3("Liste de tous les partenaires"));

            // Récupération des partenaires depuis GestionBdD
            List<Partenaire> partenaires = tousLesPartenaires(con);

            // Affichage des partenaires via PartenaireGrid
            PartenaireGrid grid = new PartenaireGrid(partenaires);
            this.add(grid);

        } catch (SQLException ex) {
            // Gestion d'erreur
            System.out.println("Problème : " + ex.getLocalizedMessage());
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
    }
}*/

/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
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
import fr.insa.toto.moveINSA.model.Partenaire;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@PageTitle("MoveINSA")
@Route(value = "partenaires/liste", layout = MainLayout.class)
public class PartenairesPanel extends VerticalLayout {

    public static class IntAsIcon extends HorizontalLayout {
        public IntAsIcon(int nbr) {
            for (int i = 0; i < nbr; i++) {
                this.add(new Icon(VaadinIcon.EXIT));
            }
        }
    }

    private ResultSetGrid gPartenaire;
    private Button bOffre;

    /*public PartenairesPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            this.add(new H2("Affichage de tables (ResultSet) à l'aide de ResultSetGrid"));

            // Requête pour afficher les partenaires
            PreparedStatement offresAvecPart = con.prepareStatement(
                    "SELECT partenaire.id AS id, partenaire.refPartenaire, partenaire.ville, partenaire.pays " +
                    "FROM partenaire"
            );

            // Table des partenaires (mise en forme désactivée ici)
            this.gPartenaire = new ResultSetGrid(offresAvecPart, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false),
                    new ColumnDescription().colData(1).headerString("Partenaire"),
                    new ColumnDescription().colData(2).headerString("Ville"),
                    new ColumnDescription().colData(3).headerString("Pays")
            )));
            this.add(this.gPartenaire);

            ResultSet rs = offresAvecPart.executeQuery();
while (rs.next()) {
    System.out.println(rs.getString("refPartenaire") + ", " + rs.getString("ville") + ", " + rs.getString("pays"));
}
            // Bouton pour postuler
            this.bOffre = new Button("Postuler");
            this.bOffre.addClickListener((t) -> {
                Set<List<Object>> lignesSelected = this.gPartenaire.getSelectedItems();
                if (lignesSelected.isEmpty()) {
                    Notification.show("Veuillez sélectionner un partenaire.");
                } else {
                    List<Object> ligne = lignesSelected.iterator().next();
                    Notification.show("Vous avez sélectionné le partenaire avec l'ID : " + ligne.get(0));
                }
            });
            this.add(this.bOffre);

            // Requête pour partenaires groupés par pays
            this.add(new H3("Partenaires groupés par pays"));
            PreparedStatement PartenaireParPays = con.prepareStatement(
                    "SELECT pays.idPays, pays.nomPays, COUNT(partenaire.id) AS nbrPartenaires, " +
                    "       (SELECT COUNT(*) FROM partenaire) AS totalPartenaires " +
                    "FROM partenaire " +
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
    }*/
    
    public PartenairesPanel() throws SQLException {
        try (Connection con = ConnectionPool.getConnection()) {
            this.add(new H2("Affichage de tables (ResultSet) quelconques à l'aide de ResultSetGrid"));
            PreparedStatement part = con.prepareStatement(
                    "select partenaire.id as idPartenaire, partenaire.refPartenaire ,partenaire.ville ,partenaire.pays \n"
                    + "  from partenaire");
            this.add(new H3("affichage direct (sans mise en forme) du ResultSet"));
            this.add(new ResultSetGrid(part));
            this.gPartenaire = new ResultSetGrid(part, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false), // on veut pouvoir accéder à l'id de l'offre mais non l'afficher
                    new ColumnDescription().colData(1).headerString("nom"),
                    new ColumnDescription().colData(2).headerString("ville"),
                    new ColumnDescription().colData(3).headerString("pays"))));
            
            this.add(new H3("la même table mais mise en forme"));
            this.add(new Paragraph("le petit bouton \"Postuler\" n'est pas vraiment opérationel : "
                    + "je n'ai même pas la notion d'étudiant dans ma base de donnée. Il est là pour que vous puissiez voir dans "
                    + "le code qu'il est facile d'interagir avec une Grid pour par exemple récupérer la ligne sélectionnée. "
                    + "Cela montre aussi l'utilité des colonnes non affichées"));
            this.add(this.gPartenaire);
            this.bOffre = new Button("Postuler");
            this.bOffre.addClickListener((t) -> {
                // comme la grille est générique, chaque ligne contient une List<Object> : un Object par colonne
                // par défaut une grille est en mono-selection
                // mais comme on peut fixer en multi-selection, on a potentiellement un ensemble d'item selectionnés
                Set<List<Object>> lignesSelected = this.gPartenaire.getSelectedItems();
                // dans notre cas 0 ou 1 item selectionné
                if (lignesSelected.isEmpty()) {
                    Notification.show("selectionnez un partenaire");
                } else {
                    List<Object> ligne = lignesSelected.iterator().next();
                    // normalement, on ne montre pas les ID à l'utilisateur
                    // ici c'est pour montrer que l'on a bien accès à la colonne 0 même si elle n'est pas visible
                    Notification.show("vous rechercher les offre pour ce partenaire : "+ligne.get(1) );
                }
            });
}
    }
}
