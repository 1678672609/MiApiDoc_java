package top.mish.MiApiDoc

import top.mish.MiApiDoc.MiApiDoc
import top.mish.MiApiDoc.model.MiApiDocDirectory

class TestMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            MiApiDoc.Builder()
                    .setDirectory(MiApiDocDirectory("用户端","user.json",newApiSignTime = 172800))
                    .setDefaultHostUrl("http://192.168.3.5:9096/")
                    .setScanningPackages("top.mish.mxsh.Controller")
                    .setFilePath("H:/project_web/MiApiDoc")
                    .setDefaultGroup("默认分组")
                    .create()
                    .run()
        }
    }
}