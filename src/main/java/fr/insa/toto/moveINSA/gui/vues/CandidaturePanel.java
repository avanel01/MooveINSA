package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.model.Candidature;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.web.bind.annotation.PathVariable;

@PageTitle("Candidature")
@Route("candidature/:idOffre") // Paramètre dans l'URL
public class CandidaturePanel extends VerticalLayout {

    private Candidature nouveau;
    private TextField idOField;  // Champ pour la référence de l'offre
    private TextField idEField;  // Champ pour l'INE
    private TextField OrdreField;  // Champ pour l'ordre de demande
    private TextField ClassField;  // Champ pour le classement
    private TextField DateField;  // Champ pour la date de séjour
    private final Button bSave;

    public CandidaturePanel(@PathVariable("idOffre") String idOffre) { // Récupérer l'ID de l'offre via l'URL
        add(new H2("Formulaire de Candidature"));

        this.nouveau = new Candidature(-1, -1, null, -1, -1, null);

        // Champs de formulaire pour saisir les données
        this.idOField = new TextField("Référence de l'offre");
        this.idOField.setValue(idOffre);  // Pré-remplir le champ avec l'ID de l'offre

        this.idEField = new TextField("INE");
        this.OrdreField = new TextField("Ordre de demande");
        this.ClassField = new TextField("Classement");
        this.DateField = new TextField("Date de séjour");

        // Bouton pour sauvegarder la candidature
        this.bSave = new Button("Sauvegarder", (t) -> {
            try (Connection con = ConnectionPool.getConnection()) {
                // Mise à jour des valeurs du modèle Candidature à partir des champs
                this.nouveau.setIdOffre(Integer.parseInt(this.idOField.getValue())); // Récupérer l'ID de l'offre
                this.nouveau.setIdEtudiant(this.idEField.getValue());

                // Validation et conversion de l'Ordre et du Classement
                try {
                    this.nouveau.setOrdre(Integer.parseInt(this.OrdreField.getValue()));
                } catch (NumberFormatException e) {
                    Notification.show("Erreur : Ordre de demande invalide. Veuillez entrer un nombre.");
                    return;
                }

                try {
                    this.nouveau.setClassement(Integer.parseInt(this.ClassField.getValue()));
                } catch (NumberFormatException e) {
                    Notification.show("Erreur : Classement invalide. Veuillez entrer un nombre.");
                    return;
                }

                // Vérification et formatage de la date
                this.nouveau.setDate(this.DateField.getValue());

                // Sauvegarde dans la base de données
                this.nouveau.saveInDB(con);

                // Notification de succès
                Notification.show("Candidature sauvegardée avec succès !");
            } catch (SQLException ex) {
                // Gestion des erreurs SQL
                System.err.println("Problème : " + ex.getLocalizedMessage());
                Notification.show("Problème : " + ex.getLocalizedMessage());
            } catch (NumberFormatException ex) {
                // Gestion des erreurs de format pour les champs numériques
                Notification.show("Erreur : Veuillez vérifier les valeurs numériques.");
            }
        });

        // Ajout des champs et du bouton au panneau
        this.add(this.idOField, this.idEField, this.OrdreField, this.ClassField, this.DateField, this.bSave);
    }
}
