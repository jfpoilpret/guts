package bench.idea;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
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
	BeanBindRegistry registry;

	BeanBindInterceptor() {

		log.info("init");

	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {

		final Object instance = invocation.getThis();
		final Method method = invocation.getMethod();
		final Object[] args = invocation.getArguments();
		final Class<? extends MethodInvocation> klaz = invocation.getClass();
		final AccessibleObject part = invocation.getStaticPart();

		log.info("intercepted instance : {}", instance);
		log.info("intercepted method : {}", method);
		log.info("intercepted args : {}", args);
		log.info("intercepted klaz : {}", klaz);
		log.info("intercepted part : {}", part);

		final Object peer = registry.find(instance);

		if (peer == null) {

			log.error("intercepted peer is null");

			return invocation.proceed();

		}

		log.info("intercepted peer : {}", peer);

		Class<? extends Object> peerProxyKlaz = peer.getClass();
		log.info("--- peerProxyKlaz : {}", peerProxyKlaz);

		for (final Method peerMethod : peerProxyKlaz.getDeclaredMethods()) {
			log.info("--- proxy peerMethod : {}", peerMethod.getName());
		}

		Class<? extends Object> peerRealKlaz = peerProxyKlaz.getSuperclass();
		log.info("+++ peerRealKlaz : {}", peerRealKlaz);

		for (final Method peerMethod : peerRealKlaz.getDeclaredMethods()) {
			log.info("+++ real peerMethod : {}", peerMethod.getName());
		}

		methodLoop: for (final Method peerMethod : peerRealKlaz
				.getDeclaredMethods()) {

			// log.info("peerMethod : {}", peerMethod.getName());

			Annotation[] peerAnnoArray = peerMethod.getDeclaredAnnotations();
			// log.info("peerAnnoArray.length  : {}", peerAnnoArray.length);

			for (final Annotation peerAnno : peerAnnoArray) {

				// log.info("peerAnno : {} ; {}", peerAnno,
				// peerAnno.annotationType());

				if (peerAnno.annotationType() == BeanBind.class) {

					log.info("!!! peer anno match: {} ; {}", peerAnno,
							peerMethod.getName());

					peerMethod.setAccessible(true);

					peerMethod.invoke(peer, args);

					break methodLoop;

				}
			}

		}

		return invocation.proceed();

	}

}