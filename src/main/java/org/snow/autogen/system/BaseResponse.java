package org.snow.autogen.system;


/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2016/8/12
 * Time: 20:02
 */
public abstract class BaseResponse {

    public static final <T> ResponseEntity buildSuccess() {
      return buildSuccess(null);
    }

    public static final <T> ResponseEntity buildSuccess(T t) {
       return buildSuccess(t, null);
    }
    public static final <T> ResponseEntity buildSuccess(String message) {
        return buildSuccess(null, message);
    }

    public static final <T> ResponseEntity buildSuccess(T t, String message) {
        ResponseEntity toReturn = new ResponseEntity();
        toReturn.setStatus(Constants.System.SUCCESSS);
        toReturn.setCode(Constants.System.OK_CODE);
        toReturn.setMessage(message);
        toReturn.setData(t);
        return toReturn;
    }


    public static final <T> ResponseEntity buildError() {
       return buildError(null);
    }

    public static final <T> ResponseEntity buildError(T t) {
       return buildError(t, null);
    }
    public static final <T> ResponseEntity buildError(String message) {
        return buildError(message, null);
    }

    public static final <T> ResponseEntity buildError(T t, String message) {
       return buildError(t, message, null);
    }

    public static final <T> ResponseEntity buildError(String message, String error) {
       return buildError(null, message, error);
    }

    public static final <T> ResponseEntity buildError(T t, String message, String error) {
        ResponseEntity toReturn = new ResponseEntity();
        toReturn.setStatus(Constants.System.ERROR);
        toReturn.setData(t);
        toReturn.setMessage(message);
        toReturn.setError(error);
        return toReturn;
    }

}
