'use strict';

const BigNumber = require('bignumber.js');

BigNumber.config({
    // higher number increases the complexity
    DECIMAL_PLACES: 1000,
});

const calcPi = function (n) {
    const four = new BigNumber(4);

    let pi = new BigNumber(0);
    for (let i = 0; i < n; i++) {
        const temp = four.dividedBy(i * 2 + 1);
        if (i % 2 == 0) {
            pi = pi.plus(temp);
        }
        else {
            pi = pi.minus(temp);
        }
    }
    return pi.toString(10);
};

function main(params) {
    console.log(params);
    const param = params.n || 0;
    const piNum = calcPi(param);
    return {payload: `Approximated Pi with ${param} iterations is ${piNum}`};

}

exports.main = main