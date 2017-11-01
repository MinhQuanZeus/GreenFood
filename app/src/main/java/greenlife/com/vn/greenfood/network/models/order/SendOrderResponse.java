package greenlife.com.vn.greenfood.network.models.order;



public class SendOrderResponse {
    private int success;
    private int failure;

    public SendOrderResponse(int success, int failure) {
        this.success = success;
        this.failure = failure;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }
}
