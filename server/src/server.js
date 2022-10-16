import { readJson } from "./library/file.js"

/**
 * Map of currently active sessions.
 * Key is the session id.
 * Value contains `userId`, `keepAliveKey` and `startTime`.
 */
const activeSessions = {
  "{SESSION_ID}": {
    userId: "abcd-1234",
    keepAliveKey: "xyz-789",
    startTime: Date.now(),
  }
}

const getGeofencePoints = async () => readJson("./private/data/geofence_points.json");

/**
 * Entry point for all internal server logic.
 * Facilitating user sessions, geofence states, etc...
 */
export default async function start() {
  console.log("Server logic");
  const geofencePoints = await getGeofencePoints();
  console.log("Loaded geofence points:", geofencePoints);
  const geofenceState = getInitialGeofenceState(geofencePoints);
  console.log("Initialized geofence state:", geofenceState);
}

/**
 * Creates the initial geofence state map from geofence points.
 * Keys are geofence ids
 * Values contain any internal state of the geofence point
 *   |- Currently a list of users that are inside the geofence
 */
function getInitialGeofenceState (geofencePoints) {
  return Object.fromEntries(geofencePoints.map(point => {
    return [ point.id, {
      users: []
    }];
  }));
}
