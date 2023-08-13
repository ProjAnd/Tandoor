package tandoori.resturant.mobile.ModelClass;

/**
 * Created by HP_Owner_Android on 5/28/2018.
 */

public class CardModel {
    public String cardNumber;
    public String cardExpMonth;
    public String cardExpYear;
    public String cardCVC;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpMonth() {
        return cardExpMonth;
    }

    public void setCardExpMonth(String cardExpMonth) {
        this.cardExpMonth = cardExpMonth;
    }

    public String getCardExpYear() {
        return cardExpYear;
    }

    public void setCardExpYear(String cardExpYear) {
        this.cardExpYear = cardExpYear;
    }

    public String getCardCVC() {
        return cardCVC;
    }

    public void setCardCVC(String cardCVC) {
        this.cardCVC = cardCVC;
    }
}
