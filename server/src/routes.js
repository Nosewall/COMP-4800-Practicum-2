import { read } from './library/file.js';
import { marked } from "marked";
import express from "express";

/**
 * Registers route mappings to a given app.
 */
export default function register(app) {
  app.use(express.static("./public"));
  app.get("/", root);
  app.get("/api", api);
}

function root(req, res) {
  res.sendFile(`./public/index.html`);
}

/**
 * Gets the api markdown file as html.
 */
async function api(req, res) {
  const markdown = await read(`./private/doc/api.md`);
  const html = await marked.parse(markdown);
  res.send(html);
}
