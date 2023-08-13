package tandoori.resturant.mobile.CustomToast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import tandoori.resturant.mobile.R;

public class CustomToast {
    public static void show(Context context, String text, boolean isLong, Drawable drawable) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.customtoast, null);
        LinearLayout linearLayout = layout.findViewById(R.id.linearLayout);
        linearLayout.setBackgroundDrawable(drawable);
        ImageView image = (ImageView) layout.findViewById(R.id.toast_image);
        image.setImageResource(R.drawable.ic_check_circle_white_24dp);

        TextView textV = (TextView) layout.findViewById(R.id.toast_text);
        textV.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 00, (int) context.getResources().getDimension(R.dimen._210sdp));
        toast.setDuration((isLong) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}