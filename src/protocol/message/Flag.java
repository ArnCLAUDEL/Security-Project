package protocol.message;

public final class Flag {	
	public final static byte AUTH_REQUEST = 1;
	public final static byte AUTH_REPLY = 2;
	public final static byte CERT_REQUEST = 3;
	public final static byte CERT_REPLY = 4;
	public final static byte SESSION_REQUEST = 5;
	public final static byte SESSION_REPLY = 6;
	public final static byte SERVICE_FILE_REQUEST = 7;
	public final static byte SERVICE_FILE_REPLY = 8;
	public final static byte SERVICE_FILE_ABORT = 9;
	public final static byte SERVICE_FILE_OK = 10;
	private Flag() {}
}
