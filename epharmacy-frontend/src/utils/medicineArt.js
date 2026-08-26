// Deterministic apothecary-label placeholder art for medicines that don't
// have a real photo yet. Real photos (imageUrl from the API) always win.

const PALETTE = [
  { bg: "#0f3d3e", fg: "#f7f3ea" }, // pine
  { bg: "#c67e1f", fg: "#1b2430" }, // amber
  { bg: "#d1552c", fg: "#f7f3ea" }, // coral
  { bg: "#3c6e57", fg: "#f7f3ea" }, // moss
  { bg: "#7a5c3e", fg: "#f7f3ea" }, // apothecary brown
  { bg: "#2e5266", fg: "#f7f3ea" }, // slate blue
];

function hashString(str) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i);
    hash |= 0;
  }
  return Math.abs(hash);
}

export function paletteFor(seed) {
  const idx = hashString(seed || "medicine") % PALETTE.length;
  return PALETTE[idx];
}

export function monogramFor(name) {
  if (!name) return "Rx";
  const words = name.trim().split(/\s+/);
  if (words.length === 1) return words[0].slice(0, 2).toUpperCase();
  return (words[0][0] + words[1][0]).toUpperCase();
}
