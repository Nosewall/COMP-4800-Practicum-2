import express from "express";
import registerRoutes from "./src/routes.js";
import startServer from "./src/server.js";

const PORT = 8000;

const app = express();
const server = await startServer();

registerRoutes(app, server);

app.listen(PORT, () => {
  console.log(`Listener started: ${PORT}`);
});
