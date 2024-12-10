package fr.insa.toto.moveINSA.gui.vues;


import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.GestionBdD;
import fr.insa.toto.moveINSA.model.Partenaire;
import static fr.insa.toto.moveINSA.model.Partenaire.tousLesPartenaires;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOpackage fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.GestionBdD;
import fr.insa.toto.moveINSA.model.Partenaire;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel affichant la liste des partenaires.
 */
@PageTitle("MoveINSA - Partenaires")
@Route(value = "partenaires/liste", layout = MainLayout.class)
public class PartenairesPanel extends VerticalLayout {

    public PartenairesPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            // Titre de la page
            this.add(new H3("Liste de tous les partenaires"));

            // Récupération des partenaires depuis GestionBdD
            List<Partenaire> partenaires = tousLesPartenaires(con);

            // Affichage des partenaires via PartenaireGrid
            PartenaireGrid grid = new PartenaireGrid(partenaires);
            this.add(grid);

        } catch (SQLException ex) {
            // Gestion d'erreur
            System.out.println("Problème : " + ex.getLocalizedMessage());
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
    }
}

    

