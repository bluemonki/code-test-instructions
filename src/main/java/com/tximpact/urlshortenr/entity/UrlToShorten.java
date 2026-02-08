package com.tximpact.urlshortenr.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class UrlToShorten {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long urlId;
    @JsonProperty("fullUrl")
    String fullUrl;
    @JsonProperty("customAlias")
    String customAlias;
    @JsonProperty("shortUrl")
    String shortUrl;
}