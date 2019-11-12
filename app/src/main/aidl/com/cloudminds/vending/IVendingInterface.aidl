package com.cloudminds.vending;

import com.cloudminds.vending.IVendingListener;

interface IVendingInterface {

    void faceRecognize(in byte[] face);

    void commodityRecognize(in List<String> imageList, String eventId, String extraType);

    void reportStatus(String event, int status);

    void reportError(String code, String msg, String extra);

    void registerCallback(IVendingListener l);

    void unregisterCallback(IVendingListener l);
}
