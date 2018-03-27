package util;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderChecker {
	private static boolean providerSet = false;
	
	public static void checkProvider() {
		if(providerSet)
			return;
		Security.addProvider(new BouncyCastleProvider());
		providerSet = true;
	}
}
