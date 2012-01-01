package net.guts.gui.simple;

import java.util.List;

import javax.swing.RootPaneContainer;

import com.google.inject.Module;

public interface GutsRoot<T extends RootPaneContainer> {

	T getRoot();

	void initModules(List<Module> moduleList);

}
