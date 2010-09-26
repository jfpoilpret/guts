package bench.idea_01;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

class BeanBindInterceptor implements MethodInterceptor {

	static private final Logger log = LoggerFactory
			.getLogger(BeanBindInterceptor.class);

	@Inject
	private BeanBindRegistry registry;

	BeanBindInterceptor() {

		// log.debug("init");

	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {

		final Object instance = invocation.getThis();
		final Method method = invocation.getMethod();
		final Object[] args = invocation.getArguments();

		// log.debug("intercepted instance : {}", instance);
		// log.debug("intercepted method : {}", method);
		// log.debug("intercepted args : {}", args);

		final Object result = invocation.proceed();

		final RegKeyList regList = registry.find(instance, method);

		regList.invokeSuper(args);

		return result;

	}

}