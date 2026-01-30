package tximpact;

import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
class UrlService {

    // replace this with SQLite or something later
    protected HashMap<String, String> shortUrlsToFullUrls = new HashMap<String, String>();
    
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
      if (urlToShorten.customAlias != null)
      {
        this.shortUrlsToFullUrls.put(urlToShorten.customAlias, urlToShorten.fullUrl);
        return urlToShorten.customAlias;
      }
      String newShortUrl = base62encode(this.shortUrlsToFullUrls.size());
      this.shortUrlsToFullUrls.put(newShortUrl, urlToShorten.fullUrl);
      return newShortUrl;
    }

    public String getFullUrl( String shortUrl )
    {
        if (shortUrlsToFullUrls.containsKey(shortUrl)) 
        {
            return shortUrlsToFullUrls.get(shortUrl);
        }
        return null;
    }

    // helper to search the hashmap
    protected String findShortUrlFromFullUrl( UrlToShorten urlToShorten )
    {
        return shortUrlsToFullUrls.entrySet()
              .stream()
              .filter(entry -> Objects.equals(entry.getValue(), urlToShorten.fullUrl))
              .map(Map.Entry::getKey)
              .collect(Collectors.toSet())
              .iterator().next();
    }

    // encode long urls into short ones using the current size of the hashmap
    // taken from 
    // https://ssojet.com/binary-encoding-decoding/base62-in-java#encoding-data-to-base62
    protected String base62encode(int aNum) {
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


}