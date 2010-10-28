package domain;

import java.util.HashSet;
import java.util.Set;

public class Person {

	private Long id;
	private String name;
	private Currency preferredCurrency;

	private Set<Pet> ownerOfPets = new HashSet<Pet>();

	@SuppressWarnings("unused")
	private Person() {
	}

	public Person(long l, String string, Currency currency) {
		this.id = l;
		this.name = string;
		this.preferredCurrency = currency;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Currency getCurrency() {
		return this.preferredCurrency;
	}

	public void setPets(Set<Pet> children) {
		this.ownerOfPets = children;
	}

	public Set<Pet> getPets() {
		return ownerOfPets;
	}

	@SuppressWarnings("unused")
	private void setCurrency(Currency values) {
		this.preferredCurrency = values;
	}

	public void addChild(Pet child) {
		ownerOfPets.add(child);
	}

}
