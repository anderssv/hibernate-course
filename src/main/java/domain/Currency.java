package domain;

import javax.persistence.Id;

public class Currency {

	private String key;
	private String content;

	// Hibernate
	protected Currency() {
	}

	public Currency(String string, String content) {
		this.key = string;
		this.content = content;
	}

	@Id
	public String getKey() {
		return this.key;
	}

	// Hibernate
	@SuppressWarnings("unused")
	private void setKey(String key) {
		this.key = key;
	}

	public String getContent() {
		return this.content;
	}

	// Hibernate
	@SuppressWarnings("unused")
	private void setContent(String value) {
		this.content = value;
	}

}
