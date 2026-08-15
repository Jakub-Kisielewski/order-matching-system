package com.quant.clob.engine;

import java.util.ArrayList;
import java.util.List;

public final class PriceLevel {
    public int priceLevel = 0;
    int size = 0;
    int totalVolume = 0;
    PriceLevel parent;
    PriceLevel leftChild;
    PriceLevel rightChild;
    Order headOrder; // oldest order
    public Order tailOrder; // newest order

    public PriceLevel() {
        this.priceLevel = 0;
        this.size = 0;
        this.totalVolume = 0;
        this.parent = null;
        this.leftChild = null;
        this.rightChild = null;
        this.headOrder = new Order(this);

        Order newTail = new Order(this);
        newTail.prevOrder = this.headOrder;
        newTail.idNumber = 1;
        this.tailOrder = newTail;

        this.headOrder.nextOrder = this.tailOrder;
    }

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

    // only for market orders currently no limit orders
    void fillOrder(Order order) {
        while (order.shares > 0) {
            if (headOrder == null) {
                addOrder(order);
                break;
            }

            if (order.shares - headOrder.shares >= 0) {
                order.shares -= headOrder.shares;
                headOrder = headOrder.nextOrder;
                Order.freeOrderObject(headOrder.prevOrder);
            } else {
                headOrder.shares -= order.shares;
                order.shares = 0;
                break;
            }
        }
    }

    static void freePriceLevelObject(PriceLevel priceLevel) {
        priceLevel.priceLevel = 0;
        priceLevel.size = 0;
        priceLevel.totalVolume = 0;
        priceLevel.parent = null;
        priceLevel.leftChild = null;
        priceLevel.rightChild = null;
        priceLevel.headOrder = null;
        priceLevel.tailOrder = null;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        List<Integer> ids = new ArrayList<>();
        ids = orderIds(ids, headOrder);
        for (Integer id : ids) {
            output.append("Order id: " + id + " , ");
        }
        return output.toString();
    }
    
    public List<Integer> orderIds(List<Integer> output, Order currentOrder) {
        if (currentOrder.nextOrder == null) {
            output.add(currentOrder.idNumber);
            return output;
        } else {
            output.add(currentOrder.idNumber);
            return orderIds(output, currentOrder.nextOrder);
        }
    }

}