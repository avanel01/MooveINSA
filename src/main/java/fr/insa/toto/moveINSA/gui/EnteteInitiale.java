package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Etudiant;

public class EnteteInitiale extends HorizontalLayout {

    public EnteteInitiale() {
        // Configuration de la mise en page
        this.setWidthFull();
        this.setJustifyContentMode(JustifyContentMode.END);
        this.setDefaultVerticalComponentAlignment(Alignment.CENTER);

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
        
         
        this.setWidthFull();
        this.setHeight("150px");

        // Définir la couleur de fond pour correspondre à la couleur du menu déroulant
        this.getStyle().set("background-color", "#F0F0F0"); // Ajustez selon la couleur exacte du menu

        // Ajouter un label pour le texte principal
        Label title = new Label("Bienvenue sur le site !");
        title.getStyle().set("color", "black"); // Couleur noire
        title.getStyle().set("font-size", "24px"); // Taille de police
        title.getStyle().set("font-weight", "bold"); // Gras

        // Disposition des éléments
        this.add(title, lNomPrenom); // Ajoute les deux éléments
        this.setJustifyContentMode(JustifyContentMode.BETWEEN); // Répartit les éléments : gauche-droite
        this.setDefaultVerticalComponentAlignment(Alignment.CENTER); // Centre les éléments verticalement
    }
}
