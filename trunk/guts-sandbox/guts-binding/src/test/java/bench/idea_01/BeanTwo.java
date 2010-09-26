package bench.idea_01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanTwo {

	static private final Logger log = LoggerFactory.getLogger(BeanTwo.class);

	private Integer value;

	public Integer getTwo() {

		return value;

	}

	@BeanBind
	public void setRedTwo(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

	@BeanBind(BeanBindKey.KEY_GREEN)
	public void setGreenTwo(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

}
