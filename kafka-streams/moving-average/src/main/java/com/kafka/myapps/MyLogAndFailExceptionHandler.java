
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.ProcessorContext;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.streams.errors.*;

import java.util.Map;


/**
 * Deserialization handler that logs a deserialization exception and then
 * signals the processing pipeline to stop processing more records and fail.
 */
public class MyLogAndFailExceptionHandler implements DeserializationExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(LogAndFailExceptionHandler.class);

    @Override
    public DeserializationHandlerResponse handle(final ProcessorContext context,
                                                 final ConsumerRecord<byte[], byte[]> record,
                                                 final Exception exception) {

        log.error("Exception caught during Deserialization, " +
                  "taskId: {}, topic: {}, partition: {}, offset: {}, record: {}",
                  context.taskId(), record.topic(), record.partition(), record.offset(), new String(record.value(), StandardCharsets.UTF_8),
                  exception);

        return DeserializationHandlerResponse.FAIL;
    }

    @Override
    public void configure(final Map<String, ?> configs) {
        // ignore
    }
}