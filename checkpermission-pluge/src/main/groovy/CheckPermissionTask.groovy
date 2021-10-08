import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject
import java.security.MessageDigest

class CheckPermissionTask extends DefaultTask {
    CheckListData checkListData;

    @Inject
    CheckPermissionTask(CheckListData checkListData) {
        setGroup("check")
        this.checkListData = checkListData;
    }

    @TaskAction
    void doAction() {
        Task task = project.tasks.findByName(checkListData.afterTask)
        def startTime = System.currentTimeMillis()
        def warningMap = new HashMap<String, ArrayList<File>>()
        def warningList

        task.getInputs().files.files.each { File file ->
            print(file.name + "\n")
            if (file.exists() && file.name.contains("AndroidManifest.xml") && file.text.contains("uses-permission")) {
                def androidManifest = new XmlParser().parse(file)
                androidManifest["uses-permission"].each { Node node ->
                    checkListData.permissionList.each { String permission ->
                        if (node.attributes().toString().contains(permission)) {
                            warningList = warningMap.get(permission)
                            if (warningList == null) {
                                warningList = new ArrayList<File>()
                            }
                            warningList.add(file)
                            warningMap.put(permission, warningList)
                            //可修改xml
//                        new XmlNodePrinter(new PrintWriter(new FileWriter(file))).print(file.text)
                        }
                    }
                }

            }
        }
        //println("检测以下权限：${checkPermissionList}\n")
        println("开始打印检测结果==============================================")
        warningMap.entrySet().each {
            println("包含[${it.key}]")
            it.value.each { File file ->
                println(file)
            }
        }
        println("\n检测结果打印结束 耗时：${System.currentTimeMillis() - startTime}毫秒")
    }

    def getFileMD5(String name) {
        print "begin file md5! \n "
        File file = new File(name)
        if (!file.isFile()) {
            print "file md5 Error! \n "
            return null
        }
        MessageDigest digest = null
        FileInputStream inputStream = null
        byte[] buffer = new byte[1024]
        int len
        try {
            digest = MessageDigest.getInstance("MD5")
            inputStream = new FileInputStream(file)
            while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len)
            }
            inputStream.close()
        } catch (Exception e) {
            e.printStackTrace()
            print "file md5 Error! \n "
            return null
        }
        BigInteger bigInt = new BigInteger(1, digest.digest())
        return bigInt.toString(16)
    }

}