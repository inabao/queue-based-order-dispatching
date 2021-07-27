package riderdispatcher.algorithms;



import riderdispatcher.core.Order;
import riderdispatcher.core.ProblemInstance;
import riderdispatcher.preProcess.DatasetGenerater;
import riderdispatcher.simulator.DataBatchProvider;
import riderdispatcher.simulator.OrderDistribution;
import riderdispatcher.simulator.ProblemInstanceLoader;
import riderdispatcher.utils.Constants;
import riderdispatcher.utils.MyLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by bigstone on 21/6/2017.
 * For schedule algorithms with predefined orders
 */
public class AlgorithmEngine {
    List<BaseAlgorithm> algorithms = new ArrayList<>();
    MyLogger logger;
    ProblemInstance instance;

    private int day = 4;
    private int frameLength = 10;//seconds
    public static int driverCount = 4*1000;
    DatasetGenerater generater = new DatasetGenerater();
    public static int round = 0;
    ProblemInstanceLoader loader = new ProblemInstanceLoader();

    List<List> algorithmScores = new ArrayList<>();
    List<List> algorithmTimes = new ArrayList<>();

    Random rand;
    public static int seed;
    public AlgorithmEngine(int seed) throws ParseException {
        this.seed = seed;
        rand = new Random(seed);
        this.algorithms.add(new IdleRatioGreedyAlgorithm(false, "IdleRatioGreedyAlgorithm"));
        this.algorithms.add(new IdleRatioGreedyAlgorithm(true, "RealIdleRatioGreedyAlgorithm"));
        this.algorithms.add(new SimpleLocalSearchAlgorithm(false, "SimpleLocalSearchAlgorithm"));
        this.algorithms.add(new SimpleLocalSearchAlgorithm(true, "RealSimpleLocalSearchAlgorithm"));
        this.algorithms.add(new IdleTimeGreedy(false, "RealIdleTimeGreedyAlgorithm"));
        this.algorithms.add(new IdleTimeGreedy(true, "IdleTimeGreedyAlgorithm"));
        this.algorithms.add(new LongTripGreedyAlgorithm());
        this.algorithms.add(new ShortestTripGreedyAlgorithm());
        this.algorithms.add(new RandomAlgorithm());
        this.algorithms.add(new NeareastGreedyAlgorithm());
        this.algorithms.add(new PolarAlgorithm());
        this.algorithms.add(new UpperAlgorithm());


        algorithms.forEach(algorithm -> {
            algorithmScores.add(new ArrayList<>());
            algorithmTimes.add(new ArrayList<>());
        });
    }


    public void varyDriverCounts() throws IOException, ParseException, IllegalAccessException, InstantiationException {
        logger = new MyLogger("varyingDriver.txt", seed);
////        varyingDriver("DriversCount:500", 500);
//        varyingDriver("DriversCount:800", 800);
        varyingDriver("DriversCount:1K", 1000);
        varyingDriver("DriversCount:2K", 2*1000);
        varyingDriver("DriversCount:3K", 3*1000);
        varyingDriver("DriversCount:4K", 4*1000);
        varyingDriver("DriversCount:5K", 5*1000);
//        varyingDriver("DriversCount:6K", 6*1000);
//        varyingDriver("DriversCount:8K", 8*1000);
//        varyingDriver("DriversCount:10K", 10*1000);

    }

    public void varyFrameLength() throws IOException, ParseException, IllegalAccessException, InstantiationException {
        logger = new MyLogger("varyingFrameLength.txt", seed);
        varyingFrameLength("FrameLength:3", 3);
        varyingFrameLength("FrameLength:5", 5);
        varyingFrameLength("FrameLength:10", 10);
        varyingFrameLength("FrameLength:20", 20);
        varyingFrameLength("FrameLength:30", 30);
    }




    public void varyingMaxWaitingTimes() throws IOException, ParseException, IllegalAccessException, InstantiationException {
        logger = new MyLogger("varyingWaitingTime.txt", seed);
        varyingMaxWaitingTime("baseTime:60", 60);
        varyingMaxWaitingTime("baseTime:120", 120);
        varyingMaxWaitingTime("baseTime:180", 180);
        varyingMaxWaitingTime("baseTime:240", 240);
        varyingMaxWaitingTime("baseTime:300", 300);
//        varyingMaxWaitingTime("baseTime:360", 360);

    }

