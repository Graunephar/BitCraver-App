package com.graunephar.bitcraver.network;

public interface ResponseCallback {


    void onSuccess(String data);

    void onError(String message);


}
