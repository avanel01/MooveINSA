package fr.insa.toto.moveINSA.gui;

import fr.insa.toto.moveINSA.gui.testDataGrid.TestDataGrid;
import fr.insa.toto.moveINSA.gui.testDataGrid.TestGridDirect;
import fr.insa.toto.moveINSA.gui.testDataGrid.TestResultSetGrid;
import fr.insa.toto.moveINSA.gui.vues.NouveauPartenairePanel;
import fr.insa.toto.moveINSA.gui.vues.NouvelleOffrePanel;
import fr.insa.toto.moveINSA.gui.vues.OffresPanel;
import fr.insa.toto.moveINSA.gui.vues.PartenairesPanel;
import fr.insa.toto.moveINSA.gui.vues.RAZBdDPanel;
import fr.insa.toto.moveINSA.gui.vues.TestDriverPanel;
import fr.insa.toto.moveINSA.model.Etudiant;
import fr.insa.toto.moveINSA.model.SRI;

import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.VaadinSession;

public class MenuGauche extends SideNav {

    public MenuGauche() {
        // Appliquer un style global au menu
        this.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("padding", "10px");

        // Récupération unique de l'utilisateur
        Object user = VaadinSession.getCurrent().getAttribute("user");
        boolean isSRIConnecte = user instanceof SRI;

        // Création des items principaux
        SideNavItem main = new SideNavItem("Accueil", VuePrincipale.class);
        SideNavItem connexion = new SideNavItem("Connexion", ConnexionPanel.class);

        // Partie Partenaires
        SideNavItem partenaires = new SideNavItem("Partenaires");
        partenaires.addItem(new SideNavItem("Liste", PartenairesPanel.class));
        if (isSRIConnecte) {
            partenaires.addItem(new SideNavItem("Nouveau", NouveauPartenairePanel.class));
        }

        // Partie Offres
        SideNavItem offres = new SideNavItem("Offres");
        offres.addItem(new SideNavItem("Liste", OffresPanel.class));
        if (isSRIConnecte) {
            offres.addItem(new SideNavItem("Nouvelle", NouvelleOffrePanel.class));
        }

        // Partie Attribution
        SideNavItem attribution = new SideNavItem("Attribution", AttributionPanel.class);

        // Déconnexion
        SideNavItem deconnexion = new SideNavItem("Déconnexion", DeconnexionPanel.class);

        // Ajout des items principaux
        this.addItem(main, connexion, partenaires, offres, attribution, deconnexion);

        // Si un membre SRI est connecté, ajouter les menus de debug
        if (isSRIConnecte) {
            SideNavItem debug = new SideNavItem("Debug");
            debug.addItem(new SideNavItem("Test Driver", TestDriverPanel.class));
            debug.addItem(new SideNavItem("RAZ BDD", RAZBdDPanel.class));
            debug.addItem(new SideNavItem("Test ResultSetGrid", TestResultSetGrid.class));
            debug.addItem(new SideNavItem("Test DataGrid", TestDataGrid.class));
            debug.addItem(new SideNavItem("Test Grid Direct", TestGridDirect.class));
            this.addItem(debug);
        }

        // Appliquer les styles aux items
        styleItem(main);
        styleItem(connexion);
        styleItem(partenaires);
        styleItem(offres);
        styleItem(attribution);
        styleItem(deconnexion);
    }

    /**
     * Applique un style cohérent à un item du menu.
     * 
     * @param item L'élément SideNavItem à styliser.
     */
    private void styleItem(SideNavItem item) {
        item.getStyle()
            .set("text-align", "center")   // Centrer le texte
            .set("text-transform", "uppercase")  // Majuscules
            .set("font-weight", "bold")    // Texte en gras
            .set("font-size", "12px")      // Taille de police
            .set("padding", "10px")        // Espacement
            .set("border-bottom", "1px solid #ddd");  // Ligne de séparation
    }
    
    public void refreshMenu() {
    // Nettoyer les éléments existants
    this.removeAll();

    // Reconstruire le menu avec les éléments appropriés
    Object user = VaadinSession.getCurrent().getAttribute("user");
    boolean isSRIConnecte = user instanceof SRI;

    // Créer les éléments du menu
    SideNavItem main = new SideNavItem("Accueil", VuePrincipale.class);
    SideNavItem connexion = new SideNavItem("Connexion", ConnexionPanel.class);

    SideNavItem partenaires = new SideNavItem("Partenaires");
    partenaires.addItem(new SideNavItem("Liste", PartenairesPanel.class));
    if (isSRIConnecte) {
        partenaires.addItem(new SideNavItem("Nouveau", NouveauPartenairePanel.class));
    }

    SideNavItem offres = new SideNavItem("Offres");
    offres.addItem(new SideNavItem("Liste", OffresPanel.class));
    if (isSRIConnecte) {
        offres.addItem(new SideNavItem("Nouvelle", NouvelleOffrePanel.class));
    }

    SideNavItem attribution = new SideNavItem("Attribution", AttributionPanel.class);
    SideNavItem deconnexion = new SideNavItem("Déconnexion", DeconnexionPanel.class);

    // Ajouter les éléments au menu
    this.addItem(main, connexion, partenaires, offres, attribution, deconnexion);

    // Si un membre SRI est connecté, ajouter les options de debug
    if (isSRIConnecte) {
        SideNavItem debug = new SideNavItem("Debug");
        debug.addItem(new SideNavItem("Test Driver", TestDriverPanel.class));
        debug.addItem(new SideNavItem("RAZ BDD", RAZBdDPanel.class));
        debug.addItem(new SideNavItem("Test ResultSetGrid", TestResultSetGrid.class));
        debug.addItem(new SideNavItem("Test DataGrid", TestDataGrid.class));
        debug.addItem(new SideNavItem("Test Grid Direct", TestGridDirect.class));
        this.addItem(debug);
    }

    // Appliquer les styles
    styleItem(main);
    styleItem(connexion);
    styleItem(partenaires);
    styleItem(offres);
    styleItem(attribution);
    styleItem(deconnexion);
}

}
