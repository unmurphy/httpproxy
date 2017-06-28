package com.wosaitest.httpproxy;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wosaitest.httpproxy.KeyPair.MethodType;

/**
 * Created by yangzhixiang on 2017/6/22.
 */
public class ParserJson {

    private static final Logger log = LoggerFactory.getLogger(ParserJson.class);
    private static final String FILE_PATH = System.getProperty("user.dir") + "/jsondata/";
    private static Map<KeyPair, DataPair> map = new HashMap<>();

    public static Map<KeyPair, DataPair> getJsonString(String filename) {
        String filepath = FILE_PATH + filename;
        File file = new File(filepath);
        if (!file.exists()) {
            return null;
        }
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(file));
            for (Object obj : jsonArray) {
                JSONObject json = (JSONObject) obj;
                String path = "(.*)" + json.get("path").toString();
                if (path.contains("*")) {
//					path = path.replaceAll("/\\*", "/\\\\w*");
                    path = path.replaceAll("/\\*", "/[A-Za-z0-9\\-]+");
                }
                MethodType method = getMethodType(json.get("method").toString());
                int wait = Util.getWait(json.get("wait").toString());
                Object response = Util.getResponse(json.get("response").toString());
                map.put(new KeyPair(path, method), new DataPair(wait, response));
            }
            return map;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static MethodType getMethodType(String method) {
        MethodType methodType = MethodType.get;
        if (method == null || method.equalsIgnoreCase("get")) {
            methodType = MethodType.get;
        } else if (method.equalsIgnoreCase("post")) {
            methodType = MethodType.post;
        } else if (method.equalsIgnoreCase("patch")) {
            methodType = MethodType.patch;
        } else if (method.equalsIgnoreCase("put")) {
            methodType = MethodType.put;
        } else if (method.equalsIgnoreCase("delete")) {
            methodType = MethodType.delete;
        }
        return methodType;
    }


}