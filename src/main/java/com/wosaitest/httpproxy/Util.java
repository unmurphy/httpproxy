package com.wosaitest.httpproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yangzhixiang on 2017/6/27.
 */
public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public static int getWait(String waitTime) {
        int time = 0;
        try {
            time = Integer.parseInt(waitTime);
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
        }
        return time;
    }

    public static boolean isNumeric(String resp) {
        try {
            Integer.parseInt(resp);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Object getResponse(String resp) {
        boolean flag = isNumeric(resp);
        if (flag) {
            return getRespStatus(Integer.parseInt(resp));
        }
        return resp;
    }

    public static HttpResponseStatus getRespStatus(int code) {
        HttpResponseStatus status;
        switch (code) {
            case 200:
                status = HttpResponseStatus.OK;
                break;
            case 400:
                status = HttpResponseStatus.BAD_REQUEST;
                break;
            case 401:
                status = HttpResponseStatus.UNAUTHORIZED;
                break;
            case 403:
                status = HttpResponseStatus.FORBIDDEN;
                break;
            case 404:
                status = HttpResponseStatus.NOT_FOUND;
                break;
            case 408:
                status = HttpResponseStatus.REQUEST_TIMEOUT;
                break;
            case 500:
                status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                break;
            case 501:
                status = HttpResponseStatus.NOT_IMPLEMENTED;
                break;
            case 502:
                status = HttpResponseStatus.BAD_GATEWAY;
                break;
            case 503:
                status = HttpResponseStatus.SERVICE_UNAVAILABLE;
                break;
            case 504:
                status = HttpResponseStatus.GATEWAY_TIMEOUT;
                break;
            default:
                status = HttpResponseStatus.OK;
                break;
        }
        return status;
    }
}
