import { use, useState } from 'react';
import logo from "./logo.png";
import { shortenUrl, getUrls } from './actions';

interface ShortenedUrl {
  shortUrl: string;
  fullUrl: string;
  customAlias?: string | null;
}

interface CopyButtonProps {
  textToCopy: string;
  showSuccessIcon?: boolean;
  className?: string;
}

const CopyButton = ({ textToCopy, showSuccessIcon = false, className = "" }: CopyButtonProps) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(textToCopy);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <button 
      onClick={handleCopy}
      className={className}>
      <span id="default-icon" className={copied ? "hidden" : ""}>
        <svg className="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 4h3a1 1 0 0 1 1 1v15a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1h3m0 3h6m-6 5h6m-6 4h6M10 3v4h4V3h-4Z"/></svg>
      </span>
      {showSuccessIcon && (
        <span id="success-icon" className={copied ? "" : "hidden"}>
          <svg className="w-4 h-4 text-fg-brand" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 4h3a1 1 0 0 1 1 1v15a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1h3m0 3h6m-6 7 2 2 4-4m-5-9v4h4V3h-4Z"/></svg>
        </span>
      )}
    </button>
  );
};

export function UrlShortenr() {

  const [url, setUrl] = useState("");
  const [customAlias, setCustomAlias] = useState("");
  const [disabled, setDisabled] = useState(true);
  const [shortUrl, setShortUrl] = useState("");
  const [pending, setPending] = useState(false);
  const [error, setError] = useState(null);
  const [uiUrl, setUiUrl] = useState('');
  const [showUrlList, setShowUrlList] = useState(false);
  const [urlsList, setUrlsList] = useState<ShortenedUrl[]>([]);
  const [urlsLoading, setUrlsLoading] = useState(false);
  const [urlsError, setUrlsError] = useState(null);

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
    
    shortenUrl(apiBaseUrl, buildData())
    .then((data) => {
      setShortUrl(data.shortUrl);
    })
    .catch((error) => {
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

  function handleShowAllUrls() {
    setShowUrlList(true);
    setUrlsLoading(true);
    setUrlsError(null);
    getUrls(apiBaseUrl)
      .then((data) => {
        setUrlsList(data);
        setUrlsLoading(false);
      })
      .catch((error) => {
        setUrlsError(error.message);
        setUrlsLoading(false);
      });
  }

  function handleShortenUrl() {
    setShowUrlList(false);
  }

  return (
    
    <div className="flex flex-col items-center justify-center pt-16 pb-4">
      <div className="absolute top-4 left-4">
        <button
          onClick={showUrlList ? handleShortenUrl : handleShowAllUrls}
          className="bg-blue-500 text-white font-bold py-2 px-4 rounded hover:bg-blue-700"
        >
          {showUrlList ? "Shorten a URL" : "Show All URLs"}
        </button>
      </div>
      
      <img src={logo} alt="URL Shortenr Logo" className="block w-64 mb-8"/>

      {!showUrlList && <form onSubmit={handleSubmit}>
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
      </form>}

      {uiUrl.length == 0 && typeof window !== 'undefined' && setUiBaseUrl()}
      { shortUrl && !error &&
        <span className="p-5 border-none">
        <span className="mt-4 font-semibold">Shortened URL:</span>
        <div className="flex items-center gap-2">
          <a href={'/' + shortUrl} className="text-blue-500 underline ml-2" target="_blank" rel="noreferrer">{uiUrl + '/' + shortUrl}</a>
          <CopyButton textToCopy={uiUrl + '/' + shortUrl} showSuccessIcon={true} />
        </div>
      </span> }
      { error && 
        <span className="p-5 border-none">
          <span className="mt-4 font-semibold">Error:</span>
          <span className="text-red-500 ml-2">{error}</span>
        </span>
      }
      
      {showUrlList && (
        <div className="p-5 border border-gray-300 rounded shadow-md">
          <h2 className="text-2xl font-bold mb-4">All Shortened URLs</h2>
          {urlsLoading && <p className="text-gray-500">Loading...</p>}
          {urlsError && <p className="text-red-500">Error: {urlsError}</p>}
          {!urlsLoading && !urlsError && urlsList.length === 0 && <p className="text-gray-500">No URLs found.</p>}
          {!urlsLoading && !urlsError && urlsList.length > 0 && (
            <ul className="space-y-2">
              {urlsList.map((item, index) => (
                <li key={index} data-testid={`url-item-${item.shortUrl}`} className="p-3 bg-gray-100 rounded">
                  <div className="flex items-center gap-2">
                    <div><strong>Short URL:</strong> {uiUrl + '/' + item.shortUrl}</div>
                    <CopyButton textToCopy={uiUrl + '/' + item.shortUrl} className="p-1 hover:opacity-75" />
                  </div>
                  <div><strong>Full URL:</strong> <a href={item.fullUrl} target="_blank" rel="noreferrer" className="text-blue-500 underline">{item.fullUrl}</a></div>
                  {item.customAlias && <div><strong>Custom Alias:</strong> {item.customAlias}</div>}
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
      
    </div>
    );
}