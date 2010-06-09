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

package net.guts.gui.task.blocker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import com.google.inject.Singleton;

/**
 * Little utility to draw a spinning animation (to notify users they have to wait).
 * Its sue is starightforward: once you have instantiated it and set it up according 
 * to your needs (number of bars, size...), you just need to call {@link #rotate()}
 * periodically (which will update the internal state), and call {@link #paint(Graphics2D)}
 * from your own {@link javax.swing.JComponent#paintComponents(java.awt.Graphics)}.
 * <p/>
 * One instance is not linked to any specific component, hence you can perfectly use it
 * to draw over several components if you want.
 *
 * @author Jean-Francois Poilpret
 */
@Singleton
public class SpinningAnimator
{
	public SpinningAnimator numBars(int numBars)
	{
		if (numBars > _trailLength)
		{
			_numBars = numBars;
			_rotationAngle = Math.PI * 2.0 / _numBars;
		}
		return this;
	}
	
	public SpinningAnimator trailLength(int trailLength)
	{
		if (trailLength > 0 && trailLength < _numBars)
		{
			_trailLength = trailLength;
			_grayIncrement = LIGHTEST_GRAY / _trailLength;
		}
		return this;
	}
	
	public SpinningAnimator innerRadius(int innerRadius)
	{
		if (innerRadius > 0 && innerRadius < _outerRadius)
		{
			_innerRadius = innerRadius;
			updateBar();
		}
		return this;
	}
	
	public SpinningAnimator outerRadius(int outerRadius)
	{
		if (outerRadius > _innerRadius)
		{
			_outerRadius = outerRadius;
			updateBar();
		}
		return this;
	}
	
	public SpinningAnimator barWidth(int barWidth)
	{
		if (barWidth > 0)
		{
			_barWidth = barWidth;
			updateBar();
		}
		return this;
	}
	
	protected void updateBar()
	{
		//	x,            y,              width,        height,    arc width, arc height
		_bar = new RoundRectangle2D.Float(
			_innerRadius, -_barWidth / 2, _outerRadius, _barWidth, _barWidth, _barWidth);
	}

	public void reset()
	{
		_currentFrame = -1;
		_firstLoop = true;
	}
	
	public void rotate()
	{
		_currentFrame = (_currentFrame + 1) % _numBars;
		if (_currentFrame == _trailLength)
		{
			_firstLoop = false;
		}
	}
	
	public void paint(Graphics2D g2d)
	{
		g2d.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Initial rotation to the first bar (the black one)
		g2d.rotate(_rotationAngle * _currentFrame);
		float gray = 0.0f;
		// For first time spinning, trail length is reduced
		int trail = (_firstLoop ? _currentFrame : _trailLength);
		for (int i = 0; i < trail; i++)
		{
			// Compute the color of the next bar to draw
			Color barColor = new Color(gray, gray, gray);

			// Draw the bar
			g2d.setColor(barColor);
			g2d.fill(_bar);

			// Prepare for the next bar to draw (lighter gray)
			gray += _grayIncrement;
			g2d.rotate(-_rotationAngle);
		}
	}
	
	static private final float LIGHTEST_GRAY = 0.75f;

	//CSOFF: MagicNumberCheck
	private int _numBars = 15;
	private double _rotationAngle = Math.PI * 2.0 / _numBars;

	private int _trailLength = 12;
	private float _grayIncrement = LIGHTEST_GRAY / _trailLength;

	private float _barWidth = 6;
	private float _outerRadius = 28;
	private float _innerRadius = 12;
	//CSON: MagicNumberCheck

	//	x,            y,              width,        height,    arc width, arc height
	private RoundRectangle2D _bar = new RoundRectangle2D.Float(
		_innerRadius, -_barWidth / 2, _outerRadius, _barWidth, _barWidth, _barWidth);

	private int _currentFrame = -1;
	private boolean _firstLoop = true;
}
