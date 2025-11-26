package top.ehre.mod.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result success() {
        return new Result(0, null, null);
    }

    public static Result success(Object data) {
        return new Result(0, null, data);
    }

    public static Result fail(String msg) {
        return new Result(1, msg, null);
    }

    public static Result fail(int code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result info(int code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result info(boolean code, String msg) {
        return new Result(code ? 0 : 1, msg, null);
    }

}