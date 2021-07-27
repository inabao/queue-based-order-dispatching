package riderdispatcher.algorithms;

import riderdispatcher.core.*;
import riderdispatcher.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class SimpleLocalSearchAlgorithm extends BaseAlgorithm {
    private String info;
    private boolean isRealDemand;
    public SimpleLocalSearchAlgorithm(boolean isRealDemand, String info){
        this.isRealDemand = isRealDemand;
        this.info = info;
    }

    @Override
    public void run(ProblemInstance instance) {
        boolean isConverged;
        if (instance.orders.isEmpty()){
            return;
        }
        buildIndex(instance);
        long currentTimeOffset = instance.currentTimeOffset;
        HashMap<Integer, Double> zoneIdleTimeMap = new HashMap<>();
        HashMap<Integer, Integer> zoneDriverCount = new HashMap<>();
        TaxiDemandSupplyOracle oracle;

        for (int i = 0; i < ZoneDemandTable.TOTAL_ZONE_COUNT; i++) {
            zoneDriverCount.put(i+1, driverIndex.queryZoneObjects(i+1).size());
        }

        if (isRealDemand){
            oracle = instance.orderReal;
        } else {
            oracle = instance.orderOracle;
        }

        for (int i = 0; i < ZoneDemandTable.TOTAL_ZONE_COUNT; i++) {

            List<Order> zoneOrders = orderIndex.queryZoneObjects(i + 1);
            List<Driver> zoneDrivers = driverIndex.queryZoneObjects(i + 1);


            int life = 100;

            do {
                life--;
                if (life==0){
                    break;
                }
                isConverged = true;

                Order selectedOrder = findBestOrder(zoneOrders, zoneIdleTimeMap, oracle, instance.taxiWatchdog, currentTimeOffset, zoneDriverCount);
                for (Driver driver : zoneDrivers) {
                    if (selectedOrder != null && driver.getServingOrder() != selectedOrder) {
                        double previousIdleRatio;
                        if (driver.getServingOrder() == null){
                            previousIdleRatio = Double.MAX_VALUE;
                        } else {
                            instance.taxiWatchdog.removeTimeRecord(driver.getServingOrder().getEndZoneID(), currentTimeOffset+driver.getServingOrder().getEndTime() - driver.getServingOrder().getStartTime(), 1);
                            previousIdleRatio = estimateZoneIdleRatio(driver.getServingOrder(), zoneIdleTimeMap, instance.orderOracle, instance.taxiWatchdog, currentTimeOffset, zoneDriverCount);
                            instance.taxiWatchdog.addTimeRecord(driver.getServingOrder().getEndZoneID(), currentTimeOffset+driver.getServingOrder().getEndTime() - driver.getServingOrder().getStartTime(), 1);
                        }
                        double newIdleRatio = estimateZoneIdleRatio(selectedOrder, zoneIdleTimeMap, instance.orderOracle, instance.taxiWatchdog, currentTimeOffset, zoneDriverCount);

                        if (previousIdleRatio <= newIdleRatio){
                            continue;
                        }

                        if (driver.getServingOrder() != null){
                            Order preAssignedOrder = driver.getServingOrder();
                            zoneOrders.add(preAssignedOrder);
                            instance.taxiWatchdog.removeTimeRecord(preAssignedOrder.getEndZoneID(), currentTimeOffset+preAssignedOrder.getEndTime() - preAssignedOrder.getStartTime(), 1);
                            driver.withdrawOrder(driver.getServingOrder());
                        }
                        isConverged = false;
                        driver.serveOrder(selectedOrder, currentTimeOffset);
                        zoneOrders.remove(selectedOrder);
                        instance.taxiWatchdog.addTimeRecord(selectedOrder.getEndZoneID(), currentTimeOffset + selectedOrder.getEndTime() - selectedOrder.getStartTime(), 1);
                        zoneIdleTimeMap.remove(selectedOrder.getEndZoneID());
                        break;
                    }
                }
            } while (!isConverged);

            for (Driver driver: zoneDrivers){
                driver.setServingOrder(null);
            }
        }

    }

    @Override
    public String getInfo() {
        return info;
    }
}
