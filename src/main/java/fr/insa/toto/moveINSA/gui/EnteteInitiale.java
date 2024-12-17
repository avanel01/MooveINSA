package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Etudiant;
import fr.insa.toto.moveINSA.model.SRI;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class EnteteInitiale extends HorizontalLayout {

    private Text lNomPrenom;

    public EnteteInitiale() {
        // Configuration globale de la mise en page
        this.setWidthFull();
        this.setHeight("110px");
        this.setPadding(true);
        this.setSpacing(true);
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        this.getStyle().set("background-color", "#F0F0F0");

        // Ajouter le logo à gauche
        Image logo = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Logo_RF.svg/440px-Logo_RF.svg.png", "Logo République");
        logo.setHeight("80px");
        this.add(logo);

        // Ajouter un label pour le texte principal au centre
        Text title = new Text("Bienvenue sur le site !");
        title.getStyle().set("color", "black");
        title.getStyle().set("font-size", "24px");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("margin-left", "20px");
        this.add(title);

        // Ajouter un label pour afficher l'état de connexion à droite
        lNomPrenom = new Text();
        updateUserInfo(); // Initialisation avec les informations actuelles
        lNomPrenom.getStyle().set("color", "black");
        lNomPrenom.getStyle().set("font-size", "16px");
        lNomPrenom.getStyle().set("font-weight", "normal");
        lNomPrenom.getStyle().set("margin-left", "auto");

        this.add(lNomPrenom);

        this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    }

    /**
     * Met à jour les informations de l'utilisateur (étudiant ou membre SRI) affichées dans l'entête.
     */
    public void updateUserInfo() {
        Object user = VaadinSession.getCurrent().getAttribute("user");
        
        if (user instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) user;
            lNomPrenom.setText("Connecté : " + etudiant.getNomEtudiant() + " " + etudiant.getPrenom());
        } else if (user instanceof SRI) {
            SRI sri = (SRI) user;
            lNomPrenom.setText("Connecté (membre du SRI) : " + sri.getLogin());
        } else {
            lNomPrenom.setText("Aucun utilisateur connecté.");
        }
    }
}
