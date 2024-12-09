package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("MoveINSA")
@Route(value = "", layout = MainLayout.class)
public class VuePrincipale extends VerticalLayout {
    
    public VuePrincipale() {
        // Titre principal centré et en gras
        H1 titrePrincipal = new H1("MOVEINSA");
        titrePrincipal.getStyle()
                      .set("text-align", "center")
                      .set("font-weight", "bold")
                      .set("font-size", "2.5em");

        // Sous-titre centré avec la phrase modifiée
        H2 sousTitre = new H2("Bienvenue dans l'univers de la mobilité internationale de l'INSA Strasbourg");
        sousTitre.getStyle()
                 .set("text-align", "center")
                 .set("font-size", "1.5em");

        // Message d'introduction
        Div introduction = new Div();
        introduction.add(
            new Paragraph("Explorez le monde, élargissez vos horizons, construisez votre avenir."),
            new Paragraph("Dans le cadre de votre parcours d’ingénierie, la mobilité internationale est une opportunité unique de vivre une expérience humaine et professionnelle enrichissante. "
                        + "Grâce à des partenariats avec des universités prestigieuses à travers le monde, l’INSA Strasbourg vous accompagne dans votre projet de découvrir de nouvelles cultures, "
                        + "de relever des défis académiques stimulants et de préparer une carrière à dimension internationale.")
        );

        // Styles pour l'introduction
        introduction.getStyle()
                    .set("text-align", "center")
                    .set("margin-top", "20px")
                    .set("line-height", "1.5")
                    .set("font-size", "1.2em");

        // Ajout des éléments dans la vue
        this.add(titrePrincipal, sousTitre, introduction);
    }
}
/* public VuePrincipale() {
        this.add(new H3("Petit programme pour démarrer le projet M3 2024"));
        List<Paragraph> attention = List.of(
                new Paragraph("Attention : la base de donnée utilisée par défaut "
                        + "est créée en mémoire."),
                new Paragraph("Vous devez la réinitialiser après chaque démarrage."
                        + "Pour cela, allez dans le menu debug;RAZ BdD et cliquez sur le bouton"),
                new Paragraph("Dans cette version, les connexions à la base de donnée sont gérées par un pool de connexion"),
                new Paragraph("Pour utiliser une autre base de donnée, vous devez modifier la classe "
                        + "fr.insa.beuvron.vaadin.utils.ConnectionPool"),
                new Paragraph("Si vous utilisez le serveur MySQL fourni pour M3, "
                        + "il vous suffit de commenter la def pour H2 et de dé-commenter "
                        + "la def pour le serveur mysql de M3. Pensez évidemment à modifier pour donner VOS login/pass")
        );
        attention.get(0).getStyle().set("color", "red");
        attention.forEach((p) -> this.add(p));

    }*/