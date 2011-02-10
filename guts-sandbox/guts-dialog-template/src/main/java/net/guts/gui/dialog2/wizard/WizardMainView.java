package net.guts.gui.dialog2.wizard;

import java.awt.CardLayout;

import javax.swing.JPanel;

import com.google.inject.Inject;

public class WizardMainView extends JPanel
{	
	@Inject public WizardMainView()
	{
		setLayout(_layout);
	}
	
	final private CardLayout _layout = new CardLayout();
}
