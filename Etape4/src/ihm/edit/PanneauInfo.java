package src.ihm.edit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.*;
import src.Controleur;
import src.metier.Attribut;
import src.metier.CreeClass;
import src.metier.Methode;

public class PanneauInfo extends JPanel implements ActionListener
{
	// Champs statiques (nom / type)
	private JTextField nomField;
	private JTextField typeField;

	// Zones de texte pour listes longues (attributs / méthodes)
	private JTextArea attributsArea;
	private JTextArea methodesArea;

	// Panels dynamiques construits selon la liste de classes disponible
	private JPanel merePanel;
	private JPanel interfacesPanel;
	private JPanel contentPanel;
	private JPanel multiplicitePanel;

	// Référence au contrôleur et nom de la classe actuellement affichée
	private Controleur ctrl;
	private String currentClasseName;

	// Boutons d'action en bas du panneau
	private JButton btnModif;
	private JButton btnValid;

	public PanneauInfo()
	{
		this.setLayout(new BorderLayout());

		// Content panel en GridBag pour organiser en deux colonnes (label / contrôle)
		this.contentPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 8, 6, 8);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		int row = 0;

		// Nom
		gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
		this.contentPanel.add(new JLabel("Nom :"), gbc);
		this.nomField = new JTextField(); this.nomField.setEditable(false);
		gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1; gbc.gridwidth = 1;
		this.contentPanel.add(this.nomField, gbc);

		// Type
		gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
		this.contentPanel.add(new JLabel("Type :"), gbc);
		this.typeField = new JTextField(); this.typeField.setEditable(false);
		gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1;
		this.contentPanel.add(this.typeField, gbc);

		// Héritage (radio buttons container)
		gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
		this.contentPanel.add(new JLabel("Héritage :"), gbc);
		this.merePanel = new JPanel();
		this.merePanel.setLayout(new BoxLayout(this.merePanel, BoxLayout.Y_AXIS));
		gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
		this.contentPanel.add(new JScrollPane(this.merePanel), gbc);

		// Interfaces
		gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
		this.contentPanel.add(new JLabel("Interfaces :"), gbc);
		this.interfacesPanel = new JPanel();
		this.interfacesPanel.setLayout(new BoxLayout(this.interfacesPanel, BoxLayout.Y_AXIS));
		gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
		JScrollPane scrollInterfaces = new JScrollPane(this.interfacesPanel);
		scrollInterfaces.setPreferredSize(new Dimension(300, 80));
		this.contentPanel.add(scrollInterfaces, gbc);

		// Multiplicités
		gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
		this.contentPanel.add(new JLabel("Multiplicités :"), gbc);
		this.multiplicitePanel = new JPanel();
		this.multiplicitePanel.setLayout(new BoxLayout(this.multiplicitePanel, BoxLayout.Y_AXIS));
		gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
		JScrollPane multScroll = new JScrollPane(this.multiplicitePanel);
		multScroll.setPreferredSize(new Dimension(300, 80));
		this.contentPanel.add(multScroll, gbc);

		// Attributs
		gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
		this.contentPanel.add(new JLabel("Attributs :"), gbc);
		this.attributsArea = new JTextArea(4, 30);
		this.attributsArea.setEditable(false);
		//this.attributsArea.setLineWrap(true);
		//this.attributsArea.setWrapStyleWord(true);
		gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
		this.contentPanel.add(new JScrollPane(this.attributsArea), gbc);

		// Méthodes
		gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
		this.contentPanel.add(new JLabel("Méthodes :"), gbc);
		this.methodesArea = new JTextArea(5, 30);
		this.methodesArea.setEditable(false);
		//this.methodesArea.setLineWrap(true);
		//this.methodesArea.setWrapStyleWord(true);
		gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
		this.contentPanel.add(new JScrollPane(this.methodesArea), gbc);

		// Remplir l'espace vertical restant
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
		//this.contentPanel.add(Box.createVerticalGlue(), gbc);

		this.add(new JScrollPane(this.contentPanel), BorderLayout.CENTER);

