package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

	Map<String, Order> orderDb;
	Map<String, DeliveryPartner> deliveryPartnerDb;
	Map<String, List<String>> orderDeliveryPartnerDb;

	public OrderRepository(){
		orderDb = new HashMap<>();

		deliveryPartnerDb = new HashMap<>();

		orderDeliveryPartnerDb = new HashMap<>();
	}


	public void addOrder(Order order) {
		String orderId = order.getId();
		orderDb.put(orderId, order);
	}

	public void addPartner(String partnerId) {
		DeliveryPartner newDeliveryPartner = new DeliveryPartner(partnerId);
		deliveryPartnerDb.put(partnerId, newDeliveryPartner);
	}

	public void addOrderPartnerPair(String orderId, String partnerId) {
//		DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
//		if(orderDeliveryPartnerDb.containsKey(partnerId)) {
//			if(orderDeliveryPartnerDb.get(partnerId).contains(orderId))
//				return;
//			orderDeliveryPartnerDb.get(partnerId).add(orderId);
//			deliveryPartner.setNumberOfOrders(orderDeliveryPartnerDb.get(partnerId).size());
//			return;
//		}
//		List<String> orders = new ArrayList<>();
//		orders.add(orderId);
//		orderDeliveryPartnerDb.put(partnerId, orders);
//		deliveryPartner.setNumberOfOrders(1);
		if(!orderDeliveryPartnerDb.containsKey(partnerId)){
			orderDeliveryPartnerDb.put(partnerId,new ArrayList<>());
		}

		//If already order is present
		for(String s:orderDeliveryPartnerDb.get(partnerId)){
			if(s.equals(orderId)){
				return;
			}
		}
		orderDeliveryPartnerDb.get(partnerId).add(orderId);
		DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
		deliveryPartner.setNumberOfOrders(orderDeliveryPartnerDb.get(partnerId).size());
	}

	public Order getOrderById(String orderId) {
			return orderDb.get(orderId);
	}

	public DeliveryPartner getPartnerById(String partnerId) {
			return deliveryPartnerDb.get(partnerId);
	}

	public Integer getOrderCountByPartnerId(String partnerId) {
		if(orderDeliveryPartnerDb.containsKey(partnerId)){
			return orderDeliveryPartnerDb.get(partnerId).size();
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
		return orderDeliveryPartnerDb.get(partnerId);
	}

	public Integer getCountOfUnassignedOrders() {
		int countOfOrdersAssigned = 0;
		for(List<String> orderCount : orderDeliveryPartnerDb.values()){
			countOfOrdersAssigned = countOfOrdersAssigned + orderCount.size();
		}
		return (orderDb.size() - countOfOrdersAssigned);
	}

	public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
		String[] s = time.split(":");
		int Time=Integer.parseInt(s[0])*60+Integer.parseInt(s[1]);
		//int Time = Integer.valueOf(time.substring(0,2))*60 + Integer.valueOf(time.substring(3));
		int count = 0;
		if(orderDeliveryPartnerDb.containsKey(partnerId)){
			for(String orderId : orderDeliveryPartnerDb.get(partnerId)){
				Order order = orderDb.get(orderId);
				if(order.getDeliveryTime() > Time)
					count++;
			}
		}
		return count;
	}

	public String getLastDeliveryTimeByPartnerId(String partnerId) {
		int time = 0;
		if(orderDeliveryPartnerDb.containsKey(partnerId)){
			for(String orderId : orderDeliveryPartnerDb.get(partnerId)){
				Order order = orderDb.get(orderId);
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
		//DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
		orderDeliveryPartnerDb.remove(partnerId);
	}

	public void deleteOrderById(String orderId) {
		orderDb.remove(orderId);
		outer :
		for(String deliveryPartner : orderDeliveryPartnerDb.keySet()){
			for(String order : orderDeliveryPartnerDb.get(deliveryPartner)){
				if(order == orderId){
					orderDeliveryPartnerDb.remove(deliveryPartner);
					//deliveryPartnerDb.remove(deliveryPartner);
					break outer;
				}
			}
		}
	}
}
