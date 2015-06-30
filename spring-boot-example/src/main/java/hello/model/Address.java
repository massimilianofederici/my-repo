package hello.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Address {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String firstLine;

	@Column(nullable = false)
	private String postCode;

	@ManyToOne(optional = false)
	@JsonIgnore
	private Person person;

	public String getFirstLine() {
		return firstLine;
	}

	public String getPostCode() {
		return postCode;
	}

	public Person getPerson() {
		return person;
	}

}
