package top.mish.MiApiDoc.annotation

import top.mish.MiApiDoc.annotation.MiDocApiHeader

/**
 *@Description 声明一个MiApiDoc的分组注解
 *@Param
 *@Return
 *@Author Mr.Ren
 *@Date 2020/4/15
 *@Time 16:26
 */
@Retention
annotation class MiDocGroup(
        val group:String,//分组名称
        val rootUrl:String="",//该分组下的所有api的公共url，如果有值将会被加入到api里
        val isAnnotationMappingUrl:Boolean=true,//是否自动获取Mapping注解的路径,true如果rootUrl参数则自动取获取注解
        val createUser:String="",//创建用户
        val directory:String="",//分组目录
        val header: MiDocApiHeader = MiDocApiHeader()//请求时头部所带参数,将会下发给所有该分组下的Api
)