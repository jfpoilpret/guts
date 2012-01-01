package bench.enums;

import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;


public class GutsRootEnum<T extends RootPaneContainer> extends
		ParamEnumBase<T, GutsRootEnum<T>> {

	private static int ENUM_ORDINAL;
	private static final int ENUM_SIZE;
	private static final GutsRootEnum<?>[] ENUM_VALUES;
	static {
		ENUM_ORDINAL = 0;
		ENUM_SIZE = countEnumFields(GutsRootEnum.class);
		ENUM_VALUES = new GutsRootEnum<?>[ENUM_SIZE];
	}

	private GutsRootEnum(T defVal) {
		super(ENUM_VALUES, ENUM_ORDINAL++, defVal);
	}

	private static final <X extends RootPaneContainer> GutsRootEnum<X> NEW(
			X defaultValue) {
		return new GutsRootEnum<X>(defaultValue);
	}

	public static final GutsRootEnum<JApplet> APPLET = NEW(new JApplet());

	public static final GutsRootEnum<JDialog> DIALOG = NEW(new JDialog());

	public static final GutsRootEnum<JFrame> FRAME = NEW(new JFrame());

	public static final GutsRootEnum<JWindow> WINDOW = NEW(new JWindow());

	public static final GutsRootEnum<JInternalFrame> INTERNAL_FRAME = NEW(new JInternalFrame());

}
