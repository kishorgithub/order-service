package com.bitmyra.lob;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class OrderList implements Iterable<Order>, Iterator<Order>{
	/*
	 * This class creates a sorted, iterable list or orders for each price  level
	 * in the order tree.
	 */
	private Order headOrder = null;;
	private Order tailOrder = null;;
	private int length = 0;
	private int volume = 0;    // Total volume at this price level
	private Order last = null;
	
	// The next three methods implement Iterator.
	public boolean hasNext() {
		if (last==null){
			return false;
	    }  
	    return true;
	}
	
	public Order next() {
	    if (last == null) {
	    	throw new NoSuchElementException();
	    }
	    Order returnVal = last;
	    last = last.getNextOrder();
	    return returnVal;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	  
	// This method implements Iterable.
	public Iterator<Order> iterator() {
		last = headOrder;
		return this;
	}
	
	public void appendOrder(Order incomingOrder) {
		if (length == 0) {
			incomingOrder.setNextOrder(null);
			incomingOrder.setPrevOrder(null);
			headOrder = incomingOrder;
			tailOrder = incomingOrder;
		} else{
			incomingOrder.setPrevOrder(tailOrder);
			incomingOrder.setNextOrder(null);
			tailOrder.setNextOrder(incomingOrder);
			tailOrder = incomingOrder;
		}
		length+=1;
		volume+=incomingOrder.getQuantity();
	}
	
	public void removeOrder(Order order) {
		volume = volume - order.getQuantity();
		length = length - 1;
		if (length == 0) {
			return;
		}
		Order tempNextOrder = order.getNextOrder();
		Order tempPrevOrder = order.getPrevOrder();
		if ((tempNextOrder != null) && (tempPrevOrder != null)) {
			tempNextOrder.setPrevOrder(tempPrevOrder);
			tempPrevOrder.setNextOrder(tempNextOrder);
		} else if (tempNextOrder != null){
			tempNextOrder.setPrevOrder(null);
			headOrder = tempNextOrder;
		} else if (tempPrevOrder != null){
			tempPrevOrder.setNextOrder(null);
			tailOrder = tempPrevOrder;
		}
	}
	
	public void moveTail(Order order) {
		/*
		 * Move 'order' to the tail of the list (after modification for e.g.)
		 */
		if (order.getPrevOrder() != null) {
			order.getPrevOrder().setNextOrder(order.getNextOrder());
		} else {
			// Update head order
			headOrder = order.getNextOrder();
		}
		order.getNextOrder().setPrevOrder(order.getPrevOrder());
		// Set the previous tail's next order to this order
		tailOrder.setNextOrder(order);
		order.setPrevOrder(tailOrder);
		tailOrder = order;
		order.setNextOrder(null);
	}
	
	public String toString() {
		String outString = "";
		for (Order o : this) {
			outString += (o.toString()+"\n");
		}
		return outString;
	}
	
	
	public Integer getLength() {
		return length;
	}

	public Order getHeadOrder() {
		return headOrder;
	}

	public Order getTailOrder() {
		return tailOrder;
	}

	public void setTailOrder(Order tailOrder) {
		this.tailOrder = tailOrder;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}
	
}
