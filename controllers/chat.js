const chatService = require('../services/chat');
const jwt = require("jsonwebtoken")

const getChat = async (req, res) => {
    const data = req.data
    const retStatus = await chatService.getChatsByUsername(data.username); //get all chats
    let contacts = []; //local variable for converting the chats according to the API
    retStatus.data.forEach(element => { //go over the list of chats and create contacts
        let contactUser;
        if(element.users[0].username == data.username) { //getting the contacts information
            contactUser = element.users[1];
        } else {
            contactUser = element.users[0];
        }
        let lastMessage = null;
        if (element.messages.length > 0) {
            lastMessage = element.messages[0];
        }
        let singleChat = {
            id : element._id ,
            user : {
                username : contactUser.username,
                displayName : contactUser.displayName,
                profilePic : contactUser.profilePic
            },
            lastMessage : lastMessage
        }
        contacts.push(singleChat)
    });
    return res.status(retStatus.status).json(contacts);

}

const addChat = async (req, res) => {
    const data = req.data
    const retStatus = await chatService.createChat(data.username, req.body.username)
    if (retStatus.success) {
        res.status(retStatus.status).json({data : retStatus.data});
    } else {
        res.status(retStatus.status).send(retStatus.message);
    }
}

const getChatById = async (req, res) => {
    const data = req.data
    const retStatus = await chatService.getChatById(data.username, req.body.id)
    if (retStatus.success) {
        return res.status(retStatus.status).json(retStatus.data);
    } else {
        return res.status(retStatus.status).json({message : retStatus.message})
    }
}

const deleteChatById = async (req, res) => {
    const data = req.data
    const retStatus = await chatService.deleteChatById(data.username, req.params.id)
    return res.status(retStatus.status).json({message : retStatus.message})
    
} 


module.exports = {getChat, addChat, getChatById, deleteChatById}