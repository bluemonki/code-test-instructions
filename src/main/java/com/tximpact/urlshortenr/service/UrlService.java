package com.tximpact.urlshortenr.service;

import com.tximpact.urlshortenr.entity.UrlToShorten;
import com.tximpact.urlshortenr.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    public UrlService() {}

    public UrlToShorten getExistingShortUrl(UrlToShorten urlToShorten ) throws IllegalArgumentException
    {
        // if we have a custom alias see if there's an
        // existing match
        if (urlToShorten.getCustomAlias() != null)
        {
            List<UrlToShorten> matches = urlRepository.findAllByFullUrlAndCustomAlias(urlToShorten.getFullUrl(), urlToShorten.getCustomAlias());
            if (!matches.isEmpty())
            {
                return matches.get(0);
            }
        }
        else {
            // otherwise see if there's a match without
            // a custom alias
            List<UrlToShorten> fullUrlMatches = urlRepository.findByFullUrl(urlToShorten.getFullUrl());

            for (UrlToShorten match : fullUrlMatches) {
                if (match.getCustomAlias() == null) {
                    return match;
                }
            }
        }
        // otherwise not found
        return null;
    }

    @Transactional
    public UrlToShorten shortenUrl ( UrlToShorten urlToShorten ) throws Exception
    {
        UrlToShorten result = urlToShorten;

        validateFullUrl(urlToShorten.getFullUrl());

        // add custom alias
        if (urlToShorten.getCustomAlias() != null) {

            result.setShortUrl(urlToShorten.getCustomAlias());
            List<UrlToShorten> matches = urlRepository.findByShortUrl(urlToShorten.getCustomAlias());
            if (!matches.isEmpty()) {
                List<String> matchingFullUrls = matches.stream().map(UrlToShorten::getFullUrl).toList();
                if (!matchingFullUrls.contains(urlToShorten.getFullUrl())) {
                    throw new Exception("ALready Taken");
                }
            }
        }
        else
        {
            String newShortUrl = base62encode(Instant.now().toEpochMilli());
            result.setShortUrl(newShortUrl);
        }

        urlRepository.saveAndFlush(result);
        return result;
    }

    public String getFullUrl( String shortUrl )
    {
        List<UrlToShorten> matches = urlRepository.findByShortUrl(shortUrl);
        if (matches.isEmpty())
        {
            // not found
            return null;
        }
        return matches.get(0).getFullUrl();
    }

//    @Transactional
    public void deleteUrl( String shortUrl )
    {
        List<UrlToShorten> matches = urlRepository.findByShortUrl(shortUrl);
        if (false == matches.isEmpty())
        {
            List<Long> ids = matches.stream().map(UrlToShorten::getUrlId).toList();
            urlRepository.deleteAllByIdInBatch(ids);
            urlRepository.flush();
        }
    }

    // encode long urls into short ones using the current size of the hashmap
    // taken from 
    // https://ssojet.com/binary-encoding-decoding/base62-in-java#encoding-data-to-base62
    protected String base62encode(long aNum) {
        BigInteger num = BigInteger.valueOf(aNum);
        if (num.equals(BigInteger.ZERO)) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        BigInteger base = BigInteger.valueOf(62);
        while (num.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = num.divideAndRemainder(base);
            num = divRem[0];
            sb.append(chars.charAt(divRem[1].intValue()));
        }
        return sb.reverse().toString();
    } 


    protected void validateFullUrl(String url) throws IllegalArgumentException{
        // basic validation
        boolean isValid = url.startsWith("http://") || url.startsWith("https://");
        if (!isValid) {
            throw new IllegalArgumentException("Invalid URL format");
        }
    }


}