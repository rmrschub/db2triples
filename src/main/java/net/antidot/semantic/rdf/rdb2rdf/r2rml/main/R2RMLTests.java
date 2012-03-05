package net.antidot.semantic.rdf.rdb2rdf.r2rml.main;

import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLMappingFactory;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;

public class R2RMLTests {
	
	public static void main(String [] args) {
		System.out.println(System.getProperty("user.dir" ));
		
		String fileToR2RMLFile = "test.rr";
		try {
			R2RMLMappingFactory.extractR2RMLMapping(fileToR2RMLFile);
		} catch (InvalidR2RMLStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidR2RMLSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (R2RMLDataError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
