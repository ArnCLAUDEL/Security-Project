package service.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import util.SerializerBuffer;

public class SimpleFileServiceProvider implements FileServiceProvider {
	
	private final SerializerBuffer buffer = new SerializerBuffer(2048);
	
	private int write(SerializerBuffer buffer, WritableByteChannel out) throws IOException {	
		return buffer.write(out);
	}
	
	@Override
	public void write(String filename, String content) throws IOException {
		File file = new File(filename);
		FileOutputStream out = new FileOutputStream(file);
		buffer.clear();
		buffer.putString(content);
		buffer.flip();
		write(buffer, out.getChannel());
		out.close();
	}
	
	private int read(SerializerBuffer buffer, ReadableByteChannel in) throws IOException {
		return buffer.read(in);
	}

	@Override
	public String read(String filename) throws IOException {
		File file = new File(filename);
		FileInputStream in = new FileInputStream(file);
		buffer.clear();
		read(buffer, in.getChannel());
		buffer.flip();
		in.close();
		return buffer.getString();
	}
	
}