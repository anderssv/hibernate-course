package domain;

public class Job {

	private Long id;
	private String title;
	private Company company;

	public Job(String title, Company company) {
		this.title = title;
		this.company = company;
	}

}
