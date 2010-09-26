package bench.idea_01;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class BeanBindRegistry {

	static private final Logger log = LoggerFactory
			.getLogger(BeanBindRegistry.class);

	private final RegKeyCache cache = new RegKeyCache();

	private final RegKeyListMap map = new RegKeyListMap();

	BeanBindRegistry() {

		// log.info("init");

	}

	public void bind(final Object instanceOne, final Object instanceTwo) {

		bind(instanceOne, instanceTwo, BeanBindKey.KEY_DEFAULT);

	}

	public void bind(final Object instanceOne, final Object instanceTwo, final BeanBindKey bindKey) {

		final Class<?> klazOne = instanceOne.getClass();

		if (!AopUtil.isEnhanced(klazOne)) {
			throw new BeanBindException("class must use intercept : "
					+ klazOne.getName());
		}

		final Class<?> klazTwo = instanceTwo.getClass();

		if (!AopUtil.isEnhanced(klazTwo)) {
			throw new BeanBindException("class must use intercept :"
					+ klazTwo.getName());
		}

		final Method methodOne = AopUtil.getBoundMethod(klazOne, bindKey);

		if (methodOne == null) {
			throw new BeanBindException("class : " + klazOne.getName()
					+ " must contain some methods annotated with : "
					+ BeanBind.class);
		}

		final Method methodTwo = AopUtil.getBoundMethod(klazTwo, bindKey);

		if (methodTwo == null) {
			throw new BeanBindException("class : " + klazTwo.getName()
					+ " must contain some methods annotated with : "
					+ BeanBind.class);
		}

		if (!AopUtil.isSameArgsTypes(methodOne, methodTwo)) {
			throw new BeanBindException(
					"annotated methods must have the same argument types"
							+ " methodOne : " + methodOne + " methodTwo : "
							+ methodTwo);
		}

		synchronized (this) {

			final RegKey keyOne = cache.obtain(instanceOne, methodOne);
			final RegKey keyTwo = cache.obtain(instanceTwo, methodTwo);

			final RegKeyList listOne = map.obtain(keyOne);
			final RegKeyList listTwo = map.obtain(keyTwo);

			listOne.addIfAbsent(keyTwo);
			listTwo.addIfAbsent(keyOne);

		}

	}

	public void bind(Object instanceOne, Object instanceTwo, BeanBindKey... bindKeyArray) {

		for (BeanBindKey bindKey : bindKeyArray) {
			bind(instanceOne, instanceTwo, bindKey);
		}

	}

	RegKeyList find(Object instance, Method method) {

		RegKey key = cache.obtain(instance, method);

		RegKeyList list = map.obtain(key);

		return list;

	}

}
