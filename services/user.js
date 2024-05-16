const User = require('../models/user');

const createUser = async (username, password, displayName, profilePic) => {

    const userExist = await User.findOne({ username: username });
    if (userExist != null) {
        const response = {
            success: false,
            message: `Username ${username} already exists`,
            status: 409
        }
        return response;
    }
    const user = new User({ username: username, password: password, displayName: displayName, profilePic: profilePic });

    const userRes = await user.save();
    const response = {
        success: true,
        message: `User ${username} created successfully`,
        status: 200,
        data: {
            "username": userRes.username,
            "displayName": userRes.displayName,
            "profilePic": userRes.profilePic
        }
    }
    return response;
}


const getUserByUsername = async (username) => {
    return await User.findOne({ username: username });
};

module.exports = { createUser, getUserByUsername };