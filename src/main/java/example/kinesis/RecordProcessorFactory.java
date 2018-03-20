package example.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordProcessorFactory implements IRecordProcessorFactory {

  private RecordProcessor recordProcessor;

  @Autowired
  public RecordProcessorFactory(RecordProcessor recordProcessor) {
    this.recordProcessor = recordProcessor;
  }

  @Override
  public IRecordProcessor createProcessor() {
    return recordProcessor;
  }
}
