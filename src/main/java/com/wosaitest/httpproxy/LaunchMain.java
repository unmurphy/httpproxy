package com.wosaitest.httpproxy;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.*;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yangzhixiang on 2017/6/22.
 */
public class LaunchMain {
    private static final Logger log = LoggerFactory.getLogger(LaunchMain.class);
    private static final int LISTEN_PORT = 9911;
    private static final String CAPTURE_TYPE = "CAPTURE_MODE";
    private static final String FIDDLE_TYPE = "FIDDLE_MODE";
    private static int type = 0;
    private static String data_file = "data.json";
    private static Map<KeyPair, DataPair> map = new HashMap<>();

    public static void main(String[] args) {
        if (args.length == 1) {
            type = Integer.parseInt(args[0].trim());
        }
        if (args.length == 2) {
            type = Integer.parseInt(args[0].trim());
            data_file = String.valueOf(args[1]).trim();
        }
        String mode = type == 0 ? CAPTURE_TYPE : FIDDLE_TYPE;
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>Running Mode: " + mode);
        if (type == 1) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>Loading Json Data File: " + data_file);
            map = ParserJson.getJsonString(data_file);
            if (map == null) {
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>Loaded Json Data File Failed: " + data_file);
                return;
            }
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>Load Completed");
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>Start Http Proxy Listening At: " + LISTEN_PORT);
        HttpProxyServer httpProxyServer = DefaultHttpProxyServer.bootstrap().withPort(LISTEN_PORT).withAllowLocalOnly(false)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFiltersAdapter(originalRequest) {

                            @Override
                            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                if (httpObject instanceof HttpRequest) {
                                    HttpRequest httpRequest = (HttpRequest) httpObject;
                                    log.info("Capture HttpRequest:" + httpRequest.getUri());
                                }
                                return null;
                            }


                            @Override
                            public HttpObject proxyToClientResponse(HttpObject httpObject) {
                                if (httpObject instanceof FullHttpResponse) {
                                    FullHttpResponse fullHttpResponse = (FullHttpResponse) httpObject;
                                    return modifyHttpResponse(originalRequest, fullHttpResponse);
                                }
                                return httpObject;
                            }
                        };
                    }

                    @Override
                    public int getMaximumRequestBufferSizeInBytes() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public int getMaximumResponseBufferSizeInBytes() {
                        return Integer.MAX_VALUE;
                    }
                }).start();
    }

    private static FullHttpResponse modifyHttpResponse(HttpRequest orgRequest, FullHttpResponse httpResponse) {
        KeyPair keyPair = new KeyPair(orgRequest.getUri(), ParserJson.getMethodType(orgRequest.getMethod().toString()));
        String acceptRanges = httpResponse.headers().get(HttpHeaderNames.ACCEPT_RANGES);
        if (acceptRanges != null && acceptRanges.contains("bytes")) {
            log.info("Org KeyPair: " + keyPair.toString() + "\n Org Response: Whoops! maybe the type of response is byte stream, so we don't show it");
        } else {
            log.info("Org KeyPair: " + keyPair.toString() + "\n Org Response: " + httpResponse.content().toString(Charset.forName("utf-8")));
        }
        if (type == 1) {
            CompositeByteBuf contentBuf = (CompositeByteBuf) httpResponse.content();
            for (Map.Entry<KeyPair, DataPair> entry : map.entrySet()) {
                KeyPair tmp = entry.getKey();
                if (tmp.equals(keyPair)) {
                    try {
                        DataPair data = entry.getValue();
                        int wait = data.getWait();
                        Object resp = data.getValue();
                        if (resp instanceof String) {
                            log.info("Find Match KeyPair: " + tmp.toString() + "\n New Response: " + resp.toString());
                            ByteBuf newContent = Unpooled.wrappedBuffer(resp.toString().getBytes("utf-8"));
                            int len = newContent.readableBytes();
                            contentBuf.clear().writeBytes(newContent);
                            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, len);
                        } else {
                            log.info("Find Match KeyPair: " + tmp.toString() + "\n New HttpStatusCode: " + ((HttpResponseStatus) resp).code());
                            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, ((HttpResponseStatus) resp));
                            log.info(httpResponse.toString());
                        }
                        if (wait != 0) {
                            log.info("Org KeyPair: " + keyPair.toString() + " response sleep for {} seconds", wait);
                            Thread.sleep(wait * 1000);
                        }
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        log.error(e.getMessage());
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        return httpResponse;
    }
}
