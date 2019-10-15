package com.cloudminds.vending.ui;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.StringDef;

import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.FACE_DETECT;
import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.PAYMENT_INFO;
import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.QR_CODE;
import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.SETTLE;

public interface IFragSwitcher extends Serializable {

    String TARGET_FRAG = "target_frag";
    String FRAG_NAME = "frag_name";

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @StringDef({FACE_DETECT, QR_CODE, SETTLE, PAYMENT_INFO})
    @interface FragDefines {
        String FACE_DETECT = "FACE_DETECT", QR_CODE = "QR_CODE", SETTLE = "SETTLE", PAYMENT_INFO = "PAYMENT_INFO";
    }

    void switchFragTo(@FragDefines String fragName);
}
