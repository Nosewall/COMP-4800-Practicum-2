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
  app.get("/geofence-data", geofenceData);
  app.post("/login", login);
  app.post("/extend-session", extendSession);
  app.post("/update-location", updateLocation);
  app.post("/geofence-enter", geofenceEnter);
  app.post("/geofence-exit", geofenceExit);
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

function login(req, res) {
  console.log(login);
  res.end();
}

function geofenceData(req, res) {
  console.log(geofenceData);
  res.end();
}

function extendSession(req, res) {
  console.log(extendSession);
  res.end();
}

function updateLocation(req, res) {
  console.log(updateLocation);
  res.end();
}

function geofenceEnter(req, res) {
  console.log(geofenceEnter);
  res.end();
}

function geofenceExit(req, res) {
  console.log(geofenceExit);
  res.end();
}
