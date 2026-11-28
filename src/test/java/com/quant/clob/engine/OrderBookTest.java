package com.quant.clob.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class OrderBookTest {

  private OrderBook orderBook;
  private Map<Integer, Order> orders;

  @BeforeEach
  void setup() {
    orders = new HashMap<>();
    orderBook = new OrderBook(new HashMap<>(), new HashMap<>(), orders, new PriceLevelPool(50));
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

  @Test
  void executeLimitOrder_SweepMultiplePriceLevels_Success() {
    Order order1 = new Order();
    order1.idNumber = 1;
    order1.price = 10;
    order1.isBuy = false;
    order1.shares = 5;
    Order order2 = new Order();
    order2.idNumber = 2;
    order2.price = 11;
    order2.isBuy = false;
    order2.shares = 5;
    Order order3 = new Order();
    order3.idNumber = 3;
    order3.price = 12;
    order3.isBuy = false;
    order3.shares = 5;
    orderBook.executeOrder(order1);
    orderBook.executeOrder(order2);
    orderBook.executeOrder(order3);
    Order orderToFill = new Order();
    orderToFill.idNumber = 4;
    orderToFill.price = 11;
    orderToFill.isBuy = true;
    orderToFill.shares = 15;
    orderToFill.isMarketOrder = false;
    orderBook.executeOrder(orderToFill);
    assertEquals(1, orderBook.sellTree.size());
    assertEquals(order3, orderBook.sellTree.get(12).headOrder);
    assertEquals(1, orderBook.buyTree.size());
    assertEquals(orderToFill, orderBook.buyTree.get(11).headOrder);
    assertEquals(5, orderToFill.shares);
    assertEquals(5, orderBook.buyTree.get(11).totalVolume);
    assertEquals(2, orders.size());
    assertEquals(order3, orders.get(3));
    assertEquals(orderToFill, orders.get(4));
    assertEquals(0, order1.price);
    assertEquals(0, order2.price);
  }

  @Test
  void cancelOrder_Success() {
    Order order = new Order();
    order.idNumber = 1;
    order.price = 10;
    order.isBuy = true;
    order.shares = 10;
    orderBook.executeOrder(order);
    orderBook.cancelOrder(order);
    assertEquals(0, orderBook.buyTree.size());
    assertEquals(0, orders.size());
    assertEquals(0, order.price);
  }

  @Test
  void cancelOrder_CancelHeadOrder_Success() {
    Order order1 = new Order();
    order1.idNumber = 1;
    order1.price = 10;
    order1.isBuy = false;
    order1.shares = 5;
    Order order2 = new Order();
    order2.idNumber = 2;
    order2.price = 10;
    order2.isBuy = false;
    order2.shares = 5;
    Order order3 = new Order();
    order3.idNumber = 3;
    order3.price = 10;
    order3.isBuy = false;
    order3.shares = 5;
    orderBook.executeOrder(order1);
    orderBook.executeOrder(order2);
    orderBook.executeOrder(order3);
    orderBook.cancelOrder(order1);
    PriceLevel priceLevel = orderBook.sellTree.get(10);
    assertEquals(2, priceLevel.size);
    assertEquals(order2, priceLevel.headOrder);
    assertEquals(order3, priceLevel.tailOrder);
    assertEquals(10, priceLevel.totalVolume);
    assertEquals(2, orders.size());
    assertEquals(null, orders.get(1));
  }

  @Test
  void cancelOrder_CancelMiddleOrder_Success() {
    Order order1 = new Order();
    order1.idNumber = 1;
    order1.price = 10;
    order1.isBuy = false;
    order1.shares = 5;
    Order order2 = new Order();
    order2.idNumber = 2;
    order2.price = 10;
    order2.isBuy = false;
    order2.shares = 5;
    Order order3 = new Order();
    order3.idNumber = 3;
    order3.price = 10;
    order3.isBuy = false;
    order3.shares = 5;
    orderBook.executeOrder(order1);
    orderBook.executeOrder(order2);
    orderBook.executeOrder(order3);
    orderBook.cancelOrder(order2);
    PriceLevel priceLevel = orderBook.sellTree.get(10);
    assertEquals(2, priceLevel.size);
    assertEquals(order1, priceLevel.headOrder);
    assertEquals(order3, priceLevel.tailOrder);
    assertEquals(order3, order1.nextOrder);
    assertEquals(order1, order3.prevOrder);
    assertEquals(10, priceLevel.totalVolume);
    assertEquals(2, orders.size());
    assertEquals(null, orders.get(2));
  }

  @Test
  void cancelOrder_CancelTailOrder_Success() {
    Order order1 = new Order();
    order1.idNumber = 1;
    order1.price = 10;
    order1.isBuy = false;
    order1.shares = 5;
    Order order2 = new Order();
    order2.idNumber = 2;
    order2.price = 10;
    order2.isBuy = false;
    order2.shares = 5;
    Order order3 = new Order();
    order3.idNumber = 3;
    order3.price = 10;
    order3.isBuy = false;
    order3.shares = 5;
    orderBook.executeOrder(order1);
    orderBook.executeOrder(order2);
    orderBook.executeOrder(order3);
    orderBook.cancelOrder(order3);
    PriceLevel priceLevel = orderBook.sellTree.get(10);
    assertEquals(2, priceLevel.size);
    assertEquals(order1, priceLevel.headOrder);
    assertEquals(order2, priceLevel.tailOrder);
    assertEquals(null, order2.nextOrder);
    assertEquals(10, priceLevel.totalVolume);
    assertEquals(2, orders.size());
    assertEquals(null, orders.get(3));
  }

  @Test
  void executeOrder_FilledOrdersReturnedToPool_Success() {
    OrderPool orderPool = new OrderPool(50);
    int idNumber = 1;
    for (int i = 0; i < 5000; i++) {
      for (int j = 0; j < 3; j++) {
        Order restingOrder = orderPool.getNewOrder();
        restingOrder.idNumber = idNumber++;
        restingOrder.price = 10;
        restingOrder.isBuy = false;
        restingOrder.shares = 5;
        orderBook.executeOrder(restingOrder);
      }
      Order orderToFill = orderPool.getNewOrder();
      orderToFill.idNumber = idNumber++;
      orderToFill.price = 10;
      orderToFill.isBuy = true;
      orderToFill.shares = 15;
      orderToFill.isMarketOrder = false;
      orderBook.executeOrder(orderToFill);
    }
    assertEquals(0, orderBook.sellTree.size());
    assertEquals(0, orderBook.buyTree.size());
    assertEquals(0, orders.size());
  }

}