package bench.idea_02.provider;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_02.api.BindId;
import bench.idea_02.api.BindRegistry;

import com.google.inject.Singleton;

@Singleton
final class BindRegistryImpl implements BindRegistry, MethodInterceptor {

	static private final Logger log = LoggerFactory
			.getLogger(BindRegistryImpl.class);

	private final RegObjectMethodsMap map = new RegObjectMethodsMap();

	BindRegistryImpl() {

		// log.info("init");

	}

	public void bind(final Object instanceOne, final Object instanceTwo) {

		bind(instanceOne, instanceTwo, BindId.KEY_DEFAULT);

	}

	@Override
	public void bind(final Object instanceOne, final Object instanceTwo,
			final BindId bindKey) {

		map.store(instanceOne, instanceTwo, bindKey);

	}

	@Override
	public void bindBoth(final Object instanceOne, final Object instanceTwo,
			final BindId bindKey) {

		map.storeBoth(instanceOne, instanceTwo, bindKey);

	}

	public void bind(Object instanceOne, Object instanceTwo,
			BindId... bindKeyArray) {

		for (BindId bindKey : bindKeyArray) {
			bind(instanceOne, instanceTwo, bindKey);
		}

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

}
