const Chat = require('../models/chat');
const User = require('../models/user');
const userService = require('../services/user')


const getChatsByUsername = async (username) => {
    var response;
    const chat = [];
    (await Chat.find({})).forEach( elem => {
        if (elem.users[0].username === username || elem.users[1].username === username) {
            chat.push(elem);
        }

    });
        response = {
        success: true,
        message: `${username} has ${chat.length} chats`,
        status: 200,
        data: chat
    }
    return response;
}

const createChat = async (username, contactUsername) => { 
    var response;
    if (username == contactUsername) {
         response = {
            success: false,
            message: `Thou shalt not talk with thy self`,
            status: 409
        }
        return response;
    }
    if (await userService.getUserByUsername(contactUsername) == null ) { //if the contact doesn't exist
        response = {
            success: false,
            message: `No such user`,
            status: 409
        }
        return response;
    }
    
    let isContactAlreadySpeakingWithUsername = false;
    const usersChats = await getChatsByUsername(username);

    //uncomment this if you want to disable same users multiple chats
    // if (usersChats.data.length > 0) //if the user already has contacts, check that he is not adding one of them again
    // {
    //     usersChats.data.forEach(element => {
    //         if (element.users[0].username == contactUsername || element.users[1].username == contactUsername) {
    //             isContactAlreadySpeakingWithUsername = true
    //         }
    //     });
    // }
    // if (isContactAlreadySpeakingWithUsername) {
    //     response = {
    //         success: false,
    //         message: `You are already speaking with ${contactUsername}.`,
    //         status: 409
    //     }
    //     return response;
    // }
    let user1 = await userService.getUserByUsername(username);
    let user2 = await userService.getUserByUsername(contactUsername);
    const chat = new Chat({
        "users" : [
            { username : user1.username,
                displayName : user1.displayName,
                profilePic : user1.profilePic
            }
            , { username : user2.username,
                displayName : user2.displayName,
                profilePic : user2.profilePic
            }
        ], "messages" : []
    })
    await chat.save()
    let chatTemp = await Chat.findById(chat._id)
    return { //if the chat was added succesfuly, return the chat id and contact info
        success: true,
        message: `Created a chat between ${username} and ${contactUsername}`,
        status: 200,
        data : {
            id : chatTemp.id, 
            user : {
                username : user2.username,
                displayName : user2.displayName,
                profilePic : user2.profilePic
            },
            lastMessage : null
        }
    }
    
}

const getChatById = async (username, id) => { //only search chats that are in getChatsByUsername(current_username)
    var response;
    const specificChat = await Chat.findById(id)
    if (specificChat == null) {
        response = {
            success: false,
            message: `Chat number ${id} could not be found`,
            status: 409
        }        
    }
    else if (specificChat.users[0].username != username && specificChat.users[1].username != username ) {
        response = {
            success: false,
            message: `Chat unaccesable for ${username}`,
            status: 409
        } 
    } else {
        response = {
            success: true,
            message: `Returning chat number ${id}`,
            status: 204,
            data: specificChat
        }
    }
    return response

}

const deleteChatById = async (username, id) => { //only search chats that are in getChatsByUsername(current_username)
    var response;

    const targetChat = await getChatById(username, id)
    if (!targetChat.success) { //if the chat doesnt exist or doesn't belong to this current user
        response = targetChat;
    } else {
        await Chat.findByIdAndDelete(id)
        response = {
            success: true,
            message: `Deleted succesfully`,
            status: 204,            
        }
    }
    return response
}


module.exports = { getChatsByUsername, createChat, getChatById, deleteChatById};
