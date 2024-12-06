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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.session.SessionInfo;
import fr.insa.toto.moveINSA.model.Etudiant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Barre d'en-tête initiale pour l'application.
 *
 * @author francois
 */
public class EnteteInitiale extends HorizontalLayout {

    private final TextField tfNom;
    private final Button bLogin;
    private final Button bLogout;

    public EnteteInitiale() {
        this.setWidthFull();
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Champ texte pour l'INE
        this.tfNom = new TextField("Votre INE :");

        // Bouton de login
        this.bLogin = new Button("Login");
        this.bLogin.addClickListener(event -> handleLogin());

        // Bouton de logout
        this.bLogout = new Button("Logout");
        this.bLogout.addClickListener(event -> handleLogout());

        // Rafraîchir l'affichage initial
        this.refresh();
    }

    private void handleLogin() {
        String ref = this.tfNom.getValue().trim();

        // Vérifie si l'utilisateur a saisi une valeur
        if (ref.isEmpty()) {
            Notification.show("Veuillez entrer un INE.");
            return;
        }

        try (Connection con = ConnectionPool.getConnection()) {
            Optional<Etudiant> etudiantOpt = Etudiant.getEtudiantByINE(con, ref);

            if (etudiantOpt.isEmpty()) {
                Notification.show("INE invalide : " + ref);
            } else {
                SessionInfo.doLogin(etudiantOpt.get());
                Notification.show("Bienvenue, " + etudiantOpt.get().getNomEtudiant() + " !");
            }
        } catch (SQLException ex) {
            Notification.show("Problème lors de la connexion : " + ex.getLocalizedMessage());
        }

        this.refresh();
    }

    private void handleLogout() {
        SessionInfo.doLogout();
        Notification.show("Déconnexion réussie.");
        this.refresh();
    }

    private void refresh() {
        this.removeAll();

        if (SessionInfo.connected()) {
            String nomUtilisateur = SessionInfo.getLoggedPartRef();
            if (nomUtilisateur == null || nomUtilisateur.isEmpty()) {
                nomUtilisateur = "Utilisateur inconnu";
            }

            this.add(new H3("Bonjour " + nomUtilisateur));
            this.add(this.bLogout);
        } else {
            this.add(this.tfNom, this.bLogin);
        }
    }
}
