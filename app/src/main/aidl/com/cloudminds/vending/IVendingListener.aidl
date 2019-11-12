package com.cloudminds.vending;

interface IVendingListener {

   void onFaceRecognize(String result);

   void onCommodityRecognize(String result);

   void onReceiveMessage(String msg);
}
