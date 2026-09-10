package com.quant.clob.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class OrderBookTest {

  @Test
  void addOrderToBuyTree_Success() {
    OrderBook orderBook = new OrderBook();
    Order order = new Order();
    order.limit = 10;
    order.isBuy = true;
    PriceLevel addedToPriceLevel = orderBook.addOrderToBuyTree(order);
    assertEquals(order.limit, addedToPriceLevel.priceLevel);
    assertEquals(1, OrderBook.buyTree.size());
    assertEquals(1, addedToPriceLevel.size);
  }
  
  @Test
  void addOrderToBuyTree_MultiOrder_Success() {
    OrderBook orderBook = new OrderBook();
    Order order = new Order();
    order.limit = 10;
    order.isBuy = true;
    Order order2 = new Order();
    order2.limit = 10;
    order.isBuy = true;
    orderBook.addOrderToBuyTree(order);
    PriceLevel addedToPriceLevel = orderBook.addOrderToBuyTree(order2);
    assertEquals(order2.limit, addedToPriceLevel.priceLevel);
    assertEquals(1, OrderBook.buyTree.size());
    assertEquals(2, addedToPriceLevel.size);
  }

  @Test
  void addOrderToSellTree_Success() {
    OrderBook orderBook = new OrderBook();
    Order order = new Order();
    order.limit = 10;
    order.isBuy = false;
    PriceLevel addedToPriceLevel = orderBook.addOrderToSellTree(order);
    assertEquals(order.limit, addedToPriceLevel.priceLevel);
    assertEquals(1, OrderBook.sellTree.size());
    assertEquals(1, addedToPriceLevel.size);
  }

  @Test
  void addOrderToSellTree_MultiOrder_Success() {
    OrderBook orderBook = new OrderBook();
    Order order = new Order();
    order.limit = 10;
    order.isBuy = false;
    Order order2 = new Order();
    order2.limit = 10;
    order.isBuy = false;
    orderBook.addOrderToSellTree(order);
    PriceLevel addedToPriceLevel = orderBook.addOrderToSellTree(order2);
    assertEquals(order2.limit, addedToPriceLevel.priceLevel);
    assertEquals(1, OrderBook.sellTree.size());
    assertEquals(2, addedToPriceLevel.size);
  }

  @Test 
  void executeMarketOrder_Success() {
    MatchingEngine matchingEngine = new MatchingEngine(); // to satisfy test
    OrderBook orderBook = new OrderBook();
    Order order = new Order();
    order.limit = 10;
    order.isBuy = false;
    order.shares = 10;
    orderBook.addOrderToSellTree(order);
    Order orderToFill = new Order();
    orderToFill.limit = 10;
    orderToFill.isBuy = true;
    orderToFill.shares = 10;
    orderToFill.isMarketOrder = true;
    orderBook.executeMarketOrder(orderToFill);
    assertEquals(0, OrderBook.sellTree.size());
    assertEquals(0, orderToFill.shares);
  }

  @Test 
  void executeLimitOrder_Success() {
    MatchingEngine matchingEngine = new MatchingEngine();
    OrderBook orderBook = new OrderBook();
    Order order = new Order();
    order.limit = 10;
    order.isBuy = false;
    order.shares = 10;
    orderBook.addOrderToSellTree(order);
    Order orderToFill = new Order();
    orderToFill.limit = 10;
    orderToFill.isBuy = true;
    orderToFill.shares = 10;
    orderToFill.isMarketOrder = false;
    orderBook.executeLimitOrder(orderToFill);
    assertEquals(0, OrderBook.sellTree.size());
    assertEquals(0, orderToFill.shares);
  }

  @Test 
  void executeLimitOrder_Failure() {
    MatchingEngine matchingEngine = new MatchingEngine();
    OrderBook orderBook = new OrderBook();
    Order order = new Order();
    order.limit = 10;
    order.isBuy = false;
    order.shares = 10;
    orderBook.addOrderToSellTree(order);
    Order orderToFill = new Order();
    orderToFill.limit = 5;
    orderToFill.isBuy = true;
    orderToFill.shares = 10;
    orderToFill.isMarketOrder = false;
    orderBook.executeLimitOrder(orderToFill);
    assertEquals(1, OrderBook.sellTree.size());
    assertEquals(1, OrderBook.buyTree.size());
    assertEquals(10, orderToFill.shares);
  }

}