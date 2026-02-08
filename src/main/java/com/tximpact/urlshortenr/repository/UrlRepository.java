package com.tximpact.urlshortenr.repository;

import com.tximpact.urlshortenr.entity.UrlToShorten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Annotation
@Repository

// Class
public interface UrlRepository
        extends JpaRepository<UrlToShorten, Long> {
    List<UrlToShorten> findByFullUrl(String fullUrl);
    List<UrlToShorten> findByShortUrl(String shortUrl);
    List<UrlToShorten> findAllByFullUrlAndCustomAlias(String fullUrl, String customAlias);
}