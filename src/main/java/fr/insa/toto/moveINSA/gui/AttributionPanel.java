package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ColumnDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.GridDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.moveINSA.model.Etudiant;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author alixvanel
 */
@Route(value = "attribution", layout = MainLayout.class) // La route reste la page d'accueil
@PageTitle("Atribution")
public class AttributionPanel extends VerticalLayout {

    private ResultSetGrid gAttribution;
    private Etudiant etudiant;

    private void configureGrid() {
        try (Connection con = ConnectionPool.getConnection()) {
            
            etudiant = (Etudiant) VaadinSession.getCurrent().getAttribute("user");
            if (etudiant == null) {
                Notification.show("Erreur : Aucun étudiant connecté. Veuillez vous connecter.");
                return;
            }
            String ine = etudiant.getINE();
            // Requête SQL avec jointure pour récupérer le nom de l'offre
            PreparedStatement offresAvecPart = con.prepareStatement(
                "SELECT Attribution.idAttribution, " +
                "       Attribution.idOffre, " +
                "       OffreMobilite.nomOffre, " + // Récupération du nom de l'offre
                "       Attribution.date " +
                "FROM Attribution " +
                "JOIN OffreMobilite ON Attribution.idOffre = OffreMobilite.idOffre " +
                "WHERE Attribution.idEtudiant = " + ine
            );

            // Vous devez définir ici l'ID de l'étudiant pour filtrer les attributions
            offresAvecPart.setInt(1, 1); // Remplacez 1 par l'ID dynamique de l'étudiant

            // Configuration de la grille avec les nouvelles colonnes
            this.gAttribution = new ResultSetGrid(offresAvecPart, new GridDescription(List.of(
                new ColumnDescription().colData(0).visible(false), // idAttribution (non affiché)
                new ColumnDescription().colData(1).visible(false), // idOffre (non affiché)
                new ColumnDescription().colData(2).headerString("Intitulé de l'offre"), // nomOffre
                new ColumnDescription().colData(3).headerString("Date d'attribution") // date
            )));

            // Ajout de styles à la grille
            this.gAttribution.getStyle()
                .set("width", "80%")
                .set("margin", "20px auto")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
                .set("background-color", "white");

            // Ajout d'un titre et de la grille à la vue
            this.add(new H3("Voici votre attribution :"));
            this.add(this.gAttribution);

        } catch (SQLException ex) {
            logAndNotifyError(ex, "Erreur lors du chargement des données");
        }
    }

    /**
     * Méthode utilitaire pour gérer et notifier les erreurs.
     * 
     * @param ex Exception SQL capturée
     * @param message Message personnalisé à afficher
     */
    private void logAndNotifyError(SQLException ex, String message) {
        ex.printStackTrace(); // Log de l'erreur pour le développeur
        System.err.println(message); // Affichage d'un message dans la console
    }
}
