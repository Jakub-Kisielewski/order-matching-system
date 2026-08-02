package com.quant.clob.engine;

final class Order {
	int idNumber;
	boolean isBuy;
	int shares;
	int limit;
	int entryTime; // time order entered book
	int eventTime; // timestamp for most recent update to order
	Order nextOrder;
	Order prevOrder;
	PriceLevel parentPriceLevel;

	Order(PriceLevel parentPriceLevel) {
		this.idNumber = 0;
		this.isBuy = false;
		this.shares = 10;
		this.limit = 20;
		this.entryTime = 0;
		this.eventTime = 0;
		this.nextOrder = null;
		this.prevOrder = null;
		this.parentPriceLevel = parentPriceLevel;
	}
}
