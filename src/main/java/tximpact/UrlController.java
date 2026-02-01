package tximpact;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class UrlController {

  UrlService urlService = new UrlService();

  @GetMapping("/{shortUrl}")
  public ResponseEntity<UrlToShorten> getFullUrl(@PathVariable String shortUrl) {
    String fullUrl = urlService.getFullUrl(shortUrl);
    UrlToShorten result = new UrlToShorten();

    if (fullUrl != null)
    {
      result.fullUrl = fullUrl;
      return new ResponseEntity<UrlToShorten>(result, HttpStatus.OK);
    }
    return new ResponseEntity<UrlToShorten>(result, HttpStatus.NOT_FOUND);
  }

  @PostMapping("/shorten")
  public ResponseEntity<UrlToShorten> shorten(@RequestBody UrlToShorten urlToShorten) {
    try 
    {
      UrlToShorten result = urlService.getExistingShortUrl(urlToShorten);
      
      if (null != result)
      {
        return new ResponseEntity<UrlToShorten>(urlToShorten, HttpStatus.OK);
      }
      return new ResponseEntity<UrlToShorten>(urlService.shortenUrl(urlToShorten), HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<UrlToShorten>(urlToShorten, HttpStatus.CONFLICT);
    }
  }

  @DeleteMapping("/{shortUrl}")
  public ResponseEntity<UrlToShorten> deleteUrl(@PathVariable String shortUrl) {
    String fullUrl = urlService.getFullUrl(shortUrl);
    UrlToShorten result = new UrlToShorten();

    if (fullUrl != null)
    {
      result.fullUrl = fullUrl;
      urlService.deleteUrl(shortUrl);
      return new ResponseEntity<UrlToShorten>(result, HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<UrlToShorten>(result, HttpStatus.NOT_FOUND);
  }

}