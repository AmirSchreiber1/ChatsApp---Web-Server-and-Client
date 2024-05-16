// We're going to use the 'mongoose' library to interact with our MongoDB database:
const mongoose = require('mongoose');

// We're going to use the Schema class from mongoose:
// Our schema will define the structure of documents in our articles collection:
const Schema = mongoose.Schema;

// We're going to create a new schema for our Users collection:
const User = new Schema({
    username: {
        type: String,
        required: true
    },
    password: {
        type: String,
        required: true
    },
    displayName: {
        type: String,
        required: true
    },
    profilePic: {
        type: String,
        required: true
    }
});
//}, { versionKey: false });

// We're going to export our schema so that we can use it in other files:
// We're going to use the 'mongoose.model()' method to create a new model for our Users collection, named 'Users':
module.exports = mongoose.model('Users', User);