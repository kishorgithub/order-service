package com.bitmyra.lob;

import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//@Component
public class OrderBook {
	private List<Trade> tape = new ArrayList<>();
	private OrderTree bids = new OrderTree();
	private OrderTree asks = new OrderTree();
	private double tickSize;
	private long time;
	private int nextQuoteID;
	private int lastOrderSign;
	
	public OrderBook(double tickSize) {
		this.tickSize = tickSize;
		this.reset();
	}
	
	public void reset() {
		tape.clear();
		bids.reset();
		asks.reset();
		time = 0;
		nextQuoteID = 0;
		lastOrderSign=1;
	}
	

	private double clipPrice(double price) {
		int numDecPlaces = (int)Math.log10(1 / tickSize);
		BigDecimal bd = new BigDecimal(price);
		BigDecimal rounded = bd.setScale(numDecPlaces, BigDecimal.ROUND_HALF_UP);
		return rounded.doubleValue();
	}
	
	
	public OrderReport processOrder(Order quote, boolean verbose) {

		//System.out.println("New order submitted: \n"+ quote.toString());

		boolean isLimit = quote.isLimit();
		OrderReport oReport;
		// Update time
		time = quote.getTimestamp();
		if (quote.getQuantity() <= 0 ) {
			throw new IllegalArgumentException("processOrder() given qty <= 0");
		}
		if (isLimit) {
			double clippedPrice = clipPrice(quote.getPrice());
			quote.setPrice(clippedPrice);
			oReport = processLimitOrder(quote, verbose);
		} else {
			oReport = processMarketOrder(quote, verbose);
		}

		return oReport;
	}
	
	private OrderReport processLimitOrder(Order quote, 
										  boolean verbose) {
		boolean orderAddedToBook;
		ArrayList<Trade> trades = new ArrayList<>();
		boolean bid = quote.isBid();
		int qtyRemaining = quote.getQuantity();
		double price = quote.getPrice();
		if (bid) {
			lastOrderSign = 1;
			while ((asks.getnOrders() > 0) &&
					(qtyRemaining > 0) && 
					(price >= asks.minPrice())) {
				OrderList ordersAtBest = asks.minPriceList();
				qtyRemaining = processOrderList(trades, ordersAtBest, qtyRemaining,
												quote, verbose);
			}
			// If volume remains, add order to book
			if (qtyRemaining > 0) {
				quote.setQueueId(nextQuoteID);
				quote.setQuantity(qtyRemaining);
				bids.insertOrder(quote);
				orderAddedToBook = true;
				nextQuoteID = nextQuoteID + 1;
			} else {
				orderAddedToBook = false;
			}
		} else {
			lastOrderSign = -1;
			while ((bids.getnOrders() > 0) &&
					(qtyRemaining > 0) && 
					(price <= bids.maxPrice())) {
				OrderList ordersAtBest = bids.maxPriceList();
				qtyRemaining = processOrderList(trades, ordersAtBest, qtyRemaining,
												quote, verbose);
			}
			// If volume remains, add to book
			if (qtyRemaining > 0) {
				quote.setQueueId(nextQuoteID);
				quote.setQuantity(qtyRemaining);
				asks.insertOrder(quote);
				orderAddedToBook = true;
				nextQuoteID = nextQuoteID + 1;
			} else {
				orderAddedToBook = false;
			}
		}

		OrderReport report = new OrderReport(trades, orderAddedToBook);
		if (orderAddedToBook) {
			report.setOrder(quote);
		}
		return report;
	}

	private OrderReport processMarketOrder(Order quote, boolean verbose) {
		ArrayList<Trade> trades = new ArrayList<Trade>();
		boolean bid = quote.isBid();
		int qtyRemaining = quote.getQuantity();
		if (bid) {
			lastOrderSign = 1;
			while ((qtyRemaining > 0) && (asks.getnOrders() > 0)) {
				OrderList ordersAtBest = asks.minPriceList();
				qtyRemaining = processOrderList(trades, ordersAtBest, qtyRemaining,
						quote, verbose);
			}
		} else {
			lastOrderSign = -1;
			while ((qtyRemaining > 0) && (bids.getnOrders() > 0)) {
				OrderList ordersAtBest = bids.maxPriceList();
				qtyRemaining = processOrderList(trades, ordersAtBest, qtyRemaining,
						quote, verbose);
			}
		}
		OrderReport report = new OrderReport(trades, false);
		return  report;
	}

