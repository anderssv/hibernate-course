package domain;


public class Country {

	private String code;
	private String displayName;

	// Hibernate requires
	protected Country() {

	}

	public Country(String code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}

	public String getCode() {
		return code;
	}

}
