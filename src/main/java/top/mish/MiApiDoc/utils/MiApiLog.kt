package top.mish.MiApiDoc.utils

class MiApiLog {
    companion object {
        /**
         *@Description 错误日志
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 15:09
         */
        fun error(msg:Any){
            System.err.println("MiApiDoc:$msg")
        }

        /**
         *@Description 消息日志
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/16
         *@Time 15:10
         */
        fun msg(msg:Any){
            println("MiApiDoc:$msg")
        }

        /**
         *@Description 打印logo
         *@Param
         *@Return
         *@Author 杨磊
         *@Date 2020/4/17
         *@Time 15:53
         */
        fun log(){
            println()
            println("□□□□□□□□□□□□□□□□□□□□□●●□□□■□□■□□□□□□□□□")
            println("□□■■□□■■□□□□●□□□□□□□●□□●□□■□■□□□□□□□□□□")
            println("□□■■□□■■□□□■■□□□□□□●□□□□●□■■□□□□□□□□□□□")
            println("□□■□■■□■□□□□■□□□□□□●□□□□●□■□■□□□□□□□□□□")
            println("□□■□□□□■□□□□■□□□□□□□●□□●□□■□□■□□□□□□□□□")
            println("□□■□□□□■□□■■■■■□□□□□□●●□□□■□□□■□□□□□□□□")
            println()
        }
    }
}