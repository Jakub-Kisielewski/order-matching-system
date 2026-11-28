package com.quant.clob.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class PriceLevelTest {
  
  final class TestListener implements MatchListener {
    final List<Integer> filledIds = new ArrayList<>();
    @Override 
    public void onTrade(Order resting, Order order, int quantity) {}
    @Override 
    public void onOrderFilled(Order resting) { filledIds.add(resting.idNumber); }
}

  @Test
  void addOrder_Success() {
    PriceLevel priceLevel = new PriceLevel();
    Order order = new Order();
    priceLevel.addOrder(order);
    assertEquals(1, priceLevel.size);
    assertEquals(priceLevel.headOrder, order);
    assertEquals(priceLevel.totalVolume, order.shares);
  }

  @Test
  void removeOrder_Success() {
    PriceLevel priceLevel = new PriceLevel();
    Order order = new Order();
    priceLevel.addOrder(order);
    priceLevel.removeOrder(order);
    assertEquals(0, priceLevel.size);
    assertEquals(null, priceLevel.headOrder);
    assertEquals(0, priceLevel.totalVolume);
  }

  @Test 
  void removeOrder_RemoveHeadOrder_Success() {
    PriceLevel priceLevel = new PriceLevel();
    Order order1 = new Order();
    Order order2 = new Order();
    priceLevel.addOrder(order1);
    priceLevel.addOrder(order2);
    priceLevel.removeOrder(order1);
    assertEquals(1, priceLevel.size);
    assertEquals(order2, priceLevel.headOrder);
    assertEquals(order2.shares, priceLevel.totalVolume);
  }

  @Test
  void removeOrder_RemoveTailOrder_Success() {
    PriceLevel priceLevel = new PriceLevel();
    Order order1 = new Order();
    Order order2 = new Order();
    priceLevel.addOrder(order1);
    priceLevel.addOrder(order2);
    priceLevel.removeOrder(order2);
    assertEquals(1, priceLevel.size);
    assertEquals(order1, priceLevel.headOrder);
    assertEquals(order1.shares, priceLevel.totalVolume);
  }

  @Test
  void removeOrder_RemoveMiddleOrder_Success() {
    PriceLevel priceLevel = new PriceLevel();
    Order order1 = new Order();
    Order order2 = new Order();
    Order order3 = new Order();
    priceLevel.addOrder(order1);
    priceLevel.addOrder(order2);
    priceLevel.addOrder(order3);
    priceLevel.removeOrder(order2);
    assertEquals(2, priceLevel.size);
    assertEquals(order1, priceLevel.headOrder);
    assertEquals(order3, priceLevel.tailOrder);
    assertEquals(order1.shares + order3.shares, priceLevel.totalVolume);
  }

  @Test 
  void fillOrder_Success() {
    PriceLevel priceLevel = new PriceLevel();
    Order order1 = new Order();
    order1.shares = 10;
    Order orderToFill = new Order();
    orderToFill.shares = 10;
    priceLevel.addOrder(order1);
    priceLevel.fillOrder(orderToFill, new TestListener());
    assertEquals(0, priceLevel.size);
    assertEquals(0, orderToFill.shares);
  }

  @Test 
  void fillOrder_NotFilled_Success() {
    PriceLevel priceLevel = new PriceLevel();
    Order order1 = new Order();
    order1.shares = 5;
    Order orderToFill = new Order();
    orderToFill.shares = 10;
    priceLevel.addOrder(order1);
    priceLevel.fillOrder(orderToFill, new TestListener());
    assertEquals(0, priceLevel.size);
    assertEquals(5, orderToFill.shares);
  }

  @Test
  void fillOrder_FilledMultiOrder_Success() {
    PriceLevel priceLevel = new PriceLevel();
    Order order1 = new Order();
    order1.shares = 5;
    Order order2 = new Order();
    order2.shares = 5;
    Order orderToFill = new Order();
    orderToFill.shares = 10;
    priceLevel.addOrder(order1);
    priceLevel.addOrder(order2);
    priceLevel.fillOrder(orderToFill, new TestListener());
    assertEquals(0, priceLevel.size);
    assertEquals(0, orderToFill.shares);
  }
}