package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Etudiant;
import fr.insa.toto.moveINSA.model.SRI;

// Imports pour les composants personnalisés

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
    // Créer l'instance de MenuGauche
    this.menuGauche = new MenuGauche();

    // Enregistrer l'instance dans la session Vaadin
    VaadinSession.getCurrent().setAttribute(MenuGauche.class, this.menuGauche);

    // Ajouter le menu à la vue
    this.menuGauche.setHeightFull();
    this.addToDrawer(this.menuGauche);

    // Créer l'entête et le toggle du drawer
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
    public void beforeEnter(BeforeEnterEvent event) {
    // Récupérer l'utilisateur connecté depuis la session
    Object user = VaadinSession.getCurrent().getAttribute("user");

    // Vérifier si un utilisateur est connecté
    if (user == null) {
        // Si aucun utilisateur n'est connecté, rediriger vers la page de connexion
        event.rerouteTo("connexion");  // Rediriger vers la page de connexion
    } else {
        // Si un utilisateur est connecté, mettre à jour l'entête avec ses informations
        updateMainLayout(null);  // Appeler updateMainLayout pour mettre à jour l'entête
    }
}


    /**
     * Cette méthode est appelée après la connexion de l'utilisateur pour
     * mettre à jour l'entête avec les informations de l'étudiant ou du SRI.
     */
    public void updateMainLayout(String userInfo) {
    // Récupérer l'utilisateur connecté depuis la session
    Object user = VaadinSession.getCurrent().getAttribute("user");

    // Vérifier si un utilisateur est connecté
    if (user != null) {
        // Si l'utilisateur est un étudiant, mettre à jour l'entête avec ses informations
        if (user instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) user;
            // Passer les informations nécessaires à la méthode pour mettre à jour l'entête
            entete.updateUserInfo();
        }
        // Si l'utilisateur est un membre SRI, mettre à jour l'entête avec ses informations
        else if (user instanceof SRI) {
            SRI sri = (SRI) user;
            // Passer les informations nécessaires à la méthode pour mettre à jour l'entête
            entete.updateUserInfo();
        }
    } else {
        // Si aucun utilisateur n'est connecté, rediriger vers la page de connexion
        Notification.show("Aucun utilisateur connecté.");
        UI.getCurrent().navigate("connexion");  // Rediriger vers la page de connexion
    }
}
    
   public void refreshLayout() {
    // Supprimer l'ancien menu gauche si nécessaire
    if (this.menuGauche != null) {
        this.remove(this.menuGauche);  // Retirer le menu actuel
    }

    // Recréer un nouveau menu gauche
    this.menuGauche = new MenuGauche();
    this.menuGauche.setHeightFull();
    this.addToDrawer(this.menuGauche);  // Ajouter le nouveau menu dans le tiroir

    // Mettre à jour l'entête avec les informations de l'utilisateur
    updateMainLayout(null);
}
}



