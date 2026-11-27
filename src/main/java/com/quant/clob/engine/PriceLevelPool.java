package com.quant.clob.engine;

final class PriceLevelPool {
  private final PriceLevel[] pool; 

  public PriceLevelPool(int size) {
    pool = new PriceLevel[size];

    for (int i = 0; i < pool.length; i++) {
      pool[i] = new PriceLevel();
    }

  }

  public final PriceLevel getNewPriceLevel() {
    for (int i = 0; i < pool.length; i++) {
      if (pool[i].priceLevel == 0) {
        return pool[i];
      }
    }
    throw new NullPointerException("Unavailable priceLevel object");
  }
  
}

