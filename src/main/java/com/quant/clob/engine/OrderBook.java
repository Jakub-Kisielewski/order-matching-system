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
        output.append("Buy Tree: " + OrderBook.buyTree.toString() + "\n");
        output.append("Sell Tree: " + OrderBook.sellTree.toString() + "\n");
        return output.toString();
    }

    public void executeMarketOrder(Order order) {
        if (order.isBuy) {
            while (order.shares > 0) {
                if (getBestAsk() == null) {
                    addOrderToBuyTree(order);
                    break;
                }

                if (getBestAsk().isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(getBestAsk().priceLevel);
                    PriceLevel.freePriceLevelObject(removedPriceLevel);
                }

                getBestAsk().fillOrder(order);
            }
        } else {
            while (order.shares > 0) {
                if (getBestBid() == null) {
                    addOrderToSellTree(order);
                    break;
                }

                if (getBestBid().isEmpty()) {
                    PriceLevel removedPriceLevel = buyTree.remove(getBestBid().priceLevel);
                    PriceLevel.freePriceLevelObject(removedPriceLevel);
                }

                getBestBid().fillOrder(order);
            }
        }
    }

    void executeLimitOrder(Order order) {
        if (order.isBuy) {
            if (getBestAsk() == null) {
                addOrderToBuyTree(order);
            }

            while (order.limit >= getBestAsk().priceLevel) {

                if (getBestAsk().isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(getBestAsk().priceLevel);
                    PriceLevel.freePriceLevelObject(removedPriceLevel);
                }

                if (order.shares == 0) {
                    return;
                }

                getBestAsk().fillOrder(order);
            }

            addOrderToBuyTree(order);
        } else {
            if (getBestBid() == null) {
                addOrderToSellTree(order);
            }

            while (order.limit >= getBestBid().priceLevel) {

                if (getBestBid().isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(getBestBid().priceLevel);
                    PriceLevel.freePriceLevelObject(removedPriceLevel);
                }

                if (order.shares == 0) {
                    return;
                }

                getBestBid().fillOrder(order);
            }

            addOrderToSellTree(order);
        }
    }

    PriceLevel addOrderToBuyTree(Order order) {
        PriceLevel priceLevel = buyTree.computeIfAbsent(order.limit, price -> {
            PriceLevel newPriceLevel = new PriceLevel();
            newPriceLevel.priceLevel = price;
            return newPriceLevel;
        });
        
        priceLevel.addOrder(order);
        return priceLevel;
    }

    PriceLevel addOrderToSellTree(Order order) {
        PriceLevel priceLevel = sellTree.computeIfAbsent(order.limit, price -> {
            PriceLevel newLevel = new PriceLevel(); 
            newLevel.priceLevel = price;
            return newLevel;
        });
        
        priceLevel.addOrder(order);
        return priceLevel;
    }

    static void freeOrderBookObject(OrderBook orderBook) {
        buyTree = null;
        sellTree = null;
    }
}