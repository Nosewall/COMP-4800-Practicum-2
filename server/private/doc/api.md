# API Specification

## Insecure (Public)

### POST login
Allows a user to “login” to get elevated permissions to use secure endpoints. The user must provide a public key and private key for the server to authenticate their attempt.

#### Inputs
public_key {string}

Key for identifying the user account, usually username or email

private_key {string}

Secret key for secure user validation, usually password

#### Responses
200 OK {json}
```
{
  user_id {string}: Internal user id for given credentials
  session_id {string}: Newly generated session id
  keep_alive_key {string}: Newly generated keep alive key for session
}
```

401 Unauthorized {string}

Message indicating credentials are incorrect

500 Internal Server Error {string}

Message indicating server error

### GET geofence_data
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

Message indicating server error

## Secure

### POST extend_session
Allows the client to “extend” a session within a restricted time period after the session is dead. Extending a session actually creates a new session based on the previous session id and keep-alive key. The server should only allow this within a specific time frame after a session dies. The client app can then use biometric login to refresh the session instead of a full login.

#### Inputs
session_id {string}

Key for identifying the user account, usually username or email

keep_alive_key {string}

Secret key for secure user validation, usually password

#### Responses
200 OK {json}
```
{
  session_id {string}: Newly generated session id
  keep_alive_key {string}: Newly generated keep alive id for session
}
```

401 Unauthorized {string}

Message indicating credentials are incorrect

403 Forbidden {string}
  
Message indicating that session cannot be extended

403 Forbidden {string}
Message indicating that session cannot be extended

500 Internal Server Error {string}

Message indicating server error

### POST update_location
Updates the internal location of a user in the server. Client provides their current location and the time it was pinged on the client device.

#### Inputs
session_id {string}

Id of active session for user

user_id {string}

Id of the user that is sending location update

location {json}
```
{
  time {string}: Time coordinates were received
  latitude {number}: User latitude
  longitude {number}: User longitude
}
```

#### Responses
200 OK {json}

Message indicating server successfully received location

401 Unauthorized {string}

Message indicating credentials are incorrect

400 Bad Request {string}

Message indicating that credentials are valid, but the location data is invalid

500 Internal Server Error {string}

Message indicating server error

### POST geofence_enter
Indicates that the specified user has entered a geofence point of a given id. Updates the internal state of a geofence point to include the user.

#### Inputs
session_id {string}

Id of active session for user

user_id {string}

Id of the user that enters geofence

geofence_id {string}

Id of the geofence that is entered

#### Responses
200 OK {json}

Message indicating server successfully registered user in geofence

401 Unauthorized {string}

Message indicating credentials are incorrect

400 Bad Request {string}

Message indicating that credentials are valid, but the geofence id is invalid

500 Internal Server Error {string}

Message indicating server error

### POST geofence_exit
Indicates that the specified user has exited a geofence point of a given id. Updates the internal state of a geofence point to remove the user.

#### Inputs
session_id {string}

Id of active session for user

user_id {string}

Id of the user that enters geofence

geofence_id {string}

Id of the geofence that is entered

#### Responses
200 OK {json}

Message indicating server successfully unregistered user in geofence

401 Unauthorized {string}

Message indicating credentials are incorrect

400 Bad Request {string}
  
Message indicating that credentials are valid, but the geofence id is invalid

500 Internal Server Error {string}

Message indicating server error
