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
	
	public static class Builder {
		private Company company = new Company();
		
		public Builder id(Long id) {
			company.id = id;
			return this;
		}
		
		public Builder name(String name) {
			company.name = name;
			return this;
		}
		
		public Builder countryOfHeadquarters(Country country) {
			company.countryOfHeadquarters = country;
			return this;
		}
		
		public Company build() {
			return company;
		}
	}

}
