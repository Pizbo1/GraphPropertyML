package csc426526.csvparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * TODO Add parallelism
 * The idea here is that any operations on the csv files can be parallelized here
 * and then future code can be made serially, however, this only parallelizes the 
 * operation by working on multiple files simultaneously, not by doing any code 
 * parallelization
 */
public interface Visitor {
	default void visit(Folder f, Visitor v, Parameters p) throws IOException {
		List<String> l = new ArrayList<String>();
		CSVFile file;
		l = f.getFileList();
		//System.out.println("FileCounnt: " + f.getFileCount());
		for(int i=0; i< f.getFileCount(); i++) {
			//System.out.println(i);
			//System.out.println("Visitor: " + l.get(i));
			file = new CSVFile(l.get(i), f.getFolderNamer(), p);
			v.visit(file, p);
			file.close();
		}
	}
	
	void visit(CSVFile f, Parameters p);
}
