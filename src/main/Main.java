package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author Yohann Bacha
 */
public class Main extends JFrame {

	private final Graph graph;
	private File fichierCharge;
	private JLabel status;
	private JComboBox listNodes;
	protected JButton afficherTable, afficherGraphe, charger;
	private Table table;

	public Main() {
		this.graph = new SingleGraph("GraphRoutage");
		
		this.setupUI();
	}

	/**
	 * Méthode se chargeant de mettre en forme le panel.
	 */
	public final void setupUI() {
		this.setTitle("Routing Table Generator");
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.insets = new Insets(5, 3, 5, 3);
		this.add(new JLabel("<html><big>Routing Tables Displayer</big></html>"), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.;
		this.charger = new JButton(new LoadButton());
		this.add(this.charger, gbc);

		gbc.gridx = 2;
		gbc.gridwidth = 1;
		this.afficherGraphe = new JButton(new DisplayGraphButton());
		this.afficherGraphe.setEnabled(false);
		this.add(this.afficherGraphe, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		this.status = new JLabel("Chargé : Aucun");
		this.add(this.status, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		listNodes = new JComboBox();
		this.add(listNodes, gbc);

		gbc.gridx = 2;
		gbc.gridwidth = 1;
		this.afficherTable = new JButton(new DisplayTableButton());
		this.afficherTable.setEnabled(false);
		this.add(this.afficherTable, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.table = new Table();
		this.add(this.table, gbc);

		pack();
		this.setLocation(420, 200);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Cette méthode se charge de griser les boutons si aucun fichier n'est chargé,
	 * et de les rendre actifs s'ils y en a un.
	 */
	public void refreshComps() {
		if(fichierCharge == null) {
			this.afficherTable.setEnabled(false);
			this.afficherGraphe.setEnabled(false);
			this.status.setText("Chargé : Aucun");
		} else {
			this.afficherTable.setEnabled(true);
			this.afficherGraphe.setEnabled(true);
			this.status.setText("Chargé : " + fichierCharge.getName());
			this.listNodes.removeAllItems();
			for(Node n : graph.getNodeSet()) {
				this.listNodes.addItem(n.getId());
			}
		}
	}
	
	/**
	 * Cette méthode sert ici à afficher les noms des noeuds et les 
	 * poids sur le graphe.
	 */
	public void refreshViewer() {
		this.graph.addAttribute("ui.stylesheet", "url('src/main/style.css')");
		for(Node n:this.graph) 
			n.addAttribute("ui.label", n.getId());
		
		for(Edge e:this.graph.getEachEdge()) 
			e.addAttribute("ui.label", e.getAttribute("weight"));
		
	}

	/**
	 * Méthode principale essayant de faire appel au look&feel par défaut du
	 * système.
	 * @param args 
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new Main();
	}
	
	/**
	 * Action se chargeant du bouton de chargement
	 */
	public class LoadButton extends AbstractAction {

		public LoadButton() {
			super("Charger");
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("Fichiers GraphStream", "dgs", "txt"));
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				try {
					graph.read(chooser.getSelectedFile().getAbsolutePath());
					fichierCharge = chooser.getSelectedFile();
				} catch (Exception e) {
					fichierCharge = null;
					JOptionPane.showMessageDialog(null, "Le fichier ne correspond pas à la syntaxe de GraphStream", "Fichier invalide", JOptionPane.ERROR_MESSAGE);
				}
				refreshComps();
				refreshViewer();
			}
		}

	}

	/**
	 * Action se chargeant du bouton pour afficher le graphe
	 */
	public class DisplayGraphButton extends AbstractAction {

		public DisplayGraphButton() {
			super("Afficher");
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			graph.display();
		}

	}

	/**
	 * Action se chargeant du bouton d'affichage de la table de routage.
	 */
	public class DisplayTableButton extends AbstractAction {

		public DisplayTableButton() {
			super("Afficher Table");
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			try {
				table.refreshTable((String)listNodes.getSelectedItem(), graph);
				pack();
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Erreur lors de l'établissement de la table de routage.", "Etablissement impossible", JOptionPane.ERROR_MESSAGE);
			}
		}

	}
}
