package example.kinesis;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class AWSConfig {
  public AmazonKinesis kinesisClient() {
    return AmazonKinesisClientBuilder.standard().withCredentials(getCredentials()).build();
  }

  public AmazonDynamoDB dynamoDBClient() {
    return AmazonDynamoDBClientBuilder.standard().withCredentials(getCredentials()).build();
  }

  public AWSCredentialsProvider getCredentials() {
    return new DefaultAWSCredentialsProviderChain();
  }

  public MetricsLevel getCloudWatchMetricsLevel() {
    return MetricsLevel.DETAILED;
  }
}
