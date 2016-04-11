/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	public Table(String node, Graph graph) {
		System.out.println("Bonjour");
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
				
			System.out.println("Pour le noeud " + dest.getId() + " : " + voisins.toString());
			
		}
	}	
}
