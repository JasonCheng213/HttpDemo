package com.demo.http;

import com.google.gson.annotations.SerializedName;
import com.winwin.common.http.response.ErrorMessage;
import com.winwin.common.http.response.IData;

/**
 * Created by Jason on 2017/9/2.
 */

public class WeatherBean implements IData {

    @SerializedName("weatherinfo")
    public WeatherinfoBean weatherinfo;

    @Override
    public int getBusinessCode() {
        return ErrorMessage.CODE_SUCCESS;
    }

    public static class WeatherinfoBean {
        @SerializedName("city")
        public String city;
        @SerializedName("cityid")
        public String cityid;
        @SerializedName("temp")
        public String temp;
        @SerializedName("WD")
        public String WD;
        @SerializedName("WS")
        public String WS;
        @SerializedName("SD")
        public String SD;
        @SerializedName("WSE")
        public String WSE;
        @SerializedName("time")
        public String time;
        @SerializedName("isRadar")
        public String isRadar;
        @SerializedName("Radar")
        public String Radar;
        @SerializedName("njd")
        public String njd;
        @SerializedName("qy")
        public String qy;
        @SerializedName("rain")
        public String rain;
    }
}
