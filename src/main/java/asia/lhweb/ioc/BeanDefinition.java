package asia.lhweb.ioc;

/**
 * bean定义
 * 用于封装/记录Bean的信息[1 scope 2 bean对应的class对象]
 *
 * @author 罗汉
 * @date 2024/04/11
 */
public class BeanDefinition {
    private String id;
    private String scope;
    private String className;

    public BeanDefinition() {
    }

    public BeanDefinition(String id, String scope, String className) {
        this.id = id;
        this.scope = scope;
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
