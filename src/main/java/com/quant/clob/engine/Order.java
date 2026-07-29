package com.quant.clob.engine;

final class Order {
	int idNumber;
	boolean buyOrSell;
	int shares;
	int limit;
	int entryTime; // time order entered book
	int eventTime; // timestamp for most recent update to order
	Order nextOrder;
	Order prevOrder;
	PriceLevel parentPriceLevel;
}
