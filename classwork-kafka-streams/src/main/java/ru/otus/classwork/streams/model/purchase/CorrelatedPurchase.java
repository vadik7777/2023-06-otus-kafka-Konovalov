package ru.otus.classwork.streams.model.purchase;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Builder(toBuilder = true)
@Value
public class CorrelatedPurchase {

    String customerId;
    @Singular("itemPurchased")
    List<String> itemsPurchased;
    double totalAmount;
    Date firstPurchaseTime;
    Date secondPurchaseTime;
}