    public void varyingLookupLengths() throws IOException, ParseException, IllegalAccessException, InstantiationException {
        logger = new MyLogger("varyingLookupLength.txt", seed);
        varyingLookupLength("lookup:300", 300);
        varyingLookupLength("lookup:600", 600);
        varyingLookupLength("lookup:900", 900);
        varyingLookupLength("lookup:1200", 1200);
        varyingLookupLength("lookup:2400", 2400);
        varyingLookupLength("lookup:3600", 3600);
        varyingLookupLength("lookup:4800", 4800);
        varyingLookupLength("lookup:6000", 6000);

    }


    private void loadProblem(String info, int day, int driverCount) throws UnsupportedEncodingException, FileNotFoundException, InstantiationException, ParseException, IllegalAccessException {

        instance = loader.loadProblemInstance(day, driverCount);
        instance.info = info;

    }


    private void varyingLookupLength(String info, long lookupLength) throws IOException, ParseException, IllegalAccessException, InstantiationException {
        loadProblem(info, day, driverCount);
        Constants.showlookupLength = lookupLength;
        runAlgorithms();
    }

    private void varyingDriver(String info, int driverCount) throws IOException, ParseException, IllegalAccessException, InstantiationException {
        loadProblem(info, day, driverCount);
        Constants.showlookupLength = 5*60;
        Constants.lookupLength = 5*60;
        runAlgorithms();
    }

    private void varyingFrameLength(String info, int frameLength) throws IOException, ParseException, IllegalAccessException, InstantiationException {
        loadProblem(info, day, this.driverCount);
        Constants.showlookupLength = 5*60;
        this.frameLength = frameLength;
        Constants.lookupLength = 5*60;
        runAlgorithms(); 
    }


    private void varyingMaxWaitingTime(String info, double baseWaitingTime) throws IOException, ParseException, IllegalAccessException, InstantiationException {
        loadProblem(info, day, driverCount);
        Constants.showlookupLength = 5*60;
        Constants.lookupLength = 5*60;
        generater.regenerateMaxWaitingTimes(instance, baseWaitingTime, rand);

        runAlgorithms();
    }

    public void run() throws IOException, ParseException, IllegalAccessException, InstantiationException {
        varyDriverCounts();
        varyingMaxWaitingTimes();
        varyFrameLength();
        varyingLookupLengths();

    }


    private void runAlgorithms() throws IOException{
        round += 1;
        for (BaseAlgorithm algorithm : algorithms) {
            ProblemInstance tmpInstance = new ProblemInstance(instance);
            OrderDistribution.getInstance().setInstance();
            long millisStart = Calendar.getInstance().getTimeInMillis();
            DataBatchProvider batchProvider = new DataBatchProvider(tmpInstance, frameLength);

            ProblemInstance lastInstance = null;
            ProblemInstance currentInstance;
            while ((currentInstance = batchProvider.fetchCurrentProblemInstance())!= null){
                if (lastInstance != null){
                    long currentTimeOffset = currentInstance.currentTimeOffset;
                    for (Order order: lastInstance.orders){
                        if (!order.isExpired(currentTimeOffset) && !order.isAssigned()){
                            currentInstance.orders.add(order);
                        }

                        if (order.isAssigned()){
                            tmpInstance.completedOrders.add(order);
                        }

                        if (!order.isAssigned() && order.isExpired(currentTimeOffset)){
                            tmpInstance.expiredOrders.add(order);
                        }
                    }
                }
                algorithm.run(currentInstance);

                lastInstance = currentInstance;
                tmpInstance.currentTimeOffset = currentInstance.currentTimeOffset;

            }
            long millisEnd = Calendar.getInstance().getTimeInMillis();
            tmpInstance.info = algorithm.getInfo() + ":" + tmpInstance.info;
            tmpInstance.startRunningMillis = millisStart;
            tmpInstance.endRunningMillis = millisEnd;

//            int index = algorithms.indexOf(algorithm);
//            algorithmTimes.get(index).add((double)millisEnd - millisStart);
//            algorithmScores.get(index).add(tmpInstance.calculateTotalDistance());

            logger.logResult(tmpInstance, algorithm);
        }
        System.out.println();

    }

}
