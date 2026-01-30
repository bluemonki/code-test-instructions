package tximpact;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
class UrlService {

    // replace this with SQLite or something later
    protected HashMap<String, String> shortUrlsToFullUrls = new HashMap<String, String>();
    // protected HashMap<String, String> fullUrlToShortUrl = new HashMap<String, String>();

    public UrlService() {}

    public String getExistingShortUrl( UrlToShorten urlToShorten ) throws Exception
    {
        if (urlToShorten.customAlias != null) 
        {
            // if the custom alias is already taken, but the fullUrl is different, return 404
            if (this.shortUrlsToFullUrls.containsKey(urlToShorten.customAlias)) 
            {
                if (this.shortUrlsToFullUrls.get(urlToShorten.customAlias).equals(urlToShorten.fullUrl)) 
                {
                    // exists but is the same
                    return urlToShorten.customAlias;
                } 
                else
                {
                    // exists but is different
                    throw new Exception("Already taken");
                }
            }            
        }
        else
        {
            if (this.shortUrlsToFullUrls.containsValue(urlToShorten.fullUrl)) 
            {
                // already exists
                return findShortUrlFromFullUrl(urlToShorten);
            }
        }

        // not found
        return null;
    }

    public String shortenUrl ( UrlToShorten urlToShorten )
    {
        // add custom alias
      this.shortUrlsToFullUrls.put(urlToShorten.customAlias, urlToShorten.fullUrl);
      //this.fullUrlToShortUrUrlToShortenl.put(urlToShorten.fullUrl, urlToShorten.shortUrl);
      //return new ResponseEntity<String>(urlToShorten.customAlias, HttpStatus.OK);
      return "temp";
    }

    protected String findShortUrlFromFullUrl( UrlToShorten urlToShorten )
    {
        return shortUrlsToFullUrls.entrySet()
              .stream()
              .filter(entry -> Objects.equals(entry.getValue(), urlToShorten.fullUrl))
              .map(Map.Entry::getKey)
              .collect(Collectors.toSet())
              .iterator().next();
    }


}