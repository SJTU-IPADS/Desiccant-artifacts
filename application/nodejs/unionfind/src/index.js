'use strict';

const UnionFind = require('union-find');

function clock(start) {
    if (!start) return process.hrtime();
    const end = process.hrtime(start);
    return Math.round((end[0] * 1000) + (end[1] / 1000000));
}

const unionfindFunc = function (n) {
    const VERTEX_COUNT = n;

    //Create some edges
    const t0 = clock();
    let edges = [];
    for (let i = 0; i < VERTEX_COUNT - 1; ++i) {
        //const vertex_a = parseInt(Math.random() * VERTEX_COUNT);
        //const vertex_b = parseInt(Math.random() * VERTEX_COUNT);

        // create edges between 10 continuous nodes
        if (i % 10 === 0) {
            continue
        }
        const vertex_a = i;
        const vertex_b = i + 1;
        edges.push([vertex_a, vertex_b]);
    }
    console.log("unionfind.init time: " + clock(t0));

    //Link all the nodes together
    const t1 = clock();
    let forest = new UnionFind(VERTEX_COUNT);
    for (let i = 0; i < edges.length; ++i) {
        forest.link(edges[i][0], edges[i][1])
    }
    console.log("unionfind.link time: " + clock(t1));

    //Label components
    const t2 = clock();
    let labels = new Array(VERTEX_COUNT);
    for (let i = 0; i < VERTEX_COUNT; ++i) {
        labels[i] = forest.find(i)
    }
    console.log("unionfind.label time: " + clock(t2));

    return labels;
};

function main(params) {
    console.log(params);
    const param = params.n || 0;
    const ret = unionfindFunc(param);
    return {payload: `Finished union find component analysis with ${param}`};
}

exports.main = main