package ru.otus.classwork.streams.serde;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serializer;
import ru.otus.classwork.streams.model.stock.FixedSizePriorityQueue;

import java.nio.charset.StandardCharsets;

public class JsonSerializer<T> implements Serializer<T> {

    private final Gson gson;

    public JsonSerializer() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(FixedSizePriorityQueue.class, new FixedSizePriorityQueueAdapter().nullSafe());
        gson = builder.create();
    }

    @Override
    public byte[] serialize(String topic, T t) {
        return gson.toJson(t).getBytes(StandardCharsets.UTF_8);
    }
}
