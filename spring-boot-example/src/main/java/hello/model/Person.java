package hello.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Person {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
	@JsonIgnore
	private Set<Address> addresses;

	private Person() {

	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Long getId() {
		return id;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}
}
