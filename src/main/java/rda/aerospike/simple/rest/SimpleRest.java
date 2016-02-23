package rda.aerospike.simple.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rda.aerospike.simple.model.Person;
import rda.aerospike.simple.repo.PersonRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SimpleRest {

    @Autowired
    private PersonRepository repository;

//    public void deleteAll() {
//        repository.deleteAll();
//    }

    @RequestMapping("/")
    List<Person> home() {
        return repository.all();
    }

    @RequestMapping("/create")
    String create(@RequestParam("f") String f, @RequestParam("l") String l) {
        Person person = new Person();
        person.firstname = f;
        person.lastname = l;
        return repository.save(person);
    }

    @RequestMapping("/uuid/{uuid}")
    Person get(@PathVariable("uuid") String uuid) {
        return repository.get(uuid);
    }

    @RequestMapping("/search/{search}")
    Map<String, List<Person>> search(@PathVariable("search") String search) {
        Map<String, List<Person>> map = new HashMap<String, List<Person>>();
        map.put("findByLastname",
                repository.findByLastname(search));
        map.put("findByFirstname",
                repository.findByFirstname(search));
        return map;
    }
    @RequestMapping("/update/{uuid}/{l}")
    boolean update(@PathVariable("uuid") String uuid, @PathVariable("l") String l) {
       return repository.updateLN(uuid,l);
    }

}
