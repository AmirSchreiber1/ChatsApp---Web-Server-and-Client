const chatController = require('../controllers/chat');
const messageController = require('../controllers/message');
const verify = require('../jwt/verify');

const express = require('express');
var chatsRouter = express.Router();

chatsRouter.route('/')
    .get(verify.isLoggedIn,chatController.getChat);

chatsRouter.route('/')
    .post(verify.isLoggedIn,chatController.addChat);

chatsRouter.route('/:id')
    .get(verify.isLoggedIn,chatController.getChatById)

chatsRouter.route('/:id')
    .delete(verify.isLoggedIn,chatController.deleteChatById)

chatsRouter.route('/:id/Messages')
    .post(verify.isLoggedIn,messageController.addMessageToChat)

chatsRouter.route('/:id/Messages')
    .get(verify.isLoggedIn,messageController.getMessagesFromChat)


module.exports = chatsRouter;