import org.gradle.api.Plugin
import org.gradle.api.Project

class CheckPermissionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        CheckListData checkListData = project.getExtensions().create("checkListData", CheckListData.class)
        project.afterEvaluate {
            CheckPermissionTask checkPermissionTask = project.tasks.create("checkPermission", CheckPermissionTask.class, checkListData)
            if (checkListData.afterTask != "") {
                project.tasks.findByName(checkListData.afterTask).finalizedBy(checkPermissionTask)
            }
        }
    }
}