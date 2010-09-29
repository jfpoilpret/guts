package bench.idea_03.provider;

import net.guts.common.injection.OneTypeListener;
import bench.idea_03.api.Registry;
import bench.idea_03.api.Sync;

import com.google.inject.AbstractModule;

public final class SyncModule extends AbstractModule {

	@Override
	protected void configure() {

		final SyncInjectListener injectionListener = new SyncInjectListener();

		final OneTypeListener<Object> typeListener = new OneTypeListener<Object>(
				Object.class, injectionListener);

		// bindListener(SyncMatchers.hasMethodAnnotatedWith(Sync.class),
		// typeListener);

		bindListener(
				net.guts.common.injection.Matchers
						.hasMethodAnnotatedWith(Sync.class),
				typeListener);

		//

		final SyncRegistryImpl registry = new SyncRegistryImpl();

		bind(Registry.class).toInstance(registry);

		bindInterceptor(//
				com.google.inject.matcher.Matchers.any(), //
				com.google.inject.matcher.Matchers.annotatedWith(Sync.class), //
				registry//
		);

	}

}