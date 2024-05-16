
const userService = require('../services/user');

const createUser = async (req, res) => {
    const {username, password, displayName, profilePic} = req.body;
    const serviceResponse = await userService.createUser(username, password, displayName, profilePic);

    if(serviceResponse.success){
        return res.status(serviceResponse.status).json(serviceResponse.data);
    }
    else{
        return res.status(serviceResponse.status).json({message: serviceResponse.message});
    }
};


const getUserByUsername = async (req, res) => {
    const user = await userService.getUserByUsername(req.params.username)

    if(user == null){
        return res.status(404).json({message: "User not found"});
    }
    return res.status(200).json(user);
};

module.exports = { createUser, getUserByUsername };