package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.grid.Grid;
import fr.insa.toto.moveINSA.model.Partenaire;
import java.util.List;

/**
 * Grille pour afficher les partenaires.
 */
public class PartenaireGrid extends Grid<Partenaire> {

    public PartenaireGrid(List<Partenaire> partenaires) {
        super(Partenaire.class, false);
        this.setColumnReorderingAllowed(true);

        // Ajout des colonnes nécessaires
        this.addColumn(Partenaire::getIdPartenaire).setHeader("ID").setSortable(true);
        this.addColumn(Partenaire::getRefPartenaire).setHeader("Référence Partenaire").setSortable(true);
        this.addColumn(Partenaire::getVille).setHeader("Ville").setSortable(true);
        this.addColumn(Partenaire::getPays).setHeader("Pays").setSortable(true);

        // Ajout des partenaires à la grille
        this.setItems(partenaires);
    }
}

