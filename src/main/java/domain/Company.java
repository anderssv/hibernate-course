package domain;

public class Company {

	private Long id;
	private String name;
	private Country countryOfHeadquarters;

	public Company(String name, Country countryOfHeadquarters) {
		this.name = name;
		this.countryOfHeadquarters = countryOfHeadquarters;
	}

}