	private int processOrderList(ArrayList<Trade> trades, OrderList orders,
								int qtyRemaining, Order quote,
								boolean verbose) {
		boolean bid = quote.isBid();
		int buyer, seller;
		int takerId = quote.gettId();
		long time = quote.getTimestamp();
		while ((orders.getLength()>0) && (qtyRemaining>0)) {
			int qtyTraded = 0;
			Order headOrder = orders.getHeadOrder();
			if (qtyRemaining < headOrder.getQuantity()) {
				qtyTraded = qtyRemaining;
				if (bid) {
					asks.updateOrderQty(headOrder.getQuantity()-qtyRemaining,
											 headOrder.getQueueId());
				} else {//offer
					bids.updateOrderQty(headOrder.getQuantity()-qtyRemaining,
											 headOrder.getQueueId());
				}
				qtyRemaining = 0;
			} else {
				qtyTraded = headOrder.getQuantity();
				if (bid) {
					asks.removeOrderByID(headOrder.getQueueId());
				} else {
					bids.removeOrderByID(headOrder.getQueueId());
				}
				qtyRemaining = qtyRemaining - qtyTraded;
			}
			if (bid) {
				buyer = takerId;
				seller = headOrder.gettId();
			} else {
				buyer = headOrder.gettId();
				seller = takerId;
			}
			Trade trade = new Trade(time, headOrder.getPrice(), qtyTraded, 
									headOrder.gettId(),takerId, buyer, seller, 
									headOrder.getQueueId());
			trades.add(trade);
			tape.add(trade);
//			if (verbose) {
//				System.out.println(trade);
//			}
		}
		return qtyRemaining;
	}
	
	
	public void cancelOrder(boolean bid, int qId, int time) {
		this.time = time;
		if (bid) {
			if (bids.orderExists(qId)) {
				bids.removeOrderByID(qId);
			}
		} else {
			if (asks.orderExists(qId)) {
				asks.removeOrderByID(qId);
			}
		}
	}
	
	
	public void modifyOrder(int qId, HashMap<String, String> quote) {
	}
	
	
	public int getVolumeAtPrice(boolean bid, double price) {
		price = clipPrice(price);
		int vol = 0;
		if(bid) {
			if (bids.priceExists(price)) {
				vol = bids.getPriceList(price).getVolume();
			}
		} else {
			if (asks.priceExists(price)) {
				vol = asks.getPriceList(price).getVolume();
			}
		}
		return vol;
		
	}
	
	public double getBestBid() {
		return bids.maxPrice();
	}
	
	public double getWorstBid() {
		return bids.minPrice();
	}
	
	public double getBestOffer() {
		return asks.minPrice();
	}
	
	public double getWorstOffer() {
		return asks.maxPrice();
	}
	
	public int getLastOrderSign() {
		return lastOrderSign;
	}
	
	public int volumeOnSide(boolean bid) {
		if (bid) {
			return bids.getVolume();
		} else {
			return asks.getVolume();
		}
	}
	
	public double getTickSize() {
		return tickSize;
	}
	
	public double getSpread() {
		return asks.minPrice() - bids.maxPrice();
	}
	
	public double getMid() {
		return getBestBid() + (getSpread()/2.0);
	}
	
	public boolean bidsAndAsksExist() {
		return ((bids.nOrders>0) && (asks.nOrders>0));
	}
	
	public String toString() {
		StringWriter fileStr = new StringWriter();
		fileStr.write("Seq: " + time + "\n");
		fileStr.write(" ********** The Order Book **********\n");

		fileStr.write("       ------- Bids --------   \n");
		fileStr.write("Price		Qty		timestamp		    Qid		TxnId\n");
		if (bids.getnOrders() > 0) {
			fileStr.write(bids.toString());
		}

		fileStr.write("\n       ------ Offers -------   \n");
		fileStr.write("Price		Qty		timestamp		    Qid		TxnId\n");
		if (asks.getnOrders() > 0) {
			fileStr.write(asks.toString());
		}



		fileStr.write("\n\n   -------- Trades  ---------   ");
		if (!tape.isEmpty()) {
			for (Trade t : tape) {
				fileStr.write(t.toString());
			}
		}
		return fileStr.toString();
	}


	public List<Trade> getTape() {
		return tape;
	}

}
