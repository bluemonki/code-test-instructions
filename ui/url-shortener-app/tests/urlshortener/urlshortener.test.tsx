import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { UrlShortenr } from '../../app/urlshortener/urlshortener';
import * as actions from '../../app/urlshortener/actions';

jest.mock('../../app/urlshortener/actions');

describe('UrlShortenr Component - Behavioral Tests', () => {
  let user: ReturnType<typeof userEvent.setup>;

  beforeEach(() => {
    jest.clearAllMocks();
    user = userEvent.setup();
  });

  describe('Form submission and success', () => {
    it('should display shortened URL on successful submission', async () => {
      const mockResponse = {
        fullUrl: 'https://example.com/very/long/url',
        shortUrl: 'abc123',
        customAlias: null,
      };

      (actions.shortenUrl as jest.Mock).mockResolvedValue(mockResponse);

      render(<UrlShortenr />);

      const urlInput = screen.getByPlaceholderText(
        'URL to shorten http://blah.com/long/url'
      );
      const submitButton = screen.getByDisplayValue('Shorten URL');

      await user.type(urlInput, 'https://example.com/very/long/url');
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Shortened URL:')).toBeInTheDocument();
        expect(screen.getByText(new RegExp('abc123'))).toBeInTheDocument();
      });
    });

    it('should display shortened URL with custom alias', async () => {
      const mockResponse = {
        fullUrl: 'https://example.com/long',
        shortUrl: 'myalias',
        customAlias: 'myalias',
      };

      (actions.shortenUrl as jest.Mock).mockResolvedValue(mockResponse);

      render(<UrlShortenr />);

      const urlInput = screen.getByPlaceholderText(
        'URL to shorten http://blah.com/long/url'
      );
      const aliasInput = screen.getByPlaceholderText('Custom Alias (optional)');
      const submitButton = screen.getByDisplayValue('Shorten URL');

      await user.type(urlInput, 'https://example.com/long');
      await user.type(aliasInput, 'myalias');
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Shortened URL:')).toBeInTheDocument();
        expect(screen.getByText(new RegExp('myalias'))).toBeInTheDocument();
      });
    });
  });

  describe('Error handling', () => {
    it('should display error when URL is already taken (409)', async () => {
      (actions.shortenUrl as jest.Mock).mockRejectedValue(
        new Error('Sorry, URL is already taken.')
      );

      render(<UrlShortenr />);

      const urlInput = screen.getByPlaceholderText(
        'URL to shorten http://blah.com/long/url'
      );
      const submitButton = screen.getByDisplayValue('Shorten URL');

      await user.type(urlInput, 'https://example.com');
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Error:')).toBeInTheDocument();
        expect(
          screen.getByText('Sorry, URL is already taken.')
        ).toBeInTheDocument();
      });
    });

    it('should display error when URL is invalid (422)', async () => {
      (actions.shortenUrl as jest.Mock).mockRejectedValue(
        new Error("URL should be valid (start with 'http://' or 'https://').")
      );

      render(<UrlShortenr />);

      const urlInput = screen.getByPlaceholderText(
        'URL to shorten http://blah.com/long/url'
      );
      const submitButton = screen.getByDisplayValue('Shorten URL');

      await user.type(urlInput, 'invalid-url');
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Error:')).toBeInTheDocument();
        expect(
          screen.getByText(
            new RegExp('URL should be valid')
          )
        ).toBeInTheDocument();
      });
    });

    it('should display generic error for other HTTP errors', async () => {
      (actions.shortenUrl as jest.Mock).mockRejectedValue(
        new Error('HTTP error 500')
      );

      render(<UrlShortenr />);

      const urlInput = screen.getByPlaceholderText(
        'URL to shorten http://blah.com/long/url'
      );
      const submitButton = screen.getByDisplayValue('Shorten URL');

      await user.type(urlInput, 'https://example.com');
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Error:')).toBeInTheDocument();
        expect(screen.getByText('HTTP error 500')).toBeInTheDocument();
      });
    });
  });

  describe('View toggling between form and URL list', () => {
    it('should toggle to show URL list when "Show All URLs" button is clicked', async () => {
      const mockUrls = [
        {
          fullUrl: 'https://example.com/1',
          shortUrl: 'abc1',
          customAlias: null,
        },
        {
          fullUrl: 'https://example.com/2',
          shortUrl: 'custom',
          customAlias: 'custom',
        },
      ];

      (actions.getUrls as jest.Mock).mockResolvedValue(mockUrls);

      render(<UrlShortenr />);

      // Initially form should be visible
      expect(
        screen.getByPlaceholderText('URL to shorten http://blah.com/long/url')
      ).toBeInTheDocument();

      // Click "Show All URLs" button
      const toggleButton = screen.getByRole('button', {
        name: /Show All URLs/,
      });
      await user.click(toggleButton);

      // URL list should appear with testIds
      await waitFor(() => {
        expect(screen.getByText('All Shortened URLs')).toBeInTheDocument();
        expect(screen.getByTestId('url-item-abc1')).toBeInTheDocument();
        expect(screen.getByTestId('url-item-custom')).toBeInTheDocument();
      });
    });

    it('should toggle back to form when "Shorten a URL" button is clicked', async () => {
      const mockUrls = [
        {
          fullUrl: 'https://example.com',
          shortUrl: 'abc123',
          customAlias: null,
        },
      ];

      (actions.getUrls as jest.Mock).mockResolvedValue(mockUrls);

      render(<UrlShortenr />);

      // Click "Show All URLs"
      const showAllButton = screen.getByRole('button', {
        name: /Show All URLs/,
      });
      await user.click(showAllButton);

      // Wait for list to appear
      await waitFor(() => {
        expect(screen.getByText('All Shortened URLs')).toBeInTheDocument();
      });

      // Click "Shorten a URL" button (same button, now with different text)
      const shortenButton = screen.getByRole('button', {
        name: /Shorten a URL/,
      });
      await user.click(shortenButton);

      // Form should be visible again
      await waitFor(() => {
        expect(
          screen.getByPlaceholderText('URL to shorten http://blah.com/long/url')
        ).toBeInTheDocument();
      });
    });

    it('should display all URLs with correct details', async () => {
      const mockUrls = [
        {
          fullUrl: 'https://example.com/very/long/url',
          shortUrl: 'abc123',
          customAlias: null,
        },
        {
          fullUrl: 'https://github.com/some/repo',
          shortUrl: 'github',
          customAlias: 'github',
        },
      ];

      (actions.getUrls as jest.Mock).mockResolvedValue(mockUrls);

      render(<UrlShortenr />);

      const showAllButton = screen.getByRole('button', {
        name: /Show All URLs/,
      });
      await user.click(showAllButton);

      await waitFor(() => {
        expect(screen.getByText('All Shortened URLs')).toBeInTheDocument();
        // Verify both URL items are rendered by testId
        expect(screen.getByTestId('url-item-abc123')).toBeInTheDocument();
        expect(screen.getByTestId('url-item-github')).toBeInTheDocument();
      });

      // Verify shortUrl values are displayed correctly in the items
      const abc123Item = screen.getByTestId('url-item-abc123');
      expect(abc123Item).toHaveTextContent('http://localhost/abc123');
      expect(abc123Item).toHaveTextContent('https://example.com/very/long/url');

      const githubItem = screen.getByTestId('url-item-github');
      expect(githubItem).toHaveTextContent('http://localhost/github');
      expect(githubItem).toHaveTextContent('https://github.com/some/repo');

      // Verify both URLs are rendered with their details
      const listItems = screen.getAllByRole('listitem');
      expect(listItems).toHaveLength(2);
    });


    it('should show loading state while fetching URLs', async () => {
      (actions.getUrls as jest.Mock).mockImplementation(
        () =>
          new Promise((resolve) =>
            setTimeout(() =>
              resolve([
                {
                  fullUrl: 'https://example.com',
                  shortUrl: 'abc123',
                  customAlias: null,
                },
              ]),
              100
            )
          )
      );

      render(<UrlShortenr />);

      const showAllButton = screen.getByRole('button', {
        name: /Show All URLs/,
      });
      await user.click(showAllButton);

      // Check for loading state
      expect(screen.getByText('Loading...')).toBeInTheDocument();

      // Wait for list to load
      await waitFor(() => {
        expect(screen.getByText('All Shortened URLs')).toBeInTheDocument();
      });
    });

    it('should display error when fetching URLs fails', async () => {
      (actions.getUrls as jest.Mock).mockRejectedValue(
        new Error('Failed to fetch URLs')
      );

      render(<UrlShortenr />);

      const showAllButton = screen.getByRole('button', {
        name: /Show All URLs/,
      });
      await user.click(showAllButton);

      await waitFor(() => {
        expect(screen.getByText(/Error: Failed to fetch URLs/)).toBeInTheDocument();
      });
    });

    it('should show empty state when no URLs exist', async () => {
      (actions.getUrls as jest.Mock).mockResolvedValue([]);

      render(<UrlShortenr />);

      const showAllButton = screen.getByRole('button', {
        name: /Show All URLs/,
      });
      await user.click(showAllButton);

      await waitFor(() => {
        expect(screen.getByText('No URLs found.')).toBeInTheDocument();
      });
    });
  });
});
