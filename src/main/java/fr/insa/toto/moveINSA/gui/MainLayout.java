package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Etudiant;
import fr.insa.toto.moveINSA.model.SRI;

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

        this.menuGauche = new MenuGauche();
        this.menuGauche.setHeightFull();
        this.addToDrawer(this.menuGauche);

        DrawerToggle toggle = new DrawerToggle();
        this.entete = new EnteteInitiale();
        this.addToNavbar(toggle, entete);
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

    /**
     * Cette méthode est appelée après la connexion de l'utilisateur pour
     * mettre à jour l'entête avec les informations de l'étudiant ou du SRI.
     */
    public void updateMainLayout(String userInfo) {
        // Si un étudiant est connecté, on met à jour avec les informations de l'étudiant
        Etudiant etudiant = (Etudiant) VaadinSession.getCurrent().getAttribute("user");
        if (etudiant != null) {
            entete.updateEtudiantInfo();  // Utiliser la méthode qui met à jour les infos de l'étudiant
        }

        // Si un membre SRI est connecté, on met à jour avec les informations du SRI
        SRI sri = (SRI) VaadinSession.getCurrent().getAttribute("user");
        if (sri != null) {
            entete.updateSRIInfo();  // Utiliser la méthode qui met à jour les infos du SRI
        }
    }
}
