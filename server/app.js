const express = require("express");
const app = express();

const PORT = 8000;

app.use(express.static("./public"));

app.get("/", function(req, res) {
  res.sendFile(__dirname + "/index.html");
});

app.listen(PORT, () => {
  console.log(`Server started: ${PORT}`)
});
