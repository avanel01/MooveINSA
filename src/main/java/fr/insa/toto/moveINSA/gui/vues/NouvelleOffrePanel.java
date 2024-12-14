package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.Partenaire;
import fr.insa.toto.moveINSA.model.OffreMobilite;
import java.sql.Connection;
import java.sql.SQLException;

@Route(value = "offres/nouveau", layout = MainLayout.class)
public class NouvelleOffrePanel extends VerticalLayout {

    private ChoixPartenaireCombo cbPartenaire;
    private IntegerField ifPlaces;
    private TextField ifPar;
    private IntegerField ifSemestre;
    private IntegerField ifNiv;
    private TextField ifDispo;
    private TextField ifNomO;
    private TextField ifSpe;
    private Button bSave;

    public NouvelleOffrePanel() {
        // Initialisation des champs
        cbPartenaire = new ChoixPartenaireCombo();
        ifPlaces = new IntegerField("Nombre de places");
        ifPar = new TextField("Proposé par");
        ifSemestre = new IntegerField("Semestre");
        ifNiv = new IntegerField("Niveau scolaire");
        ifDispo = new TextField("Dispositif");
        ifNomO = new TextField("Nom de l'offre");
        ifSpe = new TextField("Spécialité");
        bSave = new Button("Sauvegarder");
        
        // Changer la couleur du bouton "Sauvegarder" en rouge
        bSave.getStyle().set("background-color", "red");
        bSave.getStyle().set("color", "white");
        bSave.getStyle().set("border", "none");
        bSave.getStyle().set("padding", "10px 20px");
        bSave.getStyle().set("cursor", "pointer");
        bSave.getStyle().set("font-weight", "bold");
        bSave.getStyle().set("border-radius", "4px");

        // Action au clic sur le bouton "Sauvegarder"
        bSave.addClickListener(e -> {
            // Récupérer le partenaire sélectionné
            Partenaire selected = cbPartenaire.getValue();
            if (selected == null) {
                Notification.show("Vous devez sélectionner un partenaire");
                return;
            }

            // Vérification du nombre de places
            Integer places = ifPlaces.getValue();
            if (places == null || places <= 0) {
                Notification.show("Vous devez préciser un nombre de places valide");
                return;
            }

            // Vérification du partenaire
            String par = ifPar.getValue();
            if (par == null || par.isEmpty()) {
                Notification.show("Vous devez préciser un partenaire valide");
                return;
            }
            
            try (Connection con = ConnectionPool.getConnection()) {
                Partenaire p = Partenaire.getPartenaireByRef(con, par).orElse(null);
                if (p == null) {
                    Notification.show("Le partenaire avec la référence " + par + " n'a pas été trouvé.");
                    return;
                }

            // Vérification du semestre
            Integer sem = ifSemestre.getValue();
            if (sem == null || sem <= 4 || sem >= 10) {
                Notification.show("Vous devez préciser un semestre valide");
                return;
            }

            // Vérification du niveau scolaire
            Integer niv = ifNiv.getValue();
            if (niv == null || niv <= 2 || niv >= 6) {
                Notification.show("Vous devez préciser un niveau scolaire valide");
                return;
            }

            // Vérification du dispositif
            String dispo = ifDispo.getValue();
            if (dispo == null || dispo.isEmpty()) {
                Notification.show("Vous devez préciser un dispositif valide");
                return;
            }

            // Vérification du nom de l'offre
            String nom = ifNomO.getValue();
            if (nom == null || nom.isEmpty()) {
                Notification.show("Vous devez préciser un nom pour l'offre valide");
                return;
            }

            // Vérification de la spécialité
            String spe = ifSpe.getValue();
            if (spe == null || spe.isEmpty()) {
                Notification.show("Vous devez préciser une spécialité valide");
                return;
            }


                // Créer l'offre de mobilité avec les données saisies
                int partId = p.getIdPartenaire();
                OffreMobilite nouvelleOffre = new OffreMobilite(places, partId, sem, niv, dispo, nom, spe);

                // Sauvegarder l'offre dans la base de données
                nouvelleOffre.saveInDB(con);
                Notification.show("Nouvelle offre enregistrée avec succès !");
            } catch (SQLException ex) {
                Notification.show("Erreur lors de l'enregistrement de l'offre : " + ex.getMessage());
            }
        });
        
        // Centrer les éléments dans la disposition verticale
        this.setAlignItems(Alignment.CENTER);

        // Ajout des composants à l'interface
        this.add(cbPartenaire, ifPlaces, ifPar, ifSemestre, ifNiv, ifDispo, ifNomO, ifSpe, bSave);
    }
}
