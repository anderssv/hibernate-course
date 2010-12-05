package domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class Person {

	@Id
	private Long id;
	
	private String name;
	
	@ManyToOne
	private Country countryOfResidence;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="person")
	private Set<Job> jobs = new HashSet<Job>();

	// Hibernate constructor
	private Person() {

	}

	public Person(long id, String name, Country countryOfResidence) {
		this.id = id;
		this.name = name;
		this.countryOfResidence = countryOfResidence;
	}

	public String getName() {
		return this.name;
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
		private Person person = new Person();
		
		public Builder name(String name) {
			person.name = name;
			return this;
		}
		
		public Builder countryOfResidence(Country country) {
			person.countryOfResidence = country;
			return this;
		}
		
		public Person build() {
			return person;
		}

		public Builder id(Long personId) {
			person.id = personId;
			return this;
		}
	}

}
