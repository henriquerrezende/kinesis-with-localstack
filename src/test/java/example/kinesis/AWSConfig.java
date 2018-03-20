package example.kinesis;

import cloud.localstack.DockerTestUtils;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("integration-test")
public class AWSConfig {

  public AmazonKinesis kinesisClient() {
    return DockerTestUtils.getClientKinesis();
  }

  public AmazonDynamoDB dynamoDBClient() {
    return DockerTestUtils.getClientDynamoDb();
  }

  public AWSCredentialsProvider getCredentials() {
    return new DefaultAWSCredentialsProviderChain();
  }

  public MetricsLevel getCloudWatchMetricsLevel() {
    return MetricsLevel.NONE;
  }
}
