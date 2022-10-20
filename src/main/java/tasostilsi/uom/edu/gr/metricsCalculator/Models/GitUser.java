package tasostilsi.uom.edu.gr.metricsCalculator.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitUser {
	
	private String id;
	@NotNull
	@NotBlank
	@Size(min = 2, message = "First name must not be less than 2 characters")
	private String username;
	@Email
	@NotNull
	private String email;
	private String accessToken;
	
	public GitUser() {
		this.id = String.valueOf(UUID.randomUUID());
	}
	
	public GitUser(String username, String accessToken) {
		this.id = String.valueOf(UUID.randomUUID());
		this.username = username;
		this.accessToken = accessToken;
	}
	
	public GitUser(String username, String email, String accessToken) {
		this.id = String.valueOf(UUID.randomUUID());
		this.username = username;
		this.email = email;
		this.accessToken = accessToken;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	
}
