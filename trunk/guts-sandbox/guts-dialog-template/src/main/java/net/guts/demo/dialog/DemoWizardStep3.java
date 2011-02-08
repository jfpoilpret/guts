package net.guts.demo.dialog;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;

public class DemoWizardStep3 extends JPanel
{
	public DemoWizardStep3()
	{
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().grid(_label).add(_field);
	}

	final private JLabel _label = new JLabel("Some label");
	final private JTextField _field = new JTextField(10);
}
