const Chat = require('../models/chat');
const User = require('../models/user');

const userService = require('./user')


const createMessage = async (username, content, chatId) => { 
    var response;

    const specificChat = await Chat.findById(chatId)
    if (specificChat == null) {
        response = {
            success: false,
            message: `Chat number  ${chatId} could not be found`,
            status: 409
        }        
    }

    /*
    // Making sure that the user with this username is part of the chat with this chatId
    else if (specificChat.users[0].username != username && specificChat.users[1].username != username ) {
        response = {
            success: false,
            message: `Chat unaccesable for ${username}`,
            status: 409
        }
    }
    */

    else {
        const sender = await userService.getUserByUsername(username)
        const message = {"created" : Date(), "sender" : sender, "content" : content}

        await specificChat.updateOne({ $push: { messages: { $each: [message], $position: 0 } } })



        response = {
            success: true,
            data: message, //
            status: 200
        }

        // Original:
        /*
        response = {
            success: true,
            message: message, //
            status: 200
        }
        */

        // OR:
        /*
        response = {
            success: true,
            data: message, //
            status: 200
        }
        */
    }

    return response;
}

const getMessagesByChatId = async (chatId) => { 
    var response;
   const specificChat = await Chat.findById(chatId)
    if (specificChat == null) {
        response = {
            success: false,
            message: `Chat number ${chatId} could not be found`,
            status: 409
        }        
    }
    else {
        // Original - lines below
        // For getting all the data from the messages collection, not only message content, use this:
        /*response = {
            success: true,
            message: `Returning messages for chat number ${chatId}`,
            status: 200,
            data: specificChat.messages
        }*/

        // New:
        // For getting only the message content, use this:
        
        // let messages = [];

        // for (let i = 0; i < specificChat.messages.length; i++) {
        //     messages.push(specificChat.messages[i].content);
        // }

        response = {
            success: true,
            message: `Returning messages for chat number ${chatId}`,
            status: 200,
            data: specificChat.messages
        }
        

        
    }
    return response

}


module.exports = { createMessage, getMessagesByChatId };
