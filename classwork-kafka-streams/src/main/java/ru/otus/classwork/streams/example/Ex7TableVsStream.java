package ru.otus.classwork.streams.example;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import ru.otus.classwork.streams.model.stock.StockTickerData;
import ru.otus.classwork.streams.serde.AppSerdes;
import ru.otus.classwork.streams.utils.StockProducer;
import ru.otus.classwork.streams.utils.Utils;

import static ru.otus.classwork.streams.utils.Utils.STOCK_TICKER_STREAM_TOPIC;
import static ru.otus.classwork.streams.utils.Utils.STOCK_TICKER_TABLE_TOPIC;

public class Ex7TableVsStream {
    public static void main(String[] args) throws Exception {
        var builder = new StreamsBuilder();

        KStream<String, StockTickerData> stockTickerStream = builder.stream(STOCK_TICKER_STREAM_TOPIC);
        KTable<String, StockTickerData> stockTickerTable = builder
                .table(STOCK_TICKER_TABLE_TOPIC, Materialized.as("ktable-store"));

        stockTickerStream
                .foreach((k, v) -> Utils.log.info("Stream: {}", v));
        stockTickerTable
                .toStream().peek((k, v) -> Utils.log.info("Table: {}", v))
                .to("stock-table-topic");

        Utils.runStockApp(builder, "ex5", new StockProducer(3, 3), b -> {
            b.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, AppSerdes.StockTicker.class);
            //b.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0); // **1
            b.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000); // **2
        });
    }
}
