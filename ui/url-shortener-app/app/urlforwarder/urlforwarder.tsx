import { use, useState, useEffect } from 'react';
import { useLocation } from 'react-router';
import { useNavigate } from 'react-router';
import logo from "./logo.png";

export function UrlForwardr() {
    const location = useLocation();
    const [fullUrl, setFullUrl] = useState(null);
    const [error, setError] = useState(null);

    const [duration, setDuration] = useState(10);

    // timer to do the forwarding
    useEffect(() => {
        duration > 0 && setTimeout(() => setDuration(duration - 1), 1000);
        if (duration == 0) { 
            if (null != fullUrl) {window.location.href = fullUrl; }
        }
      }, [duration]);
    
    // call the API on page load
    useEffect( () => {
        if(!fullUrl && !error) {
            console.log("calling api");
            callApi();
        }
    }, [fullUrl]);

    // API call
    function callApi() {
        setError(null);
        console.log("Submitted:", location);
        
        let urlToCall = "http://localhost:8080" + location.pathname;
        
        fetch(urlToCall, {
            method: "GET",
            headers: {
                "Content-Type": "Application/JSON",
            },
        })
        .then((response) => {
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error("Sorry, URL does not exist");
            }
            else {
                throw new Error("HTTP error " + response.status);
            }
        }
        return response.json();
        })
        .then((data) => {
            console.log(data);
            setFullUrl(data.fullUrl);
            setDuration(5);
        })
        .catch((error) => {
            console.log(error);
            setError(error.message);
        });
    }
    

    return (
        <p className="flex flex-col items-center justify-center pt-16 pb-4">

        <img src={logo} alt="URL Shortenr Logo" className="block w-64 mb-8"/>

        
        {fullUrl && 
            <span className="mt-4 font-semibold">Forwaring to: {fullUrl}</span>
        }
        {error && 
            <span className="p-5 border-none">
                <span className="mt-4 font-semibold">Error:</span>
                <span className="text-red-500 ml-2">{error}</span>
            </span>
        }
        </p>
        
    );
    
}