package cn.cyc.ai.cog.infra.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.trace.TraceContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常处理测试控制器。
 *
 * @author cyc
 */
@RestController
class ExceptionTestController {

    /**
     * 触发业务异常。
     *
     * @return 永不返回
     */
    @GetMapping("/test/business")
    String businessError() {
        throw new BusinessException("INVALID_ARGUMENT", "参数不合法");
    }

    /**
     * 触发系统异常。
     *
     * @return 永不返回
     */
    @GetMapping("/test/system")
    String systemError() {
        throw new IllegalStateException("boom");
    }

    /**
     * 返回当前 TraceId。
     *
     * @return TraceId 响应
     */
    @GetMapping("/test/trace")
    ApiResponse<String> trace() {
        return ApiResponse.success(TraceContext.getTraceId(), TraceContext.getTraceId());
    }

    /**
     * 回显必填请求参数。
     *
     * @param name 名称参数
     * @return 原始名称
     */
    @GetMapping("/test/required-param")
    String requiredParam(@RequestParam("name") String name) {
        return name;
    }

    /**
     * 回显整型参数。
     *
     * @param value 整型参数
     * @return 原始整数
     */
    @GetMapping("/test/int-param")
    int intParam(@RequestParam("value") int value) {
        return value;
    }

    /**
     * 回显 JSON 请求值。
     *
     * @param request 请求体
     * @return 字段值
     */
    @PostMapping(path = "/test/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    String json(@RequestBody EchoRequest request) {
        return request.value();
    }

    /**
     * JSON 回显请求对象。
     *
     * @param value 输入值
     * @author cyc
     */
    record EchoRequest(String value) {
    }
}
