const tokenController = require('../controllers/token');

const express = require('express');
var router = express.Router();

router.route('/')
    .post(tokenController.login);

module.exports = router;