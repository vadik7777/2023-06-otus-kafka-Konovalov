package ru.otus.classwork.streams.model.stock;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
@AllArgsConstructor
public class StockTickerData {

    double price;
    String symbol;
}
