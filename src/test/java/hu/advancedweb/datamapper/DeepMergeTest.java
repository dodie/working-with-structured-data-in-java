package hu.advancedweb.datamapper;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Deep merging Maps works by default, but in order to deep merge POJOs the object mapper
 * has to be configured with one of the following:
 * - enable deep merging of a field with the @JsonMerge annotation
 * - enable deep merging of a specific type with
 *   objectMapper.configOverride(MyNestedClass.class).setMergeable(true);
 * - enable deep merging by default with
 *   objectMapper.setDefaultMergeable(true);
 * 
 * More info:
 * https://medium.com/@cowtowncoder/jackson-2-9-features-b2a19029e9ff
 */
public class DeepMergeTest {

    @Test
    public void deep_merging_maps_works_by_default() throws Exception {
	final String jsonPath = getClass().getClassLoader().getResource("record.json").getFile();
	Map<String, Object> person = new ObjectMapper().readValue(new File(jsonPath), Map.class);

	// Original data from record.json
	assert "Robert".equals(person.get("name"));
	assert Integer.valueOf(25).equals(person.get("age"));
	assert "United Kingdom".equals(((Map) person.get("address")).get("country"));
	assert "London".equals(((Map) person.get("address")).get("city"));

	// Let's update the address, by changing the city to Birmingham,
	// without affecting address.country
	Map<String, Object> updates = Map.of("address", Map.of("city", "Birmingham"));
	new ObjectMapper().updateValue(person, updates);

	// Successfully updated the nested map:
	// - address.city is now Birmingham, not London
	// - all other fields, including address.country remain the same
	assert "Robert".equals(person.get("name"));
	assert Integer.valueOf(25).equals(person.get("age"));
	assert "Birmingham".equals(((Map) person.get("address")).get("city"));
	assert "United Kingdom".equals(((Map) person.get("address")).get("country"));
    }

    @Test
    public void deep_merging_pojos_does_not_work_by_default() throws Exception {
	final String jsonPath = getClass().getClassLoader().getResource("record.json").getFile();
	Person person = new ObjectMapper().readValue(new File(jsonPath), Person.class);

	// Original data from record.json
	assert "Robert".equals(person.getName());
	assert Integer.valueOf(25).equals(person.getAge());
	assert "United Kingdom".equals(person.getAddress().getCountry());
	assert "London".equals(person.getAddress().getCity());

	// Let's update the address, by changing the city to Birmingham,
	// without affecting address.country
	Map<String, Object> updates = Map.of("address", Map.of("city", "Birmingham"));
	new ObjectMapper().updateValue(person, updates);

	// Failed to updated the nested Address object:
	// - address.country is now NULL
	assert "Robert".equals(person.getName());
	assert Integer.valueOf(25).equals(person.getAge());
	assert "Birmingham".equals(person.getAddress().getCity());
	assert "United Kingdom".equals(person.getAddress().getCountry()); // address.country is null!
    }
    
    @Test
    public void deep_merging_pojos_with_json_merge() throws Exception {
	final String jsonPath = getClass().getClassLoader().getResource("record.json").getFile();
	
	ObjectMapper objectMapper = new ObjectMapper();
	// Enable deep merge: https://medium.com/@cowtowncoder/jackson-2-9-features-b2a19029e9ff
	objectMapper.setDefaultMergeable(true);
		
	Person person = objectMapper.readValue(new File(jsonPath), Person.class);

	// Original data from record.json
	assert "Robert".equals(person.getName());
	assert Integer.valueOf(25).equals(person.getAge());
	assert "United Kingdom".equals(person.getAddress().getCountry());
	assert "London".equals(person.getAddress().getCity());

	// Let's update the address, by changing the city to Birmingham,
	// without affecting address.country
	Map<String, Object> updates = Map.of("address", Map.of("city", "Birmingham"));
	objectMapper.updateValue(person, updates);

	// Successfully updated the nested Address object:
	// - address.city is now Birmingham, not London
	// - all other fields, including address.country remain the same
	assert "Robert".equals(person.getName());
	assert Integer.valueOf(25).equals(person.getAge());
	assert "Birmingham".equals(person.getAddress().getCity());
	assert "United Kingdom".equals(person.getAddress().getCountry());
    }

}
