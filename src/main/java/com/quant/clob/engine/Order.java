package com.quant.clob.engine;

class Order {
    int idNumber;
    boolean buyOrSell;
    int shares;
    int limit;
    int entryTime;
    int eventTime;
    Order nextOrder;
    Order prevOrder;
    PriceLevel parentPriceLevel;

    Order() {
        
    }
}