package com.example.fcmchatapplication.model;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.example.fcmchatapplication.R;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CurrentAddressDecode {
    public  void getAddressFromLocation(final double latitude, final double longitude,
                                        final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }

                        sb.append(address.getAddressLine(0));
                        result = sb.toString();
                    }
                } catch (IOException e) {
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString(context.getResources().getText(R.string.loc_address).toString(), result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result=context.getText(R.string.unlocation).toString();

                        bundle.putString(context.getResources().getText(R.string.loc_address).toString(), result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}