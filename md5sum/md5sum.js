var EventProxy=require('eventproxy')
var filepath=require('filepath')

function create() {
	
	var obj=new Object()
	obj.ep=EventProxy.create();
	obj.on=function (e,cb) {
		console.info(`event is ${e}`)
		this.ep.on(e,cb)
		return this
	}
	
	obj.md5=function (p) {
		console.info(`file path is ${p}`)
		var fp=filepath.create(p)
		if(!fp.exists()){
			console.info(`path ${p} not exist`)
			obj.ep.emit('error',new Error(`path ${p} not exist`))
		    return this
		}

		const exec = require('child_process').exec;
		const child = exec(`"md5sum.exe" "${p}"`,
				{timeout:5000},
				(e, stdout, stderr) => {
				console.info(`error is ${e}`);
				console.info(`stderr is ${stderr}`);
				console.info(`stdout is ${stdout}`);
				if(e){
					obj.ep.emit('error',e)
				}else{
					obj.ep.emit('ok',stdout)
				}
			}
		);
		return this
	}
	
	return obj

}

module.exports=create