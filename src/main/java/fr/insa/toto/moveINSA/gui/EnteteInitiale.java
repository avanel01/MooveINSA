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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.model.Etudiant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Barre d'en-tête initiale pour l'application.
 * Fournit les fonctionnalités de connexion et déconnexion.
 * 
 * @author francois
 */
public class EnteteInitiale extends HorizontalLayout {

    private final TextField tfNom;
    private final PasswordField pfMdp;
    private final Button bLogin;
    private final Button bLogout;
    private boolean isConnected = false;


    public EnteteInitiale() {
        // Configuration de la mise en page
        this.setWidthFull();
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Champ texte pour l'INE
        this.tfNom = new TextField("Votre INE :");

        // Champ mot de passe
        this.pfMdp = new PasswordField("Votre mot de passe :");

        // Bouton de login
        this.bLogin = new Button("Login");
        this.bLogin.addClickListener(event -> handleLogin());

        // Bouton de logout
        this.bLogout = new Button("Logout");
        this.bLogout.addClickListener(event -> handleLogout());

        // Ajout des composants à la mise en page
        this.add(tfNom, pfMdp, bLogin, bLogout);

        // Rafraîchir l'affichage initial
        this.refresh();
    }

    /**
     * Gère la connexion de l'utilisateur.
     */
    private void handleLogin() {
        String ref = this.tfNom.getValue().trim();
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
                    isConnected = true;
                    Notification.show("Bienvenue, " + etudiant.getNomEtudiant() + " !");
                    // Effectuer d'autres actions de connexion ici (stockage de session, etc.)
                } else {
                    Notification.show("Mot de passe incorrect.");
                }
            }
        } catch (SQLException ex) {
            Notification.show("Problème lors de la connexion : " + ex.getLocalizedMessage());
        }

        this.refresh();
    }

    /**
     * Gère la déconnexion de l'utilisateur.
     */
    private void handleLogout() {
        isConnected = false;  // Réinitialiser l'état de connexion
        Notification.show("Déconnexion réussie.");
        refresh();
    }

    /**
     * Rafraîchit l'interface en fonction de l'état actuel.
     */
    private void refresh() {
        if (isConnected) {
        // Si l'utilisateur est connecté
        tfNom.setVisible(false);       // Cacher le champ INE
        pfMdp.setVisible(false);       // Cacher le champ mot de passe
        bLogin.setVisible(false);      // Cacher le bouton "Login"
        bLogout.setVisible(true);      // Montrer le bouton "Logout"
    } else {
        // Si l'utilisateur n'est pas connecté
        tfNom.setVisible(true);        // Montrer le champ INE
        pfMdp.setVisible(true);        // Montrer le champ mot de passe
        bLogin.setVisible(true);       // Montrer le bouton "Login"
        bLogout.setVisible(false);     // Cacher le bouton "Logout"
    }
    }
}
