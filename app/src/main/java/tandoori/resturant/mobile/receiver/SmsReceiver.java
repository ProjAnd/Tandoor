package tandoori.resturant.mobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;



public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //You must check here if the sender is your provider and not another one with same text.

            String messageBody = smsMessage.getMessageBody();
            String verificationCode = getVerificationCode(messageBody);

            //Pass on the text to our listener.
            mListener.messageReceived(verificationCode);
        }
    }

    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf("");

        if (index != -1) {
            int start = index + 2;
            int length = 4;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}