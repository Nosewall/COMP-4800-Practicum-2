const express = require("express");

/**
 * STATIC
 */

const app = express();

app.use(express.static("./public"));

app.get("/", function(req, res) {
  res.sendFile(__dirname + "/index.html");
});

app.listen(8000, error => {
  console.warn(error);
});
