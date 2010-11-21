package domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Company {

	@Id
	private Long id;
	private String name;
	
	@ManyToOne
	private Country countryOfHeadquarters;

	@SuppressWarnings("unused")
	private Company() {
		
	}
	
	public Company(Long id, String name, Country countryOfHeadquarters) {
		this.id = id;
		this.name = name;
		this.countryOfHeadquarters = countryOfHeadquarters;
	}

}
