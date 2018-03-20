package example.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import example.KinesisMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RecordProcessor implements IRecordProcessor {

  private KinesisMessageProcessor kinesisMessageProcessor;

  @Autowired
  public RecordProcessor(KinesisMessageProcessor kinesisMessageProcessor) {
    this.kinesisMessageProcessor = kinesisMessageProcessor;
  }

  @Override
  public void initialize(InitializationInput initializationInput) {
    log.info("Initializing RecordProcessor");
  }

  @Override
  public void processRecords(ProcessRecordsInput processRecordsInput) {
    log.info(String.format("Processing %d records from Kinesis", processRecordsInput.getRecords().size()));

    processRecordsInput.getRecords().forEach(this::handleSingleRecord);
    checkpoint(processRecordsInput.getCheckpointer());
  }

  private void handleSingleRecord(Record record) {
    String message = StandardCharsets.UTF_8.decode(record.getData()).toString();
    kinesisMessageProcessor.processKinesisMessage(message);
  }

  @Override
  public void shutdown(ShutdownInput shutdownInput) {
    if (shutdownInput.getShutdownReason() == ShutdownReason.TERMINATE) {
      checkpoint(shutdownInput.getCheckpointer());
    }
    log.info("Shutting down RecordProcessor");
  }

  private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
    try {
      checkpointer.checkpoint();
    } catch (InvalidStateException e) {
      log.error("Failed to checkpoint. KCL threw an InvalidStateException", e);
    } catch (ShutdownException e) {
      log.error("Failed to checkpoint. The RecordProcessor instance has been shutdown", e);
    }
  }
}
