const sharp = require('sharp'),
    path = require('path');
const ip_addr = '192.168.22.156';
const port = 5984;
const couchdb_username = 'whisk_admin';
const couchdb_password = 'some_passw0rd';
const couchdb_dbname = 'images';
const couchdb_url = `http://${couchdb_username}:${couchdb_password}@${ip_addr}:${port}`;

function getCouchdb() {
    return require('nano')({
        url: couchdb_url
    });
}


exports.main = async function(event) {
    width = 100;
    height = 100;
    console.log(JSON.stringify(event));

    var couchdb = getCouchdb();
    var imageName = event.imageName;

    var hrTime = process.hrtime()
    var microTime = hrTime[0] * 1000 + hrTime[1] / 1000000
    var thumbnialName = `t-${microTime}-${imageName}`;
    console.log(thumbnialName);
    couchdb = couchdb.use(couchdb_dbname);

    const sharp_resizer = sharp().resize(width, height);

    const promise = couchdb.attachment.insert(thumbnialName,
        thumbnialName,
        couchdb.attachment.getAsStream(imageName, imageName)
            .on('error', e => console.error)
            .pipe(sharp_resizer),
        'image/png',
        {}
    );
    await promise;
    return {output: "ok"}
};

exports.main({imageName: "test.jpg"})
