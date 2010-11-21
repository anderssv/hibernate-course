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
	@SuppressWarnings("unused")
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

	public void addJob(String title, Company company) {
		this.jobs.add(new Job(this, title, company));
	}

	public boolean hasAJob() {
		return !this.jobs.isEmpty();
	}

}
