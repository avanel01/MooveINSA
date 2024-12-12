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
    }
}
