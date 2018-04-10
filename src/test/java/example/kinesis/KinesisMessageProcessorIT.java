package example.kinesis;

import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import cloud.localstack.docker.annotation.EC2HostNameResolver;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import example.KinesisMessageProcessor;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.nio.ByteBuffer;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * Make sure that docker is running locally
 */
@RunWith(LocalstackDockerTestRunner.class)
@LocalstackDockerProperties(services = {"dynamodb", "kinesis"})
@SpringBootTest
@ActiveProfiles("integration-test")
public class KinesisMessageProcessorIT {

  @ClassRule
  public static final SpringClassRule springClassRule = new SpringClassRule();
  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Value("${kinesis.streamName}")
  public String streamName;

  @MockBean
  private KinesisMessageProcessor kinesisMessageProcessor;

  @Autowired
  private AWSConfig awsConfig;

  @Autowired
  private KinesisListener listener;

  private AmazonKinesis kinesisClient;

  static {
    TestUtils.setEnv("AWS_ACCESS_KEY_ID", "some_aws_access_key_id");
    TestUtils.setEnv("AWS_SECRET_ACCESS_KEY", "some_aws_secret_access_key");

    // https://github.com/mhart/kinesalite/blob/master/README.md#cbor-protocol-issues-with-the-java-sdk
    TestUtils.setEnv("AWS_CBOR_DISABLE", "1");
  }

  @Before
  public void setup() {
    kinesisClient = awsConfig.kinesisClient();
  }

  @Test
  public void shouldProcessKinesisMessage() {
    givenThereIsAKinesisStream:
    {
      kinesisClient.createStream(streamName, 1);
      await().until(() ->
        kinesisClient.describeStream(streamName).getStreamDescription().getStreamStatus().equals("ACTIVE")
      );
    }

    whenThereIsARecordInTheStream:
    {
      PutRecordRequest putRecordRequest = new PutRecordRequest()
        .withStreamName(streamName)
        .withPartitionKey("some_partition_key")
        .withData(ByteBuffer.wrap("Hello".getBytes()));
      kinesisClient.putRecord(putRecordRequest);
    }

    thenTheReaderReadsTheMessageFromTheStream:
    {
      verify(kinesisMessageProcessor, timeout(30000)).processKinesisMessage("Hello");
    }
  }

  @After
  public void shutDownWorkerAndDeleteSetup() {
    listener.stop();
    kinesisClient.deleteStream(streamName);
  }
}
