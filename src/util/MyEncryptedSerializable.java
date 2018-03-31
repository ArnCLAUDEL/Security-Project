package util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import io.MySerializable;

public interface MyEncryptedSerializable extends MySerializable {
	/**
	 * Serializes this object to the given {@link SerializerBuffer} using the {@link Cipher}.<br />
	 * The {@code Cipher} must have been initialized with the method <tt>init()</tt>.
	 * @param ms The buffer to serialize to
	 * @param cipher The cipher to use
	 * @throws InvalidKeyException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws ShortBufferException 
	 */
	public void writeToBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException;
	
	/**
	 * Deserializes this object from the given {@link SerializerBuffer} using the {@link Cipher}.<br />
	 * The {@code Cipher} must have been initialized with the method <tt>init()</tt>.
	 * @param ms The buffer to deserialize from
	 * @param cipher The cipher to use
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws ShortBufferException 
	 */
	public void readFromBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException;
}
