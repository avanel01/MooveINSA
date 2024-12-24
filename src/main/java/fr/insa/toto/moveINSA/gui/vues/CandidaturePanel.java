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

    private Candidature candidature;
    private Paragraph idOffreField;
    private Paragraph idEtudiantField;
    private ComboBox<Integer> ordreField;
    private ComboBox<Integer> semestreField;
    private Button saveButton;

    private Etudiant etudiantConnecte;
    private OffreMobilite offre;

    public CandidaturePanel() {
        configureLayout();
        addTitle();

        // Récupérer l'étudiant connecté
        etudiantConnecte = (Etudiant) VaadinSession.getCurrent().getAttribute("user");
        if (etudiantConnecte == null) {
            Notification.show("Erreur : Aucun étudiant connecté. Veuillez vous connecter.");
            return;
        }

        // Initialiser une nouvelle candidature
        this.candidature = new Candidature(-1, -1, null, -1, -1, -1);

        createFormFields();
        createSaveButton();
    }

    private void configureLayout() {
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setSizeFull();
        this.getStyle().set("padding", "20px").set("background-color", "#f9f9f9");
    }

    private void addTitle() {
        H2 title = new H2("Formulaire de Candidature");
        title.getStyle().set("text-align", "center").set("color", "#333").set("margin-bottom", "20px");
        add(title);
    }

    private void createFormFields() {
        // Champs pour le formulaire
        this.idOffreField = new Paragraph("Référence de l'offre : (non chargée)");
        this.idEtudiantField = new Paragraph("INE : " + etudiantConnecte.getINE());

        // ComboBox pour l'ordre de demande
        this.ordreField = new ComboBox<>("Ordre de la demande");
        this.ordreField.setItems(1, 2, 3, 4, 5);
        this.ordreField.setPlaceholder("Choisissez un ordre");

        // ComboBox pour le semestre
        this.semestreField = new ComboBox<>("Semestre de sejour");
        this.semestreField.setPlaceholder("Choisissez un semestre");

        this.add(idOffreField, idEtudiantField, ordreField, semestreField);
    }

    private void createSaveButton() {
        this.saveButton = new Button("Sauvegarder", event -> handleSave());
        this.saveButton.getStyle()
            .set("background-color", "red")
            .set("color", "white")
            .set("font-size", "16px")
            .set("font-weight", "bold")
            .set("cursor", "pointer")
            .set("border-radius", "5px")
            .set("padding", "10px 20px");
        this.add(saveButton);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        Optional<Integer> idOffreOpt = getIdOffreFromParameters(parameters);

        if (idOffreOpt.isPresent()) {
            int idOffre = idOffreOpt.get();
            this.candidature.setIdOffre(idOffre);

            try (Connection con = ConnectionPool.getConnection()) {
                Optional<OffreMobilite> offreOpt = OffreMobilite.getOffreById(con, idOffre);

                if (offreOpt.isPresent()) {
                    this.offre = offreOpt.get();
                    this.idOffreField.setText("Référence de l'offre : " + offre.getNomOffre());

                    // Charger les semestres disponibles
                    List<Integer> semestresDisponibles = offre.getSemestres();
                    if (semestresDisponibles.isEmpty()) {
                        Notification.show("Aucun semestre disponible pour cette offre.");
                    } else {
                        this.semestreField.setItems(semestresDisponibles);
                    }
                } else {
                    Notification.show("Erreur : Offre introuvable.");
                    this.idOffreField.setText("Référence de l'offre : Offre introuvable");
                }
            } catch (SQLException ex) {
                Notification.show("Erreur lors de la récupération de l'offre : " + ex.getLocalizedMessage());
                this.idOffreField.setText("Référence de l'offre : Erreur de chargement");
            }
        } else {
            Notification.show("Erreur : ID de l'offre invalide ou manquant.");
            this.idOffreField.setText("Référence de l'offre : (invalide)");
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
            this.candidature.setIdEtudiant(etudiantConnecte.getINE());

            Integer ordre = this.ordreField.getValue();
            if (ordre == null) {
                Notification.show("Erreur : Veuillez sélectionner un ordre de demande.");
                return;
            }
            this.candidature.setOrdre(ordre);

            Integer semestre = this.semestreField.getValue();
            if (semestre == null || !offre.getSemestres().contains(semestre)) {
                Notification.show("Erreur : Veuillez sélectionner un semestre valide.");
                return;
            }
            this.candidature.setDate(semestre);

            this.candidature.saveInDB(con);
            Notification.show("Candidature sauvegardée avec succès !");
            UI.getCurrent().navigate("");
        } catch (SQLException ex) {
            Notification.show("Erreur lors de la sauvegarde : " + ex.getLocalizedMessage());
        }
    }
}
