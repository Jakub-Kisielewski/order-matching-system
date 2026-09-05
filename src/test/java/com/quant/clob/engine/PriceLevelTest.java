package com.quant.clob.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PriceLevelTest {

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
    assertEquals(order, priceLevel.headOrder);
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
}