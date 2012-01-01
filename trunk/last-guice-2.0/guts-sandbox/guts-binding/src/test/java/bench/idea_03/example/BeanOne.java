package bench.idea_03.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_03.api.SyncKind;
import bench.idea_03.api.Sync;
import bench.idea_03.api.SyncTopic;

public class BeanOne {

	static private final Logger log = LoggerFactory.getLogger(BeanOne.class);

	private Integer value;

	public Integer getOne() {

		return value;

	}

	@Sync(kind = SyncKind.PUBLISH)
	public void setRedOne(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@Sync(topic = SyncTopic.KEY_GREEN)
	public void setGreenOne(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@Sync(topic = SyncTopic.KEY_ORANGE)
	public void setGreenOne(Integer value, String text) {

		log.info("value, text : {} {}", value, text);

		this.value = value;

	}

	@Sync(topic = SyncTopic.KEY_0)
	public void setIntOne(int value) {

		log.info("value : {}", value);

		this.value = value;

	}

}
