package com.quant.clob.engine;

import java.util.Collections;
import java.util.TreeMap;

public final class OrderBook {
    static TreeMap<Integer, PriceLevel> buyTree = new TreeMap<>(Collections.reverseOrder()); // descending for highest bid
    static TreeMap<Integer, PriceLevel> sellTree = new TreeMap<>(); // ascending for lowest ask 
    
    PriceLevel getBestBid() {
        return buyTree.isEmpty() ? null : buyTree.firstEntry().getValue();
    }

    PriceLevel getBestAsk() {
        return sellTree.isEmpty() ? null : sellTree.firstEntry().getValue();
    }

    public OrderBook() {
    } 
    
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Buy Tree: " + this.buyTree.toString() + "\n");
        output.append("Sell Tree: " + this.sellTree.toString() + "\n");
        return output.toString();
    }

    // only for market orders
    public void executeOrder(Order order) {
        if (order.isBuy) {
            while (order.shares > 0) {
                if (getBestAsk() == null) {
                    addOrderToBuyTree(order);
                    break;
                }

                getBestAsk().fillOrder(order);
                if (order.shares > 0) {
                }

            }
            // start filling order from lowestSell, then walk UP sell tree for worse prices
        } else {
            // start filling order from bestBuy, then walk down buy tree for worse prices
        }

    }

    PriceLevel addOrderToBuyTree(Order order) {
        PriceLevel level = buyTree.computeIfAbsent(order.limit, price -> {
            PriceLevel newLevel = new PriceLevel();
            newLevel.priceLevel = price;
            return newLevel;
        });
        
        level.addOrder(order);
        return level;
    }

    PriceLevel addOrderToSellTree(Order order) {
        PriceLevel level = sellTree.computeIfAbsent(order.limit, price -> {
            PriceLevel newLevel = new PriceLevel(); 
            newLevel.priceLevel = price;
            return newLevel;
        });
        
        level.addOrder(order);
        return level;
    }

    static void freeOrderBookObject(OrderBook orderBook) {
        buyTree = null;
        sellTree = null;
    }
}