import express from "express";
import registerRoutes from "./src/routes.js";
import start from "./src/server.js";

const app = express();

const PORT = 8000;

registerRoutes(app);

app.listen(PORT, () => {
  console.log(`Server started: ${PORT}`);
  start();
});
