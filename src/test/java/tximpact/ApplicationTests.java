package tximpact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

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
		urlToShorten.setCustomAlias("example");

		var response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
	}

	@Test
	void testExactDuplicates() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");

		var response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());

		response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
	}

	@Test
	void testNonExactDuplicates() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");

		var response = this.wrapper.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());

		urlToShorten.setFullUrl("http://different-example.com");

		response = this.wrapper.shorten(urlToShorten);
		assert(false == response.getStatusCode().is2xxSuccessful());
	}

}
