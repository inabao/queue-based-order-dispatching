package riderdispatcher.simulator;

public class OrderDistribution {
    private static OrderDistribution instance = null;
    long moreDriver;
    long moreOrder;
    private OrderDistribution(){
        this.moreDriver = 0;
        this.moreOrder = 0;
    }

    public long getMoreDriver() {
        return moreDriver;
    }

    public long getMoreOrder() {
        return moreOrder;
    }

    public void addMoreDriver() {
        moreDriver ++;
    }

    public void addMoreOrder() {
        moreOrder ++;
    }

    public static OrderDistribution getInstance() {
        if (instance == null) {
            instance = new OrderDistribution();
        }
        return instance;
    }

    public void setInstance() {
        instance = new OrderDistribution();
    }

    @Override
    public String toString() {
        return "OrderDistribution{" +
                "moreDriver=" + moreDriver +
                ", moreOrder=" + moreOrder +
                '}';
    }
}
