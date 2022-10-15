package tasostilsi.uom.edu.gr.metricsCalculator.Helpers;

import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Map;

public class ErrorMessage {

    private Date timestamp;
    private String message;
    private String cause;
    private String requestUri;
    private Map<String, String[]> paramsUsed;


    public ErrorMessage(Exception ex, WebRequest request) {
        this.timestamp = new Date();
        this.requestUri = request.getHeader("host") + request.getDescription(false).replace("uri=", "");
        this.message = ex.getLocalizedMessage();
        this.cause = ex.toString();
        this.paramsUsed = request.getParameterMap();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public Map<String, String[]> getParamsUsed() {
        return paramsUsed;
    }

    public void setParamsUsed(Map<String, String[]> paramsUsed) {
        this.paramsUsed = paramsUsed;
    }
}
