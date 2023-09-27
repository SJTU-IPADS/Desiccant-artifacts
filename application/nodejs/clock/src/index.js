const v8 = require('v8');


function clock(start) {
    if (!start) return process.hrtime();
    const end = process.hrtime(start);
    return Math.round((end[0] * 1000) + (end[1] / 1000000));
};

function main(params) {
    var ret = clock(params.start);
    return {payload: `ret is ${ret}` };
}

exports.main = main