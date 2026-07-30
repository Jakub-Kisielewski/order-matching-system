package com.quant.clob.engine;

final class OrderBook {
    PriceLevel buyTree;
    PriceLevel sellTree;
    PriceLevel ask;
    PriceLevel bid;

    void addOrderToBuyTree(Order order) {
        PriceLevel priceLevel = priceLevelExists(buyTree, order.limit);
        if (priceLevel == null) {
            PriceLevel newPriceLevel = new PriceLevel();
            newPriceLevel.addOrder(order);
        } else {
            priceLevel.addOrder(order);
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
}