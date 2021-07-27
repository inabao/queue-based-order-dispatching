/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package riderdispatcher.utils;

/**
 *
 * @author bigstone
 */
public class Constants {

//    public static final String DATASET_DIR = "D:\\data\\project\\queueing-car-hailing-framework\\dataset\\raw_data\\";
    public static final String DATASET_DIR = "D:\\data\\project\\queueing-car-hailing-framework\\dataset\\raw_data\\";
    public static final String OUTPUT_DIR = "D:\\data\\project\\queueing-car-hailing-framework\\dataset\\raw_data\\";
    public static final String RESULT_DIR = "D:\\data\\project\\queueing-car-hailing-framework\\results\\";


    public static final String REAL_DEMAND_FILE_NAME = "deepst_gc.txt_real";
    public static final String NEW_REAL_DEMAND_FILE_NAME = "new_deepst_gc.txt_real";
    public static final String PREDICTION_FILE_NAME = "deepst_gc.txt";
    public static final String NEW_PREDICTION_FILE_NAME = "new_deepst_gc.txt";
    public static final String YELLOW_ORDER_FILE_NAME = "yellow_tripdata_2018-06.csv";
    public static final String GREEN_ORDER_FILE_NAME = "green_tripdata_2018-06.csv";
    public static final String CLEAN_ORDER_RECORD_FILE_NAME = "clean_tripdata_2018-06.csv";
    public static final String DEMAND_DISTRIBUTION_FILE_NAME = "demand_distribution_2018-06.csv";
    public static final String DRIVER_DISTRIBUTION_FILE_NAME = "driver_distribution_2018-06.csv";


    public static final String ORDER_BASIC_FILE_NAME = "orders_basic.txt";
    public static final String DRIVER_BASIC_TXT_FILE_NAME = "drivers_basic.txt";


    public static final String POINT_BASIC_FILE_NAME = "points_basic.json";

    public static final String TAG_UNIFORM = "Uniform_";

    public static final String TAG_WHOLE_REAL = "WholeReal_";

    public static final String TAG_SAMPLE_REAL = "SampleReal_";




    public static final String TKY_CHECKIN_FILE_NAME = "dataset_TSMC2014_TKY.txt";

    public static double alpha = 0.5;

    public static double s = 1;//constant for calculating the topic score fo worker/task

    public static double k = 1;// propagation factor

    public static boolean IS_DEBUG = false;

    public static long lookupLength = 5*60;

    public static long showlookupLength = 5*60;
}
