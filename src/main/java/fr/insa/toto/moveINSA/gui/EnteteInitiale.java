package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Etudiant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class EnteteInitiale extends HorizontalLayout {

    public EnteteInitiale() {
        // Configuration de la mise en page
        this.setWidthFull();
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Récupérer l'étudiant connecté
        Etudiant etudiant = VaadinSession.getCurrent().getAttribute(Etudiant.class);

        // Créer un label pour afficher le nom et prénom de l'étudiant
        Label lNomPrenom = new Label();

        // Vérifier si un étudiant est connecté
        if (etudiant != null) {
            lNomPrenom.setText("Connecté : " + etudiant.getNomEtudiant() + " " + etudiant.getPrenom());
        } else {
            lNomPrenom.setText("Aucun étudiant connecté.");
        }

        // Ajouter le label à la mise en page
        this.add(lNomPrenom);
        
        // Label pour l'état de connexion
        Label etatConnexion = new Label(lNomPrenom.getText());
        etatConnexion.getStyle().set("color", "black");
        etatConnexion.getStyle().set("font-size", "16px");
        etatConnexion.getStyle().set("font-weight", "normal");
        
         
        // Configuration de la mise en page
        this.setWidth("85%"); // La largeur est réduite ici pour rendre l'entête plus compacte
        this.setHeight("150px");

        // Couleur de fond de l'entête 
        this.getStyle().set("background-color", "white"); 

        // Logo à gauche
        Image logo = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Logo_RF.svg/440px-Logo_RF.svg.png", "Logo République");
        logo.setHeight("80px"); // Réduit la taille du logo

        // Ajouter le logo à la mise en page
        this.add(logo);

        // Ajouter un label pour le texte principal
        Label title = new Label("Bienvenue sur le site !");
        title.getStyle().set("color", "black"); // Couleur noire
        title.getStyle().set("font-size", "24px"); // Taille de police
        title.getStyle().set("font-weight", "bold"); // Gras
        title.getStyle().set("text-align", "center");

        // Disposition des éléments
        this.add(title, lNomPrenom); // Ajoute les deux éléments
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN); // Répartit les éléments : gauche-droite
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER); // Centre les éléments verticalement
    }
}
