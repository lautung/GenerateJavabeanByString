package action;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import utils.CommonUtil;

import java.util.HashMap;
import java.util.Map;

public class GenerateJavabeanByString extends AnAction {

    private String member="public";

    //定义生成文件的协议
    private String pasteStr="name String\n"+
            "age int\n"+"id Integer\n";

    @Override
    public void actionPerformed(AnActionEvent e) {
        //点菜单的时候就会执行这个方法中的代码了
        generateFile(e,"User",pasteStr);
    }
    private void generateFile(AnActionEvent actionEvent,String fileName,String pasteStr){
        //得到当前工程对象
        Project project = actionEvent.getProject();
        //得到目录服务
        JavaDirectoryService directoryService=JavaDirectoryService.getInstance();
        //得到当前路径(相对路径)
        IdeView iveView = actionEvent.getRequiredData(LangDataKeys.IDE_VIEW);
        PsiDirectory directory = iveView.getOrChooseDirectory();

        //填入模版文件的参数
        Map<String,String> map=new HashMap();
        map.put("NAME",fileName);
        map.put("INTERFACES","implements Serializable");
        map.put("PACKAGE_NAME", CommonUtil.getPackageName(project));

        //模块已经做好了，就可以生成文件
        PsiClass psiClass=directoryService.createClass(directory,fileName,"GenerateFileByString",false,map);

        //开始加入字段
        WriteCommandAction.runWriteCommandAction(project,
                new Runnable() {
                    @Override
                    public void run() {
                        generateModelField(pasteStr,project,psiClass);
                    }
                });
    }
    private void generateModelField(String pasteStr,Project project,PsiClass psiClass){
        if(psiClass==null){
            return;
        }
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        //根据用户输入的字符串生产出代码
        //"name String\n"+"age int\n"+"id Integer\n";
        String[] lineString=pasteStr.split("\n");
        StringBuilder sb=new StringBuilder();
        for (String line : lineString) {
            String[] temp=line.split(" ");
            String fieldName=temp[0];
            String fieldType=temp[1];
            //public String name;
            sb.append(member+" "+fieldType+" "+fieldName+";");
            PsiField field=factory.createFieldFromText(sb.toString(),psiClass);
            psiClass.add(field);
            sb.delete(0,sb.length());
        }


    }
}


















