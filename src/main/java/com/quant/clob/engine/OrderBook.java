package com.quant.clob.engine;

public final class OrderBook {
    public PriceLevel buyTree;
    public PriceLevel sellTree;
    PriceLevel ask;
    PriceLevel bid;

    public OrderBook() {
        this.buyTree = null;
        this.sellTree = null;
        this.ask = null;
        this.bid = null;
    } 
    
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Buy Tree: " + this.buyTree.toString() + "\n");
        output.append("Sell Tree: " + this.sellTree.toString() + "\n");
        return output.toString();
    }

    public void executeOrder(Order order) {

    }

    public PriceLevel addOrderToBuyTree(Order order) {
        PriceLevel priceLevel = priceLevelExists(buyTree, order.limit);
        if (priceLevel == null) {
            PriceLevel newPriceLevel = new PriceLevel();
            newPriceLevel.addOrder(order);
            addPriceLevel(buyTree, newPriceLevel);
            return newPriceLevel;
        } else {
            priceLevel.addOrder(order);
            return priceLevel;
        }
    }

    PriceLevel addOrderToSellTree(Order order) {
        PriceLevel priceLevel = priceLevelExists(sellTree, order.limit);
        if (priceLevel == null) {
            PriceLevel newPriceLevel = new PriceLevel();
            newPriceLevel.addOrder(order);
            addPriceLevel(buyTree, newPriceLevel);
            return newPriceLevel;
        } else {
            priceLevel.addOrder(order);
            return priceLevel;
        }
    }

    PriceLevel priceLevelExists(PriceLevel currentPriceLevel, int price) {
        if (currentPriceLevel == null) {
            return null;
        }

        if (price == currentPriceLevel.priceLevel) {
            return currentPriceLevel;
        } else if (price > currentPriceLevel.priceLevel) {
            return priceLevelExists(currentPriceLevel.rightChild, price);
        } else {
            return priceLevelExists(currentPriceLevel.leftChild, price);
        }
    }

    void addPriceLevel(PriceLevel rootPriceLevel, PriceLevel newPriceLevel) {
        if (rootPriceLevel == null) {
            rootPriceLevel = newPriceLevel;
        }

        if (newPriceLevel.priceLevel > rootPriceLevel.priceLevel) {
            if (rootPriceLevel.rightChild == null) {
                rootPriceLevel.rightChild = newPriceLevel;
            } else {
                addPriceLevel(rootPriceLevel.rightChild, newPriceLevel);
            }
        } else {
            if (rootPriceLevel.leftChild == null) {
                rootPriceLevel.leftChild = newPriceLevel;
            } else {
                addPriceLevel(rootPriceLevel.leftChild, newPriceLevel);
            }
        }
    }

    static void freeOrderBookObject(OrderBook orderBook) {
        PriceLevel.freePriceLevelObject(orderBook.buyTree);
        PriceLevel.freePriceLevelObject(orderBook.sellTree);
        orderBook.ask = null;
        orderBook.bid = null;
    }
}