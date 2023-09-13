package ru.otus.classwork.streams.example;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import ru.otus.classwork.streams.model.stock.FixedSizePriorityQueue;
import ru.otus.classwork.streams.model.stock.ShareVolume;
import ru.otus.classwork.streams.model.stock.StockTransaction;
import ru.otus.classwork.streams.serde.AppSerdes;
import ru.otus.classwork.streams.utils.StockTransactionProducer;
import ru.otus.classwork.streams.utils.Utils;

import java.util.Iterator;

import static ru.otus.classwork.streams.utils.Utils.STOCK_TRANSACTIONS_TOPIC;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Ex8Aggregation {
    public static void main(String[] args) throws Exception {
        var builder = new StreamsBuilder();

        Serde<String> stringSerde = Serdes.String();
        Serde<StockTransaction> stockTransactionSerde = AppSerdes.stockTransaction();
        Serde<ShareVolume> shareVolumeSerde = AppSerdes.shareVolume();
        Serde<FixedSizePriorityQueue<ShareVolume>> fixedSizePriorityQueueSerde = AppSerdes.fixedSizePriorityQueue();

        KTable<String, ShareVolume> shareVolume = builder
                // читаем исходные данные и приводим их к ShareVolume (оставляя только нужное)
                .stream(STOCK_TRANSACTIONS_TOPIC, Consumed.with(stringSerde, stockTransactionSerde))
                .mapValues(st -> ShareVolume.newBuilder(st).build())
                // группируем по тикеру и суммируем, получаем таблицу
                .groupBy((k, v) -> v.getSymbol(), Grouped.with(stringSerde, shareVolumeSerde))
                .reduce(ShareVolume::sum, Materialized.as("stock-group"));

        shareVolume
                // группируем по сфере деятельности и аггрегируем, накапливая 3
                .groupBy((k, v) -> KeyValue.pair(v.getIndustry(), v), Grouped.with(stringSerde, shareVolumeSerde))
                .aggregate(() -> new FixedSizePriorityQueue<>((sv1, sv2) -> sv2.getShares() - sv1.getShares(), 3),
                        (k, v, agg) -> agg.add(v),
                        (k, v, agg) -> agg.remove(v),
                        Utils.materialized("aggregate", stringSerde, fixedSizePriorityQueueSerde))
                // преобразуем значения в текст
                .mapValues(new QueueToString())
                // выдаем в топик
                .toStream()
                .peek((k, v) -> Utils.log.info("Stock volume by industry {} {}", k, v))
                .to("stock-volume-by-company", Produced.with(stringSerde, stringSerde));


        Utils.runStockApp(builder, "ex7",
                new StockTransactionProducer(15, 50, 25, false),
                b -> {
                    b.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
                });
    }

    private static class QueueToString implements ValueMapper<FixedSizePriorityQueue, String> {
        @Override
        public String apply(FixedSizePriorityQueue fpq) {
            StringBuilder sb = new StringBuilder();
            Iterator<ShareVolume> iterator = fpq.iterator();
            int counter = 1;
            while (iterator.hasNext()) {
                ShareVolume stockVolume = iterator.next();
                if (stockVolume != null) {
                    sb
                            .append(counter++).append(")")
                            .append(stockVolume.getSymbol()).append(":")
                            .append(stockVolume.getShares()).append(" ");
                }
            }
            return sb.toString();
        }
    }
}
