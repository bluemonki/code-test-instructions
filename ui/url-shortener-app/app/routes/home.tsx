import type { Route } from "./+types/home";
import { UrlShortenr } from "../urlshortener/urlshortener";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "URL Shortenr" },
    { name: "description", content: "Shorten a long URL" },
  ];
}

export default function Home() {
  return <UrlShortenr />
}
