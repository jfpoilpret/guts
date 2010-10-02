package bench.enums;

public interface ParamEnum<V, T extends ParamEnum<V, T>> extends
		Comparable<ParamEnum<?, ?>> {

	int ordinal();

	String name();

	V defaultValue();

	// TODO narrow closure

	boolean is(ParamEnum<?, ?> that);

	boolean isIn(ParamEnum<?, ?>... thatArray);

	@Override
	int compareTo(ParamEnum<?, ?> that);

}
