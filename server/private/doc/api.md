# API Specification

## Insecure (Public)

### POST login
Allows a user to “login” to get elevated permissions to use secure endpoints. The user must provide a public key and private key for the server to authenticate their attempt.

#### Inputs
public_key {string}

Key for identifying the user account, usually username or email.

private_key {string}

Secret key for secure user validation, usually password.

#### Responses
200 OK {json}
```
{
  user_id {string}: Internal user id for given credentials
  session_id {string}: Newly generated session id
}
```

400 Bad Request {string}

Message indicating the request is missing either public/private key(s).

401 Unauthorized {string}

Message indicating credentials are incorrect.

500 Internal Server Error {string}

Message indicating server error.

### GET geofence-data
Gets an array of geofence points. A geofence point contains data describing its central location, and its radius. This allows our client app to register when geofence points have been entered/exited and provide feedback to the server.

#### Responses
200 OK {json}
```
[
  {
    id {string|number}: Internal geofence id
    latitude {number}: Geofence area latitude
    longitude {number}: Geofence area longitude
    radius {number}: Geofence area radius
  },
  {
    ...
  },
  ...
]
```

500 Internal Server Error {string}

Message indicating server error.

## Secure

### POST update-location
Updates the internal location of a user in the server. Client provides their current location and the time it was pinged on the client device.

#### Headers
user_id {string}

The user id for the active user.

authorization {string}

The session-id or o-auth token for the active user.

#### Inputs
location {json}
```
{
  time {string}: Time coordinates were received
  latitude {number}: User latitude
  longitude {number}: User longitude
}
```

#### Responses
200 OK {string}

Message indicating server successfully received location.

401 Unauthorized {string}

Message indicating credentials are incorrect.

400 Bad Request {string}

Message indicating that credentials are valid, but the location data is invalid.

500 Internal Server Error {string}

Message indicating server error.

### POST geofence-enter
Indicates that the specified user has entered a geofence point of a given id. Updates the internal state of a geofence point to include the user.

#### Headers
user_id {string}

The user id for the active user.

authorization {string}

The session-id or o-auth token for the active user.

#### Inputs
geofence_id {string}

Id of the geofence that is entered.

#### Responses
200 OK {string}

Message indicating server successfully registered user in geofence.

401 Unauthorized {string}

Message indicating credentials are incorrect.

400 Bad Request {string}

Message indicating that credentials are valid, but the geofence id is invalid.

500 Internal Server Error {string}

Message indicating server error.

### POST geofence-exit
Indicates that the specified user has exited a geofence point of a given id. Updates the internal state of a geofence point to remove the user.

#### Headers
user_id {string}

The user id for the active user.

authorization {string}

The session-id or o-auth token for the active user.

#### Inputs
geofence_id {string}

Id of the geofence that is exited.

#### Responses
200 OK {string}

Message indicating server successfully unregistered user in geofence.

401 Unauthorized {string}

Message indicating credentials are incorrect.

400 Bad Request {string}

Message indicating that credentials are valid, but the geofence id is invalid.

500 Internal Server Error {string}

Message indicating server error.

### POST verify-biometrics
Indicates that the user has successfully performed a biometrics check on their device via the companion app.

#### Headers
user_id {string}

The user id for the active user.

authorization {string}

The session-id or o-auth token for the active user.

Responses
200 OK {string}

Message indicating server successfully unregistered user in geofence.

401 Unauthorized {string}

Message indicating credentials are incorrect.

500 Internal Server Error {string}

Message indicating server error.