		// Ajouter les boutons de modification au SOUTH
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		this.btnModif = new JButton("Modifier Classe");
		this.btnValid = new JButton("Valider Modifications");

		buttonPanel.add(this.btnModif);
		buttonPanel.add(this.btnValid);

		this.add(buttonPanel, BorderLayout.SOUTH);

		this.effacer();

		// Ajouter les écouteurs d'action aux boutons
		this.btnModif.addActionListener(this);
		this.btnValid.addActionListener(this);
	}

	/**
	 * Affiche les informations d'une classe CreeClass
	 * @param classe la classe à afficher
	 */
	public void afficherInfoClasse(CreeClass classe)
	{
		if (classe == null)
		{
			this.effacer();
			return;
		}

		// Nom et type
		this.nomField.setText(classe.getNom());
		this.typeField.setText(classe.getType());
		// Les panels de checkboxes pour héritage/interfaces sont gérés par majInfoClasse(...)

		// Attributs

		String attributsText = "";

		if (!classe.getLstAttribut().isEmpty())
		{
			for (Attribut attr : classe.getLstAttribut())
			{
				attributsText += attr.getType()+" "+ attr.getNom()+"\n";
			}
		}
		if (!classe.getLstClassAttribut().isEmpty())
		{
			for (Attribut att : classe.getLstClassAttribut())
			{
				attributsText += att.getType() + "\t" + att.getNom() + "\n";
			}
		}
		if (classe.getLstClassAttribut().isEmpty() && classe.getLstAttribut().isEmpty() )
		{
			attributsText += "Aucun attribut";
		}
		this.attributsArea.setText(attributsText.toString());

		// Méthodes
		StringBuilder methodesText = new StringBuilder();
		if (!classe.getLstMethode().isEmpty())
		{
			for (Methode method : classe.getLstMethode())
			{
				methodesText.append(method.getVisibilite()).append(" ")
					.append(method.getType()).append(" ")
					.append(method.getNom()).append("(...)"+"\n");
			}
		}
		else
		{
			methodesText.append("Aucune méthode");
		}
		this.methodesArea.setText(methodesText.toString());
	}

	public void majInfoClasse(String nomClasse, src.Controleur ctrl)
	{
		// Trouver la classe CreeClass par son nom
		CreeClass classeTrouvee = null;
		// Supposons que nous avons accès à une liste de classes quelque part
		// Ici, nous devrions obtenir cette liste depuis le contrôleur ou un autre composant
		// Pour l'exemple, imaginons que nous avons une méthode getLstClass() qui retourne cette liste
		java.util.List<CreeClass> listeClasses = ctrl.getLstClass();
        
		for (CreeClass classe : listeClasses)
		{
			if (classe.getNom().equals(nomClasse))
			{
				classeTrouvee = classe;
				break;
			}
		}

		// Mettre à jour les champs texte (nom/type/attributs/méthodes)
		this.afficherInfoClasse(classeTrouvee);

		// Construire les boutons radio (lecture seule) pour l'héritage — une seule sélection possible
		this.merePanel.removeAll();
		ButtonGroup bg = new ButtonGroup();
		if (listeClasses != null)
		{
			for (CreeClass c : listeClasses)
			{
				String nom = c.getNom();
				JRadioButton rb = new JRadioButton(nom);
				// Ne pas permettre de sélectionner la classe elle-même comme mère
				if (nom.equals(nomClasse)) rb.setEnabled(false);
				// Pré-sélectionner si c'est la mère
				if (classeTrouvee != null && classeTrouvee.getMere() != null && classeTrouvee.getMere().equals(nom))
				{
					rb.setSelected(true);
				}
				// Lecture seule : désactiver l'interaction
				rb.setEnabled(false);
				bg.add(rb);
				this.merePanel.add(rb);
			}
		}
		this.merePanel.revalidate();
		this.merePanel.repaint();

		// Construire les checkboxes pour les interfaces
		this.interfacesPanel.removeAll();
		if (listeClasses != null)
		{
			for (CreeClass c : listeClasses)
			{
				String nom = c.getNom();
				JCheckBox cb = new JCheckBox(nom);
				if (classeTrouvee != null && classeTrouvee.getInterfaces() != null && classeTrouvee.getInterfaces().contains(nom))
				{
					cb.setSelected(true);
				}
				// Lecture seule
				cb.setEnabled(false);
				this.interfacesPanel.add(cb);
			}
		}
		this.interfacesPanel.revalidate();
		this.interfacesPanel.repaint();

		// Construire les lignes d'édition/l'affichage des multiplicités
		this.multiplicitePanel.removeAll();
		// sauvegarder ctrl et classe courante pour validation
		this.ctrl = ctrl;
		this.currentClasseName = nomClasse;

		if (classeTrouvee != null && classeTrouvee.getMultiplicite() != null && classeTrouvee.getMultiplicite().getMapMultiplicites() != null)
		{
			Map<CreeClass, List<List<String>>> map = classeTrouvee.getMultiplicite().getMapMultiplicites();
			for (Entry<CreeClass, List<List<String>>> entry : map.entrySet())
			{
				CreeClass other = entry.getKey();
				List<List<String>> pairs = entry.getValue();

				// récupérer les noms d'attributs impliqués dans les liaisons, dans le même ordre
				java.util.List<String> attrsThis = new ArrayList<>();
				java.util.List<String> attrsOther = new ArrayList<>();
				for (Attribut att : classeTrouvee.getLstClassAttribut())
				{
					if (att.getType().contains(other.getNom())) attrsThis.add(att.getNom());
				}
				for (Attribut att : other.getLstClassAttribut())
				{
					if (att.getType().contains(classeTrouvee.getNom())) attrsOther.add(att.getNom());
				}

				for (int p = 0; p < pairs.size(); p++)
				{
					List<String> pair = pairs.get(p);
					String left = pair.size() >= 1 ? pair.get(0) : "1..1";
					String right = pair.size() >= 2 ? pair.get(1) : "1..1";
					String attrThis = p < attrsThis.size() ? attrsThis.get(p) : "";
					String attrOther = p < attrsOther.size() ? attrsOther.get(p) : "";

					JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
					row.setName(other.getNom());
					JLabel lbl = new JLabel(other.getNom()+" : ");
					lbl.setPreferredSize(new Dimension(120, 20));
					row.add(lbl);
					String[] options = new String[]{"1..1", "1..*", "0..*"};

					JComboBox<String> cbLeft = new JComboBox<>(options);
					cbLeft.setSelectedItem(left);
					cbLeft.setEnabled(false);
					row.add(cbLeft);
					row.add(new JLabel(attrThis.isEmpty() ? "" : " (" + attrThis + ")"));
					row.add(new JLabel(" / "));

					JComboBox<String> cbRight = new JComboBox<>(options);
					cbRight.setSelectedItem(right);
					cbRight.setEnabled(false);
					row.add(cbRight);
					row.add(new JLabel(attrOther.isEmpty() ? "" : " (" + attrOther + ")"));
					this.multiplicitePanel.add(row);
				}
			}
		}
		this.multiplicitePanel.revalidate();
		this.multiplicitePanel.repaint();
	}

	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource() == this.btnModif)
		{
			// Activer la modification des champs
			this.nomField.setEditable(true);
			this.typeField.setEditable(true);
			// Activer les boutons radio et checkboxes
			Component[] mereComponents = this.merePanel.getComponents();
			for (Component comp : mereComponents)
			{
				if (comp instanceof JRadioButton rb)
				{
					rb.setEnabled(true);
				}
			}
			Component[] interfaceComponents = this.interfacesPanel.getComponents();
			for (Component comp : interfaceComponents)
			{
				if (comp instanceof JCheckBox cb)
				{
					cb.setEnabled(true);
				}
			}
			// activer les combos de multiplicité
			for (Component rowComp : this.multiplicitePanel.getComponents())
			{
				if (!(rowComp instanceof JPanel)) continue;
				for (Component c : ((JPanel)rowComp).getComponents())
				{
					if (c instanceof JComboBox cb) cb.setEnabled(true);
				}
			}
		}
		else if (e.getSource() == this.btnValid)
		{
			// Désactiver la modification des champs
			this.nomField.setEditable(false);
			this.typeField.setEditable(false);
			// Désactiver les boutons radio et checkboxes
			Component[] mereComponents = this.merePanel.getComponents();
			for (Component comp : mereComponents)
			{
				if (comp instanceof JRadioButton rb)
				{
					rb.setEnabled(false);
				}
			}
			Component[] interfaceComponents = this.interfacesPanel.getComponents();
			for (Component comp : interfaceComponents)
			{
				if (comp instanceof JCheckBox cb)
				{
					cb.setEnabled(false);
				}
			}
			// désactiver les combos de multiplicité
			for (Component rowComp : this.multiplicitePanel.getComponents())
			{
				if (!(rowComp instanceof JPanel)) continue;
				for (Component c : ((JPanel)rowComp).getComponents())
				{
					if (c instanceof JComboBox cb) cb.setEnabled(false);
				}
			}
			// Ici, vous pouvez ajouter le code pour sauvegarder les modifications effectuées
			// Sauvegarder les modifications de multiplicité uniquement
			if (this.ctrl != null && this.currentClasseName != null)
			{
				// retrouver la CreeClass
				CreeClass classeToUpdate = null;
				for (CreeClass c : this.ctrl.getLstClass())
				{
					if (c.getNom().equals(this.currentClasseName)) { classeToUpdate = c; break; }
				}
				if (classeToUpdate != null)
				{
					if (classeToUpdate.getMultiplicite() == null)
					{
						classeToUpdate.initLienMulti();
					}
					java.util.Map<src.metier.CreeClass, java.util.List<java.util.List<String>>> newMap = new java.util.HashMap<>();
					Component[] rows = this.multiplicitePanel.getComponents();
					for (Component rowComp : rows)
					{
						if (!(rowComp instanceof JPanel)) continue;
						JPanel row = (JPanel) rowComp;
						String otherName = row.getName();
						CreeClass otherClass = null;
						for (CreeClass cc : this.ctrl.getLstClass()) if (cc.getNom().equals(otherName)) { otherClass = cc; break; }
						if (otherClass == null) continue;
						// find combos in row
						JComboBox left = null; JComboBox right = null;
						for (Component comp : row.getComponents())
						{
							if (comp instanceof JComboBox cb)
							{
								if (left == null) left = cb; else right = cb;
							}
						}
						if (left != null && right != null)
						{
							String l = (String) left.getSelectedItem();
							String r = (String) right.getSelectedItem();
							java.util.List<String> pair = new java.util.ArrayList<>();
							pair.add(l);
							pair.add(r);
							newMap.computeIfAbsent(otherClass, k -> new java.util.ArrayList<>()).add(pair);
						}
					}
					// remplacer la map existante
					classeToUpdate.getMultiplicite().getMapMultiplicites().clear();
					classeToUpdate.getMultiplicite().getMapMultiplicites().putAll(newMap);
				}
			}
		}
	}

	/**
	 * Efface les informations affichées
	 */
	public void effacer()
	{
		this.nomField.setText("");
		this.typeField.setText("");

		// Vider les panels dynamiques (héritage / interfaces)
		if (this.merePanel != null)
		{
			this.merePanel.removeAll();
			this.merePanel.revalidate();
			this.merePanel.repaint();
		}
		if (this.interfacesPanel != null)
		{
			this.interfacesPanel.removeAll();
			this.interfacesPanel.revalidate();
			this.interfacesPanel.repaint();
		}

		// Attributs / méthodes
		if (this.attributsArea != null) this.attributsArea.setText("");
		if (this.methodesArea != null) this.methodesArea.setText("");
		// Vider le panel de multiplicité
		if (this.multiplicitePanel != null)
		{
			this.multiplicitePanel.removeAll();
			this.multiplicitePanel.revalidate();
			this.multiplicitePanel.repaint();
		}
		this.currentClasseName = null;
		this.ctrl = null;
	}
}
