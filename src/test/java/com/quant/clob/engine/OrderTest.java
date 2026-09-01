package com.quant.clob.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OrderTest {

  @Test
  void freeingOrderObjectSuccess() {
    Order order = new Order();
    Order.freeOrderObject(order);
    assertEquals(true, order.isBuy);
  }

}