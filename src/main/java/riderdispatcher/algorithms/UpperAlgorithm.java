package riderdispatcher.algorithms;

import com.sun.org.apache.xpath.internal.operations.Or;
import riderdispatcher.core.Driver;
import riderdispatcher.core.Order;
import riderdispatcher.core.ProblemInstance;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;

public class UpperAlgorithm extends BaseAlgorithm {
    private String info = "UpperAlgorithm";
    @Override
    public void run(ProblemInstance instance) {

        int orderCount = instance.orders.size();
        int driverCount = instance.drivers.size();
        for (int i = 0; i < Math.min(orderCount, driverCount); i++) {
            instance.drivers.get(i).serveOrder(instance.orders.get(i), instance.currentTimeOffset);
        }

    }

    @Override
    public String getInfo() {
        return info;
    }
}
