package bench.idea_02.api;

import bench.idea_02.provider.BindException;

public interface BindRegistry {

	void bind(final Object instanceOne, final Object instanceTwo,
			final BindId bindKey) throws BindException;

	void bindBoth(final Object instanceOne, final Object instanceTwo,
			final BindId bindKey) throws BindException;

}