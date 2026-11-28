package com.quant.clob.engine;

public final class MatchingEngine {

  final OrderBook orderBook;
  final OrderPool orderPool;
  final PriceLevelPool priceLevelPool;
  final int assetIPOReferencePrice = 0; // change once decide what asset to trade

  public MatchingEngine(OrderBook orderBook, OrderPool orderPool, PriceLevelPool priceLevelPool) {
    this.orderBook = orderBook;
    this.orderPool = orderPool;
    this.priceLevelPool = priceLevelPool;
  }

  public void executeOrder(Order order) {
    if (order.shares <= 0 || order.price <= 0 || order == null) {
      throw new NullPointerException("Invalid input order with id: " + order.idNumber);
    }
    orderBook.executeOrder(order);
  }

  public void cancelOrder(Order order) {
    orderBook.cancelOrder(order);
  }

}