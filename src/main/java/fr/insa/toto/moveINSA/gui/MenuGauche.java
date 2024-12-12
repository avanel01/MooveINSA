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

import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import fr.insa.toto.moveINSA.gui.jeu.BoiteACoucou;
import fr.insa.toto.moveINSA.gui.jeu.TrouveEntier;
import fr.insa.toto.moveINSA.gui.testDataGrid.TestDataGrid;
import fr.insa.toto.moveINSA.gui.testDataGrid.TestGridDirect;
import fr.insa.toto.moveINSA.gui.testDataGrid.TestResultSetGrid;
import fr.insa.toto.moveINSA.gui.vues.NouveauPartenairePanel;
import fr.insa.toto.moveINSA.gui.vues.NouvelleOffrePanel;
import fr.insa.toto.moveINSA.gui.vues.OffresPanel;
import fr.insa.toto.moveINSA.gui.vues.PartenairesPanel;
import fr.insa.toto.moveINSA.gui.vues.RAZBdDPanel;
import fr.insa.toto.moveINSA.gui.vues.TestDriverPanel;

/**
 *
 * @author francois
 */
public class MenuGauche extends SideNav {

    public MenuGauche() {
        // Styliser le conteneur principal
        this.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("padding", "10px");

        // Crée les items principaux et applique les styles immédiatement
        SideNavItem main = new SideNavItem("main", VuePrincipale.class);
        styleItem(main);

        SideNavItem connexion = new SideNavItem("Connexion", ConnexionPanel.class);
        styleItem(connexion);

        SideNavItem partenaires = new SideNavItem("partenaires");
        styleItem(partenaires);
        partenaires.addItem(new SideNavItem("liste", PartenairesPanel.class));
        partenaires.addItem(new SideNavItem("nouveau", NouveauPartenairePanel.class));

        SideNavItem offres = new SideNavItem("offres");
        styleItem(offres);
        offres.addItem(new SideNavItem("liste", OffresPanel.class));
        offres.addItem(new SideNavItem("nouvelle", NouvelleOffrePanel.class));

        SideNavItem debug = new SideNavItem("debug");
        styleItem(debug);
        debug.addItem(new SideNavItem("test driver", TestDriverPanel.class));
        debug.addItem(new SideNavItem("raz BdD", RAZBdDPanel.class));
        debug.addItem(new SideNavItem("test ResultSetGrid", TestResultSetGrid.class));
        debug.addItem(new SideNavItem("test DataGrid", TestDataGrid.class));
        debug.addItem(new SideNavItem("test Grid direct", TestGridDirect.class));

        SideNavItem jeux = new SideNavItem("jeux");
        styleItem(jeux);
        jeux.addItem(new SideNavItem("boite à coucou", BoiteACoucou.class));
        jeux.addItem(new SideNavItem("trouve", TrouveEntier.class));

        // Ajoute tous les éléments au SideNav
        this.addItem(main, connexion, partenaires, offres, debug, jeux);
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
