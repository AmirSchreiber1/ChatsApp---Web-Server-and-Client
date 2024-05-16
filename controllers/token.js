
const tokenService = require('../services/token');

const login = async (req, res) => {
    const {username, password} = req.body;
    const serviceResponse = await tokenService.login(username, password);
    if(serviceResponse.success){
        return res.status(serviceResponse.status).send(serviceResponse.data);
    }
    else{
        return res.status(serviceResponse.status).json({message: serviceResponse.message});
    }
};

module.exports = { login };