// Minimal static file server with SPA (client-side routing) fallback.
// Usage: node static-server.mjs <dir> <port>
import http from "http";
import fs from "fs";
import path from "path";

const dir = process.argv[2] || "build";
const port = Number(process.argv[3] || 3000);

const TYPES = {
  ".html": "text/html",
  ".js": "text/javascript",
  ".css": "text/css",
  ".json": "application/json",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".svg": "image/svg+xml",
  ".ico": "image/x-icon",
  ".map": "application/json",
  ".woff": "font/woff",
  ".woff2": "font/woff2",
  ".txt": "text/plain",
};

http
  .createServer((req, res) => {
    const urlPath = decodeURIComponent((req.url || "/").split("?")[0]);
    let file = path.join(dir, urlPath);
    try {
      if (!fs.existsSync(file) || fs.statSync(file).isDirectory()) {
        // SPA fallback — let React Router handle the route.
        file = path.join(dir, "index.html");
      }
      const data = fs.readFileSync(file);
      res.writeHead(200, {
        "Content-Type": TYPES[path.extname(file)] || "application/octet-stream",
      });
      res.end(data);
    } catch (e) {
      res.writeHead(404);
      res.end("Not found");
    }
  })
  .listen(port, () => console.log(`static server on http://localhost:${port} (dir: ${dir})`));
