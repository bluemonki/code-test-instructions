package tximpact;

import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
class UrlService {

    // replace this with SQLite or something later
    protected HashMap<String, String> shortUrlsToFullUrls = new HashMap<String, String>();
    protected ArrayList<String> deletedUrls = new ArrayList<String>();
    
    public UrlService() {}

    public UrlToShorten getExistingShortUrl( UrlToShorten urlToShorten ) throws Exception
    {
        UrlToShorten result = urlToShorten;
        if (urlToShorten.customAlias != null) 
        {
            boolean deletedUrl = deletedUrls.contains(urlToShorten.customAlias);
            // if the custom alias is already taken, but the fullUrl is different, return 404
            if (!deletedUrl && shortUrlsToFullUrls.containsKey(urlToShorten.customAlias)) 
            {
                if (this.shortUrlsToFullUrls.get(urlToShorten.customAlias).equals(urlToShorten.fullUrl)) 
                {
                    // exists but is the same
                    result.shortUrl = urlToShorten.customAlias;
                    return result;
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
            if (shortUrlsToFullUrls.containsValue(urlToShorten.fullUrl)) 
            {
                // already exists
                result.shortUrl = findShortUrlFromFullUrl(urlToShorten);
                // unless it's deleted
                if (false == deletedUrls.contains(result.shortUrl)) {
                    return result;
                }
            }
        }

        // not found
        return null;
    }

    public UrlToShorten shortenUrl ( UrlToShorten urlToShorten )
    {
        UrlToShorten result = urlToShorten;

        // add custom alias
        if (urlToShorten.customAlias != null)
        {
            deletedUrls.remove(urlToShorten.customAlias);
            shortUrlsToFullUrls.put(urlToShorten.customAlias, urlToShorten.fullUrl);
            result.shortUrl = urlToShorten.customAlias;
            return result;
        }
        else
        {
            String newShortUrl = base62encode(this.shortUrlsToFullUrls.size());
            this.shortUrlsToFullUrls.put(newShortUrl, urlToShorten.fullUrl);
            result.shortUrl = newShortUrl;
        }
        // make sure it's not in the deleted list
        deletedUrls.remove(result.shortUrl);
        return result;
    }

    public String getFullUrl( String shortUrl )
    {
        boolean deletedUrl = deletedUrls.contains(shortUrl);
        boolean existingUrl = shortUrlsToFullUrls.containsKey(shortUrl);
        if ( !deletedUrl && existingUrl )
        {
            return shortUrlsToFullUrls.get(shortUrl);
        }
        return null;
    }

    public void deleteUrl( String shortUrl )
    {
        //shortUrlsToFullUrls.remove(shortUrl);
        deletedUrls.add(shortUrl);
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