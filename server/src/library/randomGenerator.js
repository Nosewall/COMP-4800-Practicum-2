const lowerAlphas = 'abcdefghijklmnopqrstuvwxyz';
const upperAlphas = lowerAlphas.toUpperCase();
const numerics = '0123456789';
const chars = lowerAlphas + upperAlphas + numerics;

function generateRandomString(stringLength) {
  let result = '';
  for(let i = 0; i < stringLength; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

export function generateUniqueSessionId(activeSessions) {
  let sessionIds = Object.keys(activeSessions);
  let sessionId = null;
  while (!sessionId) {
    let tempSessionId = generateRandomString(8);
    sessionId = sessionIds.includes(tempSessionId) ? null : tempSessionId;
  }
  return sessionId;
}
  
export function generateUniqueKeepAliveKey(activeSessions) {
  let keepAliveKeys = [];
  for (let activeSession in Object.keys(activeSessions)) {
    keepAliveKeys.push(activeSession.keepAliveKey);
  }
  let keepAliveKey = null;
  while (!keepAliveKey) {
    let tempkeepAliveKey = generateRandomString(6);
    keepAliveKey = keepAliveKeys.includes(tempkeepAliveKey) ? null : tempkeepAliveKey;
  }
  return keepAliveKey;
}