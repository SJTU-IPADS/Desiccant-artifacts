'use strict';



function calcFib (n) {
    let [a, b] = [1, 0];
    while (n-- > 0) {
        [a, b] = [b + a, a];
    }
    return b;
};

function main(params) {
    console.log(params);
    const param = params.n || 0;
    const fibNum = calcFib(param);
    return {payload: `The ${param}th fibonacci number is ${fibNum}`};
}


exports.main = main
