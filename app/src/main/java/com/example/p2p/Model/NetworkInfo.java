package com.example.p2p.Model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class NetworkInfo {
    @Id
    private long id;

    private String ip;
    private long port;
    private long field4;
}