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

package fr.insa.toto.moveINSA.gui;


//import fr.insa.toto.moveINSA.gui.jeu.BoiteACoucou;
//import fr.insa.toto.moveINSA.gui.jeu.TrouveEntier;
import fr.insa.toto.moveINSA.gui.testDataGrid.TestDataGrid;
import fr.insa.toto.moveINSA.gui.testDataGrid.TestGridDirect;
import fr.insa.toto.moveINSA.gui.testDataGrid.TestResultSetGrid;
import fr.insa.toto.moveINSA.gui.vues.NouveauPartenairePanel;
import fr.insa.toto.moveINSA.gui.vues.NouvelleOffrePanel;
import fr.insa.toto.moveINSA.gui.vues.OffresPanel;
import fr.insa.toto.moveINSA.gui.vues.PartenairesPanel;
import fr.insa.toto.moveINSA.gui.vues.RAZBdDPanel;
import fr.insa.toto.moveINSA.gui.vues.TestDriverPanel;


import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Etudiant;
import fr.insa.toto.moveINSA.model.SRI;

public class MenuGauche extends SideNav {

    public MenuGauche() {
        // Styliser le conteneur principal
        this.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("padding", "10px");

        // Crée les items principaux et applique les styles immédiatement
        SideNavItem main = new SideNavItem("Accueil", VuePrincipale.class);
        styleItem(main);

        SideNavItem connexion = new SideNavItem("Connexion", ConnexionPanel.class);
        styleItem(connexion);

        SideNavItem partenaires = new SideNavItem("partenaires");
        styleItem(partenaires);
        partenaires.addItem(new SideNavItem("liste", PartenairesPanel.class));
        
        SideNavItem offres = new SideNavItem("offres");
            styleItem(offres);
            offres.addItem(new SideNavItem("liste", OffresPanel.class));

        // Vérifier si un utilisateur est connecté et de quel type
        boolean isEtudiantConnected = VaadinSession.getCurrent().getAttribute(Etudiant.class) != null;
        boolean isSRIConnected = VaadinSession.getCurrent().getAttribute(SRI.class) != null;

        // Si un membre du SRI est connecté, afficher toutes les pages
        if (isSRIConnected) {
            partenaires.addItem(new SideNavItem("nouveau", NouveauPartenairePanel.class));
            offres.addItem(new SideNavItem("nouvelle", NouvelleOffrePanel.class));

            SideNavItem debug = new SideNavItem("debug");
            styleItem(debug);
            debug.addItem(new SideNavItem("test driver", TestDriverPanel.class));
            debug.addItem(new SideNavItem("raz BdD", RAZBdDPanel.class));
            debug.addItem(new SideNavItem("test ResultSetGrid", TestResultSetGrid.class));
            debug.addItem(new SideNavItem("test DataGrid", TestDataGrid.class));
            debug.addItem(new SideNavItem("test Grid direct", TestGridDirect.class));
            this.addItem(debug);
        }
        
        // création de l'élément deconnexion
        SideNavItem deconnexion = new SideNavItem("Deconnexion", DeconnexionPanel.class);
        styleItem(deconnexion); // Appliquer le style cohérent au bouton Déconnexion
        
        // Ajouter les éléments au menu
        this.addItem(main, connexion, partenaires, offres, deconnexion);
    }

    // Méthode pour appliquer les styles directement à un SideNavItem
    private void styleItem(SideNavItem item) {
        item.getStyle()
            .set("text-align", "center")  // Centrer le texte
            .set("text-transform", "uppercase")  // Majuscules
            .set("font-weight", "bold")  // Texte en gras
            .set("font-size", "12px")  // Réduire la taille de l'écriture
            .set("padding", "10px")  // Espacement
            .set("border-bottom", "1px solid #ddd");  // Ligne de séparation
    }
}
