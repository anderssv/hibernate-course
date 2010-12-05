package domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SocialSecurityNumber {

	@Column(name="ssn")
	private String value;

	public SocialSecurityNumber(String ssn) {
		this.value = ssn;
	}
	
}
