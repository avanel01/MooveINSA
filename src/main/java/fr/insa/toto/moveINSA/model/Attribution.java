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
package fr.insa.toto.moveINSA.model;

import fr.insa.toto.moveINSA.model.EntiteDejaSauvegardee;
import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.list.ListUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Attribution {
    
    private int idAttribution;
    private int idOffre;
    private String idEtudiant;
    private int date;
  
    // Constructeur
    public Attribution(int idAttribution, int idOffre, String idEtudiant, int date) {
        this.idAttribution = idAttribution;
        this.idOffre = idOffre;
        this.idEtudiant = idEtudiant;
        this.date = date;
    }

    // toString method
    @Override
    public String toString() {
        return "Attribution{" 
            + "idAttribution=" + this.getIdAttribution()
            + ", idOffre=" + this.getIdOffre()
            + ", idEtudiant=" + this.getIdEtudiant()
            + ", date='" + date + '\''
            + '}';
    }

    // Méthode pour sauvegarder l'attribution dans la base de données
    public int saveInDB(Connection con) throws SQLException, EntiteDejaSauvegardee {
        if (this.getIdAttribution() != -1) {
            throw new EntiteDejaSauvegardee();
        }

        // Insertion des données dans la table Attribution
        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO Attribution (idOffre, idEtudiant, date) VALUES (?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Assigner les valeurs des attributs
            insert.setInt(1, this.getIdOffre());
            insert.setString(2, this.getIdEtudiant());
            insert.setInt(3, this.getDate());
            insert.executeUpdate();

            // Récupérer la clé générée (ID)
            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idAttribution = rid.getInt(1); // récupération de l'ID généré
                }
                return this.getIdAttribution();
            }
        }
    }

    // Getters and Setters
    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getIdAttribution() {
        return idAttribution;
    }

    public int getIdOffre() {
        return idOffre;
    }

    public String getIdEtudiant() {
        return idEtudiant;
    }
}
