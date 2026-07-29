package com.quant.clob.engine;

final class PriceLevel {
    int priceLevel;
    int size;
    int totalVolume;
    PriceLevel parent;
    PriceLevel leftChild;
    PriceLevel rightChild;
    Order headOrder; // oldest order
    Order tailOrder; // newest order

    void addOrder(Order order) {
        order.parentPriceLevel = this;
        if (headOrder == null && tailOrder == null) {
            headOrder.nextOrder = tailOrder;
            headOrder = order;
            tailOrder.prevOrder = headOrder;
            tailOrder = order;
        } else {
            order.prevOrder = tailOrder;
            tailOrder.nextOrder = order;
            tailOrder = order;
        }
    }

    void cancelOrder(int idNumber) {

    }

    void executeOrder(int idNumber) {

    }

}