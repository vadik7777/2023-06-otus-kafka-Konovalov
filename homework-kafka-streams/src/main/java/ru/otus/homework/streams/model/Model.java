package ru.otus.homework.streams.model;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder(toBuilder = true)
@ToString
@Value
public class Model {
    Integer key;
    Integer value;
}
