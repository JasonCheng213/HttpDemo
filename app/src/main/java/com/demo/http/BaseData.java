package com.demo.http;


import com.winwin.common.http.response.ErrorMessage;
import com.winwin.common.http.response.IData;

/**
 * Created by Jason on 2017/9/3.
 */

public class BaseData implements IData {

    public int resultCode = -1;
    public String message;

    @Override
    public int getBusinessCode() {
        if (resultCode == 0) {
            return ErrorMessage.CODE_SUCCESS;
        }
        return resultCode;
    }
}
