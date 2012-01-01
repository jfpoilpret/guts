package bench.idea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanOne {

	static private final Logger log = LoggerFactory.getLogger(BeanOne.class);

	private Integer value;

	public Integer getOne() {

		return value;

	}

	@BeanBind
	public void setOne(Integer value) {

		log.info("value : {}", value);

		this.value = value;

	}

}
