import { shortenUrl } from '../../app/urlshortener/actions';
import type { ShortenedUrl } from '../../app/urlshortener/actions';

describe('shortenUrl', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should throw error when URL is already taken (409)', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        status: 409,
      } as Response)
    );

    const data = { fullUrl: 'https://example.com', customAlias: null };
    
    await expect(shortenUrl('http://localhost:8080', data)).rejects.toThrow(
      'Sorry, URL is already taken.'
    );
  });

  it('should throw error when URL is invalid (422)', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        status: 422,
      } as Response)
    );

    const data = { fullUrl: 'invalid-url', customAlias: null };
    
    await expect(shortenUrl('http://localhost:8080', data)).rejects.toThrow(
      "URL should be valid (start with 'http://' or 'https://')."
    );
  });

  it('should throw error for other HTTP error status codes', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        status: 500,
      } as Response)
    );

    const data = { fullUrl: 'https://example.com', customAlias: null };
    
    await expect(shortenUrl('http://localhost:8080', data)).rejects.toThrow(
      'HTTP error 500'
    );
  });

  it('should successfully shorten URL and return ShortenedUrl', async () => {
    const mockResponse: ShortenedUrl = {
      fullUrl: 'https://example.com/long/url',
      shortUrl: 'abc123',
      customAlias: null,
    };

    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockResponse),
      } as Response)
    );

    const data = { fullUrl: 'https://example.com/long/url', customAlias: null };
    const result = await shortenUrl('http://localhost:8080', data);
    
    expect(result).toEqual(mockResponse);
  });

  it('should successfully shorten URL with custom alias', async () => {
    const mockResponse: ShortenedUrl = {
      fullUrl: 'https://example.com/long/url',
      shortUrl: 'myalias',
      customAlias: 'myalias',
    };

    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockResponse),
      } as Response)
    );

    const data = { fullUrl: 'https://example.com/long/url', customAlias: 'myalias' };
    const result = await shortenUrl('http://localhost:8080', data);
    
    expect(result).toEqual(mockResponse);
  });

  it('should call fetch with correct parameters', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ shortUrl: 'abc123', fullUrl: 'https://example.com', customAlias: null }),
      } as Response)
    );

    const data = { fullUrl: 'https://example.com', customAlias: 'custom' };
    await shortenUrl('http://localhost:8080', data);
    
    expect(global.fetch).toHaveBeenCalledWith(
      'http://localhost:8080/shorten',
      expect.objectContaining({
        method: 'POST',
        headers: { 'Content-Type': 'Application/JSON' },
        body: JSON.stringify(data),
      })
    );
  });
});
