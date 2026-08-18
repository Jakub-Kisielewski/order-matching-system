package com.quant.clob.engine;

import java.util.List;
import java.util.Map;

public final class MatchingEngine {

  Map<Integer, Order> orders;
  Map<Integer, PriceLevel> buyPriceLevels;
  Map<Integer, PriceLevel> sellPriceLevels;
  Order[] orderPool;
  int currentFreeOrderIndex = 0;
  PriceLevel[] priceLevelPool;
  int currentFreePriceLevelIndex = 0;
  
  OrderBook orderBook;

  public void init() {

  }

  public void seedOrderBook() {

  }
  
  public void cancelOrder(Order order) {
    PriceLevel priceLevel;
    if (order.isBuy) {
      priceLevel = buyPriceLevels.get(order.limit);
      priceLevel.removeOrder(order);
      if (priceLevel.isEmpty()) {
        buyPriceLevels.remove(priceLevel.priceLevel);
        OrderBook.buyTree.remove(priceLevel.priceLevel);
        freePriceLevelObject(priceLevel);
      }
    } else {
      priceLevel = sellPriceLevels.get(order.limit);
      priceLevel.removeOrder(order);
      if (priceLevel.isEmpty()) {
        sellPriceLevels.remove(priceLevel.priceLevel);
        OrderBook.sellTree.remove(priceLevel.priceLevel);
        freePriceLevelObject(priceLevel);
      }
    }

    orders.remove(order.idNumber);
    freeOrderObject(order);
  }
  
  public void addOrder(Order order) {
    PriceLevel priceLevelOrderAddedTo;
    if (order.isMarketOrder) {
      priceLevelOrderAddedTo = orderBook.executeMarketOrder(order);
    } else {
      priceLevelOrderAddedTo = orderBook.executeLimitOrder(order);
    }

    if (priceLevelOrderAddedTo == null) {
      return;
    }

    if (order.isBuy && !(buyPriceLevels.containsKey(priceLevelOrderAddedTo.priceLevel))) {
      buyPriceLevels.put(priceLevelOrderAddedTo.priceLevel, priceLevelOrderAddedTo);
    } else if (!(order.isBuy) && !(sellPriceLevels.containsKey(priceLevelOrderAddedTo.priceLevel))) {
      sellPriceLevels.put(priceLevelOrderAddedTo.priceLevel, priceLevelOrderAddedTo);
    }

    orders.put(order.idNumber, order);
  }

  public void freeOrderObject(Order order) {
    Order.freeOrderObject(order);
    if (orderPool[currentFreeOrderIndex] == null) {
      orderPool[currentFreeOrderIndex] = order;
    } else {
      orderPool[++currentFreeOrderIndex] = order;
    }
  }

  public void freePriceLevelObject(PriceLevel priceLevel) {
    PriceLevel.freePriceLevelObject(priceLevel);
    if (priceLevelPool[currentFreePriceLevelIndex] == null) {
      priceLevelPool[currentFreePriceLevelIndex] = priceLevel;
    } else {
      priceLevelPool[++currentFreePriceLevelIndex] = priceLevel;
    }
  }
}