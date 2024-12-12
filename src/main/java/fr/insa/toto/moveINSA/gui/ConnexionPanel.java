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
package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.model.Etudiant;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author alixvanel
 */
@Route(value = "", layout = MainLayout.class) // La route reste la page d'accueil
@PageTitle("Connexion")
public class ConnexionPanel extends VerticalLayout {

    private final TextField tfINE;
    private final PasswordField pfMdp;
    private final Button bLogin;

    public ConnexionPanel() {
        // Configuration de la mise en page
        this.setWidthFull();
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);

        // Champ texte pour l'identifiant
        this.tfINE = new TextField("Votre INE :");

        // Champ mot de passe
        this.pfMdp = new PasswordField("Votre mot de passe :");

        // Bouton de connexion
        this.bLogin = new Button("Login");
        this.bLogin.addClickListener(event -> handleLogin());

        // Ajout des composants à la mise en page
        this.add(new H3("Connexion à MoveINSA"));
        this.add(tfINE, pfMdp, bLogin);
    }

    private void handleLogin() {
    String ref = this.tfINE.getValue().trim();
    String mdpSaisi = this.pfMdp.getValue().trim();

    // Vérifie si les champs sont remplis
    if (ref.isEmpty()) {
        Notification.show("Veuillez entrer un INE.");
        return;
    }
    if (mdpSaisi.isEmpty()) {
        Notification.show("Veuillez entrer votre mot de passe.");
        return;
    }

    try (Connection con = ConnectionPool.getConnection()) {
        Optional<Etudiant> etudiantOpt = Etudiant.getEtudiantByINE(con, ref);

        if (etudiantOpt.isEmpty()) {
            Notification.show("INE invalide : " + ref);
        } else {
            Etudiant etudiant = etudiantOpt.get();

            // Vérifie le mot de passe
            if (etudiant.getMdp().equals(mdpSaisi)) {
                // Stocker l'étudiant dans la session
                VaadinSession.getCurrent().setAttribute(Etudiant.class, etudiant);
                Notification.show("Bienvenue, " + etudiant.getNomEtudiant() + " " + etudiant.getPrenom() + " !");
                // Naviguer vers la vue principale
                UI.getCurrent().navigate(VuePrincipale.class);
            } else {
                Notification.show("Mot de passe incorrect.");
            }
        }
    } catch (SQLException ex) {
        Notification.show("Problème lors de la connexion : " + ex.getLocalizedMessage());
    }
}

}

