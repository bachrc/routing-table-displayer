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
	
	public void refreshTable(String node, Graph graph) throws Exception {
		this.removeAll();
		this.routage = routage(node, graph);
		setLayout(new GridLayout(this.routage.size(), this.routage.get((Node)this.routage.keySet().toArray()[0]).length));
		for(Node noeud:this.routage.keySet()) {
			this.add(new JLabel(noeud.getId()));
			for(Node temp:this.routage.get(noeud)) {
				this.add(new JLabel(temp.getId()));
			}
		}
	}
	
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
				
			System.out.println("Pour le noeud " + dest.getId() + " : " + voisins.toString());
			
			retour.put(dest, Arrays.copyOf(voisins.toArray(), voisins.size(), Node[].class));
		}
		
		return retour;
	}
}
