package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

/**
 * Utilisé par toutes les pages comme layout.
 * <p>
 * C'est ici que sont initialisées les infos valables pour l'ensemble de la
 * session, et en particulier la connection à la base de donnée.
 * </p>
 *
 * @author francois
 */
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private MenuGauche menuGauche;
    private EnteteInitiale entete;

    public MainLayout() {
        // Initialisation du menu gauche
        this.menuGauche = new MenuGauche();
        this.menuGauche.setHeightFull();

        // Ajout du menu gauche au drawer
        this.addToDrawer(this.menuGauche);

        // Ajout du DrawerToggle pour ouvrir/fermer le menu
        DrawerToggle toggle = new DrawerToggle();
        
        // Initialisation de l'entête
        this.entete = new EnteteInitiale();

        // Ajout du DrawerToggle et de l'entête à la navbar
        this.addToNavbar(toggle, entete);
    }

    // Ajout d'un getter pour l'entête
    public EnteteInitiale getEntete() {
        return entete;
    }

    /**
     * Cette méthode est appelée systématiquement par Vaadin avant l'affichage
     * de toute page ayant ce layout (donc à priori toutes les pages "normales"
     * sauf pages d'erreurs) de l'application.
     * <p>
     * Pour l'instant, je ne m'en sers pas, mais je l'ai gardé pour me souvenir
     * de cette possibilité.
     * </p>
     *
     * @param bee
     */
    @Override
    public void beforeEnter(BeforeEnterEvent bee) {
        // Permet par exemple de modifier la destination en cas de problème
        // bee.rerouteTo(NoConnectionToBDDErrorPanel.class);
    }
}
