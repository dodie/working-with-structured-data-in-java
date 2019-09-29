package hu.advancedweb.datamapper;

import java.io.File;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PrettyPrintTest {

    @Test
    public void print_object_without_tosting() throws Exception {
	final String jsonPath = getClass().getClassLoader().getResource("record.json").getFile();
	Person original = new ObjectMapper().readValue(new File(jsonPath), Person.class);
	
	System.out.println(ReflectionToStringBuilder.toString(original, new RecursiveToStringStyle()));
	// hu.advancedweb.datamapper.Person@525b461a[name=Robert,age=25,address=hu.advancedweb.datamapper.Address@6591f517[city=London,country=United Kingdom]]
    }

}
