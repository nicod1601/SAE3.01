package src.ihm;

import javax.swing.*;
import src.metier.CreeClass;
import src.metier.Attribut;
import src.metier.Methode;

public class PanneauInfo extends JPanel
{
	private JTextField nomField;
	private JTextField typeField;
	private JTextField mereField;
	private JTextArea interfacesArea;
	private JTextArea attributsArea;
	private JTextArea methodesArea;

	public PanneauInfo()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Créer un panneau de contenu avec ScrollPane
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		// Nom de la classe
		contentPanel.add(new JLabel("Nom de la classe :"));
		this.nomField = new JTextField();
		this.nomField.setEditable(false);
		contentPanel.add(this.nomField);
		contentPanel.add(Box.createVerticalStrut(10));

		// Type de la classe
		contentPanel.add(new JLabel("Type :"));
		this.typeField = new JTextField();
		this.typeField.setEditable(false);
		contentPanel.add(this.typeField);
		contentPanel.add(Box.createVerticalStrut(10));

		// Classe mère
		contentPanel.add(new JLabel("Hérite de :"));
		this.mereField = new JTextField();
		this.mereField.setEditable(false);
		contentPanel.add(this.mereField);
		contentPanel.add(Box.createVerticalStrut(10));

		// Interfaces
		contentPanel.add(new JLabel("Interfaces :"));
		this.interfacesArea = new JTextArea(3, 30);
		this.interfacesArea.setEditable(false);
		this.interfacesArea.setLineWrap(true);
		this.interfacesArea.setWrapStyleWord(true);
		contentPanel.add(new JScrollPane(this.interfacesArea));
		contentPanel.add(Box.createVerticalStrut(10));

		// Attributs
		contentPanel.add(new JLabel("Attributs :"));
		this.attributsArea = new JTextArea(4, 30);
		this.attributsArea.setEditable(false);
		this.attributsArea.setLineWrap(true);
		this.attributsArea.setWrapStyleWord(true);
		contentPanel.add(new JScrollPane(this.attributsArea));
		contentPanel.add(Box.createVerticalStrut(10));

		// Méthodes
		contentPanel.add(new JLabel("Méthodes :"));
		this.methodesArea = new JTextArea(5, 30);
		this.methodesArea.setEditable(false);
		this.methodesArea.setLineWrap(true);
		this.methodesArea.setWrapStyleWord(true);
		contentPanel.add(new JScrollPane(this.methodesArea));

		// Ajouter le contentPanel dans un ScrollPane
		JScrollPane scrollPane = new JScrollPane(contentPanel);
		this.add(scrollPane);

		// Message initial
		this.effacer();
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
		
		// Classe mère
		if (classe.getMere() != null && !classe.getMere().isEmpty())
		{
			this.mereField.setText(classe.getMere());
		}
		else
		{
			this.mereField.setText("Aucune");
		}

		// Interfaces
		StringBuilder interfacesText = new StringBuilder();
		if (classe.getInterfaces() != null && !classe.getInterfaces().isEmpty())
		{
			for (String inter : classe.getInterfaces())
			{
				interfacesText.append("- ").append(inter).append("\n");
			}
		}
		else
		{
			interfacesText.append("Aucune interface");
		}
		this.interfacesArea.setText(interfacesText.toString());

		// Attributs
		StringBuilder attributsText = new StringBuilder();
		if (!classe.getLstAttribut().isEmpty())
		{
			for (Attribut attr : classe.getLstAttribut())
			{
				attributsText.append(attr.getType()).append(" ").append(attr.getNom()).append("\n");
			}
		}
		else
		{
			attributsText.append("Aucun attribut");
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
					.append(method.getNom()).append("(...)\n");
			}
		}
		else
		{
			methodesText.append("Aucune méthode");
		}
		this.methodesArea.setText(methodesText.toString());
	}

	/**
	 * Efface les informations affichées
	 */
	public void effacer()
	{
		this.nomField.setText("");
		this.typeField.setText("");
		this.mereField.setText("");
		this.interfacesArea.setText("Sélectionnez une classe pour afficher ses informations.");
		this.attributsArea.setText("");
		this.methodesArea.setText("");
	}
}
