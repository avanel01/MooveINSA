package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.BeforeEnterEvent;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.model.Candidature;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@PageTitle("Candidature")
@Route("candidature/:idOffre") // Paramètre dynamique dans l'URL
public class CandidaturePanel extends VerticalLayout {

    private Candidature nouveau;
    private Label idOField;  // Label pour la référence de l'offre
    private TextField idEField;  // Champ pour l'INE
    private TextField OrdreField;  // Champ pour l'ordre de demande
    private TextField ClassField;  // Champ pour le classement
    private TextField DateField;  // Champ pour la date de séjour
    private Button bSave;

    public CandidaturePanel() {
        add(new H2("Formulaire de Candidature"));

        this.nouveau = new Candidature(-1, -1, null, -1, -1, null);

        // Champs de formulaire pour saisir les données
        this.idOField = new Label("Référence de l'offre");
        this.idEField = new TextField("INE");
        this.OrdreField = new TextField("Ordre de demande");
        this.ClassField = new TextField("Classement");
        this.DateField = new TextField("Date de séjour");

        // Bouton pour sauvegarder la candidature
        this.bSave = new Button("Sauvegarder", (t) -> {
            try (Connection con = ConnectionPool.getConnection()) {
                // Mise à jour des valeurs du modèle Candidature à partir des champs
                this.nouveau.setIdOffre(Integer.parseInt(this.idOField.getText())); // Récupérer l'ID de l'offre
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

    // Méthode pour récupérer l'idOffre depuis les paramètres de la route
    public Optional<Integer> getidO(RouteParameters parameters) {
        String idOffreStr = parameters.get("idOffre").orElse("0"); // Valeur par défaut si le paramètre est manquant
        try {
            return Optional.of(Integer.parseInt(idOffreStr)); // Retourner l'idOffre sous forme d'Optional<Integer>
        } catch (NumberFormatException e) {
            return Optional.empty(); // Retourner un Optional vide si la conversion échoue
        }
    }

    // Récupérer le paramètre 'idOffre' dans la méthode beforeEnter()
   
    public void beforeEnter(BeforeEnterEvent event) {
        // Récupérer les paramètres de la route
        RouteParameters parameters = event.getRouteParameters();

        // Récupérer et pré-remplir le label 'idOField' avec l'idOffre
        Optional<Integer> idOffre = getidO(parameters);
        idOffre.ifPresent(id -> this.idOField.setText("Référence de l'offre : " + id.toString())); // Afficher l'idOffre dans le label
    }
}
