package riderdispatcher.algorithms;

import riderdispatcher.core.*;
import riderdispatcher.utils.Constants;

import java.util.List;
import java.util.Random;

import static riderdispatcher.utils.Constants.lookupLength;

public class PolarAlgorithm extends BaseAlgorithm {
    private static String info = "PolarOPAlgorithm";
    private int timeDelay = 10;
    @Override
    public void run(ProblemInstance instance) {
        if (instance.orders.isEmpty()){
            return;
        }
        buildIndex(instance);
        double[] resDriver = new double[ZoneDemandTable.TOTAL_ZONE_COUNT];
        double[] expectDemand = new double[ZoneDemandTable.TOTAL_ZONE_COUNT];
        TaxiDemandSupplyOracle orderOracle = instance.orderReal;
        Long currentTimeOffset = instance.currentTimeOffset;
        for (int i = 0; i < ZoneDemandTable.TOTAL_ZONE_COUNT; i++) {

            List<Order> zoneOrders = orderIndex.queryZoneObjects(i+1);
            List<Driver> zoneDrivers = driverIndex.queryZoneObjects(i+1);

            expectDemand[i] = orderOracle.queryDemand(currentTimeOffset + timeDelay, currentTimeOffset+20, i+1);
            while (!zoneDrivers.isEmpty() && !zoneOrders.isEmpty()){
                Driver selectedDriver = zoneDrivers.get(0);
                int selectedOrderIndex = -1;
                double longestTrip = -1;
                for (int j = 0; j < zoneOrders.size(); j++) {
                    double tripLength = zoneOrders.get(j).getDistance(selectedDriver);
                    if (tripLength > longestTrip){
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
            if (zoneDrivers.isEmpty()) {
                resDriver[i] = 0;
            } else {
                resDriver[i] = zoneDrivers.size();
            }
        }
        double[][] assign = arrange(resDriver, expectDemand);
        for (int i = 0; i < ZoneDemandTable.TOTAL_ZONE_COUNT; i++) {

            List<Driver> zoneDrivers = driverIndex.queryZoneObjects(i + 1);
            int arrangeCount = 0;
            int j = 0;
            while (!zoneDrivers.isEmpty() && j < assign[i].length) {
                if (i == j) {
                    j += 1;
                    continue;
                }
                if (arrangeCount < (int)assign[i][j]) {
                    Driver driver = zoneDrivers.get(0);
                    driver.goHotZone(j+1, instance.currentTimeOffset, 0);
                    arrangeCount += 1;
                    zoneDrivers.remove(driver);
                } else {
                    arrangeCount = 0;
                    j += 1;
                }
            }
        }
    }

    public double[][] arrange(double[] resDriver, double[] demandOrder) {
        double[][] assigns = new double[resDriver.length][demandOrder.length];
        FlowNetwork flowNetwork = new FlowNetwork(resDriver.length + demandOrder.length + 2);
        for (int i = 0; i < resDriver.length; i++) {
            if (resDriver[i] != 0) {
                flowNetwork.addEdge(new FlowEdge(0, i+1, resDriver[i]));
            }
        }
        for (int i = 0; i < resDriver.length; i++) {
            for (int j = 0; j < demandOrder.length; j++) {
                if (resDriver[i] !=0 && demandOrder[i] != 0){
                    flowNetwork.addEdge(new FlowEdge(i+1, j+1 + resDriver.length, 10000));
                }
            }
        }
        for (int i = 0; i < demandOrder.length; i++) {
            if (demandOrder[i] != 0) {
                flowNetwork.addEdge(new FlowEdge(i+1 + resDriver.length,
                        resDriver.length + demandOrder.length + 1, demandOrder[i]));
            }
        }

        for (int i = 0; i < resDriver.length; i++) {
            for (int j = 0; j < demandOrder.length; j++) {
                assigns[i][j] = flowNetwork.getEageFlow(i+1, j+1 + resDriver.length);
            }
        }
        return assigns;
    }



    @Override
    public String getInfo() {
        return info;
    }
}
