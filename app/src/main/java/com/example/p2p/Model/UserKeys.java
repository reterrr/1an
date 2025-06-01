package com.example.p2p.Model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class UserKeys {
    @Id
    private long id;

    private ToOne<User> user;

    private String publicKey;       // base64 or hex-encoded
    private String privateKeyEnc;   // encrypted private key
    private String keyType;         // e.g., "X25519"
}
