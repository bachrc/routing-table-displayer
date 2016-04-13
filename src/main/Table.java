/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 *
 * @author totorolepacha
 */
public class Table extends JPanel {
	HashMap<Node,Node[]> routage;
	
	public Table() {
		this.setBorder(BorderFactory.createTitledBorder("Table de routage"));
	}
	
	/**
	 * Méthode affichant en son sein le contenu de la table de routage.
	 * @param node L'identifiant du noeud source à la table de routage
	 * @param graph Le graphe représentant le réseau en cours
	 * @throws Exception 
	 */
	public void refreshTable(String node, Graph graph) throws Exception {
		this.removeAll();
		this.routage = routage(node, graph);
		setLayout(new GridLayout(this.routage.size(), this.routage.get((Node)this.routage.keySet().toArray()[0]).length));
		
		List<Node> noeuds = new ArrayList<>(this.routage.keySet());
		Collections.sort(noeuds, new Comparator<Node>(){
			public int compare(Node n1, Node n2) {
				return n1.getId().compareTo(n2.getId());
			}
		});
		
		for(Node noeud:noeuds) {
			this.add(new JLabel("<html><b>" + noeud.getId() + "</b></html>"));
			for(Node temp:this.routage.get(noeud)) {
				this.add(new JLabel(temp.getId()));
			}
		}
	}
	
	/**
	 * Méthode renvoyant la Hashmap contenant les ordres de routage.
	 * @param node L'identifiant du noeud source à la table de routage
	 * @param graph Le graphe représentant le réseau en cours
	 * @return La Hashmap contenant les informations relatives à la table de routage
	 * @throws Exception 
	 */
	public static HashMap<Node, Node[]> routage(String node, Graph graph) throws Exception {
		HashMap<Node, Node[]> retour = new HashMap<>();
		
		final Node depart = graph.getNode(node);
		ArrayList<Node> voisins = new ArrayList<>();
		
		for(Edge e : depart.getEachEdge())
			voisins.add(e.getOpposite(depart));
		
		for(Node dest:graph.getEachNode()) {
			final Dijkstra d = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");
			d.init(graph);
			d.setSource(dest);
			d.compute();
			
			Collections.sort(voisins, new Comparator<Node>() {
				@Override
				public int compare(Node n1, Node n2) {
					return Double.compare(d.getPathLength(n1) + (int)depart.getEdgeBetween(n1).getAttribute("weight"), d.getPathLength(n2) + (int)depart.getEdgeBetween(n2).getAttribute("weight"));
				}
			});
				
			retour.put(dest, Arrays.copyOf(voisins.toArray(), voisins.size(), Node[].class));
		}
		
		return retour;
	}
}
