package domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Person {

	@Id
	private Long id;

	@Embedded
	private SocialSecurityNumber ssn;

	private String name;

	@ManyToOne
	private Country countryOfResidence;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
	private Set<Job> jobs = new HashSet<Job>();

	@Version
	@Column(name="VERSION")
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
