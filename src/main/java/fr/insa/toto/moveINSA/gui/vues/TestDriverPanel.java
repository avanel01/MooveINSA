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

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.gui.session.SessionInfo;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;

/**
 *
 * @author francois
 */
@Route(value = "debug/testDriver", layout = MainLayout.class)
public class TestDriverPanel extends VerticalLayout {

    public TestDriverPanel() {
        
    // Styliser le conteneur principal avec une image de fond
        this.getStyle()
            .set("background-image", "url('https://www.insa-strasbourg.fr/wp-content/uploads/28070823_1895678403840285_8548131256382231960_o.jpg')")
            .set("background-size", "cover")
            .set("background-position", "center")
            .set("height", "100vh")
            .set("display", "flex")                
            .set("flex-direction", "column")       
            .set("justify-content", "center")      
            .set("align-items", "center");          
            
               
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
            .set("padding", "30px")
            .set("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.2)") // Ombre légère
            .set("text-align", "center")
            .set("max-width", "600px");
       
        
    // Ajouter le titre dans le conteneur
    container.add(new H3("Test du driver"));
    
    try (Connection con = ConnectionPool.getConnection()){
            Class<Driver> mysqlDriver = (Class<Driver>) Class.forName("com.mysql.cj.jdbc.Driver");
            container.add(new Paragraph("com.mysql.cj.jdbc.Driver OK"));
            DatabaseMetaData meta = con.getMetaData();
            container.add(new Paragraph("JDBC driver de la connection : " + meta.getDriverName() + " ; " + meta.getDriverVersion()));
        } catch (ClassNotFoundException ex) {
            container.add(new Paragraph("com.mysql.cj.jdbc.Driver not found"));
        } catch (SQLException ex) {
            container.add(new H3("Problème sql : "));
            StringWriter mess = new StringWriter();
            PrintWriter out = new PrintWriter(mess);
            ex.printStackTrace(out);
            container.add(new Paragraph(mess.toString()));
        }
    
        // Ajouter les composants à la vue principale
        this.add(logo, container);

    }

}
