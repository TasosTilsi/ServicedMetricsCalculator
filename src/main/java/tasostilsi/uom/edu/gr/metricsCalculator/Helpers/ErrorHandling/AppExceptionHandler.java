package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.ErrorHandling;

import ch.qos.logback.classic.Logger;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AppExceptionHandler.class);
	
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<Object> handleAnyException(Exception ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(ex, request);
		LOGGER.error(JSONSerializer.serializeObject(errorMessage));
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = {NullPointerException.class})
	public ResponseEntity<Object> handleBadRequestException(NullPointerException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(ex, request);
		LOGGER.error(JSONSerializer.serializeObject(errorMessage));
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}
	
}
