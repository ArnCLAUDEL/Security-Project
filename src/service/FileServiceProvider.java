package service;

public interface FileServiceProvider {
	boolean write(String content, String filename);
	String read(String filename);
}
