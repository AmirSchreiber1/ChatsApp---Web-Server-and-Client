const userController = require('../controllers/user');
const verify = require('../jwt/verify');

const express = require('express');
var router = express.Router();

router.route('/')
    .post(userController.createUser);

router.route('/:username')
    .get(verify.isLoggedIn,userController.getUserByUsername)

module.exports = router;