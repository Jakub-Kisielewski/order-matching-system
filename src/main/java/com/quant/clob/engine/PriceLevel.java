package com.quant.clob.engine;

final class PriceLevel {
    int priceLevel = 0;
    int size = 0;
    int totalVolume = 0;
    PriceLevel parent;
    PriceLevel leftChild;
    PriceLevel rightChild;
    Order headOrder; // oldest order
    Order tailOrder; // newest order

    void addOrder(Order order) {
        order.parentPriceLevel = this;
        if (headOrder == null && tailOrder == null) {
            headOrder.nextOrder = tailOrder;
            headOrder.prevOrder = null;
            headOrder = order;
            tailOrder.prevOrder = headOrder;
            tailOrder.nextOrder = null;
            tailOrder = order;
            size++;
            totalVolume += order.shares;
            priceLevel = order.limit;
        } else {
            order.prevOrder = tailOrder;
            tailOrder.nextOrder = order;
            tailOrder = order;
            size++;
            totalVolume += order.shares;
        }
    }

    void cancelOrder(Order order) {
        order.prevOrder.nextOrder = order.nextOrder;
        order.nextOrder.prevOrder = order.prevOrder;
        order.nextOrder = null;
        order.prevOrder = null;
        size--;
        totalVolume -= order.shares;
    }

    void executeOrder(Order order) {

    }

}