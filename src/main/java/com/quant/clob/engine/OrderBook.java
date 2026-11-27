package com.quant.clob.engine;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

final class OrderBook {
    final TreeMap<Integer, PriceLevel> buyTree; // descending for highest bid
    final TreeMap<Integer, PriceLevel> sellTree; // ascending for lowest ask 
    private final Map<Integer, PriceLevel> buyPriceLevels;
    private final Map<Integer, PriceLevel> sellPriceLevels;
    private final Map<Integer, Order> orders;

    private final PriceLevelPool priceLevelPool;

    public OrderBook(Map<Integer, PriceLevel> buyPriceLevels, Map<Integer, PriceLevel> sellPriceLevels, Map<Integer, Order> orders, PriceLevelPool priceLevelPool) {
        buyTree = new TreeMap<>(Collections.reverseOrder());
        sellTree = new TreeMap<>();
        this.buyPriceLevels = buyPriceLevels;
        this.sellPriceLevels = sellPriceLevels;
        this.orders = orders;
        this.priceLevelPool = priceLevelPool;
    } 
    
    void executeOrder(Order order) {
        PriceLevel priceLevelOrderAddedTo;
        if (order.isMarketOrder) {
        priceLevelOrderAddedTo = executeMarketOrder(order);
        } else {
        priceLevelOrderAddedTo = executeLimitOrder(order);
        }

        if (priceLevelOrderAddedTo == null) {
            freeOrderObject(order);
            return;
        }

        if (order.isBuy && !(buyPriceLevels.containsKey(priceLevelOrderAddedTo.priceLevel))) {
            buyPriceLevels.put(priceLevelOrderAddedTo.priceLevel, priceLevelOrderAddedTo);
        } else if (!(order.isBuy) && !(sellPriceLevels.containsKey(priceLevelOrderAddedTo.priceLevel))) {
            sellPriceLevels.put(priceLevelOrderAddedTo.priceLevel, priceLevelOrderAddedTo);
        }

        orders.put(order.idNumber, order);
    }

    // returns price level order added to
    // removes unfulfilled market orders 
    private PriceLevel executeMarketOrder(Order order) {
        if (order.isBuy) {
            while (order.shares > 0) {
                PriceLevel currentBestAsk = getBestAsk();
                if (currentBestAsk == null) {
                    return null;
                }

                currentBestAsk.fillOrder(order);

                if (currentBestAsk.isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(currentBestAsk.priceLevel);
                    freePriceLevelObject(removedPriceLevel); // think about removing this from here
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
                    freePriceLevelObject(removedPriceLevel);
                }
            }
        }
        return null;
    }

    private PriceLevel executeLimitOrder(Order order) {
        if (order.isBuy) {
            if (getBestAsk() == null) {
                return addOrderToBuyTree(order);
            }

            while (order.price >= getBestAsk().priceLevel) {
                PriceLevel currentBestAsk = getBestAsk();

                currentBestAsk.fillOrder(order);

                if (currentBestAsk.isEmpty()) {
                    PriceLevel removedPriceLevel = sellTree.remove(currentBestAsk.priceLevel);
                    freePriceLevelObject(removedPriceLevel);
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

            while (order.price <= getBestBid().priceLevel) {
                PriceLevel currentBestBid = getBestBid();

                currentBestBid.fillOrder(order);

                if (currentBestBid.isEmpty()) {
                    PriceLevel removedPriceLevel = buyTree.remove(currentBestBid.priceLevel);
                    freePriceLevelObject(removedPriceLevel);
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

    void cancelOrder(Order order) {
        PriceLevel priceLevel;
        if (order.isBuy) {
            priceLevel = buyPriceLevels.get(order.price);
            priceLevel.removeOrder(order);
            if (priceLevel.isEmpty()) {
                buyTree.remove(priceLevel.priceLevel);
                freePriceLevelObject(priceLevel);
            }
        } else {
            priceLevel = sellPriceLevels.get(order.price);
            priceLevel.removeOrder(order);
            if (priceLevel.isEmpty()) {
                sellTree.remove(priceLevel.priceLevel);
                freePriceLevelObject(priceLevel);
            }
        }

        freeOrderObject(order);
    }

    // helpers
    PriceLevel addOrderToBuyTree(Order order) {
        PriceLevel priceLevel = buyTree.computeIfAbsent(order.price, price -> {
            PriceLevel newPriceLevel = priceLevelPool.getNewPriceLevel();
            newPriceLevel.priceLevel = price;
            return newPriceLevel;
        });
        
        priceLevel.addOrder(order);
        return priceLevel;
    }

    PriceLevel addOrderToSellTree(Order order) {
        PriceLevel priceLevel = sellTree.computeIfAbsent(order.price, price -> {
            PriceLevel newPriceLevel = priceLevelPool.getNewPriceLevel(); 
            newPriceLevel.priceLevel = price;
            return newPriceLevel;
        });
        
        priceLevel.addOrder(order);
        return priceLevel;
    }

    final void freeOrderBookObject(OrderBook orderBook) {
        orderBook.buyTree.clear();
        orderBook.sellTree.clear();
    }

    private final void freePriceLevelObject(PriceLevel priceLevel) {
        if (buyPriceLevels.containsKey(priceLevel.priceLevel)) {
            buyPriceLevels.remove(priceLevel.priceLevel);
        } else {
            sellPriceLevels.remove(priceLevel.priceLevel);
        }

        PriceLevel.freePriceLevelObject(priceLevel);
    }

    private final void freeOrderObject(Order order) {
        orders.remove(order.idNumber);
        Order.freeOrderObject(order);
    }
    
    private PriceLevel getBestBid() {
        return buyTree.isEmpty() ? null : buyTree.firstEntry().getValue();
    }

    private PriceLevel getBestAsk() {
        return sellTree.isEmpty() ? null : sellTree.firstEntry().getValue();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Buy Tree: " + buyTree.toString() + "\n");
        output.append("Sell Tree: " + sellTree.toString() + "\n");
        return output.toString();
    }
}
