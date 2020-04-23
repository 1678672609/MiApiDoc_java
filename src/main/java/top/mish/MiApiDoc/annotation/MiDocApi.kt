package top.mish.MiApiDoc.annotation

import kotlin.reflect.KClass


/**
 *@Description 声明一个MiApiDoc的Api注解
 *@Param
 *@Return
 *@Author Mr.Ren
 *@Date 2020/4/15
 *@Time 16:38
 */
@Target(AnnotationTarget.FUNCTION)
annotation class MiDocApi(
        val title:String,//api标题说明
        val isUseGroupConfig:Boolean=true,//是否使用分组下发的配置信息
        val isAnnotationMappingUrl:Boolean=true,//是否自动获取Mapping注解的路径,true如果rootUrl参数则自动取获取注解
        val url:String="",//api请求url,如果没有值，则自动获取方法的注解的值（PostMapping、GetMapping）,如果MiDocGroup也有该值，则相加
        val type:String="",//api请求方式，如果没有值，则自动获取方法的注解（PostMapping、GetMapping）
        val remarks:String="",//api说明
        val resultExample:String="",//api请求结果实例
        val createUser:String="",//创建人，如果不填写，则默认使用如果MiDocGroup.createUser
        val group:String="",//api分组
        val header: MiDocApiHeader = MiDocApiHeader(),//请求时头部所带参数
        val isAutoAnnotationBody:Boolean=true,//是否自动从函数注解body参数，如果自动注解，参数fill自动默认为true
        val body: MiDocApiBody = MiDocApiBody(),//请求时所携带的body参数，如果'isAutoAnnotationBody=true'自动注解参数，则合并
        val bodyClass: KClass<*> = Unit::class,//请求参数实体类,如果该类存在则body、isAutoAnnotationBody参数则无效
        val bodyClassAllParam:Boolean=true//参数实体类所有公共变量作为body参数，如果为false,则只保存@MiDocParam的参数
)
