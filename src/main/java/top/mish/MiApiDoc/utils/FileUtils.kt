package top.mish.MiApiDoc.utils

import org.json.JSONArray
import org.json.JSONObject
import java.io.*

class FileUtils {
    companion object {
        private const val directoryFileName:String="/listConfig.json"
        /**
        *@Description 保存文档目录数据
        *@Param 
        *@Return 
        *@Author 杨磊
        *@Date 2020/4/17
        *@Time 15:29
        */
        fun saveDirectory(filePath:String,data:String){
            val file=File(filePath+ directoryFileName)
            if(file.isFile){
                file.delete()
                file.createNewFile()
            }
            val fileOutputStream= FileOutputStream(file)
            fileOutputStream.write(data.toByteArray(charset("utf-8")))
            fileOutputStream.flush()
            fileOutputStream.close()
        }

        /**
         *@Description 保存文档目录数据
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/17
         *@Time 15:29
         */
        fun getDirectory(filePath:String):String{
            val file=File(filePath+ directoryFileName)
            if(file.exists()){
                val fileReader= FileReader(file)
                return fileReader.readText()
            }
            val data=JSONObject()
            data.put("list",JSONArray())
            return data.toString()
        }

        /**
         *@Description 保存文档目录数据
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/17
         *@Time 15:29
         */
        fun saveGroup(filePath:String,data:String){
            val file=File(filePath)
            if(file.isFile){
                file.delete()
                file.createNewFile()
            }
            val fileOutputStream= FileOutputStream(file)
            fileOutputStream.write(data.toByteArray(charset("utf-8")))
            fileOutputStream.flush()
            fileOutputStream.close()
        }
        /**
         *@Description 保存文档目录数据
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/17
         *@Time 15:29
         */
        fun getGroup(filePath:String):String{
            val file=File(filePath)
            if(file.exists()){
                val fileReader= FileReader(file)
                return fileReader.readText()
            }
            return JSONObject().toString()
        }

    }
}