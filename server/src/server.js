import { readJson } from "./library/file.js"
import { generateUniqueSessionId,
  generateUniqueKeepAliveKey } from "./library/randomGenerator.js";

/**
 * Map of currently active sessions.
 * Key is the session id.
 * Value contains `userId`, `keepAliveKey` and `startTime`.
 */
export var activeSessions = [
  {
    sessionId: "{SESSION_ID}",
    userId: "abcd-1234",
    keepAliveKey: "xyz-789",
    startTime: Date.now(),
  }
]

export function setActiveSessions(newSessions) { activeSessions = newSessions; }

export function createNewSession(userId) {
  if (!userId) throw Error('Must provide userId for new session.')
  setActiveSessions(activeSessions.filter(session => session.userId != userId))
  let sessionId = generateUniqueSessionId(activeSessions);
  let newSession = {
    sessionId: sessionId,
    userId: userId,
    keepAliveKey: generateUniqueKeepAliveKey(activeSessions),
    startTime: Date.now()
  }
  activeSessions.push(newSession);
  return newSession;
}

const getGeofencePoints = async () => readJson("./private/data/geofencePoints.json");

/**
 * Entry point for all internal server logic.
 * Facilitating user sessions, geofence states, etc...
 *
 * Returns a server interface allowing external interactivity with server.
 */
export default async function start() {
  console.log("Server logic");

  const geofencePoints = await getGeofencePoints();
  console.log("Loaded geofence points:", geofencePoints);
  const geofenceState = getInitialGeofenceState(geofencePoints);
  console.log("Initialized geofence state:", geofenceState);

  return {
    activeSessions,
    geofencePoints,
  }
}

/**
 * Creates the initial geofence state map from geofence points.
 * Keys are geofence ids
 * Values contain any internal state of the geofence point
 *   |- Currently a list of users that are inside the geofence
 */
function getInitialGeofenceState(geofencePoints) {
  return Object.fromEntries(geofencePoints.map(point => {
    return [ point.id, {
      users: []
    }];
  }));
}