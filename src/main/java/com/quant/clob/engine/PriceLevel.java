package com.quant.clob.engine;

class PriceLevel {
    int priceLevel;
    int size;
    int totalVolume;
    PriceLevel parent;
    PriceLevel leftChild;
    PriceLevel rightChild;
    Order headOrder;
    Order tailOrder;

    void addOrder() {

    }

    void cancelOrder(int idNumber) {

    }

    void executeOrder(int idNumber) {

    }
}