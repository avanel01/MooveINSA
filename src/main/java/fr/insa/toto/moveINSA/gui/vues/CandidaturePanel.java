package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.model.Candidature;
import fr.insa.toto.moveINSA.model.Etudiant;
import fr.insa.toto.moveINSA.model.OffreMobilite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@PageTitle("Candidature")
@Route("candidature/:idOffre")
public class CandidaturePanel extends VerticalLayout implements BeforeEnterObserver {

    private Candidature nouveau;
    private Paragraph idOField;
    private Paragraph idEField;
    private ComboBox<Integer> ordreField;
    private ComboBox<Integer> semestreField;
    private Button bSave;

    private Etudiant etudiantConnecte;
    private OffreMobilite offre;

    public CandidaturePanel() {
        add(new H2("Formulaire de Candidature"));

        // Récupérer l'étudiant connecté depuis la session
        etudiantConnecte = (Etudiant) VaadinSession.getCurrent().getAttribute("user");
        if (etudiantConnecte == null) {
            Notification.show("Erreur : Aucun étudiant connecté. Veuillez vous connecter.");
            return;
        }

        // Initialiser une nouvelle candidature
        this.nouveau = new Candidature(-1, -1, null, -1, -1, -1);

        // Champs pour le formulaire
        this.idOField = new Paragraph("Référence de l'offre : (non chargée)");
        this.idEField = new Paragraph("INE : " + etudiantConnecte.getINE());

        // ComboBox pour l'ordre de demande
        this.ordreField = new ComboBox<>("Ordre de demande");
        this.ordreField.setItems(1, 2, 3, 4, 5);
        this.ordreField.setPlaceholder("Choisissez un ordre");

        // ComboBox pour le semestre
        this.semestreField = new ComboBox<>("Semestre");
        this.semestreField.setItems(5, 6, 7, 8, 9);
        this.semestreField.setPlaceholder("Choisissez un semestre");

        // Bouton pour sauvegarder la candidature
        this.bSave = new Button("Sauvegarder", event -> handleSave());

        // Ajout des champs et du bouton au panneau
        this.add(idOField, idEField, ordreField, semestreField, bSave);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        Optional<Integer> idOffreOpt = getIdOffreFromParameters(parameters);

        if (idOffreOpt.isPresent()) {
            int idOffre = idOffreOpt.get();
            this.nouveau.setIdOffre(idOffre);

            try (Connection con = ConnectionPool.getConnection()) {
                Optional<OffreMobilite> offreOpt = OffreMobilite.getOffreById(con, idOffre);

                if (offreOpt.isPresent()) {
                    this.offre = offreOpt.get();

                    // Afficher le nom de l'offre
                    String nomOffre = offre.getNomOffre();
                    this.idOField.setText("Référence de l'offre : " + nomOffre);

                    // Charger les semestres disponibles
                    List<Integer> semestres = offre.getSemestres();
                    if (semestres.isEmpty()) {
                        Notification.show("Aucun semestre disponible pour cette offre.");
                    } else {
                        this.semestreField.setItems(semestres);
                    }
                } else {
                    Notification.show("Erreur : Offre introuvable.");
                    this.idOField.setText("Référence de l'offre : Offre introuvable");
                }
            } catch (SQLException ex) {
                Notification.show("Erreur lors de la récupération de l'offre : " + ex.getLocalizedMessage());
                this.idOField.setText("Référence de l'offre : Erreur de chargement");
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
            // Valider et remplir les informations de la candidature
            this.nouveau.setIdEtudiant(etudiantConnecte.getINE());

            Integer ordre = this.ordreField.getValue();
            if (ordre == null) {
                Notification.show("Erreur : Veuillez sélectionner un ordre de demande.");
                return;
            }
            this.nouveau.setOrdre(ordre);

            Integer semestre = this.semestreField.getValue();
            if (semestre == null || !offre.getSemestres().contains(semestre)) {
                Notification.show("Erreur : Veuillez sélectionner un semestre valide.");
                return;
            }
            this.nouveau.setDate(semestre);

            // Sauvegarde
            this.nouveau.saveInDB(con);
            Notification.show("Candidature sauvegardée avec succès !");

            // Rediriger vers la vue principale (route "/")
            UI.getCurrent().navigate("");
        } catch (SQLException ex) {
            Notification.show("Erreur : " + ex.getLocalizedMessage());
        }
    }
}
