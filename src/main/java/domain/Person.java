package domain;

import java.util.HashSet;
import java.util.Set;

public class Person {

	private Long id;

	private SocialSecurityNumber ssn;

	private String name;

	private Country countryOfResidence;

	private Set<Job> jobs = new HashSet<Job>();

	private Integer version;

	// Hibernate constructor
	private Person() {

	}

	public Person(long id, SocialSecurityNumber ssn, String name,
			Country countryOfResidence) {
		this.id = id;
		this.name = name;
		this.countryOfResidence = countryOfResidence;
		this.version = 1;
	}

	public String getName() {
		return this.name;
	}

	public Integer getVersion() {
		return this.version;
	}

	public Country getCountryOfResidence() {
		return this.countryOfResidence;
	}

	public void changeName(String name) {
		this.name = name;
	}

	public void addJob(String title, Company company) {
		this.jobs.add(new Job(this, title, company));
	}

	public boolean hasAJob() {
		return !this.jobs.isEmpty();
	}

	public static class Builder {

		private String name;
		private Country countryOfResidence;
		private Long id;
		private SocialSecurityNumber ssn;
		private Long version;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder countryOfResidence(Country country) {
			this.countryOfResidence = country;
			return this;
		}

		public Person build() {
			return new Person(id, this.ssn, this.name, this.countryOfResidence);
		}

		public Builder id(Long personId) {
			this.id = personId;
			return this;
		}

		public Builder socialSecurityNumber(
				SocialSecurityNumber socialSecurityNumber) {
			this.ssn = socialSecurityNumber;
			return this;
		}
	}
}
