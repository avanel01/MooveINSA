package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Etudiant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class EnteteInitiale extends HorizontalLayout {

    public EnteteInitiale() {
        // Configuration globale de la mise en page
        this.setWidthFull();
        this.setHeight("110px"); // Hauteur fixe de l'entête
        this.setPadding(true);
        this.setSpacing(true);
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        this.getStyle().set("background-color", "#F0F0F0"); // Couleur de fond de l'entête

        // Ajouter le logo à gauche
        Image logo = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Logo_RF.svg/440px-Logo_RF.svg.png", "Logo République");
        logo.setHeight("80px"); // Taille réduite du logo pour s'adapter à l'entête
        this.add(logo);

        // Ajouter un label pour le texte principal au centre
        Label title = new Label("Bienvenue sur le site !");
        title.getStyle().set("color", "black");
        title.getStyle().set("font-size", "24px");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("margin-left", "20px");
        this.add(title);

        // Ajouter un label pour afficher l'état de connexion à droite
        Label lNomPrenom = new Label();
        Etudiant etudiant = VaadinSession.getCurrent().getAttribute(Etudiant.class);

        if (etudiant != null) {
            lNomPrenom.setText("Connecté : " + etudiant.getNomEtudiant() + " " + etudiant.getPrenom());
        } else {
            lNomPrenom.setText("Aucun étudiant connecté.");
        }

        lNomPrenom.getStyle().set("color", "black");
        lNomPrenom.getStyle().set("font-size", "16px");
        lNomPrenom.getStyle().set("font-weight", "normal");
        lNomPrenom.getStyle().set("margin-left", "auto"); // Décale ce label à l'extrême droite

        this.add(lNomPrenom);

        // Justification des éléments entre gauche, centre et droite
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    }
}
