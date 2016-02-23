package rda.aerospike.simple.repo;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.client.task.IndexTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import rda.aerospike.simple.model.Person;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Repository
public class PersonRepository {
//    String namespace, String setName -> "test", "demo"

    @Autowired
    AerospikeClient client;

    @PostConstruct
    void init() {
//        String namespace = ,
//        String setName,
//        String indexName,
//        String binName,
        IndexTask indexL = client.createIndex(null, "test", "demo", "lastnameIndex", "lastname", IndexType.STRING);
        IndexTask indexF = client.createIndex(null, "test", "demo", "firstnameIndex", "firstname", IndexType.STRING);
    }

    public void deleteAll() {
    }

    public String save(Person person) {
        Key key;
        if (person.uuid == null) {
            key = new Key("test", "demo", UUID.randomUUID().toString());
            person.uuid = key.userKey.toString();
        } else {
            key = new Key("test", "demo", person.uuid);
        }
        Bin binF = new Bin("firstname", person.firstname);
        Bin binL = new Bin("lastname", person.lastname);

// Write a record
        client.put(null, key, binF, binL);

// Read a record
        Record record = client.get(null, key);
        Map<String, Object> bins = record.bins;

        assert bins.get("firstname") == person.firstname;
        assert bins.get("lastname") == person.lastname;

        return person.uuid;
    }

    public Person get(String uuid) {
        Key key = new Key("test", "demo", uuid);
        Record record = client.get(null, key);
        if (record == null) {
            throw new IllegalArgumentException("Wrong uuid:" + uuid);
        }
        Map<String, Object> bins = record.bins;

        Person person = new Person((String) bins.get("firstname"), (String) bins.get("lastname"));
        person.uuid = uuid;
        return person;
    }

    public List<Person> findByLastname(String name) {
        return getPersons(Filter.equal("lastname", name));
    }

    public List<Person> findByFirstname(String name) {
        return getPersons(Filter.equal("firstname", name));
    }

    private List<Person> getPersons(Filter f) {
        Statement statement = new Statement();
        statement.setNamespace("test");
        statement.setSetName("demo");
        statement.setFilters(f);


        RecordSet rs = client.query(null, statement);
        if (rs == null) {
            throw new IllegalArgumentException("Wrong f:" + f);
        }
        List<Person> persons = new ArrayList<Person>();
        try {
            while (rs.next()) {
                Person person = getPerson(rs);
                persons.add(person);
            }
        } finally {
            rs.close();
        }

        return persons;
    }

    private Person getPerson(RecordSet rs) {
        Key key = rs.getKey();
        Record record = rs.getRecord();
        return getPerson(key, record);
    }

    private Person getPerson(Key key, Record record) {
        Person person = new Person((String) record.bins.get("firstname"),
                (String) record.bins.get("lastname"));
        if (key.userKey != null) {
            person.uuid = key.userKey.toString();
        }
        return person;
    }

    public boolean updateLN(String uuid, String lname) {
        Key key = new Key("test", "demo", uuid);
        if (client.exists(null, key)) {
            Bin binL = new Bin("lastname", lname);
            client.put(null, key, binL);

            return true;
        }
        return false;
    } //

    public List<Person> all() {
        try {
            // Java Scan
            ScanPolicy policy = new ScanPolicy();
            policy.concurrentNodes = true;
            policy.priority = Priority.LOW;
            policy.includeBinData = true;
            final List<Person> persons = new ArrayList<Person>();
            client.scanAll(policy, "test", "demo", new ScanCallback() {

                @Override
                public void scanCallback(Key key, Record record)
                        throws AerospikeException {

                    persons.add(getPerson(key, record));
                }
            });
            return persons;
        } catch (AerospikeException e) {
            System.out.println("EXCEPTION - Message: " + e.getMessage());
            return new ArrayList<Person>();
        }

    } //
}
