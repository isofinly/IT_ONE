package com.github.kxrxh.javalin.rest.util;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class SSLUtils {
    public static final String KEYSTORE_PATH = "certs/keystore.jks";
    public static final String TRUSTSTORE_PATH = "certs/truststore.jks";
    public static final String STORE_PASSWORD;
    public static final String KEY_PASSWORD;
    public static final String ALGORITHM = "SunX509";

    static {
        KEY_PASSWORD = System.getenv("KEY_PASSWORD");
        STORE_PASSWORD = System.getenv("STORE_PASSWORD");
    }

    private SSLUtils() {
    }

    public static KeyStore loadKeystore(String path)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore store = KeyStore.getInstance("JKS");
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path))) {
            store.load(in, STORE_PASSWORD.toCharArray());
        }
        return store;
    }

    public static KeyManager[] createKeyManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        KeyStore store = loadKeystore(KEYSTORE_PATH);
        KeyManagerFactory factory = KeyManagerFactory.getInstance(ALGORITHM);
        factory.init(store, KEY_PASSWORD.toCharArray());
        return factory.getKeyManagers();
    }

    public static TrustManager[] createTrustManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore store = loadKeystore(TRUSTSTORE_PATH);
        TrustManagerFactory factory = TrustManagerFactory.getInstance(ALGORITHM);
        factory.init(store);
        return factory.getTrustManagers();
    }

    public static SSLContext createSSLContext() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(createKeyManagers(), createTrustManagers(), new SecureRandom());
        return ctx;
    }
}