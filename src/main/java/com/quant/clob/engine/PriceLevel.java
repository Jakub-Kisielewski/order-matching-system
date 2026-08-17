package com.quant.clob.engine;

import java.util.ArrayList;
import java.util.List;

final class PriceLevel {
    int priceLevel = 0;
    int size = 0;
    int totalVolume = 0;
    PriceLevel parent;
    PriceLevel leftChild;
    PriceLevel rightChild;
    Order headOrder; // oldest order
    Order tailOrder; // newest order

    PriceLevel() {
    }

    void addOrder(Order order) {
        order.parentPriceLevel = this;
        if (this.isEmpty()) {
            this.headOrder = order;
            this.headOrder.nextOrder = tailOrder;
        } else {
            if (this.size == 1) {
                this.tailOrder = order;
                this.tailOrder.prevOrder = headOrder;
            } else {
                this.tailOrder.nextOrder = order;
                order.prevOrder = this.tailOrder;
                this.tailOrder = order;
            }
        }
        size++;
        totalVolume += order.shares;
    }

    void removeOrder(Order order) {
        if (order == headOrder) {
            this.totalVolume -= headOrder.shares;
            headOrder = headOrder.nextOrder;
            Order.freeOrderObject(headOrder.prevOrder);
            headOrder.prevOrder = null;
        } else {
            this.totalVolume -= order.shares;
            order.prevOrder.nextOrder = order.nextOrder;
            order.nextOrder.prevOrder = order.prevOrder;
            Order.freeOrderObject(order);
        }
        this.size--;
    }

    void fillOrder(Order order) {
        while (order.shares > 0) {
            if (headOrder == null) {
                break;
            }

            if (order.shares - headOrder.shares >= 0) {
                order.shares -= headOrder.shares;
                removeOrder(headOrder);
            } else {
                headOrder.shares -= order.shares;
                this.totalVolume -= order.shares;
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
    
    List<Integer> orderIds(List<Integer> output, Order currentOrder) {
        if (currentOrder.nextOrder == null) {
            output.add(currentOrder.idNumber);
            return output;
        } else {
            output.add(currentOrder.idNumber);
            return orderIds(output, currentOrder.nextOrder);
        }
    }

    boolean isEmpty() {
        return this.size == 0;
    }

}