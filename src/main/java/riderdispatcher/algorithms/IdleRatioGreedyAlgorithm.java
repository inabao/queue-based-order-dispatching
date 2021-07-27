package riderdispatcher.algorithms;

import riderdispatcher.core.*;
import riderdispatcher.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class IdleRatioGreedyAlgorithm extends BaseAlgorithm {
    private String info;

    private boolean isRealDemand;

    public IdleRatioGreedyAlgorithm(boolean isRealDemand, String info){
        this.isRealDemand = isRealDemand;
        this.info = info;
    }

    @Override
    public void run(ProblemInstance instance) {
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

            List<Order> zoneOrders = orderIndex.queryZoneObjects(i+1);
            List<Driver> zoneDrivers = driverIndex.queryZoneObjects(i+1);
            Order selectedOrder;
            while (!zoneDrivers.isEmpty() && !zoneOrders.isEmpty()){
                List<Driver> assignedDrivers = new ArrayList<>();
                for (Driver driver: zoneDrivers){
                    selectedOrder = findBestOrder(zoneOrders,  zoneIdleTimeMap, oracle, instance.taxiWatchdog, currentTimeOffset, zoneDriverCount );
                    if (driver != null && selectedOrder != null){
                        driver.serveOrder(selectedOrder, currentTimeOffset);
                        zoneOrders.remove(selectedOrder);
                        assignedDrivers.add(driver);
                        instance.taxiWatchdog.addTimeRecord(selectedOrder.getEndZoneID(), currentTimeOffset + selectedOrder.getEndTime() - selectedOrder.getStartTime(), 1);
                        zoneIdleTimeMap.remove(selectedOrder.getEndZoneID());
                        zoneDriverCount.put(selectedOrder.getStartZoneID(), zoneDriverCount.get(selectedOrder.getStartZoneID()) - 1);
                    }
                }
                zoneDrivers.removeAll(assignedDrivers);
            }
        }
    }





    @Override
    public String getInfo() {
        return info;
    }
}
