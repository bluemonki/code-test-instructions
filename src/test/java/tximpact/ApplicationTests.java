package tximpact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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

		var response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
	}

	@Test
	void addShortUrlWithCustomAlias() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");

		var response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(response.getBody() == "example");
	}

	@Test
	void testExactDuplicates() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");
		
		var response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(response.getBody() == "example");

		response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(response.getBody() == "example");
	}

	@Test
	void testNonExactDuplicates() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");

		var response1 = this.wrapper.shorten(urlToShorten);
		assert(response1.getStatusCode().is2xxSuccessful());
		assert(response1.getBody() == "example");

		urlToShorten.setFullUrl("http://different-example.com");

		var response2 = this.wrapper.shorten(urlToShorten);
		assert(response2.getStatusCode() == HttpStatus.CONFLICT);

		assert(response1.getBody() != response2.getBody());
	}

	@Test
	void testNonCharacters() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		
		var response1 = this.wrapper.shorten(urlToShorten);
		assert(response1.getStatusCode().is2xxSuccessful());

		urlToShorten.setFullUrl("http://ex-ample.com");

		var response2 = this.wrapper.shorten(urlToShorten);
		assert(response2.getStatusCode().is2xxSuccessful());

		assert(response1.getBody() != response2.getBody());
	}

	@Test
	void testMoreNonCharacters() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		
		var response1 = this.wrapper.shorten(urlToShorten);
		assert(response1.getStatusCode().is2xxSuccessful());
		
		urlToShorten.setFullUrl("http://3x-@mp^$;.com");

		var response2 = this.wrapper.shorten(urlToShorten);
		assert(response2.getStatusCode().is2xxSuccessful());
		
		assert(response1.getBody() != response2.getBody());
	}

	@Test
	void testGettingShortUrl() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");

		var response1 = this.wrapper.shorten(urlToShorten);
		assert(response1.getStatusCode().is2xxSuccessful());

		var response2 = this.wrapper.getFullUrl(response1.getBody().toString());
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(response2.getBody() == "http://example.com");
	}

	@Test
	void testGettingNonExistantShortUrl() {
		var response = this.wrapper.getFullUrl("fake");
		assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
	}

	@Test
	void testDecodeWithMoreNonCharacters() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		
		var response1Short = this.wrapper.shorten(urlToShorten);
		assert(response1Short.getStatusCode().is2xxSuccessful());
		var response1Full = this.wrapper.getFullUrl(response1Short.getBody().toString());
		assert(response1Full.getBody() == "http://example.com");

		urlToShorten.setFullUrl("http://3x-@mp^$;.com");

		var response2Short = this.wrapper.shorten(urlToShorten);
		assert(response2Short.getStatusCode().is2xxSuccessful());
		var response2Full = this.wrapper.getFullUrl(response2Short.getBody().toString());
		assert(response2Full.getBody() == "http://3x-@mp^$;.com");
	}
}
