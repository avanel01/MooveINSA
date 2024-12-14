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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
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

@Route(value = "connexion", layout = MainLayout.class) // La route reste la page d'accueil
@PageTitle("Connexion")
public class ConnexionPanel extends VerticalLayout {

    private final TextField tfINE;
    private final PasswordField pfMdp;
    private final Button bLogin;

    public ConnexionPanel() {
        // Configuration du style global pour correspondre au style de la page d'accueil
        this.getStyle()
            .set("background-image", "url('https://www.insa-strasbourg.fr/wp-content/uploads/28070823_1895678403840285_8548131256382231960_o.jpg')")
            .set("background-size", "cover")
            .set("background-position", "center")
            .set("height", "100vh")
            .set("position", "relative")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center");

        // Ajout du logo dans le coin supérieur gauche
        Image logo = new Image("https://apps.insa-strasbourg.fr/WebObjects/logos/logo_insa_strasbourg_234px.png", "Logo INSA Strasbourg");
        logo.getStyle()
            .set("position", "absolute")
            .set("top", "20px")
            .set("left", "20px")
            .set("width", "150px")
            .set("height", "auto");
        this.add(logo);

        // Conteneur transparent pour les champs de connexion
        Div container = new Div();
        container.getStyle()
                 .set("background", "rgba(255, 255, 255, 0.9)") // Blanc avec transparence
                 .set("padding", "20px")
                 .set("border-radius", "10px")
                 .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.3)")
                 .set("max-width", "400px")
                 .set("text-align", "center");

        // Titre de connexion
        H3 titre = new H3("Connexion à MoveINSA");
        titre.getStyle()
             .set("margin", "0 0 20px 0")
             .set("font-weight", "bold")
             .set("font-size", "1.5em");

        // Champs texte pour l'INE et le mot de passe
        this.tfINE = new TextField("Votre INE :");
        this.tfINE.getStyle()
                  .set("width", "100%")
                  .set("color", "black"); // Texte en noir

        this.pfMdp = new PasswordField("Votre mot de passe :");
        this.pfMdp.getStyle()
                  .set("width", "100%")
                  .set("color", "black"); // Texte en noir

        // Bouton de connexion
        this.bLogin = new Button("Login");
        this.bLogin.getStyle()
                   .set("margin-top", "10px")
                   .set("width", "100%")
                   .set("background-color", "#FF0000")
                   .set("color", "white")
                   .set("border", "none")
                   .set("padding", "10px 0")
                   .set("font-size", "1em")
                   .set("border-radius", "5px");
        this.bLogin.addClickListener(event -> handleLogin());

        // Ajout des éléments au conteneur
        container.add(titre, tfINE, pfMdp, bLogin);

        // Ajout du conteneur à la vue
        this.add(container);
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


