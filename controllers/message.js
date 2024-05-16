const messageService = require('../services/message');

const getMessagesFromChat = async (req, res) => {
    const data = req.data
    const message = await messageService.getMessagesByChatId(req.params.id);
    if (message.success) {
        return res.status(message.status).json(message.data);
    } else {
        return res.status(message.status).json({message : message.message})
    }
}

const addMessageToChat = async (req, res) => {
    const data = req.data
    const message = await messageService.createMessage(data.username, req.body.msg, req.params.id);
//sender, content, chatId
    if (message.success) {
        return res.status(message.status).json(message.data);
    } else {
        return res.status(message.status).json({message : message.message})
    }
}

module.exports = {getMessagesFromChat, addMessageToChat}