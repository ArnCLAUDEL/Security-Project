package protocol;

public final class Flag {	
	public final static byte AUTH_REQUEST = 1;
	public final static byte AUTH_REPLY = 2;
	public final static byte CERT_REQUEST = 3;
	public final static byte CERT_REPLY = 4;
	public final static byte SESSION_REQUEST = 5;
	public final static byte SESSION_REPLY = 6;
	public final static byte SERVICE_FILE_ABORT = 9;
	public final static byte SERVICE_FILE_WRITE_REQUEST = 11;
	public final static byte SERVICE_FILE_WRITE_REPLY = 12;
	public final static byte SERVICE_FILE_READ_REQUEST = 13;
	public final static byte SERVICE_FILE_READ_REPLY = 14;
	public final static byte SESSION_INIT = 15;
	public final static byte SESSION_ACK = 16;
	public final static byte SESSION_OK = 17;
	private Flag() {}
}