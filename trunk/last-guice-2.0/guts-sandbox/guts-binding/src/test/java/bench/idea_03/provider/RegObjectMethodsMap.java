package bench.idea_03.provider;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import bench.idea_03.api.Sync;
import bench.idea_03.api.SyncTopic;

import com.google.common.collect.MapMaker;

final class RegObjectMethodsMap {

	// note: weak keys
	private final ConcurrentMap<Object, RegMethodEntriesMap> map = new MapMaker()
			.weakKeys().makeMap();

	void store(final Object instanceOne, final Object instanceTwo,
			final SyncTopic bindKey) {

		final Class<?> klazOne = instanceOne.getClass();

		if (!SyncUtil.isEnhanced(klazOne)) {
			throw new SyncException("class must use intercept : "
					+ klazOne.getName());
		}

		final Class<?> klazTwo = instanceTwo.getClass();

		if (!SyncUtil.isEnhanced(klazTwo)) {
			throw new SyncException("class must use intercept : "
					+ klazTwo.getName());
		}

		final Method methodOne = SyncUtil.getSyncMethod(klazOne, bindKey);

		if (methodOne == null) {
			throw new SyncException("class : " + klazOne.getName()
					+ " must contain some methods annotated with : "
					+ Sync.class);
		}

		final Method methodTwo = SyncUtil.getSyncMethod(klazTwo, bindKey);

		if (methodTwo == null) {
			throw new SyncException("class : " + klazTwo.getName()
					+ " must contain some methods annotated with : "
					+ Sync.class);
		}

		if (!SyncUtil.isSameArgsTypes(methodOne, methodTwo)) {
			throw new SyncException(
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
			final SyncTopic bindKey) {

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
