export interface ShortenedUrl {
  fullUrl: string;
  shortUrl: string;
  customAlias: string | null;
}

export async function shortenUrl(apiBaseUrl: string, data: { fullUrl: string; customAlias: string | null }): Promise<ShortenedUrl> {
  const response = await fetch(apiBaseUrl + "/shorten", {
    method: "POST",
    headers: {
      "Content-Type": "Application/JSON",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    if (response.status === 409) {
      throw new Error("Sorry, URL is already taken.");
    } else if (response.status === 422) {
      throw new Error("URL should be valid (start with 'http://' or 'https://').");
    } else {
      throw new Error("HTTP error " + response.status);
    }
  }

  return response.json();
}

export async function getUrls(apiBaseUrl: string): Promise<ShortenedUrl[]> {
  const response = await fetch(apiBaseUrl + "/urls", {
    method: "GET",
    headers: {
      "Content-Type": "Application/JSON",
    },
  });

  if (!response.ok) {
    throw new Error("HTTP error " + response.status);
  }

  return response.json();
}
