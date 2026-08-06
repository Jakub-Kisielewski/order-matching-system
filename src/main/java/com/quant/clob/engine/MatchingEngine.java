package com.quant.clob.engine;

import java.util.Map;

public final class MatchingEngine {

  Map<Integer, Order> orders;
  Map<Integer, PriceLevel> priceLevels;
  OrderBook orderBook;

  public void init() {

  }

  public void seedOrderBook() {

  }
  
  public void removeOrder(Order order) {
    Order.freeOrderObject(order);
  }
  
  public void addOrder(Order order) {
    PriceLevel priceLevel;
    if (order.isBuy) {
      priceLevel = orderBook.addOrderToBuyTree(order);
    } else {
      priceLevel = orderBook.addOrderToBuyTree(order);
    }
    priceLevels.put(priceLevel.priceLevel, priceLevel);
    orders.put(order.idNumber, order);
  }
}