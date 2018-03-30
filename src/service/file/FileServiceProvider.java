package service.file;

import java.io.IOException;

public interface FileServiceProvider {
	void write(String filename, String content) throws IOException;
	String read(String filename) throws IOException;	
}
