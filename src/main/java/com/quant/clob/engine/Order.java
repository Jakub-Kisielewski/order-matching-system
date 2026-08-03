package com.quant.clob.engine;

public final class Order {
	public int idNumber;
	boolean isBuy;
	boolean isMarketOrder; // market order fills volume completely, limit order fills up to limit and is then added to tree
	int shares;
	int limit;
	int entryTime; // time order entered book
	int eventTime; // timestamp for most recent update to order
	Order nextOrder;
	Order prevOrder;
	PriceLevel parentPriceLevel;

	public Order(PriceLevel parentPriceLevel) {
		this.idNumber = 0;
		this.isBuy = true;
		this.shares = 10;
		this.limit = 20;
		this.entryTime = 0;
		this.eventTime = 0;
		this.nextOrder = null;
		this.prevOrder = null;
		this.parentPriceLevel = parentPriceLevel;
	}

	@Override
	public String toString() {
		return "id" + idNumber + " prevOrder" + prevOrder.idNumber + " parentpricelevel" + parentPriceLevel.toString();
	}
}
