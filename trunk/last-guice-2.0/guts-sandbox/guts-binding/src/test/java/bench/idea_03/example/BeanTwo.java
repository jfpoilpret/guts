package bench.idea_03.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_03.api.Sync;
import bench.idea_03.api.SyncTopic;

public class BeanTwo {

	static private final Logger log = LoggerFactory.getLogger(BeanTwo.class);

	private Integer value;

	public Integer getTwo() {

		return value;

	}

	@Sync
	public void setRedTwo(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@Sync(topic = SyncTopic.KEY_GREEN)
	public void setGreenTwo(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@Sync(topic = SyncTopic.KEY_ORANGE)
	public void setGreenTwo(Integer value, String text) {

		log.info("value, text : {} {}", value, text);

		this.value = value;

	}

	@Sync(topic = SyncTopic.KEY_0)
	public void setIntTwo(int value) {

		log.info("value : {}", value);

		this.value = value;

	}

}
