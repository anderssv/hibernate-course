package domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Job {

	@Id
	@GeneratedValue
	private Long id;
	private String title;
	
	@ManyToOne
	private Company company;
	
	@ManyToOne
	private Person person;

	// Hibernate requires
	@SuppressWarnings("unused")
	private Job() {
		
	}
	
	public Job(Person person, String title, Company company) {
		this.person = person;
		this.title = title;
		this.company = company;
	}

}
