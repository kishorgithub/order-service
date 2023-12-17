package com.bitmyra.lob;

public class Trade {
	private long timestamp;
	private double price;
	private int qty;
	private int maker;
	private int taker;
	private int buyer;
	private int seller;
	private int orderHit;
	
	public Trade(long time, double price, int qty, int maker,
				 int taker, int buyer, int seller, int orderHit) {
		this.timestamp = time;
		this.price = price;
		this.qty = qty;
		this.maker = maker;
		this.taker = taker;
		this.buyer = buyer;
		this.seller = seller;
		this.orderHit = orderHit; // the qId of the order that was in the book
	}
	
	
	@Override
	public String toString() {
		return ("\n| TRADE\tt= " + timestamp + 
				"\tprice = " + price +
				"\tquantity = " + qty +
				"\tMaker = " + maker +
				"\tTaker = " + taker +
				"\tBuyer = " + buyer +
				"\tSeller = " + seller);
	}
	
	public String toCSV() {
		return (timestamp + ", " + 
				price + ", " + 
				qty + ", " +
				maker + ", " +
				taker + ", " + 
				buyer + ", " + 
				seller + "\n");
	}


	public long getTimestamp() {
		return timestamp;
	}



	public double getPrice() {
		return price;
	}



	public int getQty() {
		return qty;
	}



	public int getMaker() {
		return maker;
	}



	public int getTaker() {
		return taker;
	}



	public int getBuyer() {
		return buyer;
	}



	public int getSeller() {
		return seller;
	}


	public int getOrderHit() {
		return orderHit;
	}
}
