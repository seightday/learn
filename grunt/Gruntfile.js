module.exports = function (grunt) {
//grunt --city=sz --profile=test --mock=false
	var cities={sz:'sz'},
		profiles={test:'test',product:'product'},
		mocks={'true':'true','false':'false'},
		city=grunt.option('city'),
		profile=grunt.option('profile'),
		mock=grunt.option('mock');
	grunt.log.writeln('city:'+city+',profile:'+profile+',mock:'+mock);
	if(!cities[city]||!profiles[profile]||!mocks[mock]){
		//grunt.fail.warn('error params');//无法输出中文
		//使用watch，注释warn
	}
	if(mock){
		mock='test';
	}else{
		mock='product';
	}
	
  // 项目配置
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
	clean: {
	  build: {
		src: ["build"]
	  }
	},
    concat: {
	  options: {
        separator: '\n;'
      },
      third: {
        src: ['bower_components/mootools/dist/mootools-core.min.js','bower_components/jquery/dist/jquery.min.js'],
        dest: 'build/lib-min.js'
      },
	  app:{
		src:['src/app/**','src/config/'+profile+'.js'],
		dest:'build/app-temp.js'
	  },
	  city:{
		src:['src/city/'+city+'/'+profile+'/'+city+'.js'],
		dest:'build/city-'+city+'-temp.js'
	  }
    },
	shell:{
		target: {
			command: 'call ant -buildfile mock_js.xml '+mock
		}
	},
    uglify: {
		options: {
		  mangle: false,
		  compress:false,
		  maxLineLen:80
		},
      app: {
        files: {
          'build/app-min.js': ['build/app-temp.js'],
		  'build/city-min.js': ['build/city-'+city+'-temp.js'],
        }
      }
    },
	copy: {
	  main: {
		src: 'build/city-min.js',
		dest: 'build/city-'+city+'-min.js',
	  },
	  //copy to local jboss
	  tojboss:{files:[{expand: true,cwd:'D:/workspaces/eclipse/workspace/src/main/webapp/common/',src:['**'],dest:'D:/jboss/server/default/deploy/web/common/'},{expand: true,cwd:'D:/workspaces/eclipse/workspace/src/main/webapp/js/',src:['**'],dest:'D:/jboss/server/default/deploy/web/js/'}]}
	},
	//upload to ftp
	'ftp-deploy': {
	  build: {
		auth: {
		  host: '192.168.0.1',
		  port: 22,
		  authKey:'key',
		  authPath:'key.ftppass'
		},
		src: 'build',
		dest: '/opt',
		exclusions: ['temp'],
		forceVerbose :true
	  }
	},
	'sftp-deploy': {
	  build: {
		auth: {
		  host: '192.168.0.1',
		  port: 22,
		  authKey:{
			"username":"root",
			"password":"123456"
		  }
		},
		cache: false,
		src: 'build',
		dest: '/opt',
		exclusions: ['*temp.js','city-min.js'],
		serverSep: '/',
		concurrency: 4,
		progress: true
	  }
	},
	watch:{
		build:{
			files:['D:/workspaces/eclipse/workspace/src/main/webapp/common/**/*.*','D:/workspaces/eclipse/workspace/src/main/webapp/scripts/**/*.*','D:/workspaces/eclipse/workspace/src/main/webapp/js/**/*.*'],
			tasks:['copy:tojboss'],
			options:{spawn:false}
		}
	}
	
  });

  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-shell');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-ftp-deploy');
  grunt.loadNpmTasks('grunt-sftp-deploy');
  //watch
  grunt.loadNpmTasks('grunt-contrib-watch');
  
  // 默认任务
  //grunt.registerTask('default', ['clean','concat','shell','uglify','copy','sftp-deploy']);
  grunt.registerTask('default', ['copy:tojboss','watch']);


}