const axios = require('axios')
const express = require('express')
let max = 0;
function testcall(start) {
    console.log("start")
    axios.get('http://localhost:3001/recipe/list?email=tt&max=100').then(data => {
        if (data) {
            var end = new Date() - start;
            if (max < end) {
                max = end;
            }
            console.log(end, max);
        }
    }).catch(err => {
        console.log("error")
        testcall(start)
    })
}

for (let i = 0; i < 10000; i++) {
    randNum = Math.floor(Math.random() * Math.floor(1000))
    var start = new Date()
    setTimeout(testcall, randNum, start);
}
setTimeout(function () {
    console.log("####################################################TEST 2#######################################################################")
    for (let i = 0; i < 10000; i++) {
        randNum = Math.floor(Math.random() * Math.floor(1000))
        setTimeout(testcall, randNum, start);
    }
}, 6000)
