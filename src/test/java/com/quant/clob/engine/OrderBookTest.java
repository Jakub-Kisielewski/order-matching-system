package com.quant.clob.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class OrderBookTest {

  private OrderBook orderBook;

  @BeforeEach
  void setup() {
    orderBook = new OrderBook(new HashMap<>(), new HashMap<>(), new HashMap<>(), new PriceLevelPool(50));
  }

  @Test
  void addOrderToBuyTree_Success() {
    Order order = new Order();
    order.price = 10;
    order.isBuy = true;
    PriceLevel addedToPriceLevel = orderBook.addOrderToBuyTree(order);
    assertEquals(order.price, addedToPriceLevel.priceLevel);
    assertEquals(1, orderBook.buyTree.size());
    assertEquals(1, addedToPriceLevel.size);
  }
  
  @Test
  void addOrderToBuyTree_MultiOrder_Success() {
    Order order = new Order();
    order.price = 10;
    order.isBuy = true;
    Order order2 = new Order();
    order2.price = 10;
    order2.isBuy = true;
    orderBook.addOrderToBuyTree(order);
    PriceLevel addedToPriceLevel = orderBook.addOrderToBuyTree(order2);
    assertEquals(order2.price, addedToPriceLevel.priceLevel);
    assertEquals(1, orderBook.buyTree.size());
    assertEquals(2, addedToPriceLevel.size);
  }

  @Test
  void addOrderToSellTree_Success() {
    Order order = new Order();
    order.price = 10;
    order.isBuy = false;
    PriceLevel addedToPriceLevel = orderBook.addOrderToSellTree(order);
    assertEquals(order.price, addedToPriceLevel.priceLevel);
    assertEquals(1, orderBook.sellTree.size());
    assertEquals(1, addedToPriceLevel.size);
  }

  @Test
  void addOrderToSellTree_MultiOrder_Success() {
    Order order = new Order();
    order.price = 10;
    order.isBuy = false;
    Order order2 = new Order();
    order2.price = 10;
    order2.isBuy = false;
    orderBook.addOrderToSellTree(order);
    PriceLevel addedToPriceLevel = orderBook.addOrderToSellTree(order2);
    assertEquals(order2.price, addedToPriceLevel.priceLevel);
    assertEquals(1, orderBook.sellTree.size());
    assertEquals(2, addedToPriceLevel.size);
  }

  @Test 
  void executeMarketOrder_Success() {
    Order order = new Order();
    order.price = 10;
    order.isBuy = false;
    order.shares = 10;
    orderBook.addOrderToSellTree(order);
    Order orderToFill = new Order();
    orderToFill.price = 10;
    orderToFill.isBuy = true;
    orderToFill.shares = 10;
    orderToFill.isMarketOrder = true;
    orderBook.executeOrder(orderToFill);
    assertEquals(0, orderBook.sellTree.size());
    assertEquals(0, orderToFill.shares);
  }

  @Test 
  void executeLimitOrder_Success() {
    Order order = new Order();
    order.price = 10;
    order.isBuy = false;
    order.shares = 10;
    orderBook.addOrderToSellTree(order);
    Order orderToFill = new Order();
    orderToFill.price = 10;
    orderToFill.isBuy = true;
    orderToFill.shares = 10;
    orderToFill.isMarketOrder = false;
    orderBook.executeOrder(orderToFill);
    assertEquals(0, orderBook.sellTree.size());
    assertEquals(0, orderToFill.shares);
  }

  @Test 
  void executeLimitOrder_Failure() {
    Order order = new Order();
    order.price = 10;
    order.isBuy = false;
    order.shares = 10;
    orderBook.addOrderToSellTree(order);
    Order orderToFill = new Order();
    orderToFill.price = 5;
    orderToFill.isBuy = true;
    orderToFill.shares = 10;
    orderToFill.isMarketOrder = false;
    orderBook.executeOrder(orderToFill);
    assertEquals(1, orderBook.sellTree.size());
    assertEquals(1, orderBook.buyTree.size());
    assertEquals(10, orderToFill.shares);
  }

}