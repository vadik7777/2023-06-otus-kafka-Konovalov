package ru.otus.homework.consumer;

public class Main {
    public static void main(String... args) {
        // kafka-producer-perf-test.sh --topic students --num-records 100 --record-size 10
        // --throughput -1 --producer-props bootstrap.servers=192.168.56.200:29098
        ConsumerKafka.consumerExample();
    }
}
