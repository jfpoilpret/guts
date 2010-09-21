package bench.idea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanTwo {

	static private final Logger log = LoggerFactory.getLogger(BeanTwo.class);

	private Integer value;

	public Integer getTwo() {

		return value;

	}

	@BeanBind
	public void setTwo(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

}
