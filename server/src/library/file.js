import { promises as fs } from "fs";

export async function read(path, encoding = "utf-8") {
  return fs.readFile(path, encoding)
}

export async function readJson(path) {
  const file = await read(path)
  return JSON.parse(file);
}

export async function readBytes(path) {
  return read(path, null)
}