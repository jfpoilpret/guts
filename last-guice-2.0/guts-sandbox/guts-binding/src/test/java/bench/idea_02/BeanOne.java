package bench.idea_02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_02.api.Bind;
import bench.idea_02.api.BindId;

public class BeanOne {

	static private final Logger log = LoggerFactory.getLogger(BeanOne.class);

	private Integer value;

	public Integer getOne() {

		return value;

	}

	@Bind
	public void setRedOne(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@Bind(BindId.KEY_GREEN)
	public void setGreenOne(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@Bind(BindId.KEY_ORANGE)
	public void setGreenOne(Integer value, String text) {

		log.info("value, text : {} {}", value, text);

		this.value = value;

	}

	@Bind(BindId.KEY_0)
	public void setIntOne(int value) {

		log.info("value : {}", value);

		this.value = value;

	}

}
