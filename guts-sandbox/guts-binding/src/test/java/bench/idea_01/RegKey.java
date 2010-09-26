package bench.idea_01;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.internal.cglib.core.Signature;
import com.google.inject.internal.cglib.proxy.MethodProxy;

class RegKey {

	static private final Logger log = LoggerFactory.getLogger(RegKey.class);

	private final Object instance;

	private final Method method;

	// TODO
	private BeanBindKey bindKey;

	private final MethodProxy methodProxy;

	private final int hashCode;

	RegKey(final Object instance, final Method method) {

		Class<?> proxyKlaz = instance.getClass();

		assert AopUtil.isEnhanced(proxyKlaz);

		this.instance = instance;

		this.method = method;

		final Signature signature = AopUtil.getSignature(method);

		this.methodProxy = MethodProxy.find(proxyKlaz, signature);

		this.hashCode = instance.hashCode() + method.hashCode();

	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof RegKey) {
			final RegKey that = (RegKey) other;
			if (that.instance.equals(this.instance)
					&& that.method.equals(this.method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	void invokeSuper(final Object[] args) {
		try {
			methodProxy.invokeSuper(instance, args);
		} catch (Throwable e) {
			log.error("", e);
		}
	}

}
