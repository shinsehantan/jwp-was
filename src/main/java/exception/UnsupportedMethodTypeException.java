package exception;

import webserver.response.ModelAndView;
import webserver.response.ServletResponse;
import webserver.response.StatusCode;

public class UnsupportedMethodTypeException extends HttpRequestException {

    public UnsupportedMethodTypeException(String input) {
        super(String.format("%s는 지원하지 않는 메소드 타입입니다.", input));
    }

    @Override
    public StatusCode getStatusCode() {
        return StatusCode.METHOD_NOT_ALLOWED;
    }
}
