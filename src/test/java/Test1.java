import asia.lhweb.config.SpringConfig;
import asia.lhweb.ioc.MyApplicationContext;

/**
 * 版本1的测试
 * @author :罗汉
 * @date : 2024/4/11
 */
public class Test1 {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(SpringConfig.class);
        Object service = myApplicationContext.getBean("aServer");
        System.out.println("");
        System.out.println(service);
    }
}
