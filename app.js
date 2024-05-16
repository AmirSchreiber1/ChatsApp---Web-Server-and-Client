const express = require('express');
var app = express();

const customEnv = require('custom-env');
customEnv.env(process.env.NODE_ENV, './config');

// We're going to use the 'body-parser' library to parse the body of our HTTP requests (POST requests):
const bodyParser = require('body-parser');
// We're going to use the 'bodyParser.urlencoded()' method to parse the body of our HTTP requests (POST requests):
app.use(bodyParser.urlencoded({extended: true}));
// We're going to pass the data in the POST request as JSON:
app.use(express.json());
app.use(express.static('public'))


// We're going to use the 'cors' library to make our code work, since the browser will block our requests otherwise:
const cors = require('cors');
// We're going to use the 'cors()' method to enable CORS:
app.use(cors());



// We're going to use the 'mongoose' library to interact with our MongoDB database:
const mongoose = require('mongoose');
// We're going to connect to our MongoDB database, using the Connection String defined in the '.env' file:
mongoose.connect(process.env.CONNECTION_STRING, {
    useNewUrlParser: true,
    useUnifiedTopology: true
});

// We're going to use the 'express.static()' method to serve static files:
// app.use(express.static('public'));

//Routing
const router = require('./routes/user');
const chatsRouter = require('./routes/chats');
const token = require('./routes/token');
app.use('/api/Users', router);
app.use('/api/Chats', chatsRouter);
app.use('/api/Tokens', token);

//Socket.io
const http = require('http');
const server = http.createServer(app);
const {Server} = require("socket.io");
const io = new Server(server, {
    cors : {
        origin : "http://localhost:3000",
        methods : ["GET", "POST", "DELETE"]
    }
});

//whenever a client sends a messages, send emit to all other clients to fetch all messages 
io.on('connection', (socket) => {    
    socket.on('msgSent', () => {
        io.emit('msg', {});
    })
})
// Running the server, with the port defined in the '.env' file:
server.listen(process.env.PORT);

