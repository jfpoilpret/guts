package bench.idea_03.provider;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_03.api.SyncTopic;
import bench.idea_03.api.Registry;

import com.google.inject.Singleton;

@Singleton
final class SyncRegistryImpl implements Registry, MethodInterceptor {

	static private final Logger log = LoggerFactory
			.getLogger(SyncRegistryImpl.class);

	private final RegObjectMethodsMap map = new RegObjectMethodsMap();

	SyncRegistryImpl() {

		// log.info("init");

	}

	@Override
	public void publish(final Object instanceOne, final Object instanceTwo,
			final SyncTopic bindKey) {

		map.store(instanceOne, instanceTwo, bindKey);

	}

	@Override
	public void replicate(final Object instanceOne, final Object instanceTwo,
			final SyncTopic bindKey) {

		map.storeBoth(instanceOne, instanceTwo, bindKey);

	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {

		final Object result = invocation.proceed();

		final Object instance = invocation.getThis();
		final Method method = invocation.getMethod();
		final Object[] arguments = invocation.getArguments();

		// log.debug("intercepted instance : {}", instance);
		// log.debug("intercepted method : {}", method);
		// log.debug("intercepted arguments : {}", arguments);

		map.obtain(instance).obtain(method).invokeSuper(arguments);

		return result;

	}

	@Override
	public void publishMatching(Object instanceOne, Object instanceTwo)
			throws SyncException {
		// TODO Auto-generated method stub

	}

	@Override
	public void replicateMatching(Object instanceOne, Object instanceTwo)
			throws SyncException {
		// TODO Auto-generated method stub

	}

	@Override
	public void publish(Object source, Object target) throws SyncException {
		publish(source, target, SyncTopic.KEY_DEFAULT);
	}

	@Override
	public void replicate(Object instanceOne, Object instanceTwo)
			throws SyncException {
		replicate(instanceOne, instanceTwo, SyncTopic.KEY_DEFAULT);
	}

}
