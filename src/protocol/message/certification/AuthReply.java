package protocol.message.certification;

import java.io.IOException;

import org.bouncycastle.cert.X509CertificateHolder;

import protocol.Flag;
import util.Creator;
import util.SerializerBuffer;

public class AuthReply extends AbstractCertificationMessage {
	public final static Creator<AuthReply> CREATOR = AuthReply::new;
	
	private String alias;
	private X509CertificateHolder holder;
	
	private AuthReply() {
		super(Flag.AUTH_REPLY);
	}
	
	public AuthReply(long id, String alias, X509CertificateHolder holder) {
		super(Flag.AUTH_REPLY, id);
		this.alias = alias;
		this.holder = holder;
	}

	public String getAlias() {
		return alias;
	}
	
	public X509CertificateHolder getCertificateHolder() {
		return holder;
	}
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		try {
			ms.putLong(id);
			ms.putString(alias);
			byte[] encoded = holder.getEncoded();
			ms.putInt(encoded.length);
			ms.put(encoded);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		try { 
			this.id = ms.getLong();
			this.alias = ms.getString();
			int length = ms.getInt();
			byte[] encoded = new byte[length];
			ms.get(encoded);
			this.holder = new X509CertificateHolder(encoded);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
