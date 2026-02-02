package tximpact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

// may need this later
class UrlControllerWrapper extends UrlController {
  public void reset() {
  }
}

@SpringBootTest
class ApplicationTests {

	private UrlControllerWrapper wrapper;

	@BeforeEach
	void setUp() {
		this.wrapper = new UrlControllerWrapper();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void addShortUrl() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");

		ResponseEntity<UrlToShorten> response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
	}

	@Test
	void addShortUrlWithCustomAlias() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");

		ResponseEntity<UrlToShorten> response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(response.getBody().shortUrl == "example");
	}

	@Test
	void testExactDuplicates() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");
		
		ResponseEntity<UrlToShorten> response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(response.getBody().shortUrl == "example");

		response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(response.getBody().shortUrl == "example");
	}

	@Test
	void testNonExactDuplicates() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		urlToShorten1.setCustomAlias("example");

		ResponseEntity<UrlToShorten> response1 = this.wrapper.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());
		assert(response1.getBody().shortUrl == "example");

		UrlToShorten urlToShorten2 = new UrlToShorten();
		urlToShorten2.setFullUrl("http://different-example.com");
		urlToShorten2.setCustomAlias("example");

		ResponseEntity<UrlToShorten> response2 = this.wrapper.shorten(urlToShorten2);
		assert(response2.getStatusCode() == HttpStatus.CONFLICT);

		assert(response1.getBody().shortUrl != response2.getBody().shortUrl);
	}

	@Test
	void testNonCharacters() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		
		ResponseEntity<UrlToShorten> response1 = this.wrapper.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		UrlToShorten urlToShorten2 = new UrlToShorten();
		urlToShorten2.setFullUrl("http://ex-ample.com");

		ResponseEntity<UrlToShorten> response2 = this.wrapper.shorten(urlToShorten2);
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(response1.getBody().shortUrl != response2.getBody().shortUrl);
	}

	@Test
	void testMoreNonCharacters() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		
		ResponseEntity<UrlToShorten> response1 = this.wrapper.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());
		
		UrlToShorten urlToShorten2 = new UrlToShorten();
		urlToShorten2.setFullUrl("http://3x-@mp^$;.com");

		ResponseEntity<UrlToShorten> response2 = this.wrapper.shorten(urlToShorten2);
		assert(response2.getStatusCode().is2xxSuccessful());
		
		assert(response1.getBody().shortUrl != response2.getBody().shortUrl);
	}

	@Test
	void testGettingShortUrl() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");

		ResponseEntity<UrlToShorten> response1 = this.wrapper.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		ResponseEntity<UrlToShorten> response2 = this.wrapper.getFullUrl(response1.getBody().shortUrl);
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(response2.getBody().fullUrl == "http://example.com");
	}

	@Test
	void testGettingNonExistantShortUrl() {
		ResponseEntity<UrlToShorten> response = this.wrapper.getFullUrl("fake");
		assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
	}

	@Test
	void testDecodeWithMoreNonCharacters() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		
		ResponseEntity<UrlToShorten> response1Short = this.wrapper.shorten(urlToShorten1);
		assert(response1Short.getStatusCode().is2xxSuccessful());
		ResponseEntity<UrlToShorten> response1Full = this.wrapper.getFullUrl(response1Short.getBody().shortUrl);
		assert(response1Full.getStatusCode().is2xxSuccessful());
		assert(response1Full.getBody().fullUrl == "http://example.com");

		UrlToShorten urlToShorten2 = new UrlToShorten();
		urlToShorten2.setFullUrl("http://3x-@mp^$;.com");
		
		ResponseEntity<UrlToShorten> response2Short = this.wrapper.shorten(urlToShorten2);
		assert(response2Short.getStatusCode().is2xxSuccessful());
		ResponseEntity<UrlToShorten> response2Full = this.wrapper.getFullUrl(response2Short.getBody().shortUrl);
		assert(response2Full.getBody().fullUrl == "http://3x-@mp^$;.com");
	}


	@Test
	void deleteExistingUrl() {
		// create the URL
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		
		ResponseEntity<UrlToShorten> response1 = this.wrapper.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		// delete it
		String shortUrl = response1.getBody().shortUrl;

		ResponseEntity<UrlToShorten> response2 = this.wrapper.deleteUrl(shortUrl);
		assert(response2.getStatusCode().is2xxSuccessful());

		// try and get it again
		ResponseEntity<UrlToShorten> response3 = this.wrapper.getFullUrl(shortUrl);
		assert(response3.getStatusCode() == HttpStatus.NOT_FOUND);
		assert(response3.getBody().shortUrl == null);
	}

	@Test
	void deleteExistingUrlWithCustomAlias() {
		// create the URL
		String customAlias = "DELETE_ME";
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		urlToShorten1.setCustomAlias(customAlias);
		
		ResponseEntity<UrlToShorten> response1 = this.wrapper.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		// delete it
		ResponseEntity<UrlToShorten> response2 = this.wrapper.deleteUrl(customAlias);
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(response2.getStatusCode() == HttpStatus.NO_CONTENT);

		// try and get it again
		ResponseEntity<UrlToShorten> response3 = this.wrapper.getFullUrl(customAlias);
		assert(response3.getStatusCode() == HttpStatus.NOT_FOUND);
		assert(response3.getBody().shortUrl == null);
	}

	@Test
	void deleteNonExistantUrl() {
		String shortUrl = "MISSING";

		ResponseEntity<UrlToShorten> response1 = this.wrapper.deleteUrl(shortUrl);
		assert(response1.getStatusCode() == HttpStatus.NOT_FOUND);
	}

	@Test
	void createDeleteCreateAgain() {
		// create the URL
		String shortUrl = null;
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		
		ResponseEntity<UrlToShorten> response1 = this.wrapper.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());
		shortUrl = response1.getBody().shortUrl;

		// delete it
		ResponseEntity<UrlToShorten> response2 = this.wrapper.deleteUrl(shortUrl);
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(response2.getStatusCode() == HttpStatus.NO_CONTENT);

		// try and get it again
		ResponseEntity<UrlToShorten> response3 = this.wrapper.getFullUrl(shortUrl);
		assert(response3.getStatusCode() == HttpStatus.NOT_FOUND);
		assert(response3.getBody().shortUrl == null);

		// create it again
		ResponseEntity<UrlToShorten> response4 = this.wrapper.shorten(urlToShorten1);
		assert(response4.getStatusCode().is2xxSuccessful());
		// this should create a different shorturl
		assert(response4.getBody().shortUrl != shortUrl);
	}

	@Test
	void createDeleteCreateAgainCustomAlias() {
		// create the URL
		String customAlias = "EXISSTENTIAL";
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		urlToShorten1.setCustomAlias(customAlias);
		
		ResponseEntity<UrlToShorten> response1 = this.wrapper.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		// delete it
		ResponseEntity<UrlToShorten> response2 = this.wrapper.deleteUrl(customAlias);
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(response2.getStatusCode() == HttpStatus.NO_CONTENT);

		// try and get it again
		ResponseEntity<UrlToShorten> response3 = this.wrapper.getFullUrl(customAlias);
		assert(response3.getStatusCode() == HttpStatus.NOT_FOUND);
		assert(response3.getBody().shortUrl == null);

		// create it again
		ResponseEntity<UrlToShorten> response4 = this.wrapper.shorten(urlToShorten1);
		assert(response4.getStatusCode().is2xxSuccessful());
		assert(response4.getBody().customAlias == customAlias);
	}

	@Test
	void addInvalidUrl() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("htp://example.com");

		ResponseEntity<UrlToShorten> response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode() == HttpStatus.UNPROCESSABLE_CONTENT);
	}
		
}
