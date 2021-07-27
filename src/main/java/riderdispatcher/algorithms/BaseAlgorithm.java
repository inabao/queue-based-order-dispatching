/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package riderdispatcher.algorithms;


import riderdispatcher.core.*;
import riderdispatcher.estimator.IdleTimeEstimator;
import riderdispatcher.simulator.OrderDistribution;

import java.util.HashMap;
import java.util.List;

import static riderdispatcher.utils.Constants.lookupLength;

/**
 *
 * @author bigstone
 */
public abstract class BaseAlgorithm {
    abstract public void run(ProblemInstance instance);
    abstract public String getInfo();
    public static double randServe = 0;
    public static int randcount = 0;

    protected ZoneIndex<Order> orderIndex;
    protected ZoneIndex<Driver> driverIndex;

    public static int count = 0;
    public static double serverTime = 0;
    
    public void buildIndex(ProblemInstance instance){
        orderIndex = new ZoneIndex<>(instance.orders);
        driverIndex = new ZoneIndex<>(instance.drivers);
    }

    public Order findBestOrder(List<Order> zoneOrders, HashMap<Integer, Double> zoneIdleTimeMap, TaxiDemandSupplyOracle orderOracle, TaxiDemandSupplyOracle taxiWatchdog, long currentTimeOffset, HashMap<Integer, Integer> zoneDriverCount){
        double minIdleRatio = Double.MAX_VALUE;
        Order selectedOrder = null;
        for (int j = 0; j < zoneOrders.size(); j++) {
            double idleRatio = estimateZoneIdleRatio(zoneOrders.get(j), zoneIdleTimeMap, orderOracle, taxiWatchdog, currentTimeOffset, zoneDriverCount);

            if (idleRatio < minIdleRatio){
                minIdleRatio = idleRatio;
                selectedOrder = zoneOrders.get(j);
            }
        }
        return selectedOrder;
    }
    public Order findShortestOrder(List<Order> zoneOrders, HashMap<Integer, Double> zoneIdleTimeMap, TaxiDemandSupplyOracle orderOracle, TaxiDemandSupplyOracle taxiWatchdog, long currentTimeOffset, HashMap<Integer, Integer> zoneDriverCount){
        double minIdleRatio = Double.MAX_VALUE;
        Order selectedOrder = null;
        for (int j = 0; j < zoneOrders.size(); j++) {
            double idleTime = estimateZoneIdleTime(zoneOrders.get(j), zoneIdleTimeMap, orderOracle, taxiWatchdog, currentTimeOffset, zoneDriverCount);

            if (idleTime < minIdleRatio){
                minIdleRatio = idleTime;
                selectedOrder = zoneOrders.get(j);
            }
        }
        return selectedOrder;
    }

    public double estimateZoneIdleRatio(Order order, HashMap<Integer, Double> zoneIdleTimeMap, TaxiDemandSupplyOracle orderOracle, TaxiDemandSupplyOracle taxiWatchdog, long currentTimeOffset, HashMap<Integer, Integer> zoneDriverCount){
        double idleTime;
        if (zoneIdleTimeMap.containsKey(order.getEndZoneID())){
            idleTime = zoneIdleTimeMap.get(order.getEndZoneID());
        } else {
            long tripTime = order.getEndTime() - order.getStartTime();
            double lambda = orderOracle.queryRate(currentTimeOffset + tripTime, currentTimeOffset+tripTime+lookupLength, order.getEndZoneID()) * 60;
            double mu = ((taxiWatchdog.queryDemand(currentTimeOffset + tripTime, currentTimeOffset+tripTime+lookupLength, order.getEndZoneID()) + 1)/(lookupLength)) * 60;
            int maxDriverCount = zoneDriverCount.get(order.getEndZoneID());//a good choice for K: the number of drivers per minute

            idleTime = IdleTimeEstimator.estimateIdleTimeMix(lambda, mu, maxDriverCount) * 60;
            zoneIdleTimeMap.put(order.getEndZoneID(), idleTime);
        }
        order.setIdleTime(idleTime);
        double idleRatio = idleTime/(idleTime + order.getEndTime() - order.getStartTime());
        return idleRatio;
    }

    public double estimateZoneIdleTime(Order order, HashMap<Integer, Double> zoneIdleTimeMap, TaxiDemandSupplyOracle orderOracle, TaxiDemandSupplyOracle taxiWatchdog, long currentTimeOffset, HashMap<Integer, Integer> zoneDriverCount){
        double idleTime;
        if (zoneIdleTimeMap.containsKey(order.getEndZoneID())){
            idleTime = zoneIdleTimeMap.get(order.getEndZoneID());
        } else {
            long tripTime = order.getEndTime() - order.getStartTime();
            double lambda = orderOracle.queryRate(currentTimeOffset + tripTime, currentTimeOffset+tripTime+lookupLength, order.getEndZoneID()) * 60;
            double mu = ((taxiWatchdog.queryDemand(currentTimeOffset + tripTime, currentTimeOffset+tripTime+lookupLength, order.getEndZoneID()) + 1)/(lookupLength)) * 60;
            int maxDriverCount = zoneDriverCount.get(order.getEndZoneID());//a good choice for K: the number of drivers per minute

            idleTime = IdleTimeEstimator.estimateIdleTimeMix(lambda, mu, maxDriverCount) * 60;
            zoneIdleTimeMap.put(order.getEndZoneID(), idleTime);
        }
        order.setIdleTime(idleTime);
        double idleRatio = idleTime + order.getEndTime();
        return idleRatio;
    }

    public int getCount(ProblemInstance problemInstance) {
        return problemInstance.calculateTotalAssignedOrderCount();
    }
    public double getServeTime(ProblemInstance problemInstance) {
        return problemInstance.calculateTotalServingTime();
    }
}
