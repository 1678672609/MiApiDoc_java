package top.mish.MiApiDoc.utils

import org.json.JSONArray
import org.json.JSONObject
import top.mish.MiApiDoc.annotation.MiDocApiParameter

class MiApiDocGenerate {
    companion object {
        /**
         *@Description 生成组结构
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 16:12
         */
        fun generateGroup(name:String,lastApiCreateTime:Long,createUser:String,rootUrl:String,header: JSONArray): JSONObject {
            val json=JSONObject()
            json.put("group", name)
            json.put("lastApiCreateTime", lastApiCreateTime)
            json.put("changeTime",0L)
            json.put("createUser", createUser)
            json.put("rootUrl",rootUrl)
            json.put("isOpen",false)
            json.put("children",JSONArray())
            json.put("header",header)
            return json
        }

        /**
         *@Description 生成目录结构
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 16:26
         */
        fun generateDirectory(name:String,file:String,hostUrl:String?,newApiSignTime:Long):JSONObject{
            val json=JSONObject()
            json.put("name",name)
            json.put("url",file)
            json.put("rootUrl",hostUrl)
            json.put("isCheck",false)
            json.put("newApiSignTime",newApiSignTime)
            return json
        }

        /**
         *@Description 生成group父级参数
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 16:27
         */
        fun generateGroupParent(directory:String,rootUrl:String,newApiSignTime:Long):JSONObject{
            val json=JSONObject()
            json.put("rootUrl",rootUrl)
            json.put("directory",directory)
            json.put("newApiSignTime",newApiSignTime)
            json.put("list",JSONArray())
            return json
        }

        /**
         *@Description 生成请求携带参数（头部、body）
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 16:27
         */
        fun generateApiParameter(miDocApiParameter: MiDocApiParameter):JSONObject{
            val json=JSONObject()
            json.put("key",miDocApiParameter.key)
            json.put("type",miDocApiParameter.type)
            json.put("fill",miDocApiParameter.fill)
            json.put("exampleValue",miDocApiParameter.exampleValue)
            json.put("remarks",miDocApiParameter.remarks)
            json.put("isCheck",true)
            json.put("changeInfo", generateApiParameterChangeInfo())
            return json
        }

        /**
        *@Description 生成请求携带参数（头部、body）
        *@Param 
        *@Return 
        *@Author 杨磊
        *@Date 2020/4/17
        *@Time 11:33
        */
        fun generateApiParameter(key:String, type:String, fill:Boolean, exampleValue:String, remarks:String):JSONObject{
            val json=JSONObject()
            json.put("key",key)
            json.put("type",type)
            json.put("fill",fill)
            json.put("exampleValue",exampleValue)
            json.put("remarks",remarks)
            json.put("isCheck",true)
            json.put("changeInfo", generateApiParameterChangeInfo())
            return json
        }

        /**
         *@Description 生成请求携带参数的更改时间信息（头部、body）
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/17
         *@Time 11:33
         */
        private fun generateApiParameterChangeInfo():JSONObject{
            val changeInfo=JSONObject()
            changeInfo.put("type",0L)
            changeInfo.put("fill",0L)
            changeInfo.put("exampleValue",0L)
            changeInfo.put("remarks",0L)
            return changeInfo
        }

        /**
         *@Description 生成Api结构
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 16:27
         */
        fun generateApi(item:String,group:String,type:String,url:String,remarks:String,resultExample:String,createTime:Long,createUser:String,header:JSONArray,parametes:JSONArray):JSONObject{
            val json=JSONObject()
            json.put("item",item)
            json.put("group",group)
            json.put("type",type)
            json.put("url",url)
            json.put("remarks",remarks)
            json.put("resultExample",resultExample)
            json.put("isCheck",false)
            json.put("createTime",createTime)
            json.put("createUser",createUser)
            json.put("header",header)
            json.put("parametes",parametes)
            json.put("changeTime",0L)
            return json
        }
    }
}