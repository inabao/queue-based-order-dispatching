package riderdispatcher.algorithms;

import riderdispatcher.core.Driver;
import riderdispatcher.core.Order;
import riderdispatcher.core.ProblemInstance;
import riderdispatcher.core.ZoneDemandTable;

import java.util.List;
import java.util.Random;

public class NeareastGreedyAlgorithm extends BaseAlgorithm{

    private static String info = "NeareastGreedyAlgorithm";

    @Override
    public void run(ProblemInstance instance) {
        if (instance.orders.isEmpty()){
            return;
        }
        buildIndex(instance);


        for (int i = 0; i < ZoneDemandTable.TOTAL_ZONE_COUNT; i++) {

            List<Order> zoneOrders = orderIndex.queryZoneObjects(i+1);
            List<Driver> zoneDrivers = driverIndex.queryZoneObjects(i+1);

            while (!zoneDrivers.isEmpty() && !zoneOrders.isEmpty()){
                Driver selectedDriver = zoneDrivers.get(0);
                int selectedOrderIndex = -1;
                double longestTrip = Double.MAX_VALUE;
                for (int j = 0; j < zoneOrders.size(); j++) {
                    double tripLength = zoneOrders.get(j).getDistance(selectedDriver);
                    if (tripLength < longestTrip){
                        longestTrip = tripLength;
                        selectedOrderIndex = j;
                    }
                }
                Order selectedOrder = zoneOrders.get(selectedOrderIndex);
                selectedDriver.serveOrder(selectedOrder, instance.currentTimeOffset);
                zoneOrders.remove(selectedOrder);
                zoneDrivers.remove(selectedDriver);
                instance.taxiWatchdog.addTimeRecord(selectedOrder.getEndZoneID(), instance.currentTimeOffset + selectedOrder.getEndTime() - selectedOrder.getStartTime(), 1);
            }
        }
    }


    @Override
    public String getInfo() {
        return info;
    }
}
