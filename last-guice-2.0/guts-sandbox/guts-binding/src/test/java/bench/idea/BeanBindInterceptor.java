package bench.idea;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.internal.asm.Type;
import com.google.inject.internal.cglib.core.Signature;
import com.google.inject.internal.cglib.proxy.MethodProxy;

class BeanBindInterceptor implements MethodInterceptor {

	static private final Logger log = LoggerFactory
			.getLogger(BeanBindInterceptor.class);

	@Inject
	BeanBindRegistry registry;

	BeanBindInterceptor() {

		log.debug("init");

	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {

		final Object instance = invocation.getThis();
		final Method method = invocation.getMethod();
		final Object[] args = invocation.getArguments();
		final Class<? extends MethodInvocation> klaz = invocation.getClass();
		final AccessibleObject part = invocation.getStaticPart();

		log.debug("intercepted instance : {}", instance);
		log.debug("intercepted method : {}", method);
		log.debug("intercepted args : {}", args);
		log.debug("intercepted klaz : {}", klaz);
		log.debug("intercepted part : {}", part);

		final Object peer = registry.find(instance);

		if (peer == null) {

			log.error("intercepted peer is null");

			return invocation.proceed();

		}

		Object result = invocation.proceed();

		log.info("intercepted peer : {}", peer);

		Class<? extends Object> peerProxyKlaz = peer.getClass();
		log.debug("--- peerProxyKlaz : {}", peerProxyKlaz);

		// for (final Method peerMethod : peerProxyKlaz.getDeclaredMethods()) {
		// log.debug("--- proxy peerMethod : {}", peerMethod.getName());
		// }

		Class<? extends Object> peerRealKlaz = peerProxyKlaz.getSuperclass();
		log.debug("+++ peerRealKlaz : {}", peerRealKlaz);

		// for (final Method peerMethod : peerRealKlaz.getDeclaredMethods()) {
		// log.debug("+++ real peerMethod : {}", peerMethod.getName());
		// }

		methodLoop: for (final Method peerMethod : peerRealKlaz
				.getDeclaredMethods()) {

			// log.debug("peerMethod : {}", peerMethod.getName());

			Annotation[] peerAnnoArray = peerMethod.getDeclaredAnnotations();
			// log.debug("peerAnnoArray.length  : {}", peerAnnoArray.length);

			for (final Annotation peerAnno : peerAnnoArray) {

				// log.debug("peerAnno : {} ; {}", peerAnno,
				// peerAnno.annotationType());

				if (peerAnno.annotationType() == BeanBind.class) {

					log.debug("!!! peer anno match: {} ; {}", peerAnno,
							peerMethod.getName());

					peerMethod.setAccessible(true);

					Class<?> type = peerProxyKlaz;

					// Signature signature = TypeUtils
					// .parseSignature("void setTwo(Integer)");

					Signature signature = getSignature(peerMethod);

					log.debug("!!! peer method signature: {}", signature);

					MethodProxy methodProxy = MethodProxy.find(type, signature);

					methodProxy.invokeSuper(peer, args);

					// peerMethod.invoke(peer, args);

					break methodLoop;

				}
			}

		}

		return result;

	}

	Signature getSignature(Method method) {

		String name = method.getName();

		Type returnType = Type.getType(method.getReturnType());

		Class<?>[] paramTypeArray = method.getParameterTypes();

		int paramCount = paramTypeArray.length;

		Type[] argumentTypes = new Type[paramCount];

		for (int k = 0; k < paramCount; k++) {
			argumentTypes[k] = Type.getType(paramTypeArray[k]);
		}

		Signature signature = new Signature(name, returnType, argumentTypes);

		return signature;

	}

}