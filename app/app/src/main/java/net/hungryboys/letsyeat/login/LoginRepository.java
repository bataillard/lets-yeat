package net.hungryboys.letsyeat.login;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.hungryboys.letsyeat.data.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

/**
 * Singleton class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credential information.
 */
public class LoginRepository {

    public static final String TAG_LOGIN_REPO = "LoginRepository";

    private static final String ALIAS_PASS = "YeatLoginRepository";
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String TRANSFORM_PASS = "AES/CBC/PKCS7Padding";
    private static final String ALGORITHM_PASS = KeyProperties.KEY_ALGORITHM_AES;
    private static final String FILE_PASS = "creds.txt";
    private static final String IV_PASS = "iv.txt";

    private static volatile LoginRepository instance;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private Credentials credentials;
    private File credFile;
    private File ivFile;
    private AlgorithmParameters cryptoParams;
    private boolean loggedIn;

    // private constructor : singleton access
    private LoginRepository(Context context) {
        this.credFile = new File(context.getFilesDir(), FILE_PASS);
        this.ivFile = new File(context.getFilesDir(), IV_PASS);
    }

    private IvParameterSpec loadIV() throws IOException {
        FileInputStream input = new FileInputStream(ivFile);
        int ivLength = input.read();

        byte[] iv = new byte[ivLength];
        if (input.read(iv) != ivLength) {
            throw new IOException("Length of IV is not equal to read bytes");
        }

        return new IvParameterSpec(iv);
    }

    private void saveIV(byte[] iv) throws IOException {
        FileOutputStream output = new FileOutputStream(ivFile);
        output.write(iv.length);
        output.write(iv);
        output.close();
    }

    private static class Credentials implements Serializable {
        User user;
        String serverAuthToken;

        Credentials(User user, String serverAuthToken) {
            this.user = user;
            this.serverAuthToken = serverAuthToken;
        }
    }

    /**
     * @return the current instance of the login repository
     */
    public static LoginRepository getInstance(Context context) {
        synchronized (LoginRepository.class) {
            if (instance == null) {
                instance = new LoginRepository(context);
            }

            return instance;
        }
    }

    /**
     * Saves user credentials to keystore
     * @param user the user that was logged in (contains email/secret)
     * @param body the result from the server (contains auth token)
     */
    public void saveUserCredentials(User user, LoginResult body, Context context) {
        Credentials credentials = new Credentials(user, body.getServerAuthToken());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            saveCredentialsToKeyStore(credentials, context);
        } else {
            this.credentials = credentials;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveCredentialsToKeyStore(Credentials credentials, Context context) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE);
            keyStore.load(null);

            if (!keyStore.containsAlias(ALIAS_PASS)) {
                createKey();
            }

            credFile = new File(context.getFilesDir(), FILE_PASS);
            encryptData(credentials, new FileOutputStream(credFile));

            loggedIn = true;
        } catch (Exception e) {
            Log.e(TAG_LOGIN_REPO, "Could not save credentials!", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createKey() throws GeneralSecurityException {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.YEAR, -1);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);

        KeyGenerator gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);
        KeyGenParameterSpec params = new KeyGenParameterSpec.Builder(ALIAS_PASS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setKeyValidityStart(start.getTime())
                .setKeyValidityEnd(end.getTime())
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .build();

        gen.init(params);
        gen.generateKey();
    }

    private void encryptData(Credentials credentials, OutputStream output)
            throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE);
        keyStore.load(null);

        // Setup cipher for encryption
        Key key = ((KeyStore.SecretKeyEntry)keyStore.getEntry(ALIAS_PASS, null)).getSecretKey();
        Cipher cipher = Cipher.getInstance(TRANSFORM_PASS);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        saveIV(cipher.getIV());

        // Encrypt credentials and write encrypted contents to output stream
        CipherOutputStream cipherOutputStream = new CipherOutputStream(output, cipher);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(cipherOutputStream);

        objectOutputStream.writeObject(credentials);
        objectOutputStream.close();
    }

    private Credentials decryptData(InputStream input) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE);
        keyStore.load(null);

        Key key = ((KeyStore.SecretKeyEntry)keyStore.getEntry(ALIAS_PASS, null)).getSecretKey();
        Cipher cipher = Cipher.getInstance(TRANSFORM_PASS);
        cipher.init(Cipher.DECRYPT_MODE, key, loadIV());

        CipherInputStream cipherInputStream = new CipherInputStream(input, cipher);
        ObjectInputStream objectInputStream = new ObjectInputStream(cipherInputStream);

        try {
            return (Credentials) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            return null;
        } finally {
            objectInputStream.close();
        }
    }

    @Nullable
    private Credentials getCredentialsFromKeyStore() {
        try {
            InputStream input = new FileInputStream(credFile);

            return decryptData(input);
        } catch (Exception e) {
            Log.e(TAG_LOGIN_REPO, "Error while fetching credentials from keyStore", e);
            return null;
        }
    }

    private void removeUserCredentialsFromKeyStore() {
        if (credFile.exists()) {
            credFile.delete();
        }

        if (ivFile.exists()) {
            ivFile.delete();
        }
    }

    /**
     * @return true if user has credentials saved
     */
    public boolean isLoggedIn() {
        if (credFile != null && credFile.exists()) {
            return true;
        } else {
            return loggedIn;
        }
    }

    /**
     * Removes user credentials saved, after logout
     */
    public void removeUserCredentials() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            removeUserCredentialsFromKeyStore();
        } else {
            this.credentials = null;
        }

        loggedIn = false;
    }

    /**
     * @return the auth token used for communication with the server (can be null)
     */
    public String getServerAuthToken() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Credentials keyStoreCredentials = getCredentialsFromKeyStore();

            if (keyStoreCredentials != null) {
                return keyStoreCredentials.serverAuthToken;
            } else {
                return null;
            }
        } else {
            return credentials.serverAuthToken;
        }
    }

    /**
     * @return the email of the user or null if not logged in
     */
    public String getUserEmail() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Credentials keyStoreCredentials = getCredentialsFromKeyStore();

            if (keyStoreCredentials != null) {
                return keyStoreCredentials.user.getEmail();
            } else {
                return null;
            }
        } else {
            return credentials.user.getEmail();
        }
    }
}
