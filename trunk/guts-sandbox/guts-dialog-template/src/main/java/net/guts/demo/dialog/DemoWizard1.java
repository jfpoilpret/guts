package net.guts.demo.dialog;

import java.awt.CardLayout;

import javax.swing.JPanel;

import com.google.inject.Inject;

public class DemoWizard1 extends JPanel
{
	public DemoWizard1()
	{
		setLayout(new CardLayout());
	}	

	@Inject void initSteps(DemoView1 view1, DemoView2 view2, DemoWizardStep3 view3)
	{
		add("step1", view1);
		add("step2", view2);
		add("step3", view3);
	}
}
