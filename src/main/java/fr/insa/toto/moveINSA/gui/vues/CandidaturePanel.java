package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.model.Candidature;
import java.sql.Connection;
import java.sql.SQLException;

@PageTitle("Candidature")
@Route("candidature/:idOffre") // Utilisez un paramètre dans l'URL
public class CandidaturePanel extends VerticalLayout {

    private Candidature nouveau;
    private TextField idOField;  // Champ pour la référence de l'offre
    private TextField idEField;  // Champ pour la ville
    private TextField OrdreField;  // Champ pour le pays
    private TextField ClassField;
    private TextField DateField;
    private Button bSave;

    public CandidaturePanel(@RouteParameter("idOffre") String idOffre) { // Récupérer l'ID de l'offre via l'URL
        add(new H2("Formulaire de Candidature"));

        this.nouveau = new Candidature(-1, -1, -1, -1, -1, null);

        // Champs de formulaire pour saisir les données
        this.idOField = new TextField("Référence de l'offre");
        this.idOField.setValue(idOffre);  // Pré-remplir le champ avec l'ID de l'offre

        this.idEField = new TextField("Ville");
        this.OrdreField = new TextField("Pays");
        this.ClassField = new TextField("Classement");
        this.DateField = new TextField("Date de séjour");

        // Bouton pour sauvegarder la candidature
        this.bSave = new Button("Sauvegarder", (t) -> {
            try (Connection con = ConnectionPool.getConnection()) {
                // Mise à jour des valeurs du modèle Candidature à partir des champs
                this.nouveau.setIdOffre(Integer.parseInt(this.idOField.getValue())); // Récupérer l'ID de l'offre
                this.nouveau.setVille(this.idEField.getValue());
                this.nouveau.setPays(this.OrdreField.getValue());
                this.nouveau.setClassement(Integer.parseInt(this.ClassField.getValue()));
                this.nouveau.setDateSejour(this.DateField.getValue());

                // Sauvegarde dans la base de données
                this.nouveau.saveInDB(con);

                // Notification de succès
                Notification.show("Candidature sauvegardée avec succès !");
            } catch (SQLException ex) {
                // Gestion des erreurs SQL
                System.err.println("Problème : " + ex.getLocalizedMessage());
                Notification.show("Problème : " + ex.getLocalizedMessage());
            }
        });

        // Ajout des champs et du bouton au panneau
        this.add(this.idOField, this.idEField, this.OrdreField, this.ClassField, this.DateField, this.bSave);
    }
}
