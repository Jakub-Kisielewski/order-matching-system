package com.quant.clob.engine;

import java.util.List;
import java.util.Map;

public final class MatchingEngine {

  Map<Integer, Order> orders;
  Map<Integer, PriceLevel> buyPriceLevels;
  Map<Integer, PriceLevel> sellPriceLevels;
  List<Order> orderPool;
  List<PriceLevel> priceLevelPool;
  
  OrderBook orderBook;

  public void init() {

  }

  public void seedOrderBook() {

  }
  
  public void cancelOrder(Order order) {
    if (order.isBuy) {
      buyPriceLevels.get(order.limit).removeOrder(order);;
    } else {
      sellPriceLevels.get(order.limit).removeOrder(order);
    }

    orders.remove(order.idNumber);
    Order.freeOrderObject(order);
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
    orderPool.add(order);
  }

  public void freePriceLevelObject(PriceLevel priceLevel) {
    PriceLevel.freePriceLevelObject(priceLevel);
    priceLevelPool.add(priceLevel);
  }
}