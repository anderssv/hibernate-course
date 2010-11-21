package domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Country {

	@Id
	private String code;
	private String displayName;

	// Hibernate requires
	@SuppressWarnings("unused")
	private Country() {

	}

	public Country(String code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}

}
