package rda.aerospike.simple.model;

public class Person {
    public String uuid;
    public String firstname;
    public String lastname;

    public Person(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Person() {
    }


}
