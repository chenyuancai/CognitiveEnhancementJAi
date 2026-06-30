package cn.cyc.ai.cog.sdk;

import java.io.IOException;

/**
 * SDK HTTP 传输接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
interface CogSdkTransport {

    /**
     * 发送 HTTP 请求。
     *
     * @param request SDK HTTP 请求
     * @return SDK HTTP 响应
     * @throws IOException          IO 异常
     * @throws InterruptedException 中断异常
     */
    SdkHttpResponse send(SdkHttpRequest request) throws IOException, InterruptedException;
}
