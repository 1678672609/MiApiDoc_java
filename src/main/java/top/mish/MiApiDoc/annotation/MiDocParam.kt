package top.mish.MiApiDoc.annotation

/**
*@Description 字段参数注解
*@Param
*@Return
*@Author 杨磊
*@Date 2020/4/16
*@Time 14:36
*/
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
annotation class MiDocParam(
        val remarks:String="",//字段解释
        val fill:Boolean=true,//是否必填
        val exampleValue:String=""//实例值
)