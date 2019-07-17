var log4js = require("log4js");
var appConfig = require('../app-config');
var path = require('path');

log4js.configure({
	appenders: { 
		console:{type:'console'},
		cheese: { type: 'dateFile',filename:"./logs/",pattern: "yyyy-MM-dd.log", maxLogSize: 11024,alwaysIncludePattern: true},
		
	 },
	categories: { 
		default: { appenders: ['console','cheese'], level: 'all',replaceConsole: true },
	}
	//测试环境的配置
	// appenders: { 
	// 	console:{type:'console'},
	// 	cheese: { type: 'dateFile', filename:'./logs/',pattern: "yyyy-MM-dd.log", maxLogSize: 11024,alwaysIncludePattern: true　},
		
	//  },
	// categories: { 
	// 	default: { appenders: ['console','cheese'], level: 'all'},
    // }

	// appenders: [{　　　
	// 	type: 'console'
	// }, {
	// 	type: 'dateFile',
	// 	filename: appConfig.logPath || 'logs/',
	// 	pattern: "yyyy-MM-dd.log",
	// 	maxLogSize: 1024,
	// 	alwaysIncludePattern: true　　　 
	// 	// backups: 4 //日志备份数量，大于该数则自动删除
	// 	// category: 'normal' //这个破玩儿，加上就写不到文件中去了
    // }],
	// replaceConsole: true
});

// log4js.setGlobalLogLevel(appConfig.logLevel || log4js.levels.INFO);

exports.getLogger = function(file){
	return log4js.getLogger(file || "log");
};