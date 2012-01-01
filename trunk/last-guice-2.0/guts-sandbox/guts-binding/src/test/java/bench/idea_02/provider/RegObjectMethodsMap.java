package bench.idea_02.provider;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import bench.idea_02.api.Bind;
import bench.idea_02.api.BindId;

import com.google.common.collect.MapMaker;

final class RegObjectMethodsMap {

	// note: weak keys
	private final ConcurrentMap<Object, RegMethodEntriesMap> map = new MapMaker()
			.weakKeys().makeMap();

	void store(final Object instanceOne, final Object instanceTwo,
			final BindId bindKey) {

		final Class<?> klazOne = instanceOne.getClass();

		if (!BindUtil.isEnhanced(klazOne)) {
			throw new BindException("class must use intercept : "
					+ klazOne.getName());
		}

		final Class<?> klazTwo = instanceTwo.getClass();

		if (!BindUtil.isEnhanced(klazTwo)) {
			throw new BindException("class must use intercept : "
					+ klazTwo.getName());
		}

		final Method methodOne = BindUtil.getBoundMethod(klazOne, bindKey);

		if (methodOne == null) {
			throw new BindException("class : " + klazOne.getName()
					+ " must contain some methods annotated with : "
					+ Bind.class);
		}

		final Method methodTwo = BindUtil.getBoundMethod(klazTwo, bindKey);

		if (methodTwo == null) {
			throw new BindException("class : " + klazTwo.getName()
					+ " must contain some methods annotated with : "
					+ Bind.class);
		}

		if (!BindUtil.isSameArgsTypes(methodOne, methodTwo)) {
			throw new BindException(
					"annotated methods must have the same argument types"
							+ " methodOne : " + methodOne + " methodTwo : "
							+ methodTwo);
		}

		//

		store(instanceOne, methodOne, instanceTwo, methodTwo);

		//

	}

	void store(final Object instanceOne, final Method methodOne,
			final Object instanceTwo, final Method methodTwo) {

		final RegMethodEntriesMap regMethod = obtain(instanceOne);

		final RegEntryList regList = regMethod.obtain(methodOne);

		final RegEntry regEntry = new RegEntry(instanceTwo, methodTwo);

		regList.store(regEntry);

	}

	void storeBoth(final Object instanceOne, final Object instanceTwo,
			final BindId bindKey) {

		store(instanceOne, instanceTwo, bindKey);

		store(instanceTwo, instanceOne, bindKey);

	}

	RegMethodEntriesMap obtain(final Object instance) {

		RegMethodEntriesMap list = map.get(instance);

		if (list == null) {
			list = new RegMethodEntriesMap();
			map.putIfAbsent(instance, list);
			list = map.get(instance);
			assert list != null;
		}

		return list;

	}

}
