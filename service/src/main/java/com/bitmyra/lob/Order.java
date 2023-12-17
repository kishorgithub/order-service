package com.bitmyra.lob;


public class Order {
	private long timestamp;
	private boolean limit;
	private int quantity;
	private boolean bid;
	private double price;
	private int queueId;
	private int tId;
	private Order nextOrder;
	private Order prevOrder;
	private OrderList oL;

	public Order(int time, boolean limit, int quantity, int tId, boolean bid) {
		this(time, limit, quantity, tId, bid, null);
	}	
	
	public Order(long time, boolean limit, int quantity,
				int tId, boolean bid, Double price) {
		
		this.timestamp = time;
		this.limit = limit;
		this.bid = bid;
		this.quantity = quantity;
		if (price!=null) {
			this.price = (double)price;
		}
		this.tId = tId;
	}
	
	public void updateQty(int qty, long timestamp) {
		if ((qty > this.quantity) && (this.oL.getTailOrder() != this)) {
			// Move order to the end of the list. i.e. loses time priority
			this.oL.moveTail(this);
			this.timestamp = timestamp;
		}
		oL.setVolume(oL.getVolume()-(this.quantity-qty));
		this.quantity = qty;
	}
	
	public String toString() {
		//String side = this.bid ? "bid" : "offer";
        return 			   price +
				"		"+ quantity +
        		"		"+ timestamp +
        		"		"+ queueId +
        		"		"+ tId;
    }

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isLimit() {
		return limit;
	}

	public void setLimit(boolean limit) {
		this.limit = limit;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isBid() {
		return bid;
	}

	public void setBid(boolean bid) {
		this.bid = bid;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQueueId() {
		return queueId;
	}

	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}

	public int gettId() {
		return tId;
	}

	public void settId(int tId) {
		this.tId = tId;
	}

	public Order getNextOrder() {
		return nextOrder;
	}

	public void setNextOrder(Order nextOrder) {
		this.nextOrder = nextOrder;
	}

	public Order getPrevOrder() {
		return prevOrder;
	}

	public void setPrevOrder(Order prevOrder) {
		this.prevOrder = prevOrder;
	}

	public OrderList getoL() {
		return oL;
	}

	public void setoL(OrderList oL) {
		this.oL = oL;
	}
}
