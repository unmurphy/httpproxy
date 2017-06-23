# HttpProxy背景与介绍

该工具是基于开源http代理服务器：littleProxy的框架基础上做的，我大致看了该框架的介绍，运用了netty的网络库，因此性能方面还是挺稳定的，也比较方便整合到我的工具里面，并且作者还将该框架与市面上其他代理服务器分别做了压测，从他的测试数据方面看还是很可观的，git地址：https://github.com/adamfisk/LittleProxy

## 工具实现方式
当初考虑的就是从代理服务器中拿到服务器的response进行修改，而LittleProxy正好也提供了这样的接口给我们，实现方面也比较简单，具体就是启动http server的时候追加一个过滤监听器，在过滤部分里面实现接口篡改部分。

## 数据准备
篡改规则从本地的json文件中读取，程序启动的时候会事先加载到内存里面，该文件其实是一个json数组，类似于这样：
```
[
    {
        "path": "/api/v1/merchants",
        "method": "get",
        "response": {
            "code": "200",
            "message": ""
        }
    },
    {
        "path": "/api/v1/merchant_audits",
        "method": "get",
        "response": {
            "code": "200",
            "message": ""
        }
    }
]
```
这示例里面写了2个规则，每个规则里面包含path，method，response，代码里面将path和method存放到一个KeyPair对象里面，最终数据的存储方式是一个Map<KeyPair, reponse>的方式，然后与过滤器里面获取到的KeyPair进行比对，比对成功的话会将该对应的response篡改掉，达到实现篡改目的。如果过滤器里面KeyPair没有比对成功，服务器原来的响应消息不会做任何修改，会将原有response透传给客户端。

## 工具测试方式
用maven打成jar包，执行jar即可，手机修改一下代理服务器地址，监听端口9911(可在程序里面修改)即可测试手机app，web的话只需要修改一下浏览器http代理即可。目前有两种模式
1. CAPTURE_MODE，抓包模式

```
java -jar httpagent-0.0.1-SNAPSHOT.jar 0 data.json
```

2. FIDDLE_MODE，篡改模式

```
java -jar httpagent-0.0.1-SNAPSHOT.jar 1 data.json
```
