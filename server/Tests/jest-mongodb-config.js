
module.exports = {
  mongodbMemoryServerOptions: {
    instance: {
      dbName: 'backenddb'
	  port:27017
    },
    binary: {
      skipMD5: true
    },
    autoStart: false
  }
};