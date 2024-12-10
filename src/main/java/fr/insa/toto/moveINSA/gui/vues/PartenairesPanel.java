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

    public PartenairesPanel() {
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
                    "JOIN pays ON partenaire.pays = pays.idPays " +
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
