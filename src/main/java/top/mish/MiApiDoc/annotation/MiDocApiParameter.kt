package top.mish.MiApiDoc.annotation

/**
 *@Description MiDocApi请求参数格式
 *@Param
 *@Return
 *@Author Mr.Ren
 *@Date 2020/4/15
 *@Time 17:21
 */
annotation class MiDocApiParameter(
        val key:String,//键值
        val type:String,//类型
        val fill:Boolean=true,//是否必填
        val remarks:String="",//字段解释
        val exampleValue:String=""//实例值
)