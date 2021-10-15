package csc426526.visitor.training;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import csc426526.csvparser.CSVFile;
import csc426526.csvparser.Folder;
import csc426526.csvparser.Parameters;

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
		for(int i=0; i< f.getFileCount(); i++) {
			file = new CSVFile(l.get(i), f.getFolderNamer(), p);
			file.accept(this, p);
			file.close();
		}
	}
	void visit(CSVFile f, Parameters p);
}
