package net.guts.gui.docking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Channel;
import net.guts.event.Consumes;
import net.guts.gui.application.support.SingleFrameLifecycle;
import net.guts.gui.exit.ExitController;
import net.guts.gui.session.StorageMedium;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.util.DirectWindowProvider;

import com.google.inject.Inject;

//CSOFF: AbstractClassNameCheck
public abstract class DockingLifecycle extends SingleFrameLifecycle
{
	static final private Logger _logger = LoggerFactory.getLogger(DockingLifecycle.class);

	@Inject void initDocking(
		CControl controller, final Channel<CDockable> selectionChannel)
	{
		_controller = controller;
		// Add necessary listeners to docking events for Event Channels production
		_controller.addFocusListener(new CFocusListener()
		{
			@Override public void focusLost(CDockable dockable)
			{
				selectionChannel.publish(null);
			}
			
			@Override public void focusGained(CDockable dockable)
			{
				selectionChannel.publish(dockable);
			}
		});
	}

	@Inject void injectFields(StorageMedium storage, ViewFactory viewFactory,
		Map<String, LayoutInitializer> layouts)
	{
		_storage = storage;
		_viewFactory = viewFactory;
		_layouts = layouts;
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.application.support.SingleFrameLifecycle#initFrame(javax.swing.JFrame)
	 */
	@Override final protected void initFrame(JFrame mainFrame)
	{
		_controller.setRootWindow(new DirectWindowProvider(mainFrame));

		LayoutInitializer initializer = _layouts.get(getDefaultPerspectiveId());
		if (initializer == null)
		{
			_logger.error("No LayoutInitializer bound for {}", getDefaultPerspectiveId());
			//TODO throw exception? show message?
			return;
		}
		// Reload last perspective or initialize a new one
		if (!loadLayout())
		{
			initializer.initLayout(_controller, _viewFactory);
		}
		
		// Add the main DockStation to the frame
		mainFrame.add(_controller.getContentArea());
		
		// Initialize main frame
		initMainFrame(mainFrame);
	}
	
	abstract protected void	initMainFrame(JFrame frame);
	
	protected String getDefaultPerspectiveId()
	{
		return Docking.DEFAULT_PERSPECTIVE;
	}

	final protected CControl controller()
	{
		return _controller;
	}

	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE)
	public void shutdown(Void nothing)
	{
		//TODO remove once sure it works without a public method, but it doesn't work yet...
		_logger.debug("shutdown()");
		// First remove all MultipleCDockables (they should not be saved/restored)
		cleanupBeforeSave();
		saveLayout();
	}
	
	private void cleanupBeforeSave()
	{
		// Note: make a copy of the original list because the original will be modified
		// by _controller during iteration! If we didn't do that, we may have side-effects
		// or a ConcurrentModificationException.
		List<MultipleCDockable> dockables = new ArrayList<MultipleCDockable>(
			_controller.getRegister().getMultipleDockables());
		for (MultipleCDockable dockable: dockables)
		{
			_controller.remove(dockable);
		}
	}

	private boolean loadLayout()
	{
		byte[] session = _storage.load(PERSISTENCE_KEY);
		if (session != null)
		{
			try
			{
				_controller.read(new DataInputStream(new ByteArrayInputStream(session)));
				_controller.load(getDefaultPerspectiveId());
				return true;
			}
			catch (IOException e)
			{
				_logger.error("loadLayout()", e);
			}
		}
		return false;
	}
	
	private void saveLayout()
	{
		try
		{
			_controller.save(getDefaultPerspectiveId());
			ByteArrayOutputStream buffer = new ByteArrayOutputStream(BUFFER_DEFAULT_SIZE);
			DataOutputStream out = new DataOutputStream(buffer);
			_controller.write(out);
			_storage.save(PERSISTENCE_KEY, buffer.toByteArray());
		}
		catch (IOException e)
		{
			_logger.error("saveLayout()", e);
		}
	}

	static final private String PERSISTENCE_KEY = "docking-frames";
	static final private int BUFFER_DEFAULT_SIZE = 8 * 1024;

	private CControl _controller;
	private StorageMedium _storage;
	private ViewFactory _viewFactory;
	private Map<String, LayoutInitializer> _layouts;
}
//CSON: AbstractClassNameCheck
