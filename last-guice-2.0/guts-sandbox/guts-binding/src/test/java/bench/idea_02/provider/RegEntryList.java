package bench.idea_02.provider;

import java.util.concurrent.CopyOnWriteArrayList;

final class RegEntryList {

	private final CopyOnWriteArrayList<RegEntry> list = new CopyOnWriteArrayList<RegEntry>();

	// note: not using equals, hashCode, due to weak refs
	synchronized void store(final RegEntry newEntry) {

		if (newEntry.isExpired()) {
			throw new BindException("newEntry.isExpired()");
		}

		for (final RegEntry oldEntry : list) {

			if (oldEntry.isExpired()) {
				list.remove(oldEntry);
				continue;
			}

			if (oldEntry.isMatch(newEntry)) {
				throw new BindException("oldEntry.isMatch(newEntry)");
			}

		}

		list.add(newEntry);

	}

	void invokeSuper(final Object[] arguments) {

		for (final RegEntry entry : list) {

			final boolean isValid = entry.invokeSuper(arguments);

			if (!isValid) {
				list.remove(entry);
			}

		}

	}

}
