package cn.itcast.order.tcc;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC  //开启TCC注解
public class TCCService {
    /**
     * try逻辑
     * TwoPhaseBusinessAction注解中name的值要和方法名的值一致
     * 提交方法对应自己编写的提交方法名
     * 回滚方法对应自己编写的取消方法名
     * @param param
     */
    @TwoPhaseBusinessAction(name = "prepare", commitMethod = "confirm", rollbackMethod = "cancel")
    void prepare(@BusinessActionContextParameter(paramName = "param") String param) {

    }


    /**
     * commit逻辑
     * 二阶段确认方法
     * @param context 上下文，可以传递try方法的参数
     * @return
     */
    boolean confirm(BusinessActionContext context) {
        return false;
    }

    /**
     * cancel逻辑
     * 二阶段回滚方法 保证与rollbackMethod一致
     */
    boolean cancel(BusinessActionContext context) {
        return false;
    }


}
