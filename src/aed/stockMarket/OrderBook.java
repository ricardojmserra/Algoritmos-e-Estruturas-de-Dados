package aed.stockMarket;

import aed.collections.MinPriorityQueue;

import java.time.LocalDateTime;

public class OrderBook {

	public enum OrderType {
		LIMIT, MARKET
	}

	public enum ActionType {
		BID, ASK
	}

	private static final float ticksPerUnit = 100.0f;
	private static long orderIDCounter = 0;

	private int lastBid;
	private int lastAsk;
	private int lastPrice;
	private int minPrice;
	private int maxPrice;
	private int previousDayPrice;
	private int variation;
	private int processedOrders;
	private int unprocessedOrders;

	private int earnings;
	private int volume;
	private String stockTitle;

	private MinPriorityQueue<Order> asks;
	private MinPriorityQueue<Order> bids;

	public OrderBook(String stockTitle, int lastBid, int lastAsk) {
		this.maxPrice = Integer.MIN_VALUE;
		this.minPrice = Integer.MAX_VALUE;
		this.stockTitle = stockTitle;
		this.previousDayPrice = lastBid;
		this.lastAsk = lastAsk;
		this.lastBid = lastBid;
		this.lastPrice = this.previousDayPrice;
		this.asks = new MinPriorityQueue<Order>();
		this.bids = new MinPriorityQueue<Order>();
		this.processedOrders = 0;
		this.unprocessedOrders = 0;
		this.earnings = 0;
		this.variation = 0;
		this.volume = 0;
	}

	public String getStockTitle() {
		return this.stockTitle;
	}

	public int getLastBid() {
		return this.lastBid;
	}

	public int getLastAsk() {
		return this.lastAsk;
	}

	public int getLastPrice() {
		return this.lastPrice;
	}

	public int getMinPrice() {
		return this.minPrice;
	}

	public int getMaxPrice() {
		return this.maxPrice;
	}

	public int getNextBestBid() {
		if (this.bids.isEmpty())
			return 0;
		Order bid = this.bids.peekMin();
		if (bid.orderType == OrderType.LIMIT)
			return bid.price;
		return this.lastBid;
	}

	public int getNextBestAsk() {
		if (this.asks.isEmpty())
			return 0;
		Order ask = this.asks.peekMin();
		if (ask.orderType == OrderType.LIMIT)
			return ask.price;
		return this.lastAsk;
	}

	public int getProcessedOrders() {
		return this.processedOrders;
	}

	public int getUnprocessedOrders() {
		return this.unprocessedOrders;
	}

	public int getVariation() {
		return this.variation;
	}

	public int getVolume() {
		return this.volume;
	}

	public int getBrokerEarnings() {
		return this.earnings;
	}

	public void placeMarketOrder(ActionType action, int quantity) {
		Order order = new Order(this.stockTitle, ++orderIDCounter, action, OrderType.MARKET, quantity, 0);
		this.unprocessedOrders++;
		if (action == OrderBook.ActionType.ASK)
			this.asks.insert(order);
		else
			this.bids.insert(order);
	}

	public void placeLimitOrder(ActionType action, int quantity, int price) {
		Order order = new Order(this.stockTitle, ++orderIDCounter, action, OrderType.LIMIT, quantity, price);
		this.unprocessedOrders++;
		if (action == OrderBook.ActionType.ASK)
			this.asks.insert(order);
		else
			this.bids.insert(order);
	}

	private int processOrder(Order Ask, Order Bid, boolean verbose) {
		int result = 0;
		// Inicio Obter valor
		int price_ask, price_bid;
		if (Ask.orderType == Bid.orderType) {
			if (Ask.orderType == OrderType.MARKET) {
				price_ask = this.lastAsk;
				price_bid = this.lastBid;
			} else {
				price_ask = Ask.price;
				price_bid = Bid.price;
			}
		} else if (Ask.orderType == OrderType.MARKET) {
			price_bid = Bid.price;
			if (this.lastAsk < Bid.price)
				price_ask = this.lastAsk;
			else
				price_ask = Bid.price;
		} else {
			price_ask = Ask.price;
			if (this.lastBid < Ask.price)
				price_bid = Ask.price;
			else
				price_bid = this.lastBid;
		}
		if(price_bid < price_ask)
			return result;//result 0, nao foi feito venda
		
		int spread = price_bid - price_ask;
		// final obter valor
		
		//inicio actualizar valores
		this.lastAsk = price_ask;	
		this.lastBid = price_bid;
		if(this.maxPrice < price_bid)
			this.maxPrice = price_bid;
		if(price_bid < this.minPrice)
			this.minPrice = price_bid;
		double temp = price_bid - this.previousDayPrice;
		temp = ( (temp/(double)this.previousDayPrice)  *10000);
		this.variation = (int)temp;
		//this.variation = (int) ( (((double)price_bid - (double)this.previousDayPrice)/(double)(price_bid))*10000 );
		this.lastPrice = price_bid;
		//final actualizar valores
		
		//inicio quantidade vendido
		int quantidade;
		if(Ask.quantity < Bid.quantity) {
			quantidade = Ask.quantity;
			Bid.quantity -= Ask.quantity;
			this.asks.removeMin();
			result = 1;	//uma feita totalmente
		}
		else if(Bid.quantity < Ask.quantity) {
			quantidade = Bid.quantity;
			Ask.quantity -= Bid.quantity;
			this.bids.removeMin();
			result = 1;	//uma feita totalmente
		}
		else {
			quantidade = Bid.quantity;
			this.bids.removeMin();
			this.asks.removeMin();
			result = 2;	//ambas feitas totalmente
		}
		this.volume += quantidade;
		//final quantidade vendido
		
		//calcular earning
		this.earnings += spread * quantidade;
		
		if(  verbose  ) {
			String varSignal;
			if(0 <= this.variation )
				varSignal = " +";
			else
				varSignal = " ";
			System.out.println(this.stockTitle + " - quantity: " + quantidade + " price: " + (this.lastPrice/ticksPerUnit) + varSignal + (variation/ticksPerUnit)+"%");
		}
		return result;
	}

