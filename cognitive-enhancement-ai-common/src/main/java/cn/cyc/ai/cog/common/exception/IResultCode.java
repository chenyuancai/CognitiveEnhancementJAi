package cn.cyc.ai.cog.common.exception;

/**
 * 统一返回码契约，便于各模块定义自有错误码枚举（借鉴 zcloud-core-common）。
 *
 * @author cyc
 */
public interface IResultCode {

    /** 业务码（字符串，与平台 {@code ApiResponse.code} 对齐）。 */
    String getCode();

    /** 提示信息。 */
    String getMessage();
}
