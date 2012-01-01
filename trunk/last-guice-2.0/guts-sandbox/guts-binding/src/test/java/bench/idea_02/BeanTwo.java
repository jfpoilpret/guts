package bench.idea_02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_02.api.Bind;
import bench.idea_02.api.BindId;

public class BeanTwo {

	static private final Logger log = LoggerFactory.getLogger(BeanTwo.class);

	private Integer value;

	public Integer getTwo() {

		return value;

	}

	@Bind
	public void setRedTwo(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@Bind(BindId.KEY_GREEN)
	public void setGreenTwo(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@Bind(BindId.KEY_ORANGE)
	public void setGreenTwo(Integer value, String text) {

		log.info("value, text : {} {}", value, text);

		this.value = value;

	}

	@Bind(BindId.KEY_0)
	public void setIntTwo(int value) {

		log.info("value : {}", value);

		this.value = value;

	}

}
