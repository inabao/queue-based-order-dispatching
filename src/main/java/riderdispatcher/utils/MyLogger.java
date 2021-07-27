package riderdispatcher.utils;

import com.sun.javafx.scene.traversal.Algorithm;
import riderdispatcher.algorithms.BaseAlgorithm;
import riderdispatcher.algorithms.IdleRatioGreedyAlgorithm;
import riderdispatcher.core.Order;
import riderdispatcher.core.ProblemInstance;
import riderdispatcher.simulator.OrderDistribution;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Created by bigstone on 23/6/2017.
 */
public class MyLogger {

    private static PrintWriter pw;

    public MyLogger(String fileName, int seed) throws IOException {
        pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(Constants.RESULT_DIR + Calendar.getInstance().getTimeInMillis() + String.format("_%d_", seed)+ fileName), "utf-8"));

    }

    public void logResult(ProblemInstance instance, BaseAlgorithm algorithm) throws IOException {


        String template = "{info},RunningTime:{running},servingTime:{servingTime},assignedOrderCount:{orderCount}";
        template = template.replace("{info}", instance.info);
        template = template.replace("{running}", String.valueOf(((double)instance.endRunningMillis - instance.startRunningMillis)));
        template = template.replace("{servingTime}", String.valueOf(instance.calculateTotalServingTime()));
        template = template.replace("{orderCount}", String.valueOf(instance.calculateTotalAssignedOrderCount()));
//        template = template.replace("{orderdistribute}", OrderDistribution.getInstance().toString());
//        template = template.replace("{revenue}", String.valueOf(instance.calculateTotalRevenue()));


        pw.println(template);
        pw.flush();
        System.out.println(template);
    }

}