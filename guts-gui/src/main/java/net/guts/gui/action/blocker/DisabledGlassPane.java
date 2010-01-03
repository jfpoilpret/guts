//  Copyright 2009 Jean-Francois Poilpret
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.guts.gui.action.blocker;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;


import com.google.inject.Inject;

// Adapted from http://tips4java.wordpress.com/2008/11/07/disabled-glass-pane/
// Credit: Rob Camick
@SuppressWarnings("serial")
class DisabledGlassPane extends JComponent
{
	@Inject DisabledGlassPane(SpinningAnimator spinner)
	{
		_spinner = spinner;
		
		setOpaque(false);
		Color base = Color.GRAY;
		Color background = new Color(base.getRed(), base.getGreen(), base.getBlue(), ALPHA);
		setBackground(background);

		// Disable Mouse, Key and Focus events for the glass pane
		MouseInputListener mouseListener = new MouseInputAdapter() {};
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		
		KeyListener keyListener = new KeyAdapter()
		{
			@Override public void keyPressed(KeyEvent e)
			{
				e.consume();
			}
		
			@Override public void keyReleased(KeyEvent e)
			{
				e.consume();
			}
		};
		addKeyListener(keyListener);
		setFocusTraversalKeysEnabled(false);

		if (_spinner != null)
		{
			_animator = new Timer(FRAME_TIME_MS, new ActionListener()
			{
				@Override public void actionPerformed(ActionEvent unused)
				{
					_spinner.rotate();
					repaint();
				}
			});
		}
		else
		{
			_animator = null;
		}
	}

	@Override protected void paintComponent(Graphics g)
	{
		// Paint transparent background
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		// Paint spinning animation
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(getWidth() / 2, getHeight() / 2);
		if (_spinner != null)
		{
			_spinner.paint(g2d);
		}
	}

	void activate()
	{
		setVisible(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		requestFocusInWindow();
		if (_spinner != null)
		{
			_spinner.reset();
			_animator.start();
		}
	}

	void deactivate()
	{
		setCursor(null);
		setVisible(false);
		if (_spinner != null)
		{
			_animator.stop();
		}
	}
	
	static private final int ALPHA = 96;
	static private final int FRAME_TIME_MS = 100;

    private final Timer _animator;
    private final SpinningAnimator _spinner;
}
