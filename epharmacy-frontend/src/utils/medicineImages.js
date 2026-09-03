import { PORTS } from "../api/client";

// Real static photos bundled with this app (copied from the medicine
// service's own src/main/resources/static/images folder) — used whenever
// a medicine record has no imageUrl of its own, or its imageUrl can't be
// reached. No remote URLs, no generated placeholder art.
const CATALOG = "/images/catalog";

export const STATIC_IMAGES = {
  paracetamol: `${CATALOG}/paracetamol_500mg.jpg`,
  vitaminB12: `${CATALOG}/vitamin_B12.webp`,
  amlaPowder: `${CATALOG}/Amla-powder-1.png`,
  ayurvedic: `${CATALOG}/ayurvedic.webp`,
  english: `${CATALOG}/english.webp`,
  homeopathy: `${CATALOG}/homiopati.webp`,
  tonic: `${CATALOG}/tonic.webp`,
  ors: `${CATALOG}/ors.webp`,
  general: `${CATALOG}/delo.webp`,
  sunscreen:`${CATALOG}/sunscreen.webp`,
  skincare:`${CATALOG}/skincare.webp`,
  antyfungal:`${CATALOG}/antyfungal.webp`,
  ashwagandha:`${CATALOG}/ashwagandha.webp`,
};

const ALL_STATIC = Object.values(STATIC_IMAGES);

// Best-fit static photo per catalog category.
const CATEGORY_IMAGE = {
  Painkiller: STATIC_IMAGES.paracetamol,
  "Cold & Flu": STATIC_IMAGES.ors,
  Vitamin: STATIC_IMAGES.vitaminB12,
  Supplement: STATIC_IMAGES.tonic,
  Antibiotic: STATIC_IMAGES.english,
  Cardiac: STATIC_IMAGES.english,
  Diabetes: STATIC_IMAGES.general,
  Skincare: STATIC_IMAGES.antyfungal,
  Digestive: STATIC_IMAGES.amlaPowder,
  Ayurvedic: STATIC_IMAGES.ayurvedic,
  Homeopathy: STATIC_IMAGES.homeopathy,
};

function hashString(str) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i);
    hash |= 0;
  }
  return Math.abs(hash);
}

// A deterministic static photo for a category the admin console typed in
// freehand (i.e. one that isn't in CATEGORY_IMAGE above).
export function staticImageFor(seed) {
  if (seed && CATEGORY_IMAGE[seed]) return CATEGORY_IMAGE[seed];
  const idx = hashString(seed || "medicine") % ALL_STATIC.length;
  return ALL_STATIC[idx];
}

// Resolves whatever the API gave us for imageUrl into something the
// <img> tag can load:
//  - a full http(s) URL is used as-is
//  - "/images/catalog/…" is one of this app's own bundled static photos
//    (public/images/catalog) — served from the frontend's own origin
//  - any other path starting with "/" is served from the medicine
//    service's own static file host (src/main/resources/static/**)
//  - anything else (blank, null) means "no photo" — caller should fall
//    back to staticImageFor(category)
export function resolveImageUrl(imageUrl) {
  if (!imageUrl) return null;
  if (/^https?:\/\//i.test(imageUrl)) return imageUrl;
  if (imageUrl.startsWith("/images/catalog/")) return imageUrl;
  const path = imageUrl.startsWith("/") ? imageUrl : `/${imageUrl}`;
  return `${PORTS.medicine}${path}`;
}

export function monogramFor(name) {
  if (!name) return "Rx";
  const words = name.trim().split(/\s+/);
  if (words.length === 1) return words[0].slice(0, 2).toUpperCase();
  return (words[0][0] + words[1][0]).toUpperCase();
}

const PALETTE = [
  { bg: "#0f3d3e", fg: "#f7f3ea" },
  { bg: "#c67e1f", fg: "#1b2430" },
  { bg: "#d1552c", fg: "#f7f3ea" },
  { bg: "#3c6e57", fg: "#f7f3ea" },
  { bg: "#7a5c3e", fg: "#f7f3ea" },
  { bg: "#2e5266", fg: "#f7f3ea" },
];

export function paletteFor(seed) {
  const idx = hashString(seed || "medicine") % PALETTE.length;
  return PALETTE[idx];
}
