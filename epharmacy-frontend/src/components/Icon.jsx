// A small, hand-picked icon set (not a generic library) so every glyph in
// the product matches the same 1.75px stroke weight and 24px grid.
const PATHS = {
  cart: "M3 3h2l2.4 12.2a2 2 0 0 0 2 1.6h7.4a2 2 0 0 0 2-1.6L20.5 8H6.2 M9 20a1 1 0 1 0 0-2 1 1 0 0 0 0 2Zm8 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z",
  user: "M12 12.5a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm-7 8c.7-3.6 3.6-6 7-6s6.3 2.4 7 6",
  search: "m20 20-3.5-3.5M18 11a7 7 0 1 1-14 0 7 7 0 0 1 14 0Z",
  pin: "M12 21s7-6.1 7-11.5a7 7 0 1 0-14 0C5 14.9 12 21 12 21Zm0-9a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5Z",
  card: "M2.5 6.5h19v11h-19v-11Zm0 4.2h19M6 15h4",
  plus: "M12 5v14M5 12h14",
  minus: "M5 12h14",
  check: "m5 13 4 4L19 7",
  chevronDown: "m6 9 6 6 6-6",
  chevronRight: "m9 6 6 6-6 6",
  arrowLeft: "M19 12H5m0 0 6-6m-6 6 6 6",
  trash: "M4 7h16M9 7V4h6v3m-8 0 1 13h8l1-13",
  box: "M3 8.5 12 4l9 4.5v7L12 20l-9-4.5v-7Zm0 0 9 4.5m0 0 9-4.5M12 12.5V20",
  shield: "M12 3c3 1.2 5 1.8 8 1.8 0 8.6-3.6 12.7-8 15.2-4.4-2.5-8-6.6-8-15.2 3 0 5-.6 8-1.8Z",
  truck: "M3 6h11v9H3V6Zm11 3h4l3 3v3h-7V9ZM6.5 18a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3Zm11 0a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3Z",
  lock: "M6 10V7a6 6 0 1 1 12 0v3m-13 0h14v11H5V10Zm7 5v3",
  logout: "M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4m6 14 5-5-5-5m5 5H9",
  flask: "M9 3h6M10 3v6.2L4.8 18a2 2 0 0 0 1.7 3h11a2 2 0 0 0 1.7-3L14 9.2V3M7.5 14h9",
  edit: "M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z",
  close: "M6 6l12 12M18 6 6 18",
  alert: "M12 9v4m0 4h.01M10.3 3.9 2.5 18a1.7 1.7 0 0 0 1.5 2.5h16a1.7 1.7 0 0 0 1.5-2.5L13.7 3.9a1.7 1.7 0 0 0-3.4 0Z",
};

export default function Icon({ name, size = 18, strokeWidth = 1.75, className = "" }) {
  const d = PATHS[name];
  if (!d) return null;
  return (
    <svg
      className={`icon ${className}`}
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={strokeWidth}
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d={d} />
    </svg>
  );
}
