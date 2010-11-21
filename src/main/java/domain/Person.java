package domain;

import java.util.List;


public class Person {

	private Long id;
	private String name;
	private Country countryOfResidence;
	private List<Job> jobs;
	
	public Person(String name) {
		this.name = name;
	}

}
