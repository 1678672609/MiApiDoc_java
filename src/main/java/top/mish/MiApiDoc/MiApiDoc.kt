package top.mish.MiApiDoc

import org.json.JSONArray
import org.json.JSONObject
import org.springframework.web.bind.annotation.*
import top.mish.MiApiDoc.annotation.MiDocApi
import top.mish.MiApiDoc.annotation.MiDocGroup
import top.mish.MiApiDoc.annotation.MiDocParam
import top.mish.MiApiDoc.model.MiApiDocDirectory
import top.mish.MiApiDoc.utils.ClassUtils
import top.mish.MiApiDoc.utils.FileUtils
import top.mish.MiApiDoc.utils.MiApiDocGenerate
import top.mish.MiApiDoc.utils.MiApiLog
import java.lang.StringBuilder
import java.lang.reflect.Method
import java.math.BigDecimal

class MiApiDoc {
    private var classList=ArrayList<Class<*>>()//所有类集合
    private var groupAnnotationList=ArrayList<Class<MiDocGroup>>()//分组集合
    private var apiMethodList= LinkedHashMap<Class<*>,ArrayList<Method>>()//api集合

    private var scanningPackageList=ArrayList<String>()//扫描类的包名集合
    private var defaultHostUrl:String=""//设置请求开头请求路径
    private var filePath:String=""//文件保存路径
    private var defaultGroup:String=""//默认分组，如果没有任何分组时生效
    private var defaultNewApiSignTime:Long=0//默认分组新api标新有限时长
    private var defaultDirectory="Api文档"//默认目录
    private var hasDefaultDirectoryArray=false//是否有默认目录

    private var directoryArray=JSONArray()//目录集合
    private var groupParentArray=JSONArray()//分组父级集合

    private var readDirectoryArray=JSONArray()//读取到的目录集合
    private var readGroupParentArray=JSONArray()//读取到的分组集合


    /**
     *@Description 文档生成入口
     *@Param
     *@Return
     *@Author 杨磊
     *@Date 2020/4/16
     *@Time 15:10
     */
    fun run(){
        if(scanningPackageList.isEmpty()){
            MiApiLog.error("无扫描路径，生存文档结束！")
            return
        }
        scanningPackageList.forEach{
            classList.addAll(ClassUtils.getClasses(it))
        }
        groupAnnotationList.addAll(ClassUtils.findAnnotationClass(MiDocGroup::class.java,classList))
        apiMethodList.putAll(ClassUtils.findAnnotationMiDocApiFun(classList))
        generateGroupParent()
        generateGroup()
        generateChildren()
        readOldData()
        newApiTag()
        dataCombine()
        saveData()
        MiApiLog.msg("生成文档成功！")
        MiApiLog.log()
    }

    /**
     *@Description 生成group父级参数
     *@Param
     *@Return
     *@Author 杨磊
     *@Date 2020/4/16
     *@Time 16:27
     */
    private  fun generateGroupParent(){
        directoryArray.forEach {
            it as JSONObject
            groupParentArray.put(MiApiDocGenerate.generateGroupParent(
                    it.getString("name"),
                    if(it.has("rootUrl")) it.getString("rootUrl") else defaultHostUrl,
                    if(it.getLong("newApiSignTime")<0) defaultNewApiSignTime else it.getLong("newApiSignTime")
            ))
        }
    }

