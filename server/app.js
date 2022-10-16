const express = require("express");
const marked = require("marked");
const fs = require("fs").promises;
const app = express();

const PORT = 8000;

app.use(express.static("./public"));

app.get("/", (req, res) => {
  res.sendFile(`${__dirname}/index.html`);
});

app.get("/api", async (req, res) => {
  const markdown = await fs.readFile(`${__dirname}/private/api.md`, "utf-8");
  const html = await marked.parse(markdown);
  res.send(html);
});

app.listen(PORT, () => {
  console.log(`Server started: ${PORT}`)
});
