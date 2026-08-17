package com.quant.clob.engine;

final class Order {
	int idNumber;
	boolean isBuy;
	boolean isMarketOrder; // market order fills volume completely, limit order fills up to limit and is then added to tree
	int shares;
	int limit;
	int entryTime; // time order entered book
	int eventTime; // timestamp for most recent update to order
	Order nextOrder;
	Order prevOrder;
	PriceLevel parentPriceLevel;

	Order(PriceLevel parentPriceLevel) {
	}

	static void freeOrderObject(Order order) {
		order.idNumber = 0;
		order.isBuy = true;
		order.isMarketOrder = false;
		order.shares = 0;
		order.limit = 0;
		order.entryTime = 0;
		order.eventTime = 0;
		order.nextOrder = null;
		order.prevOrder = null;
		order.parentPriceLevel = null;
	}


	@Override
	public String toString() {
		return "id" + idNumber + " prevOrder" + prevOrder.idNumber + " parentpricelevel" + parentPriceLevel.toString();
	}
}
