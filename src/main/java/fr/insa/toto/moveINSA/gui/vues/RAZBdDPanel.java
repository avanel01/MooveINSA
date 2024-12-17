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
package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.gui.session.SessionInfo;
import fr.insa.toto.moveINSA.model.GestionBdD;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author francois
 */
@PageTitle("MoveINSA")
@Route(value = "debug/RAZBDD", layout = MainLayout.class)
public class RAZBdDPanel extends VerticalLayout {

    private Button bRAZ;

    public RAZBdDPanel() {
        
        // Styliser le conteneur principal avec une image de fond
        this.getStyle()
            .set("background-image", "url('https://www.insa-strasbourg.fr/wp-content/uploads/28070823_1895678403840285_8548131256382231960_o.jpg')")
            .set("background-size", "cover")
            .set("background-position", "center")
            .set("height", "100vh")
            .set("position", "relative");

        // Ajouter le logo en haut à gauche
        Image logo = new Image("https://apps.insa-strasbourg.fr/WebObjects/logos/logo_insa_strasbourg_234px.png", "Logo INSA Strasbourg");
        logo.getStyle()
            .set("position", "absolute")
            .set("top", "20px")
            .set("left", "20px")
            .set("width", "150px")
            .set("height", "auto");
        
        // Conteneur transparent centré 
        Div container = new Div();
        container.getStyle()
            .set("background", "rgba(255, 255, 255, 0.9)") // Fond blanc semi-transparent
            .set("border-radius", "10px")
            .set("padding", "50px")
            .set("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.2)") // Ombre légère
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("justify-content", "center")
            .set("align-items", "center");
        
        this.bRAZ = new Button("!!! RAZ BDD !!!");
        this.bRAZ.addClickListener((t) -> {
            try (Connection con = ConnectionPool.getConnection()){
                GestionBdD.razBDD(con);
                this.add(new H3("La base de données a été (ré-)initalisée"));
            } catch (SQLException ex) {
                System.out.println("Problème : " + ex.getLocalizedMessage());
                Notification.show("Problème : " + ex.getLocalizedMessage());
            }
        });
        this.add(this.bRAZ);
        
        
        bRAZ.getStyle()
            .set("background-color", "#FF0000")
            .set("color", "white")
            .set("border", "none")
            .set("padding", "10px 20px")
            .set("font-size", "1em")
            .set("border-radius", "5px")
            .set("font-weight", "bold")
            .set("cursor", "pointer")
            .set("margin", "10px 0");

        // Ajouter le bouton au conteneur
        container.add(bRAZ);

        // Aligner les éléments dans le VerticalLayout
        this.setAlignItems(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);

        // Ajouter les composants à la vue principale
        this.add(logo, container);
    }

}
