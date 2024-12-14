/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.Partenaire;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe permettant la création d'un nouveau partenaire.
 * Fournit une interface utilisateur pour entrer les informations nécessaires.
 * 
 * @author Francois
 */
@PageTitle("MoveINSA")
@Route(value = "partenaires/nouveau", layout = MainLayout.class)
public class NouveauPartenairePanel extends VerticalLayout {

    private Partenaire nouveau;
    private TextField refField;  // Champ pour la référence
    private TextField villeField;  // Champ pour la ville
    private TextField paysField;  // Champ pour le pays
    private Button bSave;
   
    public NouveauPartenairePanel() {
        // Titre du panneau
        this.add(new H3("Création d'un nouveau partenaire"));

        // Initialisation de l'objet Partenaire avec des valeurs par défaut
        this.nouveau = new Partenaire("", null, null);

        // Champs de formulaire pour saisir les données
        this.refField = new TextField("Référence du partenaire");
        this.villeField = new TextField("Ville");
        this.paysField = new TextField("Pays");
        
        // Changer la largeur des champs pour les rendre plus larges et homogènes
        refField.getStyle().set("width", "300px");
        villeField.getStyle().set("width", "300px");
        paysField.getStyle().set("width", "300px");
        
        // Centrer les labels et le texte des champs
        refField.getElement().getStyle().set("text-align", "center");
        villeField.getElement().getStyle().set("text-align", "center");
        paysField.getElement().getStyle().set("text-align", "center");
        
        // Centrer le texte des labels (les titres au-dessus des champs)
        refField.setLabel("Référence du partenaire");
        villeField.setLabel("Ville");
        paysField.setLabel("Pays");

        // Bouton pour sauvegarder le partenaire
        this.bSave = new Button("Sauvegarder", (t) -> {
            try (Connection con = ConnectionPool.getConnection()) {
                // Mise à jour des valeurs du modèle Partenaire à partir des champs
                this.nouveau.setRefPartenaire(this.refField.getValue());
                this.nouveau.setVille(this.villeField.getValue());
                this.nouveau.setPays(this.paysField.getValue());

                // Sauvegarde dans la base de données
                this.nouveau.saveInDB(con);

                // Notification de succès
                Notification.show("Partenaire sauvegardé avec succès !");
            } catch (SQLException ex) {
                // Gestion des erreurs SQL
                System.err.println("Problème : " + ex.getLocalizedMessage());
                Notification.show("Problème : " + ex.getLocalizedMessage());
            }
            
            
        });
        
        // Appliquer le style au bouton pour qu'il devienne rouge
        bSave.getStyle().set("background-color", "red");
        bSave.getStyle().set("color", "white");
        bSave.getStyle().set("border", "none");
        bSave.getStyle().set("padding", "10px 20px");
        bSave.getStyle().set("cursor", "pointer");
        bSave.getStyle().set("font-weight", "bold");
        bSave.getStyle().set("border-radius", "4px");

        // Ajout des champs et du bouton au panneau
        this.add(this.refField, this.villeField, this.paysField, this.bSave);
        
        // Centrer les éléments dans la disposition verticale
        this.setAlignItems(Alignment.CENTER);
    }
}
