package example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KinesisMessageProcessor {

  public void processKinesisMessage(String message) {
    log.info("Message received from the Kinesis stream: " + message);
  }
}
