package util;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderChecker {
	private static boolean providerSet = false;
	private static BouncyCastleProvider provider;
	
	public static void checkProvider() {
		if(providerSet && provider != null)
			return;
		provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		providerSet = true;
	}
}
