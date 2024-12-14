package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.combobox.ComboBox;  // Importation du ComboBox
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
import fr.insa.toto.moveINSA.model.OffreMobilite;  // Ajout de l'import de la classe OffreMobilite

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@PageTitle("Candidature")
@Route("candidature/:idOffre") // Paramètre dynamique dans l'URL
public class CandidaturePanel extends VerticalLayout implements BeforeEnterObserver {

    private Candidature nouveau;
    private Label idOField;   // Label pour afficher la référence de l'offre
    private Label idEField;   // Label pour afficher l'INE de l'étudiant
    private ComboBox<Integer> ordreField; // Champ pour l'ordre de demande (ComboBox au lieu de TextField)
    private ComboBox<String> semestreField;  // ComboBox pour le semestre
    private Button bSave;

    private Etudiant etudiantConnecte;
    private OffreMobilite offre;  // Offre associée à la candidature

    public CandidaturePanel() {
        add(new H2("Formulaire de Candidature"));

        // Récupérer l'étudiant connecté depuis la session
        etudiantConnecte = VaadinSession.getCurrent().getAttribute(Etudiant.class);
        if (etudiantConnecte == null) {
            Notification.show("Erreur : Aucun étudiant connecté. Veuillez vous connecter.");
            return;
        }

        // Initialiser une nouvelle candidature
        this.nouveau = new Candidature(-1, -1, null, -1, -1, -1);

        // Champs pour le formulaire
        this.idOField = new Label("Référence de l'offre : (non chargée)");
        this.idEField = new Label("INE : " + etudiantConnecte.getINE());
        
        // ComboBox pour l'ordre de demande avec des valeurs de 1 à 5
        this.ordreField = new ComboBox<>("Ordre de demande");
        this.ordreField.setItems(1, 2, 3, 4, 5);  // Valeurs possibles de 1 à 5
        this.ordreField.setPlaceholder("Choisissez un ordre");

        // ComboBox pour le semestre
        this.semestreField = new ComboBox<>("Semestre");
        this.ordreField.setItems(5, 6, 7, 8, 9);
        this.semestreField.setPlaceholder("Choisissez un semestre");

        // Bouton pour sauvegarder la candidature
        this.bSave = new Button("Sauvegarder", event -> handleSave());

        // Ajout des champs et du bouton au panneau
        this.add(idOField, idEField, ordreField, semestreField, bSave);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Récupérer les paramètres de la route
        RouteParameters parameters = event.getRouteParameters();

        // Vérifier l'ID de l'offre
        Optional<Integer> idOffreOpt = getIdOffreFromParameters(parameters);
        if (idOffreOpt.isPresent()) {
            int idOffre = idOffreOpt.get();
            this.idOField.setText("Référence de l'offre : " + idOffre);  // Correction de l'affichage de l'ID de l'offre
            this.nouveau.setIdOffre(idOffre);

            // Récupérer l'offre depuis la base de données ou autre source
            try (Connection con = ConnectionPool.getConnection()) {
                Optional<OffreMobilite> offreOpt = OffreMobilite.getOffreById(con, idOffre);  // Méthode pour récupérer l'offre par son ID
                if (offreOpt.isPresent()) {
                    this.offre = offreOpt.get();

                    // Récupérer les semestres de l'offre et les ajouter dans le ComboBox
                    List<String> semestres = offre.getSemestres();  // Liste des semestres disponibles pour l'offre
                    semestreField.setItems(semestres);  // Ajouter les semestres au ComboBox
                } else {
                    Notification.show("Erreur : Offre introuvable.");
                }
            } catch (SQLException ex) {
                Notification.show("Erreur lors de la récupération de l'offre : " + ex.getLocalizedMessage());
            }
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

            // Validation et récupération de l'ordre de demande
            Integer ordre = this.ordreField.getValue();
            if (ordre == null) {
                Notification.show("Erreur : Veuillez sélectionner un ordre de demande.");
                return;
            }
            this.nouveau.setOrdre(ordre);

            // Validation et récupération du semestre
            String semestre = this.semestreField.getValue();
            if (semestre == null || !offre.getSemestres().contains(semestre)) {
                Notification.show("Erreur : Veuillez sélectionner un semestre valide pour l'offre.");
                return;
            }
            this.nouveau.setDate(Integer.parseInt(semestre));

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
