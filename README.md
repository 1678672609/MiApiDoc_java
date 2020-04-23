# MiApiDoc_java
Spring项目文档生成工具

该工具需配合https://github.com/1678672609/MIApi  MIApi使用



第一个实例
```Kotlin
MiApiDoc.Builder()
                    .setDirectory(MiApiDocDirectory("用户端","user.json",newApiSignTime = 60*60*24))
                    .setDefaultHostUrl("http://192.168.3.5:9096/")
                    .setScanningPackages("top.test.Controller")
                    .setFilePath("H:/project_web/MiApiDoc")
                    .setDefaultGroup("默认分组")
                    .create()
                    .run()
```

生成一个用户端文档，默认请求根路径是http://192.168.3.5:9096/

生成文档保存路径为H:/project_web/MiApiDoc

扫描接口的包名是top.test.Controller.

#### 大部分注解会自动的获取GetMapping、PostMapping等一些注解的参数，前提是注解触发获取条件的值为空。

# 注解介绍

## 分组注解 
#### MiDocGroup
```kotlin
annotation class MiDocGroup(
        val group:String,//分组名称
        val rootUrl:String="",//该分组下的所有api的公共url，如果有值将会被加入到api里
        val isAnnotationMappingUrl:Boolean=true,//是否自动获取Mapping注解的路径,true如果rootUrl参数则自动取获取注解
        val createUser:String="",//创建用户
        val directory:String="",//分组目录
        val header: MiDocApiHeader = MiDocApiHeader()//请求时头部所带参数,将会下发给所有该分组下的Api
)
```

## Api接口注解 
#### MiDocApi
```kotlin
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
```

## Api参数注解 
#### MiDocApiParameter
```kotlin
annotation class MiDocApiParameter(
        val key:String,//键值
        val type:String,//类型
        val fill:Boolean=true,//是否必填
        val remarks:String="",//字段解释
        val exampleValue:String=""//实例值
)
```

## Api接口接收参数注解 
#### MiDocParam
```kotlin
annotation class MiDocParam(
        val remarks:String="",//字段解释
        val fill:Boolean=true,//是否必填
        val exampleValue:String=""//实例值
)
