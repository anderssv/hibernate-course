package domain;


public class Job {

	private Long id;
	private String title;

	private Company company;

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
