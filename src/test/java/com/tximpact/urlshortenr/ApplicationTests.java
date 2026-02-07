package com.tximpact.urlshortenr;

import com.tximpact.urlshortenr.controller.UrlController;
import com.tximpact.urlshortenr.entity.UrlToShorten;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Objects;


@SpringBootTest
// don't let tests effect one another
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApplicationTests {

	@Autowired
	UrlController urlController;

	@Test
	void contextLoads() {
	}

	@Test
	void addShortUrl() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");

		ResponseEntity<UrlToShorten> response = this.urlController.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
	}

	@Test
	void addShortUrlWithCustomAlias() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");

		ResponseEntity<UrlToShorten> response = this.urlController.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(Objects.equals(Objects.requireNonNull(response.getBody()).getShortUrl(), "example"));
	}

	@Test
	void testExactDuplicates() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("http://example.com");
		urlToShorten.setCustomAlias("example");

		ResponseEntity<UrlToShorten> response = this.urlController.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(Objects.equals(Objects.requireNonNull(response.getBody()).getShortUrl(), "example"));

		response = this.urlController.shorten(urlToShorten);
		assert(response.getStatusCode().is2xxSuccessful());
		assert(Objects.equals(Objects.requireNonNull(response.getBody()).getShortUrl(), "example"));
	}

	@Test
	void testNonExactDuplicates() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		urlToShorten1.setCustomAlias("example");

		ResponseEntity<UrlToShorten> response1 = this.urlController.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());
		assert(Objects.equals(Objects.requireNonNull(response1.getBody()).getShortUrl(), "example"));

		UrlToShorten urlToShorten2 = new UrlToShorten();
		urlToShorten2.setFullUrl("http://different-example.com");
		urlToShorten2.setCustomAlias("example");

		ResponseEntity<UrlToShorten> response2 = this.urlController.shorten(urlToShorten2);
		assert(response2.getStatusCode() == HttpStatus.CONFLICT);
	}

	@Test
	void testNonCharacters() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");

		ResponseEntity<UrlToShorten> response1 = this.urlController.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		UrlToShorten urlToShorten2 = new UrlToShorten();
		urlToShorten2.setFullUrl("http://ex-ample.com");

		ResponseEntity<UrlToShorten> response2 = this.urlController.shorten(urlToShorten2);
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(!Objects.equals(Objects.requireNonNull(response1.getBody()).getShortUrl(), Objects.requireNonNull(response2.getBody()).getShortUrl()));
	}

	@Test
	void testMoreNonCharacters() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");

		ResponseEntity<UrlToShorten> response1 = this.urlController.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		UrlToShorten urlToShorten2 = new UrlToShorten();
		urlToShorten2.setFullUrl("http://3x-@mp^$;.com");

		ResponseEntity<UrlToShorten> response2 = this.urlController.shorten(urlToShorten2);
		assert(response2.getStatusCode().is2xxSuccessful());

		assert(!Objects.equals(Objects.requireNonNull(response1.getBody()).getShortUrl(), Objects.requireNonNull(response2.getBody()).getShortUrl()));
	}

	@Test
	void testGettingShortUrl() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");

		ResponseEntity<UrlToShorten> response1 = this.urlController.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		ResponseEntity<UrlToShorten> response2 = this.urlController.getFullUrl(response1.getBody().getShortUrl());
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(Objects.equals(Objects.requireNonNull(response2.getBody()).getFullUrl(), "http://example.com"));
	}

	@Test
	void testGettingNonExistentShortUrl() {
		ResponseEntity<UrlToShorten> response = this.urlController.getFullUrl("fake");
		assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
	}

	@Test
	void testDecodeWithMoreNonCharacters() {
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");

		ResponseEntity<UrlToShorten> response1Short = this.urlController.shorten(urlToShorten1);
		assert(response1Short.getStatusCode().is2xxSuccessful());
		ResponseEntity<UrlToShorten> response1Full = this.urlController.getFullUrl(response1Short.getBody().getShortUrl());
		assert(response1Full.getStatusCode().is2xxSuccessful());
		assert(Objects.equals(Objects.requireNonNull(response1Full.getBody()).getFullUrl(), "http://example.com"));

		UrlToShorten urlToShorten2 = new UrlToShorten();
		urlToShorten2.setFullUrl("http://3x-@mp^$;.com");

		ResponseEntity<UrlToShorten> response2Short = this.urlController.shorten(urlToShorten2);
		assert(response2Short.getStatusCode().is2xxSuccessful());
		ResponseEntity<UrlToShorten> response2Full = this.urlController.getFullUrl(response2Short.getBody().getShortUrl());
		assert(response2Full.getBody().getFullUrl() == "http://3x-@mp^$;.com");
	}


	@Test
	void deleteExistingUrl() {
		// create the URL
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");

		ResponseEntity<UrlToShorten> response1 = this.urlController.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		// delete it
		String shortUrl = response1.getBody().getShortUrl();

		ResponseEntity<UrlToShorten> response2 = this.urlController.deleteUrl(shortUrl);
		assert(response2.getStatusCode().is2xxSuccessful());

		// try and get it again
		ResponseEntity<UrlToShorten> response3 = this.urlController.getFullUrl(shortUrl);
		assert(response3.getStatusCode() == HttpStatus.NOT_FOUND);
		assert(response3.getBody().getShortUrl() == null);
	}

	@Test
	void deleteExistingUrlWithCustomAlias() {
		// create the URL
		String customAlias = "DELETE_ME";
		UrlToShorten urlToShorten1 = new UrlToShorten();
		urlToShorten1.setFullUrl("http://example.com");
		urlToShorten1.setCustomAlias(customAlias);

		ResponseEntity<UrlToShorten> response1 = this.urlController.shorten(urlToShorten1);
		assert(response1.getStatusCode().is2xxSuccessful());

		// delete it
		ResponseEntity<UrlToShorten> response2 = this.urlController.deleteUrl(customAlias);
		assert(response2.getStatusCode().is2xxSuccessful());
		assert(response2.getStatusCode() == HttpStatus.NO_CONTENT);

		// try and get it again
		ResponseEntity<UrlToShorten> response3 = this.urlController.getFullUrl(customAlias);
		assert(response3.getStatusCode() == HttpStatus.NOT_FOUND);
		assert(response3.getBody().getShortUrl() == null);
	}

	@Test
	void deleteNonExistantUrl() {
		String shortUrl = "MISSING";

		ResponseEntity<UrlToShorten> response1 = this.urlController.deleteUrl(shortUrl);
		assert(response1.getStatusCode() == HttpStatus.NOT_FOUND);
	}

	@Test
	void createDeleteCreateAgain() {
		// create the URL
		String shortUrl = null;
		{
			UrlToShorten urlToShorten1 = new UrlToShorten();
			urlToShorten1.setFullUrl("http://example.com");


			ResponseEntity<UrlToShorten> response1 = this.urlController.shorten(urlToShorten1);
			assert (response1.getStatusCode().is2xxSuccessful());
			shortUrl = response1.getBody().getShortUrl();

			// delete it
			ResponseEntity<UrlToShorten> response2 = this.urlController.deleteUrl(shortUrl);
			assert (response2.getStatusCode().is2xxSuccessful());
			assert (response2.getStatusCode() == HttpStatus.NO_CONTENT);

			// try and get it again
			ResponseEntity<UrlToShorten> response3 = this.urlController.getFullUrl(shortUrl);
			assert (response3.getStatusCode() == HttpStatus.NOT_FOUND);
			assert (Objects.requireNonNull(response3.getBody()).getShortUrl() == null);

		}

		{
			UrlToShorten urlToShorten2 = new UrlToShorten();
			urlToShorten2.setFullUrl("http://example.com");

			// create it again
			ResponseEntity<UrlToShorten> response4 = this.urlController.shorten(urlToShorten2);
			assert(response4.getStatusCode().is2xxSuccessful());
			// this should create a different shorturl
			assert(!Objects.equals(Objects.requireNonNull(response4.getBody()).getShortUrl(), shortUrl));
		}


	}

	@Test
	void createDeleteCreateAgainCustomAlias() {
		// create the URL
		String customAlias = "EXISSTENTIAL";
		{
			UrlToShorten urlToShorten1 = new UrlToShorten();
			urlToShorten1.setFullUrl("http://example.com");
			urlToShorten1.setCustomAlias(customAlias);

			ResponseEntity<UrlToShorten> response1 = this.urlController.shorten(urlToShorten1);
			assert (response1.getStatusCode().is2xxSuccessful());

			// delete it
			ResponseEntity<UrlToShorten> response2 = this.urlController.deleteUrl(customAlias);
			assert (response2.getStatusCode().is2xxSuccessful());
			assert (response2.getStatusCode() == HttpStatus.NO_CONTENT);

			// try and get it again
			ResponseEntity<UrlToShorten> response3 = this.urlController.getFullUrl(customAlias);
			assert (response3.getStatusCode() == HttpStatus.NOT_FOUND);
			assert (response1.hasBody());
			assert (response3.getBody().getCustomAlias() == null);
		}

		{
			UrlToShorten urlToShorten1 = new UrlToShorten();
			urlToShorten1.setFullUrl("http://example.com");
			urlToShorten1.setCustomAlias(customAlias);

			// create it again
			ResponseEntity<UrlToShorten> response4 = this.urlController.shorten(urlToShorten1);
			assert(response4.getStatusCode().is2xxSuccessful());
			assert(response4.getBody().getCustomAlias() == customAlias);
		}
	}

	@Test
	void addInvalidUrl() {
		UrlToShorten urlToShorten = new UrlToShorten();
		urlToShorten.setFullUrl("htp://example.com");

		ResponseEntity<UrlToShorten> response = this.urlController.shorten(urlToShorten);
		assert(response.getStatusCode() == HttpStatus.UNPROCESSABLE_CONTENT);
	}
		
}
