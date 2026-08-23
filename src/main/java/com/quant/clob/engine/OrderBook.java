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
    // converts unfulfilled market order to limit order
    PriceLevel executeMarketOrder(Order order) {
        if (order.isBuy) {
            while (order.shares > 0) {
                PriceLevel currentBestAsk = getBestAsk();
                if (currentBestAsk == null) {
                    return null;
                }

                currentBestAsk.fillOrder(order);

                if (currentBestAsk.isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(currentBestAsk.priceLevel);
                    MatchingEngine.freePriceLevelObject(removedPriceLevel);
                }
            }
        } else {
            while (order.shares > 0) {
                PriceLevel currentBestBid = getBestBid();
                if (currentBestBid == null) {
                    return null;
                }

                currentBestBid.fillOrder(order);

                if (currentBestBid.isEmpty()) {
                    PriceLevel removedPriceLevel = buyTree.remove(currentBestBid.priceLevel);
                    MatchingEngine.freePriceLevelObject(removedPriceLevel);
                }
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
                PriceLevel currentBestAsk = getBestAsk();

                currentBestAsk.fillOrder(order);

                if (currentBestAsk.isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(currentBestAsk.priceLevel);
                    MatchingEngine.freePriceLevelObject(removedPriceLevel);
                }

                if (order.shares == 0) {
                    return null;
                }
                
                if (sellTree.isEmpty()) {
                    break;
                }
            }

            return addOrderToBuyTree(order);
        } else {
            if (getBestBid() == null) {
                return addOrderToSellTree(order);
            }

            while (order.limit <= getBestBid().priceLevel) {
                PriceLevel currentBestBid = getBestBid();

                currentBestBid.fillOrder(order);

                if (currentBestBid.isEmpty()) {
                    PriceLevel removedPriceLevel = buyTree.remove(currentBestBid.priceLevel);
                    MatchingEngine.freePriceLevelObject(removedPriceLevel);
                }

                if (order.shares == 0) {
                    return null;
                }

                if (buyTree.isEmpty()) {
                    break;
                }
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
        buyTree.clear();
        sellTree.clear();
    }
}