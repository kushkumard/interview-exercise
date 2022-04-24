package com.acme.mytrader.model;

import lombok.Data;

@Data
public class LimitOrder {
    private final String security;
    private final double threshold;
    private final int lot;
}
