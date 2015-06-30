package hello;

import hello.model.Person;
import hello.service.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@Autowired
	private PersonRepository personRepository;

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@Transactional(readOnly = true)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Person getPersonById(@PathVariable final Long id) {
		final Person person = personRepository.findOne(id);
		return person;
	}

}
