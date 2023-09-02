package local.loeches.restfull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestfullApplication {
	private static final Logger LOGGER = LogManager.getLogger(RestfullApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(RestfullApplication.class, args);
		LOGGER.info("logger desde RestFullApplication");
	}

}
