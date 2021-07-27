package riderdispatcher.estimator;

import riderdispatcher.core.Order;
import riderdispatcher.simulator.OrderDistribution;

import static riderdispatcher.utils.Constants.lookupLength;

public class IdleTimeEstimator {
    public static double beta = 2;//good choice for beta
    private static double cutThreshold = 0.00001;
    private static int iterationThreshold = 20;
    private static double positivate;

    public static double estimateIdleTime(double lambda, double mu, int K, boolean simple) {
        if (simple) {
            return estimateIdleTimeSimple(lambda, mu, K);
        } else {
            return estimateIdleTime(lambda, mu, K);
        }
    }

    public static double estimateIdleTimeMix(double lambda, double mu, int K) {
        if (lambda > mu) {
            return estimateIdleTimeSimple(lambda, mu, K);
        } else {
            return estimateIdleTime(lambda, mu, K);
        }

    }

    public static double estimateIdleTimeSimple(double lambda, double mu, int K){
        if (lambda == 0){
            return lookupLength/60;
        }
//     lambda 是订单的生成速率, mu是车的生成速率
        if (lambda>=mu){
            OrderDistribution.getInstance().addMoreOrder();
            return 1d/lambda;
        } else {
            OrderDistribution.getInstance().addMoreDriver();
            return K/lambda + 1d/lambda;
        }
    }

    //lambda : the rate of riders, mu: the rate of drivers, K: maximum driver count
    public static double estimateIdleTime(double lambda, double mu, int K){
        double idleTime = 0;
        double p0 = pZore(lambda, mu, K);
        if (lambda == mu){
            idleTime = p0 * ((K+1)*(K+2) / (2*lambda) + positivate/lambda);
        }

        if (lambda < mu){
            OrderDistribution.getInstance().addMoreDriver();
//            double middle = (K+1)*Math.pow(mu/lambda, K+2);
//            middle -= K*Math.pow(mu/lambda, K+1);
//            middle -= 2*mu/lambda;
//            middle += 1;
//
//            middle = middle/Math.pow((mu/lambda - 1), 2);
//            idleTime = p0 * middle;

            idleTime = p0 *
                    (((K+1)*Math.pow(mu/lambda, K+2) - (K+2)*Math.pow(mu/lambda, K+1) + 1) /
                    (Math.pow((mu/lambda - 1), 2)*lambda) + positivate/lambda);
        }

        if (lambda > mu){
            OrderDistribution.getInstance().addMoreOrder();
            idleTime = p0 *( mu / Math.pow(lambda - mu, 2) + positivate/lambda);
        }

        if (lambda == 0){
            idleTime = lookupLength/60;//if idle time is too large, change it to a large enough value
        } else {
            if (mu > lambda && (Double.isInfinite(idleTime)) || Double.isNaN(idleTime)){
                if (Double.isNaN(idleTime)){
                    idleTime = 1/lambda;//if idle time is too large, change it to a large enough value
                }

                if (Double.isInfinite(idleTime)){
                    idleTime = lookupLength/60;//if idle time is too large, change it to a large enough value
                }

            }
        }
        return idleTime;//in minutes
    }

    public static double pZore(double lambda, double mu, double K){
        positivate = 0;
        double p0 = 1/(pLeftBottom(lambda,mu,K) + positivate);
//        positivate *= 2;

//        if (lambda == mu){
//            p0 = 1/(K + 1 + positiveAccumulateValue(lambda, mu));
//        }
//
//        if (lambda < mu){
//            p0 = 1/((Math.pow(mu/lambda, K+1) - 1)/(mu/lambda - 1) + positiveAccumulateValue(lambda, mu));
//        }
//
//        if (lambda > mu){
//            p0 = 1/(lambda/(lambda - mu) + positiveAccumulateValue(lambda, mu));
//        }
        return p0;
    }

    public static double ratio(double lambda, double mu, double K){
        double ratio = 1;
        if (lambda == mu){
            ratio = ((K+1)*(K+2)) / (2*lambda);
        }

        if (lambda < mu){
            ratio = ((K+1)*Math.pow(mu/lambda, K+2) - K*Math.pow(mu/lambda, K+1) - 2*mu/lambda + 1) /
                    (Math.pow((mu/lambda - 1), 2)*lambda);
        }

        if (lambda > mu){
            ratio = lambda / Math.pow(lambda - mu, 2);
        }

        return ratio;
    }

    public static double pLeftBottom(double lambda, double mu, double K){
        double pLB = 0;
        if (lambda == mu){
            pLB = K + 1;
        }

        if (lambda < mu){
            pLB =(Math.pow(mu/lambda, K+1) - 1)/(mu/lambda - 1);
        }

        if (lambda > mu){
            pLB = mu/(lambda - mu);
        }
        return pLB;
    }

    public static double positiveAccumulateValue(double lambda, double mu){
        double pAcc = 0;
        double ratio;
        double multipleElment = 1;
        double rExp = Math.exp(beta/mu);
        double r = 1;

//        for (int i = 0; i < iterationThreshold; i++) {
//            r = r*rExp;
//            ratio = lambda / (mu + r);
//            multipleElment = multipleElment * ratio;
//
//            pAcc += multipleElment;
//        }

        while (multipleElment > cutThreshold){
            r = r*rExp;
            ratio = lambda / (mu + r);
            multipleElment = multipleElment * ratio;

            pAcc += multipleElment;
        }

        return pAcc;
    }

    public static double rn(int n, double mu){
        double rn = 1;
        double rExp = Math.exp(beta/mu);
        for (int i = 0; i < n; i++) {
            rn = rn*rExp;
        }

        return rn;
    }
}
