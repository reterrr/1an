package com.example.p2p;

import android.content.Context;
import android.util.Base64;

import com.example.p2p.Model.User;
import com.example.p2p.Model.UserKeys;
import com.example.p2p.Model.UserKeys_;
import com.example.p2p.Model.User_;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.hybrid.HybridConfig;
import com.google.crypto.tink.integration.android.AndroidKeysetManager;
import com.google.crypto.tink.hybrid.HybridKeyTemplates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public class E2ETool {
    private final Box<UserKeys> userKeysBox = ObjectBox.get().boxFor(UserKeys.class);
    private final Context context;
    private final User user;
    private final String pref = "tink_keyset_pref_";
    private final String masterLink = "master_key_alias_";
    public final String masterKey;
    public final String prefName;

    private E2ETool(Context context, User user) {
        this.context = context;
        this.user = user;

        masterKey = masterLink + user.username;
        prefName = pref + user.username;
    }

    public static class Builder {
        private Context context;
        private User user;

        public Builder() {
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public E2ETool build() {
            if (context == null || user == null) {
                throw new IllegalStateException("Context and User must be set");
            }

            return new E2ETool(context, user);
        }
    }

    public static Builder make() {
        return new Builder();
    }

    public void makeAndStoreKeyPair() {
        try {
            AndroidKeysetManager keySetManager = new AndroidKeysetManager.Builder()
                    .withSharedPref(context, prefName, masterKey)
                    .withKeyTemplate(HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM)
                    .build();

            KeysetHandle privateHandle = keySetManager.getKeysetHandle();

            KeysetHandle publicHandle = privateHandle.getPublicKeysetHandle();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CleartextKeysetHandle.write(publicHandle, JsonKeysetWriter.withOutputStream(baos));
            String publicKeyJson = baos.toString(StandardCharsets.UTF_8.toString());

            storeKeyToBox(publicKeyJson);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to generate/store key pair", e);
        }
    }

    /**
     * Stores the given public key JSON and type in the UserKeys entity linked to this user.
     */
    private void storeKeyToBox(String publicKeyJson) {
        UserKeys existing = userKeysBox.query()
                .equal(UserKeys_.userId, user.id)
                .build().findFirst();

        if (existing != null) {
            userKeysBox.remove(existing);
        }

        UserKeys keys = new UserKeys();
        keys.user.setTarget(user);
        keys.publicKey = publicKeyJson;
        keys.fingerPrint = computeFingerprint(publicKeyJson);
        userKeysBox.put(keys);
    }

    public static String getPublicKey() {
        QueryBuilder<UserKeys> builder = ObjectBox.get().boxFor(UserKeys.class).query();
        builder.link(UserKeys_.user).equal(User_.id, CurrentUserManager.getUser().user.getTargetId());
        UserKeys keys = builder.build().findFirst();

        return keys.publicKey;
    }

    public static String computeFingerprint(String publicKeyJson) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(publicKeyJson.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(String.format("%02X", digest[i]));
                if (i < 15) sb.append(":");
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
