package ru.otus.classwork.spring.kafka.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.classwork.spring.kafka.engine.Producer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@Component
public class SendMessageTask {
    private final Logger logger = LoggerFactory.getLogger(SendMessageTask.class);

    private final Producer producer;

    public SendMessageTask(Producer producer) { this.producer = producer; }

    @Scheduled(fixedRateString = "1000")
    public void send() throws ExecutionException, InterruptedException {
        var completableFuture =
                this.producer.sendMessage("INPUT_DATA", "KEY", LocalDate.now().toString());

        var result = completableFuture.get();

        logger.info(String.format("Producer:\ntopic: %s\noffset: %d\npartiton: %d\nvalue size: %d",
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().offset(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().serializedValueSize()));
    }
}
