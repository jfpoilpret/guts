package bench.idea_03.provider;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.internal.cglib.core.Signature;
import com.google.inject.internal.cglib.proxy.MethodProxy;

final class RegEntry {

	static private final Logger log = LoggerFactory.getLogger(RegEntry.class);

	private final WeakReference<Object> reference;

	private final Method method;

	private final MethodProxy methodProxy;

	RegEntry(final Object instance, final Method method) {

		assert instance != null && method != null;

		final Class<?> proxyKlaz = instance.getClass();

		assert SyncUtil.isEnhanced(proxyKlaz);

		this.reference = new WeakReference<Object>(instance);

		this.method = method;

		final Signature signature = SyncUtil.getSignature(method);

		this.methodProxy = MethodProxy.find(proxyKlaz, signature);

		assert methodProxy != null;

	}

	// "true" means still valid
	boolean invokeSuper(final Object[] arguments) {

		final Object instance = reference.get();

		if (instance == null) {
			return false;
		}

		try {
			methodProxy.invokeSuper(instance, arguments);
			return true;
		} catch (Throwable e) {
			log.error("", e);
			return false;
		}

	}

	boolean isExpired() {
		return null == reference.get();
	}

	boolean isMatch(final RegEntry that) {

		final Object thisInstance = this.reference.get();

		final Object thatInstance = that.reference.get();

		if (thisInstance == null || thatInstance == null) {
			return false;
		}

		return thisInstance == thatInstance && this.method.equals(that.method);

	}

	// do not override
	// @Override
	// public boolean equals(final Object other) {
	// return super.equals(other);
	// }

	// do not override
	// @Override
	// public int hashCode() {
	// return super.hashCode();
	// }

}
