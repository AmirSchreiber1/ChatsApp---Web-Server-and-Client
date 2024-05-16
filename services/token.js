const User = require('../models/user');
const jwt = require("jsonwebtoken")
const key = "Some super secret key shhhhhhhhhhhhhhhhh!!!!!"

const login = async (username, password) => {

    const userExist = await User.findOne({ username: username, password: password });
    if (userExist == null) {
        const response = {
            success: false,
            message: `Invalid username and/or password`,
            status: 404
        }
        return response;
    }

    const data = { username: username }
    const token = jwt.sign(data, key)

    const response = {
        success: true,
        status: 200,
        data: token
    }
    return response;
}

module.exports = { login };