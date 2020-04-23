package top.mish.MiApiDoc.model

data class MiApiDocDirectory (
        var name:String,//目录名称
        var file:String,//该目录下的api保存路径以及文件名
        var hostUrl:String?=null,//请求路径
        var newApiSignTime:Long=-1//新建api 标新时长
)