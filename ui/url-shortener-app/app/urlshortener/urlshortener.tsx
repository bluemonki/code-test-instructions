import { use, useState } from 'react';
import logo from "./logo.png";

export function UrlShortenr() {

  const [url, setUrl] = useState("");
  const [customAlias, setCustomAlias] = useState("");
  const [disabled, setDisabled] = useState(true);
  const [shortUrl, setShortUrl] = useState("");
  const [pending, setPending] = useState(false);
  const [error, setError] = useState(null);
  const [uiUrl, setUiUrl] = useState('');

  const apiBaseUrl = 'http://localhost:8080';

  // build the form data
  function buildData() {
    let tempCustomAlias = null;
    if (customAlias && customAlias.length > 0) {
      tempCustomAlias = customAlias;
    }
    return ( {fullUrl: url, customAlias: tempCustomAlias} );
  }

  // handle the submit and errors
  function handleSubmit(event:any) {
    event.preventDefault();
    setPending(true);
    setError(null);
    console.log("Submitted:", url);
    console.log("pending:", pending);
    
    fetch( apiBaseUrl + "/shorten", {
      method: "POST",
      headers: {
        "Content-Type": "Application/JSON",
      },
      body: JSON.stringify(buildData()),
    })
    .then((response) => {
      if (!response.ok) {
        if (response.status === 409) {
          throw new Error("Sorry, URL is already taken.");
        }
        else if (response.status === 422) {
          throw new Error("URL should be valid (start with 'http://' or 'https://').");
        }
        else {
          throw new Error("HTTP error " + response.status);
        }
      }
      return response.json();
    })
    .then((data) => {
      console.log(data);
      setShortUrl(data.shortUrl);
    })
    .catch((error) => {
      console.log(error);
      setError(error.message);
    });
    setPending(false);
  }

  function handleUrlChange(e:any) {
    setUrl(e.target.value);
    if (e.target.value.length > 0) {
      setDisabled(false);
    } else {
      setDisabled(true);
    }
  }

  function handleAliasChange(e:any) {
    setCustomAlias(e.target.value);
    if (e.target.value.length > 0) {
      setDisabled(false);
    } else {
      setDisabled(true);
    }
  }

  function setUiBaseUrl() {
    if (window !== undefined && window.location) {
      const host = window.location.host;
      const protocol = window.location.protocol;
      setUiUrl(protocol + '//' + host );
      return true;
    }
    return false;
  }

  return (
    
    <div className="flex flex-col items-center justify-center pt-16 pb-4">
      
      <img src={logo} alt="URL Shortenr Logo" className="block w-64 mb-8"/>

      <form onSubmit={handleSubmit}>
        { /* input button for full url */ }
        <input
            placeholder="URL to shorten http://blah.com/long/url"
            className="shadow-md p-2 m-5 rounded border border-gray-300px"  
            type="text" 
            value={url}
            onChange={handleUrlChange}
          />
        { /* input button for custom alias - optional */ }
        <br className="p-10" />
        <input
            placeholder="Custom Alias (optional)"
            className="shadow-md p-2 m-5 rounded border border-gray-300px"  
            type="text" 
            value={customAlias}
            onChange={handleAliasChange}
          />
        <br/>
        <input  type="submit" 
                className="bg-green-500 text-white font-bold py-2 px-4 m-5 rounded mt-4 hover:bg-green-700" 
                value="Shorten URL"
                disabled={disabled || pending}/>
      </form>
      {uiUrl.length == 0 && setUiBaseUrl()}
      { shortUrl && 
        <span className="p-5 border-none">
        <span className="mt-4 font-semibold">Shortened URL:</span>
        <a href={'/' + shortUrl} className="text-blue-500 underline ml-2" target="_blank" rel="noreferrer">{uiUrl + '/' + shortUrl}</a>
        <button 
          onClick={() => {
            navigator.clipboard.writeText(uiUrl + '/' + shortUrl);
          }}>

          <span id="default-icon">
              <svg className="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 4h3a1 1 0 0 1 1 1v15a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1h3m0 3h6m-6 5h6m-6 4h6M10 3v4h4V3h-4Z"/></svg>
          </span>
          <span id="success-icon" className="hidden">
              <svg className="w-4 h-4 text-fg-brand" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 4h3a1 1 0 0 1 1 1v15a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1h3m0 3h6m-6 7 2 2 4-4m-5-9v4h4V3h-4Z"/></svg>
          </span>
        </button>
      </span> }
      { error && 
        <span className="p-5 border-none">
          <span className="mt-4 font-semibold">Error:</span>
          <span className="text-red-500 ml-2">{error}</span>
        </span>
      }
    </div>
    );
}