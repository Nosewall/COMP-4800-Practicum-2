import { read } from './library/file.js';
import { marked } from "marked";
import express from "express";

/**
 * Registers route mappings to a given app.
 */
export default function register(app, server) {
  app.use(express.static("./public"));

  const get = (path, route) => map(app, "get", path, route, server);
  const post = (path, route) => map(app, "post", path, route, server);

  get("/", root);
  get("/api", api);
  get("/geofence-data", geofenceData);
  post("/login", login);
  post("/extend-session", extendSession);
  post("/update-location", updateLocation);
  post("/geofence-enter", geofenceEnter);
  post("/geofence-exit", geofenceExit);
}

/**
 * Maps a route implicitly including args.
 */
function map(app, method, path, route, ...args) {
  app[method](path, (req, res) => {
    route(req, res, ...args)
  });
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

function login(req, res, server) {
  console.log(login);
  res.end();
}

/**
 * Gets geofence point data.
 */
function geofenceData(req, res, server) {
  res.send(server.geofencePoints);
}

function extendSession(req, res, server) {
  console.log(extendSession);
  res.end();
}

function updateLocation(req, res, server) {
  console.log(updateLocation);
  res.end();
}

function geofenceEnter(req, res, server) {
  console.log(geofenceEnter);
  res.end();
}

function geofenceExit(req, res, server) {
  console.log(geofenceExit);
  res.end();
}
