package bench.idea_02.provider;

import bench.idea_02.api.Bind;
import bench.idea_02.api.BindRegistry;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public final class BindModule extends AbstractModule {

	@Override
	protected void configure() {

		final BindRegistryImpl registry = new BindRegistryImpl();

		bind(BindRegistry.class).toInstance(registry);

		bindInterceptor(//
				Matchers.any(), //
				Matchers.annotatedWith(Bind.class), //
				registry//
		);

	}

}