package com.quant.clob.engine;

import java.util.Map;

public final class MatchingEngine {

  Map<Integer, Order> orders;
  Map<Integer, PriceLevel> buyPriceLevels;
  Map<Integer, PriceLevel> sellPriceLevels;
  OrderBook orderBook;

  public void init() {

  }

  public void seedOrderBook() {

  }
  
  public void removeOrder(Order order) {
    if (order.isBuy) {
      buyPriceLevels.get(order.limit).size--;
      buyPriceLevels.get(order.limit).totalVolume -= order.shares;
    } else {
      sellPriceLevels.get(order.limit).size--;
      sellPriceLevels.get(order.limit).totalVolume -= order.shares;
    }

    Order.freeOrderObject(order);
  }
  
  public void addOrder(Order order) {
    PriceLevel priceLevel;
    if (order.isBuy) {
      priceLevel = buyPriceLevels.get(order.limit);

      if (priceLevel != null) {
        priceLevel.addOrder(order);
      } else {
        priceLevel = orderBook.addOrderToBuyTree(order);
        buyPriceLevels.put(priceLevel.priceLevel, priceLevel);
      }

    } else {
      priceLevel = sellPriceLevels.get(order.limit);

      if (priceLevel != null) {
        priceLevel.addOrder(order);
      } else {
        priceLevel = orderBook.addOrderToSellTree(order);
        sellPriceLevels.put(priceLevel.priceLevel, priceLevel);
      }
    }
    orders.put(order.idNumber, order);
  }
}