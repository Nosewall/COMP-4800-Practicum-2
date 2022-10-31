const lowerAlphas = 'abcdefghijklmnopqrstuvwxyz';
const upperAlphas = lowerAlphas.toUpperCase();
const numerics = '0123456789';
const chars = lowerAlphas + upperAlphas + numerics;

export default function generateRandomString(stringLength) {
    let result = '';
    for(let i = 0; i < stringLength; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  }