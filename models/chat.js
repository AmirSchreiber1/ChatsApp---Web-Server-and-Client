// We're going to use the 'mongoose' library to interact with our MongoDB database:
const mongoose = require('mongoose');

// We're going to use the Schema class from mongoose:
const Schema = mongoose.Schema;

// We're going to create a new schema for our Chats collection:
const Chat = new Schema({
    users: [{//array of the 2 users who are having the chat
        username: { type: String, required: true},
        displayName: { type: String, required: true},
        profilePic: { type: String, required: true}
    }],

    messages: [{
        created: {
            type: String,
            required: true
        },
        sender: {
            type: {
                username: String,
                displayName: String,
                profilePic: String
            },
            required: true
        },
        content: {
            type: String,
            required: true
        }
    }]
});

// We're going to export our schema so that we can use it in other files:
// We're going to use the 'mongoose.model()' method to create a new model for our Chats collection, named 'Chats':
module.exports = mongoose.model('Chats', Chat);