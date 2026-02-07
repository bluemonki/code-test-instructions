package com.tximpact.urlshortenr.controller;

import com.tximpact.urlshortenr.service.UrlService;
import com.tximpact.urlshortenr.entity.UrlToShorten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class UrlController {

  @Autowired UrlService urlService;

  @GetMapping("/{shortUrl}")
  public ResponseEntity<UrlToShorten> getFullUrl(@PathVariable String shortUrl) {
    String fullUrl = urlService.getFullUrl(shortUrl);
    UrlToShorten result = new UrlToShorten();

    if (fullUrl != null)
    {
      result.setFullUrl(fullUrl);
      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
  }

  @PostMapping("/shorten")
  public ResponseEntity<UrlToShorten> shorten(@RequestBody UrlToShorten urlToShorten) {
    try 
    {
      UrlToShorten result = urlService.getExistingShortUrl(urlToShorten);
      
      if (null != result)
      {
        return new ResponseEntity<>(urlToShorten, HttpStatus.OK);
      }
      return new ResponseEntity<>(urlService.shortenUrl(urlToShorten), HttpStatus.OK);
    }
    catch (IllegalArgumentException iae)
    {
      return new ResponseEntity<>(urlToShorten, HttpStatus.UNPROCESSABLE_CONTENT);
    }
    catch (Exception e)
    {
      return new ResponseEntity<>(urlToShorten, HttpStatus.CONFLICT);
    }
  }

  @DeleteMapping("/{shortUrl}")
  public ResponseEntity<UrlToShorten> deleteUrl(@PathVariable String shortUrl) {
    String fullUrl = urlService.getFullUrl(shortUrl);
    UrlToShorten result = new UrlToShorten();

    if (fullUrl != null)
    {
      result.setFullUrl(fullUrl);
      urlService.deleteUrl(shortUrl);
      return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
  }

}