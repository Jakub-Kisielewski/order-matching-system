package com.quant.clob.engine;

interface MatchListener {
  void onTrade(Order resting, Order incoming, int quantity);
  void onOrderFilled(Order resting);
}
  
