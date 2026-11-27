package com.quant.clob.engine;

final class OrderPool {
  private final Order[] pool; 

  public OrderPool(int size) {
    pool = new Order[size];

    for (int i = 0; i < pool.length; i++) {
      pool[i] = new Order();
    }

  }

  public final Order getNewOrder() {
    for (int i = 0; i < pool.length; i++) {
      if (pool[i].price == 0) {
        return pool[i];
      }
    }
    throw new NullPointerException("Unavailable order object");
  }
  
}
