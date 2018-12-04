package onair.onems.models;

public class PaymentMethodModel {
    private int PaymentMethodID;
    private String PaymentMethodName;
    private boolean IsActive;
    private boolean IsDeleted;

    public PaymentMethodModel() {
        PaymentMethodID = 0;
        PaymentMethodName = "";
        IsActive = false;
        IsDeleted = true;
    }

    public PaymentMethodModel(String PaymentMethodID, String paymentMethodName, String IsActive, String IsDeleted) {
        if(PaymentMethodID.equals("null") || PaymentMethodID.equals("")) {
            this.PaymentMethodID = 0;
        } else {
            this.PaymentMethodID = Integer.parseInt(PaymentMethodID);
        }

        PaymentMethodName = paymentMethodName;

        this.IsActive = !IsActive.equals("null") && !IsActive.equals("") && Boolean.parseBoolean(IsActive);
        this.IsDeleted = IsDeleted.equals("null") || IsDeleted.equals("") || Boolean.parseBoolean(IsDeleted);
    }

    public void setPaymentMethodID(String PaymentMethodID) {
        if(PaymentMethodID.equals("null") || PaymentMethodID.equals("")) {
            this.PaymentMethodID = 0;
        } else {
            this.PaymentMethodID = Integer.parseInt(PaymentMethodID);
        }
    }

    public int getPaymentMethodID() {
        return PaymentMethodID;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        PaymentMethodName = paymentMethodName;
    }

    public String getPaymentMethodName() {
        return PaymentMethodName;
    }

    public void setIsActive(String IsActive) {
        this.IsActive = !IsActive.equals("null") && !IsActive.equals("") && Boolean.parseBoolean(IsActive);
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public void setIsDeleted(String IsDeleted) {
        this.IsDeleted = IsDeleted.equals("null") || IsDeleted.equals("") || Boolean.parseBoolean(IsDeleted);
    }

    public boolean getIsDeleted() {
        return IsDeleted;
    }
}
