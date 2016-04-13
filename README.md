# Routing Table Displayer

Cet outil permet à quiconque, à partir d'un fichier GraphStream représentant un réseau, d'établir de façon graphique les tables de routage du réseau renseigné. Ce logiciel est réalisé dans le cadre un TP de Réseau encadré par M. DUVALLET pour le S8 du Master SIRES à l'Université du Havre. (Le PDF du TP en question est disponible à l'adresse [suivante](http://litis.univ-lehavre.fr/~duvallet/enseignements/Cours/M1INFO/Reseau/MI-TP-Routage.pdf)).

## Sommaire

<!-- MarkdownTOC -->

- Téléchargements
- Rappel du sujet
- Le contexte
    - Les tables de routage
    - GraphStream
- L'application
    - Interface de sélection
        - Sélection d'un fichier
        - Affichage du réseau
    - Affichage de la table de routage

<!-- /MarkdownTOC -->

## Téléchargements

Le logiciel est disponible via téléchargement des sources, ou par téléchargement du jar et de ses librairies. Vous pourrez télécharger la dernière version via le lien suivant : [Téléchargement](https://github.com/totorolepacha/routing-table-displayer/releases)

Si vous souhaitez télécharger le logiciel au format jar, faites bien attention de ne pas dissocier le fichier jar de son dossier de librairies GraphStream, qui sont essentielles à son fonctionnement.

## Rappel du sujet

Le but du TP est de créer un programme Java permettant de donner le chemin le plus court dans une topologie réseau donnée. On imagine que l'on dispose d'un réseau dans lequel se trouvent des machines et des commutateurs avec plusieurs interfaces réseaux.

Le programme doit :

1. Permettre de saisir le graphe correspondant à la topologie réseau
2. Donner un résultat sous forme textuelle ou graphique.
3. Permettre d'établir les tables de routage au niveau de chaque commutateur.

## Le contexte

### Les tables de routage

La table de routage de chaque site, dans une topologie réseau, indique à quel(s) voisin(s) il faut envoyer un paquet pour que celui-ci arrive à destination le plus vite possible.

Pour donc aller à un site donné, le premier voisin cité est le plus efficace globalement, le suivant le deuxième plus efficace, etc. Ce qui est important lorsque le réseau est surchargé sur certains commutateurs.

### GraphStream

GraphStream est une série de librairies Java pour la modélisation et l'analyse de graphes dynamiques. Le projet GraphStream est basé à l'Université du Havre. Il a été débuté et maintenu par les membres de l'équipe de recherche RI2C, venant du laboratoire de recherche informatique LITIS. Vous pouvez retrouver le site internet du projet à l'adresse [suivante](http://graphstream-project.org/).

L'utilisation de ces librairies de traitement de graphes est totalement justifiée ici. En effet, une topologie de réseau n'est rien de plus qu'un graphe non-orienté où les commutateurs sont les noeuds du graphes, qui sont simplement liés entre eux, comme le seraient les commutateurs liés en réseau.

De plus, comme dans une topologie réseau, les liens entre les noeuds peuvent y être pondérés, ce qui sera essentiel afin de calculer les plus courts chemins entre les différents commutateurs.

## L'application

### Interface de sélection

L'application dispose d'une interface graphique afin de fournir à l'utilisateur un plus grand confort d'utilisation. Mise en forme à l'aide d'un GridBagLayout, cette interface permet de charger un fichier GraphStream afin de pouvoir de pouvoir l'analyser, afficher le graphe du réseau, ainsi que la table de routage du fichier chargé.

![Interface](https://i.imgur.com/PkriyOi.png)

#### Sélection d'un fichier

Le logiciel vous permet de sélectionner la topologie réseau de votre choix, à partir d'un fichier GraphStream sur votre ordinateur. Il vous suffit de sélectionner un fichier `dgs` ou `txt`, par exemple, le fichier `graphtest.dgs` fourni avec le logiciel, correspondant à la topologie réseau vue dans le TD sur les tables de routage.

![Interface](https://i.imgur.com/dAhVOuv.png)

Ce fichier au format DGS contient toutes les informations nécéssaires à l'établissement d'une topologie, et d'une table de routage : 

```
DGS004
null 0 0

an C1
an C2
an C3
an C4
an C5
an C6

ae e1 C1 C2 weight:2
ae e2 C1 C5 weight:2
ae e3 C1 C6 weight:4
ae e4 C2 C3 weight:1
ae e5 C3 C4 weight:1
ae e6 C3 C5 weight:3
ae e7 C4 C6 weight:1
ae e8 C5 C6 weight:1
```

Le but de ce fichier est très simple : contenir l'ensemble des noeuds, ainsi que les liens entre ces noeuds, muni de leur poids. A partir de ces informations, conformes à la syntaxe de GraphStream. Vous pouvez obtenir plus d'informations sur cette syntaxe en cliquant sur [ce lien](http://graphstream-project.org/doc/Advanced-Concepts/The-DGS-File-Format/).

#### Affichage du réseau

Après avoir chargé le fichier, si aucun message d'erreur de n'est affiché, votre fichier est chargé en mémoire ! Les boutons pour afficher le réseau, ainsi que pour afficher la table de routage se sont dégrisés, et vous êtes libre de cliquer dessus. Voici le visualiseur de GraphStream en action, nous affichant notre réseau chargé.

![Visualiseur](https://i.imgur.com/D01FLwA.png)

### Affichage de la table de routage

Voici enfin la principale fonctionnalité de ce logiciel : la génération des tables du routage. En sélectionnant simplement le noeud du réseau, nous pouvons simplement afficher la table de routage lui correspondant !

![Table de routage](https://i.imgur.com/DtCCvJ0.png)

Le secret de l'organisation de cette table réside en une seule méthode, située dans le fichier [Table.java](src/main/Table.java#L54).

```java
public static HashMap<Node, Node[]> routage(String node, Graph graph) throws Exception {
    HashMap<Node, Node[]> retour = new HashMap<>();
    
    final Node depart = graph.getNode(node);
    ArrayList<Node> voisins = new ArrayList<>();
    
    for(Edge e : depart.getEachEdge())
        voisins.add(e.getOpposite(depart));
```
Cette méthode n'a besoin que de l'identifiant du noeud à partir duquel la table de routage sera établie, ainsi que du graphe représentant le réseau. Nous récupérons d'abord le noeud source, ainsi que la liste de ses voisins directs.

```java
    for(Node dest:graph.getEachNode()) {
        final Dijkstra d = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");
        d.init(graph);
        d.setSource(dest);
        d.compute();
```

Puis pour chaque noeud du graphe (chaque commutateur du réseau), nous allons initialiser d'un algorithme de Dijkstra, avec comme source ce même noeud.

```java
        Collections.sort(voisins, new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return Double.compare(d.getPathLength(n1) + (int)depart.getEdgeBetween(n1).getAttribute("weight"), d.getPathLength(n2) + (int)depart.getEdgeBetween(n2).getAttribute("weight"));
            }
        });
```

Puis, nous allons tout simplement trier la liste des voisins avec le comparateur ci-dessus ! En effet, en partance du noeud parcouru, nous allons trier la liste des voisins en fonction de la distance prise par le chemin le plus court, entre le noeud parcouru et le noeud source, en passant par ce voisin. Nous obtenons donc une liste triée et optimale.

```java
        retour.put(dest, Arrays.copyOf(voisins.toArray(), voisins.size(), Node[].class));
    }

    return retour;
}
```

Nous stockons ensuite la liste triée dans une Hashmap, liée au noeud de destination parcouru. Une fois tous les noeuds parcourus, la Hashmap est retournée au JPael gérant le tableau, qui se chargera de l'afficher.