package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("MoveINSA")
@Route(value = "", layout = MainLayout.class)
public class VuePrincipale extends VerticalLayout {

    public VuePrincipale() {
        // Configuration du style global
        this.getStyle()
            .set("background-image", "url('https://www.insa-strasbourg.fr/wp-content/uploads/28070823_1895678403840285_8548131256382231960_o.jpg')")
            .set("background-size", "cover")
            .set("background-position", "center")
            .set("height", "100vh")
            .set("position", "relative") // Position relative pour permettre le positionnement absolu des éléments enfants
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center");

        // Ajout du logo dans le coin supérieur gauche
        Image logo = new Image("https://th.bing.com/th/id/R.f285f84a2330ff493b7f5d7c6813e38a?rik=4jLzIHr5J2BpqQ&riu=http%3a%2f%2fwww.alsacetech.org%2fwp-content%2fuploads%2f2017%2f08%2fLogo_INSAStrasbourgDeveloppe-quadri_marge.jpg&ehk=5hF6%2b0HEor54t6NVgihM127qIINO%2fSGHq7Z6pZu7Zs8%3d&risl=&pid=ImgRaw&r=0", "Logo INSA Strasbourg");
        logo.getStyle()
            .set("position", "absolute")
            .set("top", "20px")
            .set("left", "20px")
            .set("width", "150px")
            .set("height", "auto");
        this.add(logo); // Ajout direct du logo à la vue

        // Conteneur principal pour le texte
        Div container = new Div();
        container.getStyle()
                 .set("background", "rgba(255, 255, 255, 0.8)") // Blanc avec transparence
                 .set("padding", "20px")
                 .set("border-radius", "10px")
                 .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.3)")
                 .set("max-width", "600px")
                 .set("text-align", "center");

        // Titre principal centré et en gras
        H1 titrePrincipal = new H1("MOVEINSA");
        titrePrincipal.getStyle()
                      .set("text-align", "center")
                      .set("font-weight", "bold")
                      .set("font-size", "2.5em")
                      .set("margin", "0");

        // Sous-titre centré avec une partie rouge
        H2 sousTitre = new H2();
        Span texteRouge = new Span("Bienvenue dans l'univers de la mobilité internationale de l'INSA Strasbourg");
        texteRouge.getStyle().set("color", "red"); // Appliquer la couleur rouge
        sousTitre.add(texteRouge);
        sousTitre.getStyle()
                 .set("text-align", "center")
                 .set("font-size", "1.5em")
                 .set("margin", "10px 0");

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
                    .set("line-height", "1.5")
                    .set("font-size", "1.2em")
                    .set("margin", "10px 0");

        // Ajout des éléments au conteneur principal
        container.add(titrePrincipal, sousTitre, introduction);

        // Ajout du conteneur à la vue
        this.add(container);
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