    /**
     *@Description 生成分组数据
     *@Param
     *@Return
     *@Author 杨磊
     *@Date 2020/4/16
     *@Time 16:08
     */
    private fun generateGroup(){
        if(groupAnnotationList.isEmpty()){//    差着创建默认分组
            groupParentArray.getJSONObject(0).getJSONArray("list").put(MiApiDocGenerate.generateGroup(defaultGroup,0L,"","",JSONArray()))
            return
        }
        groupAnnotationList.forEach {
            val miDocGroup=it.getAnnotation(MiDocGroup::class.java)
            val groupHeader=JSONArray()
            miDocGroup.header.header.forEach { header->
                groupHeader.put(MiApiDocGenerate.generateApiParameter(header))
            }
            val url=if(miDocGroup.rootUrl.isNotEmpty()) {
                miDocGroup.rootUrl
            }else{
                if(miDocGroup.isAnnotationMappingUrl) {
                    when {
                        it.isAnnotationPresent(PostMapping::class.java) -> {
                            if (it.getAnnotation(PostMapping::class.java).value.isNotEmpty()) {
                                it.getAnnotation(PostMapping::class.java).value[0]
                            } else {
                                ""
                            }
                        }
                        it.isAnnotationPresent(GetMapping::class.java) -> {
                            if (it.getAnnotation(GetMapping::class.java).value.isNotEmpty()) {
                                it.getAnnotation(GetMapping::class.java).value[0]
                            } else {
                                ""
                            }
                        }
                        it.isAnnotationPresent(RequestMapping::class.java) -> {
                            if (it.getAnnotation(RequestMapping::class.java).value.isNotEmpty()) {
                                it.getAnnotation(RequestMapping::class.java).value[0]
                            } else {
                                ""
                            }
                        }
                        it.isAnnotationPresent(DeleteMapping::class.java) -> {
                            if (it.getAnnotation(DeleteMapping::class.java).value.isNotEmpty()) {
                                it.getAnnotation(DeleteMapping::class.java).value[0]
                            } else {
                                ""
                            }
                        }
                        it.isAnnotationPresent(PatchMapping::class.java) -> {
                            if (it.getAnnotation(PatchMapping::class.java).value.isNotEmpty()) {
                                it.getAnnotation(PatchMapping::class.java).value[0]
                            } else {
                                ""
                            }
                        }
                        it.isAnnotationPresent(PutMapping::class.java) -> {
                            if (it.getAnnotation(PutMapping::class.java).value.isNotEmpty()) {
                                it.getAnnotation(PutMapping::class.java).value[0]
                            } else {
                                ""
                            }
                        }
                        else -> ""
                    }
                }else{
                    ""
                }
            }
            if(miDocGroup.directory.isEmpty()){
                if(!hasDefaultDirectoryArray){
                    val thisDirectoryArray=directoryArray
                    val thisGroupParentArray=groupParentArray
                    directoryArray= JSONArray()
                    groupParentArray= JSONArray()
                    directoryArray.put(0, MiApiDocGenerate.generateDirectory(defaultDirectory,"data.json",null,-1))
                    groupParentArray.put(0, MiApiDocGenerate.generateGroupParent(
                            defaultDirectory,
                            defaultHostUrl,
                            defaultNewApiSignTime
                    ))
                    thisDirectoryArray.forEach {
                        directoryArray.put(it)
                    }
                    thisGroupParentArray.forEach {
                        groupParentArray.put(it)
                    }
                    hasDefaultDirectoryArray=true
                }
                groupParentArray.getJSONObject(0)
                        .getJSONArray("list")
                        .put(MiApiDocGenerate.generateGroup(
                                miDocGroup.group,
                                0L,
                                miDocGroup.createUser,
                                url
                                ,
                                groupHeader))
            }else{
                groupParentArray.forEach { parent ->
                    parent as JSONObject
                    if(parent.getString("directory")==miDocGroup.directory){
                        parent.getJSONArray("list").put(MiApiDocGenerate.generateGroup(miDocGroup.group,0L,"",url,groupHeader))
                    }
                }
            }

        }
    }

