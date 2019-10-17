package com.cloudminds.vending.ui;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.StringDef;

import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.FACE_DETECT;
import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.LOCK_OPENED;
import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.OPEN_SERVICE;
import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.PAYMENT_INFO;
import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.SCAN_CODE;
import static com.cloudminds.vending.ui.IFragSwitcher.FragDefines.SETTLE_UP;

public interface IFragSwitcher extends Serializable {

    String TARGET_FRAG = "target_frag";
    String FRAG_NAME = "frag_name";

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @StringDef({FACE_DETECT, SCAN_CODE, OPEN_SERVICE, LOCK_OPENED, SETTLE_UP, PAYMENT_INFO})
    @interface FragDefines {
        String FACE_DETECT = "FACE_DETECT", SCAN_CODE = "SCAN_CODE", OPEN_SERVICE = "OPEN_SERVICE",
                LOCK_OPENED = "LOCK_OPENED", SETTLE_UP = "SETTLE_UP", PAYMENT_INFO = "PAYMENT_INFO";
    }

    void switchFragTo(@FragDefines String fragName);
}
