package rda.aerospike.simple;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AerospikeConfiguration {
    @Bean(destroyMethod = "close")
    public AerospikeClient aerospikeClient() {
        ClientPolicy policy = new ClientPolicy();
        //http://stackoverflow.com/questions/27229399/aerospike-how-do-i-get-record-key
        policy.writePolicyDefault.sendKey = true;
        policy.readPolicyDefault.sendKey = true;
        policy.failIfNotConnected = true;
        return new AerospikeClient(policy, "localhost", 3000);
    }
}


//@Configuration
//@EnableAerospikeRepositories(basePackageClasses = ContactRepository.class)
//public class AerospikeConfiguration extends AbstractAerospikeConfiguration {
//
//    @Bean(destroyMethod = "close")
//    public AerospikeClient aerospikeClient() {
//
//        ClientPolicy policy = new ClientPolicy();
//        policy.failIfNotConnected = true;
//
//        return new AerospikeClient(policy, "localhost", 3000);
//    }
//
//    @Bean
//    public AerospikeTemplate aerospikeTemplate() {
//        return new AerospikeTemplate(aerospikeClient(), "bar");
//    }
//}
