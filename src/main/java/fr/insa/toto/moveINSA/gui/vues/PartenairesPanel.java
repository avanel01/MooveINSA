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

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.Partenaire;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author francois
 */
@PageTitle("MoveINSA")
@Route(value = "partenaires/liste", layout = MainLayout.class)
public class PartenairesPanel extends VerticalLayout {

    public PartenairesPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            // Titre de la page
            this.add(new H3("Liste de tous les partenaires"));

            // Créez une grille pour afficher les partenaires
            Grid<Partenaire> grid = new Grid<>(Partenaire.class, false);

            // Ajouter des colonnes à la grille
            grid.addColumn(Partenaire::getIdPartenaire).setHeader("ID");
            grid.addColumn(Partenaire::getRefPartenaire).setHeader("Référence Partenaire");
            grid.addColumn(Partenaire::getVille).setHeader("Ville");
            grid.addColumn(Partenaire::getPays).setHeader("Pays");

            // Récupérez la liste des partenaires depuis la base de données
            List<Partenaire> partenaires = Partenaire.tousLesPartenaires(con);

            // Ajoutez les partenaires à la grille
            grid.setItems(partenaires);

            // Ajoutez la grille au layout
            this.add(grid);

        } catch (SQLException ex) {
            // En cas de problème, afficher un message d'erreur
            System.out.println("Problème : " + ex.getLocalizedMessage());
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
    }
}

    

