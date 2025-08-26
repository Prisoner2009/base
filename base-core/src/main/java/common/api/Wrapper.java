package common.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import common.constant.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 接口返回格式
 */
@Data
@Schema(description = "接口返回对象")
public class Wrapper<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "成功标志")
    private boolean success = true;

    @Schema(description = "返回处理消息")
    private String message = "";

    @Schema(description = "返回代码")
    private Integer code = 0;

    @Schema(description = "返回数据对象")
    @JsonProperty("data")
    private T result;

    @Schema(description = "时间戳")
    private long timestamp = System.currentTimeMillis();

    // ====== 构造器 ======
    public Wrapper() {
    }

    public Wrapper(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    // ====== 工厂方法 ======
    public static <T> Wrapper<T> ok() {
        return new Wrapper<T>().success(CommonConstant.SC_OK_200, "操作成功");
    }

    public static <T> Wrapper<T> ok(String msg) {
        return new Wrapper<T>().success(CommonConstant.SC_OK_200, msg);
    }

    public static <T> Wrapper<T> ok(T data) {
        return new Wrapper<T>().success(CommonConstant.SC_OK_200, "操作成功").result(data);
    }

    public static <T> Wrapper<T> ok(String msg, T data) {
        return new Wrapper<T>().success(CommonConstant.SC_OK_200, msg).result(data);
    }

    public static <T> Wrapper<T> error(String msg) {
        return new Wrapper<T>().failure(CommonConstant.SC_INTERNAL_SERVER_ERROR_500, msg);
    }

    public static <T> Wrapper<T> error(int code, String msg) {
        return new Wrapper<T>().failure(code, msg);
    }

    public static <T> Wrapper<T> error(String msg, T data) {
        return new Wrapper<T>().failure(CommonConstant.SC_INTERNAL_SERVER_ERROR_500, msg).result(data);
    }

    public static <T> Wrapper<T> noauth(String msg) {
        return new Wrapper<T>().failure(CommonConstant.SC_JEECG_NO_AUTHZ, msg);
    }

    // ====== 链式方法 ======
    public Wrapper<T> success(int code, String message) {
        this.success = true;
        this.code = code;
        this.message = message;
        return this;
    }

    public Wrapper<T> failure(int code, String message) {
        this.success = false;
        this.code = code;
        this.message = message;
        return this;
    }

    public Wrapper<T> result(T result) {
        this.result = result;
        return this;
    }

    public Wrapper<T> message(String message) {
        this.message = message;
        return this;
    }

    @JsonIgnore
    private String onlTable;
}

