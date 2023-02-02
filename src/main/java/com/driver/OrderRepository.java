package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

	Map<String, Order> orderDb = new HashMap<>();
	Map<String, DeliveryPartner> deliveryPartnerDb = new HashMap<>();

	Map<DeliveryPartner, List<Order>> orderDeliveryPartnerDb = new HashMap<>();


	public void addOrder(Order order) {
		String orderId = order.getId();
		orderDb.put(orderId, order);
	}

	public void addPartner(String partnerId) {
		DeliveryPartner newDeliveryPartner = new DeliveryPartner(partnerId);
		deliveryPartnerDb.put(partnerId, newDeliveryPartner);
	}

	public void addOrderPartnerPair(String orderId, String partnerId) {
		Order order = orderDb.get(orderId);
		DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
		if(orderDeliveryPartnerDb.containsKey(deliveryPartner)) {
			orderDeliveryPartnerDb.get(deliveryPartner).add(order);
			deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders() + 1);
			return;
		}
		List<Order> orders = new ArrayList<>();
		orders.add(order);
		orderDeliveryPartnerDb.put(deliveryPartner, orders);
		deliveryPartner.setNumberOfOrders(1);
	}

	public Order getOrderById(String orderId) {
		if(orderDb.containsKey(orderId)){
			return orderDb.get(orderId);
		}
		return null;
	}

	public DeliveryPartner getPartnerById(String partnerId) {
		if(deliveryPartnerDb.containsKey(partnerId)){
			return deliveryPartnerDb.get(partnerId);
		}
		return null;
	}

	public Integer getOrderCountByPartnerId(String partnerId) {
		DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
		if(orderDeliveryPartnerDb.containsKey(deliveryPartner)){
			return orderDeliveryPartnerDb.get(deliveryPartner).size();
		}
		return 0;
		//return deliveryPartner.getNumberOfOrders();
	}

	public List<String> getAllOrders() {
		List<String> orders = new ArrayList<>();
		for(String orderId : orderDb.keySet())
			orders.add(orderId);
		return orders;
	}

	public List<String> getOrdersByPartnerId(String partnerId) {
		List<String> ordersByPartner = new ArrayList<>();
		if(orderDeliveryPartnerDb.containsKey(partnerId)){
			for(Order order : orderDeliveryPartnerDb.get(partnerId)){
				String orderId = order.getId();
				ordersByPartner.add(orderId);
			}
		}
		return ordersByPartner;
	}

	public Integer getCountOfUnassignedOrders() {
		int countOfOrdersAssigned = 0;
		for(List<Order> orderCount : orderDeliveryPartnerDb.values()){
			countOfOrdersAssigned += orderCount.size();
		}
		return orderDb.size() - countOfOrdersAssigned;
	}

	public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
		int Time = Integer.valueOf(time.substring(0,2))*60 + Integer.valueOf(time.substring(3));
		int count = 0;
		DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
		if(orderDeliveryPartnerDb.containsKey(deliveryPartner)){
			for(Order order : orderDeliveryPartnerDb.get(deliveryPartner)){
				if(order.getDeliveryTime() > Time)
					count++;
			}
		}
		return count;
	}

	public String getLastDeliveryTimeByPartnerId(String partnerId) {
		int time = 0;
		DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
		if(orderDeliveryPartnerDb.containsKey(deliveryPartner)){
			for(Order order : orderDeliveryPartnerDb.get(deliveryPartner)){
				if(order.getDeliveryTime() > time)
					time = order.getDeliveryTime();
			}
		}
		int HH = time/60;
		int MM = time%60;
		String lastTime = "" + HH + ":" + MM;
		return lastTime;
	}

	public void deletePartnerById(String partnerId) {
		deliveryPartnerDb.remove(partnerId);
		DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
		orderDeliveryPartnerDb.remove(deliveryPartner);
	}

	public void deleteOrderById(String orderId) {

		Order orderDelete = orderDb.get(orderId);
		orderDb.remove(orderId);
		outer :
		for(DeliveryPartner deliveryPartner : orderDeliveryPartnerDb.keySet()){
			for(Order order : orderDeliveryPartnerDb.get(deliveryPartner)){
				if(order == orderDelete){
					orderDeliveryPartnerDb.get(deliveryPartner).remove(order);
					deliveryPartnerDb.remove(deliveryPartner.getId());
					break outer;
				}
			}
		}
	}
}
