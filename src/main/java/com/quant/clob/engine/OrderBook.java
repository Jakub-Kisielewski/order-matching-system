package com.quant.clob.engine;

import java.util.Collections;
import java.util.TreeMap;

final class OrderBook {
    static TreeMap<Integer, PriceLevel> buyTree = new TreeMap<>(Collections.reverseOrder()); // descending for highest bid
    static TreeMap<Integer, PriceLevel> sellTree = new TreeMap<>(); // ascending for lowest ask 
    
    PriceLevel getBestBid() {
        return buyTree.isEmpty() ? null : buyTree.firstEntry().getValue();
    }

    PriceLevel getBestAsk() {
        return sellTree.isEmpty() ? null : sellTree.firstEntry().getValue();
    }

    OrderBook() {
    } 
    
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Buy Tree: " + OrderBook.buyTree.toString() + "\n");
        output.append("Sell Tree: " + OrderBook.sellTree.toString() + "\n");
        return output.toString();
    }

    // returns price level order added to
    PriceLevel executeMarketOrder(Order order) {
        if (order.isBuy) {
            while (order.shares > 0) {
                if (getBestAsk() == null) {
                    return addOrderToBuyTree(order);
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
                    return addOrderToSellTree(order);
                }

                if (getBestBid().isEmpty()) {
                    PriceLevel removedPriceLevel = buyTree.remove(getBestBid().priceLevel);
                    PriceLevel.freePriceLevelObject(removedPriceLevel);
                }

                getBestBid().fillOrder(order);
            }
        }
        return null;
    }

    PriceLevel executeLimitOrder(Order order) {
        if (order.isBuy) {
            if (getBestAsk() == null) {
                return addOrderToBuyTree(order);
            }

            while (order.limit >= getBestAsk().priceLevel) {

                if (getBestAsk().isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(getBestAsk().priceLevel);
                    PriceLevel.freePriceLevelObject(removedPriceLevel);
                }

                if (order.shares == 0) {
                    return null;
                }

                getBestAsk().fillOrder(order);
            }

            return addOrderToBuyTree(order);
        } else {
            if (getBestBid() == null) {
                return addOrderToSellTree(order);
            }

            while (order.limit >= getBestBid().priceLevel) {

                if (getBestBid().isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(getBestBid().priceLevel);
                    PriceLevel.freePriceLevelObject(removedPriceLevel);
                }

                if (order.shares == 0) {
                    return null;
                }

                getBestBid().fillOrder(order);
            }

            return addOrderToSellTree(order);
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