    /**
     *@Description 生成api子集数据
     *@Param
     *@Return
     *@Author 杨磊
     *@Date 2020/4/16
     *@Time 17:33
     */
    private fun generateChildren(){
        apiMethodList.keys.forEach {
            val array=apiMethodList[it]!!
            val miDocGroup=it.getAnnotation(MiDocGroup::class.java)?:null
            for(methods in array){
                for(annotation in methods.annotations) {
                    if (annotation is MiDocApi) {
                        val headerArray=JSONArray()
                        val parameterArray=JSONArray()
                        miDocGroup?.header?.header?.forEach { header->
                            headerArray.put(MiApiDocGenerate.generateApiParameter(header))
                        }
                        annotation.header.header.forEach { header->
                            headerArray.put(MiApiDocGenerate.generateApiParameter(header))
                        }
                        if(annotation.bodyClass!=Unit::class){
                            val fields=annotation.bodyClass.java.declaredFields
                            if(annotation.bodyClassAllParam){
                                fields.forEach {field->
                                    var fieldAnnotation: MiDocParam?=null
                                    if(field.isAnnotationPresent(MiDocParam::class.java)){
                                        fieldAnnotation=field.getAnnotation(MiDocParam::class.java)
                                    }
                                    parameterArray.put(MiApiDocGenerate.generateApiParameter(field.name,field.type.simpleName,fieldAnnotation?.fill?:true,fieldAnnotation?.exampleValue?:"",fieldAnnotation?.remarks?:""))
                                }
                            }else {
                                fields.forEach { field ->
                                    if (field.isAnnotationPresent(MiDocParam::class.java)) {
                                        val fieldAnnotation = field.getAnnotation(MiDocParam::class.java)
                                        parameterArray.put(MiApiDocGenerate.generateApiParameter(field.name, field.type.simpleName, fieldAnnotation.fill, fieldAnnotation.exampleValue, fieldAnnotation.remarks))
                                    }
                                }
                            }
                        }else{
                            if(annotation.isAutoAnnotationBody){
                                methods.parameters.forEach {parameter->
                                    when(parameter.type){
                                        Boolean::class.java,String::class.java,StringBuffer::class.java,StringBuilder::class.java,Float::class.java,Int::class.java,Double::class.java,BigDecimal::class.java,Long::class.java,Short::class.java->{
                                            var fieldAnnotation: MiDocParam?=null
                                            if(parameter.isAnnotationPresent(MiDocParam::class.java)){
                                                fieldAnnotation=parameter.getAnnotation(MiDocParam::class.java)
                                            }
                                            parameterArray.put(MiApiDocGenerate.generateApiParameter(parameter.name, parameter.type.simpleName, fieldAnnotation?.fill?:true, fieldAnnotation?.exampleValue?:"", fieldAnnotation?.remarks?:""))
                                        }
                                    }
                                }
                            }
                        }

                        if(annotation.isUseGroupConfig){
                            val group = getGroup(if(annotation.group.isEmpty())  miDocGroup?.group?:defaultGroup else annotation.group)
                            var rootUrl=group.getString("rootUrl")
                            rootUrl+=if(annotation.url.isNotEmpty()) {
                                annotation.url
                            }else{
                                if(annotation.isAnnotationMappingUrl) {
                                    when {
                                        methods.isAnnotationPresent(PostMapping::class.java) -> {
                                            if (methods.getAnnotation(PostMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(PostMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(GetMapping::class.java) -> {
                                            if (methods.getAnnotation(GetMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(GetMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(RequestMapping::class.java) -> {
                                            if (methods.getAnnotation(RequestMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(RequestMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(DeleteMapping::class.java) -> {
                                            if (methods.getAnnotation(DeleteMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(DeleteMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(PatchMapping::class.java) -> {
                                            if (methods.getAnnotation(PatchMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(PatchMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(PutMapping::class.java) -> {
                                            if (methods.getAnnotation(PutMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(PutMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        else -> ""
                                    }
                                }else{
                                    ""
                                }
                            }
                            group.getJSONArray("children").put(
                                    MiApiDocGenerate.generateApi(
                                            annotation.title,
                                            miDocGroup?.group?:defaultGroup,
                                            if(annotation.type.isEmpty()){
                                                when {
                                                    methods.isAnnotationPresent(PostMapping::class.java) -> "POST"
                                                    methods.isAnnotationPresent(GetMapping::class.java) -> "GET"
                                                    methods.isAnnotationPresent(RequestMapping::class.java) -> "ALL"
                                                    methods.isAnnotationPresent(DeleteMapping::class.java) -> "DELETE"
                                                    methods.isAnnotationPresent(PatchMapping::class.java) -> "PATCH"
                                                    methods.isAnnotationPresent(PutMapping::class.java) -> "PUT"
                                                    else -> "UnKnown"
                                                }
                                            }  else {
                                                annotation.type.toUpperCase()
                                            },
                                            rootUrl
                                            ,
                                            annotation.remarks,
                                            annotation.resultExample,
                                            0L,
                                            if(annotation.createUser.isEmpty()) miDocGroup?.createUser?:"" else annotation.createUser,
                                            headerArray,
                                            parameterArray
                                    )
                            )
                        }else{
                            val group = getGroup(if(annotation.group.isEmpty())  defaultGroup else annotation.group)
                            val rootUrl=if(annotation.url.isNotEmpty()) {
                                annotation.url
                            }else{
                                if(annotation.isAnnotationMappingUrl) {
                                    when {
                                        methods.isAnnotationPresent(PostMapping::class.java) -> {
                                            if (methods.getAnnotation(PostMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(PostMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(GetMapping::class.java) -> {
                                            if (methods.getAnnotation(GetMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(GetMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(RequestMapping::class.java) -> {
                                            if (methods.getAnnotation(RequestMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(RequestMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(DeleteMapping::class.java) -> {
                                            if (methods.getAnnotation(DeleteMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(DeleteMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(PatchMapping::class.java) -> {
                                            if (methods.getAnnotation(PatchMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(PatchMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        methods.isAnnotationPresent(PutMapping::class.java) -> {
                                            if (methods.getAnnotation(PutMapping::class.java).value.isNotEmpty()) {
                                                methods.getAnnotation(PutMapping::class.java).value[0]
                                            } else {
                                                ""
                                            }
                                        }
                                        else -> ""
                                    }
                                }else{
                                    ""
                                }
                            }
                            group.getJSONArray("children").put(
                                    MiApiDocGenerate.generateApi(
                                            annotation.title,
                                            defaultGroup,
                                            if(annotation.type.isEmpty()){
                                                when {
                                                    methods.isAnnotationPresent(PostMapping::class.java) -> "POST"
                                                    methods.isAnnotationPresent(GetMapping::class.java) -> "GET"
                                                    methods.isAnnotationPresent(RequestMapping::class.java) -> "ALL"
                                                    methods.isAnnotationPresent(DeleteMapping::class.java) -> "DELETE"
                                                    methods.isAnnotationPresent(PatchMapping::class.java) -> "PATCH"
                                                    methods.isAnnotationPresent(PutMapping::class.java) -> "PUT"
                                                    else -> "UnKnown"
                                                }
                                            }  else {
                                                annotation.type.toUpperCase()
                                            },
                                            rootUrl
                                            ,
                                            annotation.remarks,
                                            annotation.resultExample,
                                            0L,
                                            annotation.createUser,
                                            headerArray,
                                            parameterArray
                                    )
                            )
                        }
                        break
                    }
                }
            }
        }
    }

    /**
     *@Description 获取group
     *@Param
     *@Return
     *@Author 杨磊
     *@Date 2020/4/16
     *@Time 17:41
     */
    private fun getGroup(group:String):JSONObject{
        groupParentArray.forEach {
            it as JSONObject
            val groupList=it.getJSONArray("list")
            for(i in 0 until groupList.length()){
                val getGroup=groupList.getJSONObject(i)
                if(getGroup.getString("group")==group){
                    return getGroup
                }
            }
        }
        if(!hasDefaultDirectoryArray){
            val thisDirectoryArray=directoryArray
            val thisGroupParentArray=groupParentArray
            directoryArray= JSONArray()
            groupParentArray= JSONArray()
            directoryArray.put(0, MiApiDocGenerate.generateDirectory((if(defaultDirectory.isNotEmpty()) defaultDirectory else "defaultDirectory"),"data.json",null,-1))
            groupParentArray.put(0, MiApiDocGenerate.generateGroupParent(
                    defaultDirectory,
                    defaultHostUrl,
                    defaultNewApiSignTime
            ))
            thisDirectoryArray.forEach {
                directoryArray.put(it)
            }
            thisGroupParentArray.forEach {
                groupParentArray.put(it)
            }
            hasDefaultDirectoryArray=true
        }
        val groupJson= MiApiDocGenerate.generateGroup(defaultGroup,0L,"","",JSONArray())
        groupParentArray.getJSONObject(0).getJSONArray("list").put(groupJson)
        return groupJson
    }

    //新参数对比旧参数
    private fun checkParameterChange(newBody:JSONArray,oldBody:JSONArray,currentTimeMillis:Long):Boolean{
        var isChange=false
        newBody.forEach {header->
            header as JSONObject
            var isOldHeader=false
            var oldHeader=JSONObject()
            oldBody.forEach {oldHeaders->
                oldHeaders as JSONObject
                if(header.getString("key")==oldHeaders.getString("key")){
                    oldHeader=oldHeaders
                    isOldHeader=true
                }
            }
            if(isOldHeader){
                if(header.getBoolean("fill")!=oldHeader.getBoolean("fill")){
                    header.getJSONObject("changeInfo").put("fill",currentTimeMillis)
                    isChange=true
                }else{
                    header.getJSONObject("changeInfo").put("fill",oldHeader.getJSONObject("changeInfo").getLong("fill"))
                }
                if(header.getString("exampleValue")!=oldHeader.getString("exampleValue")){
                    header.getJSONObject("changeInfo").put("exampleValue",currentTimeMillis)
                    isChange=true
                }else{
                    header.getJSONObject("changeInfo").put("exampleValue",oldHeader.getJSONObject("changeInfo").getLong("exampleValue"))
                }
                if(header.getString("type")!=oldHeader.getString("type")){
                    header.getJSONObject("changeInfo").put("type",currentTimeMillis)
                    isChange=true
                }else{
                    header.getJSONObject("changeInfo").put("type",oldHeader.getJSONObject("changeInfo").getLong("type"))
                }
                if(header.getString("remarks")!=oldHeader.getString("remarks")){
                    header.getJSONObject("changeInfo").put("remarks",currentTimeMillis)
                    isChange=true
                }else{
                    header.getJSONObject("changeInfo").put("remarks",oldHeader.getJSONObject("changeInfo").getLong("remarks"))
                }
            }
        }
        return isChange
    }

    //读取旧数据
    private fun readOldData(){
        readDirectoryArray=JSONObject(FileUtils.getDirectory(filePath)).getJSONArray("list")
        readDirectoryArray.forEach {
            it as JSONObject
            readGroupParentArray.put(JSONObject(FileUtils.getGroup(filePath+"/"+it.getString("url"))))
        }
    }

    //标记新api
    private fun newApiTag(){
        val newDirectoryArray=JSONArray()//新申明的目录列表
        directoryArray.forEach {
            var isHave=false
            it as JSONObject
            readDirectoryArray.forEach {directory->
                directory as JSONObject
                if(directory.getString("name")==it.getString("name")){
                    isHave=true
                }
            }
            if(!isHave){
                newDirectoryArray.put(it)
            }
        }

        newDirectoryArray.forEach {
            it as JSONObject
            groupParentArray.forEach { groupParent->
                groupParent as JSONObject
                if(groupParent.getString("directory") == it.getString("name")){
                    groupParent.getJSONArray("list").forEach { group->
                        group as JSONObject
                        val currentTimeMillis=System.currentTimeMillis()
                        group.put("lastApiCreateTime",currentTimeMillis)
                        group.getJSONArray("children").forEach {children->
                            children as JSONObject
                            children.put("createTime",currentTimeMillis)
                        }
                    }
                }
            }
        }
    }

    //新老数据合并
    private fun dataCombine(){
        groupParentArray.forEach {groupParent->
            groupParent as JSONObject
            val groupArray=groupParent.getJSONArray("list")
            val currentTimeMillis=System.currentTimeMillis()
            for(readGroupParen in readGroupParentArray){
                readGroupParen as JSONObject
                if(readGroupParen.getString("directory")!=groupParent.getString("directory")){
                    continue
                }
                val readGroupArray=readGroupParen.getJSONArray("list")
                groupArray.forEach { group->
                    group as JSONObject
                    val childrenArray=group.getJSONArray("children")
                    for(readGroup in readGroupArray){
                        readGroup as JSONObject
                        if(group.getString("group")!=readGroup.getString("group")){
                            continue
                        }
                        val readChildrenArray=readGroup.getJSONArray("children")
                        childrenArray.forEach {children->
                            children as JSONObject
                            var isOldApi=false
                            var oldApi=JSONObject()
                            for(readChildren in readChildrenArray){
                                readChildren as JSONObject
                                if(readChildren.getString("item")==children.getString("item")){
                                    isOldApi=true
                                    oldApi=readChildren
                                    continue
                                }
                            }
                            if(isOldApi){//如果是旧的接口
                                if(oldApi.getString("item")==children.getString("item")){
                                    children.put("createTime",oldApi.getLong("createTime"))
                                    children.put("changeTime",oldApi.getLong("changeTime"))
                                    if(group.getLong("lastApiCreateTime")<children.getLong("createTime")){
                                        group.put("lastApiCreateTime",children.getLong("createTime"))
                                    }
                                    if(
                                            children.getString("type")!=oldApi.getString("type")||
                                            children.getString("url")!=oldApi.getString("url")||
                                            children.getString("remarks")!=oldApi.getString("remarks")||
                                            children.getString("resultExample")!=oldApi.getString("resultExample")||
                                            children.getJSONArray("header").length()!=oldApi.getJSONArray("header").length()||
                                            children.getJSONArray("parametes").length()!=oldApi.getJSONArray("parametes").length()
                                    ){
                                        children.put("changeTime",currentTimeMillis)
                                    }

                                    if(checkParameterChange(children.getJSONArray("header"),oldApi.getJSONArray("header"),currentTimeMillis)){
                                        children.put("changeTime",currentTimeMillis)
                                    }

                                    if(checkParameterChange(children.getJSONArray("parametes"),oldApi.getJSONArray("parametes"),currentTimeMillis)){
                                        children.put("changeTime",currentTimeMillis)
                                    }
                                    if(group.getLong("changeTime")<children.getLong("changeTime")){
                                        group.put("changeTime",children.getLong("changeTime"))
                                    }
                                }
                            }else{//如果是新的接口
                                children.put("createTime",currentTimeMillis)
                                group.put("lastApiCreateTime",currentTimeMillis)
                            }
                        }
                    }
                }
            }
        }
    }

    //保存生成好的数据
    private fun saveData(){
        val json=JSONObject()
        json.put("list",directoryArray)
        FileUtils.saveDirectory(filePath,json.toString())
        groupParentArray.forEach {
            it as JSONObject
            directoryArray.forEach {directory->
                directory as JSONObject
                if(it.getString("directory")==directory.getString("name")){
                    FileUtils.saveGroup(filePath+"/"+directory.getString("url"),it.toString())
                }
            }
        }
    }

    /**
     *@Description build入口
     *@Param
     *@Return
     *@Author 杨磊
     *@Date 2020/4/16
     *@Time 17:41
     */
    class Builder{
        private val miApiDoc= MiApiDoc()

        /**
         *@Description 设置接口扫描包名
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 14:14
         */
        fun setScanningPackages(vararg packages:String): Builder {
            packages.forEach {
                miApiDoc.scanningPackageList.add(it)
            }
            return this
        }

        /**
         *@Description
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 14:18
         */
        fun setDirectory(vararg directorys: MiApiDocDirectory): Builder {
            if(directorys.isNotEmpty()){
                directorys.forEach {
                    miApiDoc.directoryArray.put(MiApiDocGenerate.generateDirectory(it.name,it.file,it.hostUrl,it.newApiSignTime))
                }
            }else{
                miApiDoc.directoryArray.put(MiApiDocGenerate.generateDirectory(if(miApiDoc.defaultDirectory.isNotEmpty()) miApiDoc.defaultDirectory else "defaultDirectory","data.json",null,-1))
                miApiDoc.hasDefaultDirectoryArray=true
            }
            return this
        }

        /**
         *@Description 设置请求路径
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 14:46
         */
        fun setDefaultHostUrl(defaultHostUrl:String): Builder {
            miApiDoc.defaultHostUrl=defaultHostUrl
            return this
        }

        /**
         *@Description 设置默认分组
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 16:16
         */
        fun setDefaultGroup(groupName:String): Builder {
            miApiDoc.defaultGroup=groupName
            return this
        }

        /**
         *@Description 设置默认新api标新有效时长
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 16:16
         */
        fun setDefaultNewApiSignTime(defaultNewApiSignTime:Long): Builder {
            miApiDoc.defaultNewApiSignTime=defaultNewApiSignTime
            return this
        }

        /**
         *@Description 文档保存路径
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/17
         *@Time 15:26
         */
        fun setFilePath(filePath:String): Builder {
            miApiDoc.filePath=filePath
            return this
        }

        //设置默认目录
        fun setDefaultDirectory(defaultDirectory:String): Builder {
            miApiDoc.defaultDirectory=defaultDirectory
            return this
        }

        /**
         *@Description 创建
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 15:04
         */
        fun create(): MiApiDoc {
            return miApiDoc
        }
    }
}