require('./md5sum.js')()
.on('ok',function (data) {
	console.info('data is '+data)
})
.on('error',function (e) {
	console.info('error is '+e)
})
.md5('md5sum.js')