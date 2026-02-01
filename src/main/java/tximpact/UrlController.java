package tximpact;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import tximpact.UrlService;
import tximpact.UrlToShorten;

@RestController
public class UrlController {

  UrlService urlService = new UrlService();


	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}

  @GetMapping("/{shortUrl}")
  public ResponseEntity<UrlToShorten> getFullUrl(@PathVariable String shortUrl) {
    String fullUrl = urlService.getFullUrl(shortUrl);
    UrlToShorten result = new UrlToShorten();
    result.fullUrl = fullUrl;
    if (fullUrl != null)
    {
      return new ResponseEntity<UrlToShorten>(result, HttpStatus.OK);
    }
    return new ResponseEntity<UrlToShorten>(result, HttpStatus.NOT_FOUND);
  }

  @PostMapping("/shorten")
  public ResponseEntity<UrlToShorten> shorten(@RequestBody UrlToShorten urlToShorten) {
    try 
    {
      UrlToShorten result = urlService.getExistingShortUrl(urlToShorten);
      
      System.out.println("result ");
      // System.out.println(result.shortUrl);
      if (null != result)
      {
        System.out.println("hitting existing");
        return new ResponseEntity<UrlToShorten>(urlToShorten, HttpStatus.OK);
      }
      System.out.println("creating new");
      return new ResponseEntity<UrlToShorten>(urlService.shortenUrl(urlToShorten), HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<UrlToShorten>(urlToShorten, HttpStatus.CONFLICT);
    }
  }

}