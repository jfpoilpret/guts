package bench.idea_03.api;

import bench.idea_03.provider.SyncException;

public interface Registry {

	void publish(Object source, Object target) //
			throws SyncException;

	void publish(Object source, Object target, SyncTopic id) //
			throws SyncException;

	void replicate(Object instanceOne, Object instanceTwo) //
			throws SyncException;

	void replicate(Object instanceOne, Object instanceTwo, SyncTopic id) //
			throws SyncException;

	void publishMatching(Object source, Object target) //
			throws SyncException;

	void replicateMatching(Object instanceOne, Object instanceTwo) //
			throws SyncException;

}
