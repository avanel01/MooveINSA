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
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.model.Candidature;
import fr.insa.toto.moveINSA.model.Etudiant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@PageTitle("Candidature")
@Route("candidature/:idOffre") // Paramètre dynamique dans l'URL
public class CandidaturePanel extends VerticalLayout implements BeforeEnterObserver {

    private Candidature nouveau;
    private Label idOField;   // Label pour afficher la référence de l'offre
    private Label idEField;   // Label pour afficher l'INE de l'étudiant
    private TextField ordreField; // Champ pour l'ordre de demande
    private TextField dateField;  // Champ pour la date de séjour
    private Button bSave;

    private Etudiant etudiantConnecte;

    public CandidaturePanel() {
        add(new H2("Formulaire de Candidature"));

        // Récupérer l'étudiant connecté depuis la session
        etudiantConnecte = VaadinSession.getCurrent().getAttribute(Etudiant.class);
        if (etudiantConnecte == null) {
            Notification.show("Erreur : Aucun étudiant connecté. Veuillez vous connecter.");
            return;
        }

        // Initialiser une nouvelle candidature
        this.nouveau = new Candidature(-1, -1, null, -1, -1, null);

        // Champs pour le formulaire
        this.idOField = new Label("Référence de l'offre : (non chargée)");
        this.idEField = new Label("INE : " + etudiantConnecte.getINE());
        this.ordreField = new TextField("Ordre de demande");
        this.dateField = new TextField("Date de séjour");

        // Bouton pour sauvegarder la candidature
        this.bSave = new Button("Sauvegarder", event -> handleSave());

        // Ajout des champs et du bouton au panneau
        this.add(idOField, idEField, ordreField, dateField, bSave);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Récupérer les paramètres de la route
        RouteParameters parameters = event.getRouteParameters();

        // Vérifier l'ID de l'offre
        Optional<Integer> idOffreOpt = getIdOffreFromParameters(parameters);
        if (idOffreOpt.isPresent()) {
            int idOffre = idOffreOpt.get();
            this.idOField.setText("Référence de l'offre : partenaire/" + idOffre);
            this.nouveau.setIdOffre(idOffre);
        } else {
            Notification.show("Erreur : ID de l'offre invalide ou manquant.");
            this.idOField.setText("Référence de l'offre : (invalide)");
        }
    }

    private Optional<Integer> getIdOffreFromParameters(RouteParameters parameters) {
        try {
            String idOffreStr = parameters.get("idOffre").orElse(null);
            if (idOffreStr != null) {
                return Optional.of(Integer.parseInt(idOffreStr));
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur : ID de l'offre non valide.");
        }
        return Optional.empty();
    }

    private void handleSave() {
        if (etudiantConnecte == null) {
            Notification.show("Erreur : Aucun étudiant connecté.");
            return;
        }

        try (Connection con = ConnectionPool.getConnection()) {
            // Remplir les informations de la candidature
            this.nouveau.setIdEtudiant(etudiantConnecte.getINE());

            // Validation et conversion des champs
            try {
                int ordre = Integer.parseInt(this.ordreField.getValue().trim());
                this.nouveau.setOrdre(ordre);
            } catch (NumberFormatException e) {
                Notification.show("Erreur : Ordre de demande invalide. Veuillez entrer un nombre.");
                return;
            }

            String dateSejour = this.dateField.getValue().trim();
            if (dateSejour.isEmpty()) {
                Notification.show("Erreur : La date de séjour est obligatoire.");
                return;
            }
            this.nouveau.setDate(dateSejour);

            // Sauvegarde dans la base de données
            this.nouveau.saveInDB(con);

            // Notification de succès
            Notification.show("Candidature sauvegardée avec succès !");
        } catch (SQLException ex) {
            // Gestion des erreurs SQL
            System.err.println("Problème lors de la sauvegarde : " + ex.getLocalizedMessage());
            Notification.show("Erreur : " + ex.getLocalizedMessage());
        }
    }
}
