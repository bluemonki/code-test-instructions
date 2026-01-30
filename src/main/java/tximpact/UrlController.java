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
  public ResponseEntity getFullUrl(@PathVariable String shortUrl) {
    String fullUrl = urlService.getFullUrl(shortUrl);
    if (fullUrl != null)
    {
      return new ResponseEntity<String>(fullUrl, HttpStatus.OK);
    }
    return new ResponseEntity<String>("Not found", HttpStatus.NOT_FOUND);
  }

  @PostMapping("/shorten")
  public ResponseEntity shorten(@RequestBody UrlToShorten urlToShorten) {
    try 
    {
      String result = urlService.getExistingShortUrl(urlToShorten);
      
      if (null != result)
      {
        return new ResponseEntity<String>(urlToShorten.customAlias, HttpStatus.OK);
      }

      return new ResponseEntity<String>(urlService.shortenUrl(urlToShorten), HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
    }
  }

}