	public int processNextOrder(boolean verbose) {
		if (this.asks.isEmpty() || this.bids.isEmpty())
			return 0;
		int result = processOrder(this.asks.peekMin(), this.bids.peekMin(), verbose);
		this.unprocessedOrders -= result;
		this.processedOrders += result;
		return result;
	}

	public boolean processNextOrders(int n, boolean verbose) {
		for(int i = 0; i < n;) {
			int process = processNextOrder(verbose);
			if( process == 0)
				return false;
			i += process;
		}
		return true;
	}

	public void printSummary() {
		System.out.println("Daily transaction summary for stock: " + this.getStockTitle());
		System.out.println("Orders processed: " + getProcessedOrders());
		System.out.println("Transaction volume: " + getVolume());
		System.out.println("Last Price: " + getLastBid() / ticksPerUnit + (getVariation() < 0 ? " " : " +")
				+ getVariation() / ticksPerUnit + "%");
		System.out.println("Maximum Price: " + this.maxPrice / ticksPerUnit);
		System.out.println("Minimum Price: " + this.minPrice / ticksPerUnit);
		System.out.println("Orders not processed: " + getUnprocessedOrders());
		System.out.println("Best remaining ask: " + getNextBestAsk() / ticksPerUnit);
		System.out.println("Best remaining bid: " + getNextBestBid() / ticksPerUnit);
		System.out.println("Daily earnings: " + getBrokerEarnings() / ticksPerUnit);
	}
	public static void main(String[] args) {
		OrderBook temp = new OrderBook("memes", 327, 327);
		temp.placeMarketOrder(ActionType.BID, 25);
		temp.placeMarketOrder(ActionType.BID, 10);
		temp.placeMarketOrder(ActionType.BID, 15);
		
		temp.placeLimitOrder(ActionType.ASK, 20, 325);
		temp.placeLimitOrder(ActionType.ASK, 15, 327);
		temp.placeLimitOrder(ActionType.ASK, 25, 329);
		
		for(int i = 0; i < 4;i++)
			System.out.println("processed " + temp.processNextOrder(true));
		System.out.println("unprocessed " + temp.unprocessedOrders + " earning " + temp.earnings);
		System.out.println("best ask: " + temp.getNextBestAsk());
	}
}

class Order implements Comparable<Order> {

	private static final float ticksPerUnit = 100.0f;

	@Override
	public int compareTo(Order o) {
		if (this.orderType != o.orderType)
			return o.orderType.compareTo(this.orderType);
		if (this.orderType == OrderBook.OrderType.MARKET)
			return (int)(this.orderID - o.orderID);
		if (this.action == o.action && this.price != o.price) {
			if (this.action == OrderBook.ActionType.ASK)
				return this.price - o.price;
			return o.price - this.price;
		}
		return (int)(this.orderID - o.orderID);
	}

	final LocalDateTime time;
	final long orderID;
	int quantity;
	final OrderBook.OrderType orderType;
	final int price;
	final OrderBook.ActionType action;
	final String stockTitle;

	public Order(String stockTitle, long orderID, OrderBook.ActionType action, OrderBook.OrderType order, int quantity,
			int price) {
		this.stockTitle = stockTitle;
		this.orderID = orderID;
		this.action = action;
		this.orderType = order;
		this.time = LocalDateTime.now();
		this.quantity = quantity;
		this.price = price;
	}
	
	public String toString() {
		String result = this.action.toString() + " ID: " + this.orderID + " type: " + this.orderType.toString()
				+ " quantity: " + this.quantity;
		if (this.orderType == OrderBook.OrderType.LIMIT)
			result += " price: " + this.price / ticksPerUnit;

		return result;
	}
}
