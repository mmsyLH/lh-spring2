package asia.lhweb.config;


import asia.lhweb.annotation.ComponentScan;

/**
 * lhspring配置
 *  类似beans.xml  容器配置文件
 * @author 罗汉
 * @date 2023/07/17
 */
@ComponentScan({"asia.lhweb.mapper","asia.lhweb.service"})
public class SpringConfig {

}
