package com.quant;

import com.quant.clob.engine.Order;
import com.quant.clob.engine.OrderBook;
import com.quant.clob.engine.PriceLevel;

public class Main {
    public static void main(String[] args) {
        PriceLevel buyTree = new PriceLevel();
        buyTree.tailOrder.idNumber = 69;
        buyTree.priceLevel = 2;
        PriceLevel sellTree = new PriceLevel();
        sellTree.priceLevel = 9;
        OrderBook orderBook = new OrderBook();
        orderBook.buyTree = buyTree;
        orderBook.sellTree = sellTree;
        System.out.println("Hello world!");
        System.out.println(orderBook.toString());

        Order order = new Order(buyTree);
        order.idNumber = 3;
        orderBook.addOrderToBuyTree(order);
        System.out.println(orderBook.toString());
        System.out.println(order.toString());
        
    }
}