import { read, readJson } from './library/file.js';
import { marked } from "marked";
import express from "express";
import { activeSessions, setActiveSessions, createNewSession } from './server.js';

const getUsers = async () => readJson("./private/data/users.json");
const emailPattern = /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/

/**
 * Registers route mappings to a given app.
 */
export default function register(app, server) {
  app.use(express.static("./public"));
  app.use(express.text());
  app.use(express.json());

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

async function login(req, res, server) {
  console.log(login);
  const users = await getUsers();
  if (!req.body.public_key || !req.body.private_key) {
    res.status(400);
    res.json({msg: 'Missing public or private key.'});
  }
  
  let foundUser = null;
  let authType = emailPattern.test(req.body.public_key) ? 'email' : 'username';
  for (let user in users) {
    if (user[authType] == req.body.public_key)
      foundUser = user;
  }

  if (!foundUser) {
    res.status(400);
    res.json({msg: 'No such user found.'})
  } else {
    if (foundUser.password != req.body.private_key) {
      res.status(401);
      res.json({msg: 'Incorrect credentials.'});
    }
    let newSession = createNewSession(foundUser.id);
    res.json({
      user_id: foundUser.userId,
      session_id: newSession.sessionId,
      keep_alive_key: newSession.keepAliveKey
    });
  }
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

  const { time, latitude, longitude } = req.body;

  res.send(`Location received: ${latitude}, ${longitude}`);
}

function geofenceEnter(req, res, server) {
  console.log(geofenceEnter);

  const { geofence_id } = req.body;

  res.send(`Geofence enter: ${geofence_id}`)
}

function geofenceExit(req, res, server) {
  console.log(geofenceExit);

  const { geofence_id } = req.body;

  res.send(`Geofence exit: ${geofence_id}`);
}
