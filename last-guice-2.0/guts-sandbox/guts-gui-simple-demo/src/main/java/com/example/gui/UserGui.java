package com.example.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.guts.gui.exception.HandlesException;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.simple.Guts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

//@BindSessionApplication
@Singleton
@SuppressWarnings("serial")
public class UserGui extends JPanel {

	static private final Logger log = LoggerFactory.getLogger(Guts.class);

	private JButton button_1;
	private JButton button_2;

	final private MessageFactory messageFactory;

	@Inject
	public UserGui(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
		initGUI();

		// messageFactory.showMessage("hello");

	}

	// Handle exceptions on the EDT
	@HandlesException
	public boolean handle(Throwable e) {

		// Log the exception
		log.info("oh-oh", e);

		// Show Message to the end user
		messageFactory.showMessage("bada-boom", e, e.getMessage());

		return true;

	}

	private void initGUI() {

		log.debug("init");

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(getButton_1())
						.addPreferredGap(ComponentPlacement.RELATED, 192,
								Short.MAX_VALUE).addComponent(getButton_2())
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING)
				.addGroup(
						Alignment.TRAILING,
						groupLayout
								.createSequentialGroup()
								.addContainerGap(263, Short.MAX_VALUE)
								.addGroup(
										groupLayout
												.createParallelGroup(
														Alignment.BASELINE)
												.addComponent(getButton_1())
												.addComponent(getButton_2()))
								.addContainerGap()));
		setLayout(groupLayout);
	}

	private JButton getButton_1() {
		if (button_1 == null) {
			button_1 = new JButton("New button 1");
		}
		return button_1;
	}

	private JButton getButton_2() {
		if (button_2 == null) {
			button_2 = new JButton("New button 2");
		}
		return button_2;
	}
}
