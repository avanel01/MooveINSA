package fr.insa.toto.moveINSA.gui;

/**
 * Panel de déconnexion pour l'application MoveINSA.
 * Permet à l'utilisateur de se déconnecter et redirige vers la page de connexion.
 */
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Etudiant;

@Route(value = "deconnexion", layout = MainLayout.class)
@PageTitle("Déconnexion")
public class DeconnexionPanel extends VerticalLayout {

    public DeconnexionPanel() {
        
        // Styliser le conteneur principal avec une image de fond
        this.getStyle()
            .set("background-image", "url('https://www.insa-strasbourg.fr/wp-content/uploads/28070823_1895678403840285_8548131256382231960_o.jpg')")
            .set("background-size", "cover")
            .set("background-position", "center")
            .set("height", "100vh")
            .set("position", "relative");

        // Ajouter le logo en haut à gauche
        Image logo = new Image("https://apps.insa-strasbourg.fr/WebObjects/logos/logo_insa_strasbourg_234px.png", "Logo INSA Strasbourg");
        logo.getStyle()
            .set("position", "absolute")
            .set("top", "20px")
            .set("left", "20px")
            .set("width", "150px")
            .set("height", "auto");

        // Conteneur transparent centré pour le bouton Déconnexion
        Div container = new Div();
        container.getStyle()
            .set("background", "rgba(255, 255, 255, 0.9)") // Fond blanc semi-transparent
            .set("border-radius", "10px")
            .set("padding", "50px")
            .set("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.2)") // Ombre légère
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("justify-content", "center")
            .set("align-items", "center");
        
        // Titre de connexion
        H3 titre = new H3("Déconnexion de MoveINSA");
        titre.getStyle()
             .set("margin", "0 0 20px 0")
             .set("font-weight", "bold")
             .set("font-size", "1.5em")
             .set("text-align", "center");
        
        // Ajout du titre et du bouton de déconnexion dans le conteneur
        container.add(titre);

        Button bDeconnexion = new Button("Déconnexion");
        bDeconnexion.getStyle()
            .set("background-color", "#FF0000")
            .set("color", "white")
            .set("border", "none")
            .set("padding", "10px 20px")
            .set("font-size", "1em")
            .set("border-radius", "5px")
            .set("font-weight", "bold")
            .set("cursor", "pointer");
        bDeconnexion.addClickListener(event -> handleDeconnexion());

        // Ajouter le bouton de déconnexion au conteneur
        container.add(bDeconnexion);
        
        // Ajouter le conteneur à la vue
        this.add(container);

        // Ajouter les composants à la vue principale
        FlexLayout layout = new FlexLayout(container);
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);

        this.add(logo, layout);
    }


    /**
     * Gère la déconnexion de l'utilisateur.
     * Supprime les données utilisateur de la session, actualise l'interface,
     * et redirige vers la page de connexion.
     */
    private void handleDeconnexion() {
    // Récupérer l'utilisateur connecté
    Object utilisateur = VaadinSession.getCurrent().getAttribute("user");

    if (utilisateur != null) {
        // Déconnexion réussie
        Notification.show("Déconnexion réussie.");

        // Supprimer l'utilisateur et son rôle de la session
        VaadinSession.getCurrent().setAttribute("user", null);
        VaadinSession.getCurrent().setAttribute("role", null);

        // Actualiser l'entête
        EnteteInitiale entete = (EnteteInitiale) VaadinSession.getCurrent().getAttribute("entete");
        if (entete != null) {
            entete.updateUserInfo();
        }

        // Mettre à jour le layout principal
        MainLayout mainLayout = VaadinSession.getCurrent().getAttribute(MainLayout.class);
        if (mainLayout != null) {
            mainLayout.updateMainLayout(null);
        }

        // Mettre à jour le menu gauche
        MenuGauche menuGauche = VaadinSession.getCurrent().getAttribute(MenuGauche.class);
        if (menuGauche != null) {
            menuGauche.refreshMenu();
        }

        // Invalider la session
        VaadinSession.getCurrent().getSession().invalidate();

        // Forcer le rechargement de la page
        UI.getCurrent().getPage().reload();
    } else {
        // Aucun utilisateur connecté
        Notification.show("Aucun utilisateur connecté.");
    }
}

}
