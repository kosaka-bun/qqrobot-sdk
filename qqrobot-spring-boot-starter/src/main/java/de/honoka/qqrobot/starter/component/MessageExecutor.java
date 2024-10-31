package de.honoka.qqrobot.starter.component;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import de.honoka.qqrobot.framework.api.model.RobotMessage;
import de.honoka.qqrobot.framework.api.model.RobotMessageType;
import de.honoka.qqrobot.framework.api.model.RobotMultipartMessage;
import de.honoka.qqrobot.starter.RobotBasicProperties;
import de.honoka.qqrobot.starter.RobotStarter;
import de.honoka.qqrobot.starter.command.CommandInvoker;
import de.honoka.qqrobot.starter.common.ConstantMessage;
import de.honoka.qqrobot.starter.common.annotation.RobotController;
import de.honoka.qqrobot.starter.component.logger.RobotLogger;
import de.honoka.qqrobot.starter.component.session.RobotSession;
import de.honoka.qqrobot.starter.component.session.SessionManager;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 消息处理器，按相应的逻辑统一地处理各类消息
 */
@Component
public class MessageExecutor {

    //region components

    @Resource
    private RobotBasicProperties basicProperties;

    @Resource
    private RobotStatus attributes;

    @Resource
    private RobotLogger robotLogger;

    @Resource
    private SessionManager sessionManager;

    @Lazy
    @Resource
    private ExceptionReporter reporter;

    //List指的是将Spring容器中，所有的此类的实例，组装为List提供到此方法
    @RobotController
    @Resource
    private List<Object> controllers;

    //endregion

    /**
     * 命令调用器
     */
    private List<CommandInvoker> invokers;

    @Getter
    private String menu = "菜单尚未初始化";

    @Getter
    private String wrongCommandMsg = "指令有误，请检查输入\n请发送“%s菜单”查看指令";

    @PostConstruct
    public void init() {
        //加载命令调用器
        invokers = CommandInvoker.getInvokers(controllers, basicProperties);
        //加载菜单
        loadMenu();
        //加载错误指令的提示信息
        wrongCommandMsg = String.format(wrongCommandMsg, basicProperties.getCommandPrefix());
    }

    /**
     * 利用命令调用器列表加载菜单文本
     */
    private void loadMenu() {
        StringBuilder menuBuilder = new StringBuilder("目前可以完成的功能有：\n");
        for(CommandInvoker invoker : invokers) {
            for(String name : invoker.getCommandName()) {
                if(invoker.isMustInvokeByAdmin()) {
                    name = "*" + name;
                }
                menuBuilder.append(name).append("、");
            }
        }
        menu = menuBuilder.substring(0, menuBuilder.length() - 1);
    }
    
    /**
     * 消息统一处理方法，处理各类群消息（群、讨论组）与各类私聊消息（好友，临时会话）
     */
    @SuppressWarnings("unchecked")
    public RobotMultipartMessage executeMsg(Long group, long qq, RobotMultipartMessage msg) {
        //起始操作，优化要处理的信息
        //移除空串部分
        msg.removeEmptyPart();
        //处理信息前先去掉信息左右两侧的空格
        if(msg.getFirst().getType().equals(RobotMessageType.TEXT)) {
            RobotMessage<String> part = (RobotMessage<String>) msg.getFirst();
            String str = part.getContent();
            str = StringUtils.stripStart(str, null);
            part.setContent(str);
        }
        if(msg.messageList.get(msg.messageList.size() - 1).getType().equals(RobotMessageType.TEXT)) {
            RobotMessage<String> part = (RobotMessage<String>) msg.messageList.get(msg.messageList.size() - 1);
            String str = part.getContent();
            str = StringUtils.stripEnd(str, null);
            part.setContent(str);
        }
        //进行简繁转换
        for(RobotMessage<?> part0 : msg.messageList) {
            if(!part0.getType().equals(RobotMessageType.TEXT)) continue;
            RobotMessage<String> part = (RobotMessage<String>) part0;
            part.setContent(ZhConverterUtil.toSimple(part.getContent()));
        }
        //判断发送消息的qq是否在会话列表内
        RobotSession session = sessionManager.getCurrentSession(group, qq);
        //如果发送此消息的qq在会话列表内，则此qq发送的任何信息都需要被处理
        if(session != null) {
            //检查总开关状态，若未开启，则不将信息传递给会话
            if(!attributes.isEnabled()) return null;
            /*
             * reply是驱动会话进行的动力，waitingForReply方法会时刻监听reply的内容，
             * 直到reply不为null时返回
             */
            //记录此消息，传递给会话线程
            session.setReply(msg);
            //截获此信息，不再进行下面的命令匹配
            return null;
        }
        //若不处于会话当中，则进行命令匹配
        //处理消息
        RobotMultipartMessage reply = executeMsg0(group, qq, msg);
        //结束操作，用于对消息进行一些记录或分析
        //内部类中需要局部变量未被更改才能调用
        RobotMultipartMessage replyCopy = (RobotMultipartMessage) Objects.requireNonNull(reply).clone();
        //在新线程中，忽略异常地进行结束操作
        RobotStarter.globalThreadPool.submit(() -> {
            //记录消息处理的相关信息
            robotLogger.logMsgExecution(group, qq, msg, replyCopy);
        });
        return reply;
    }

    @SuppressWarnings("unchecked")
    private RobotMultipartMessage executeMsg0(Long group, long qq, RobotMultipartMessage msg) {
        RobotMultipartMessage reply = null;
        //是否找到了对应的命令
        boolean foundCommand = false;
        try {
            //判断是否由起始字符开始，由起始字符开始，则去除起始字符
            //noPrefix：这个命令是否是不含起始字符的命令
            boolean noPrefix = true;
            if(msg.getFirst().getType().equals(RobotMessageType.TEXT)) {
                RobotMessage<String> part = (RobotMessage<String>) msg.getFirst();
                if(part.getContent().startsWith(basicProperties.getCommandPrefix())) {
                    part.setContent(part.getContent().substring(basicProperties.getCommandPrefix().length()));
                    noPrefix = false;
                }
            } else {
                //消息的首个部分不是TEXT类型的，说明不是命令
                return null;
            }
            //获取并检查命令名
            List<Object> parts = new ArrayList<>();
            for(RobotMessage<?> part : msg.messageList) {
                if(part.getType().equals(RobotMessageType.TEXT)) {
                    parts.addAll(Arrays.asList(((String) part.getContent()).split(" ")));
                } else {
                    parts.add(part);
                }
            }
            //命令名为空串，不处理
            if(parts.get(0).equals("")) return null;
            //处理命令名，将字母全部转为小写
            parts.set(0, ((String) parts.get(0)).toLowerCase());
            //遍历所有命令调用器
            for(CommandInvoker invoker : invokers) {
                //若提供的命令没有前缀，而此调用器有前缀
                //（或提供的命令有前缀，而此调用器没有前缀），则忽略此调用器
                if(noPrefix != invoker.isNoPrefix()) continue;
                //处理命令名集合，将调用器中的所有命令名复制一份，并转为小写字母
                List<String> nameInInvoker = new ArrayList<>();
                invoker.getCommandName().forEach(name -> nameInInvoker.add(name.toLowerCase()));
                //此调用器的命令名不包含要调用的命令名
                if(!nameInInvoker.contains((String) parts.get(0))) continue;
                //包含，则可确认此消息是需要响应的命令，将调用此命令调用器，并跳出
                foundCommand = true;
                //响应命令前先检查总开关状态，若未开启，则回复提示信息，不响应命令
                if(!attributes.isEnabled()) {
                    return RobotMultipartMessage.of(ConstantMessage.OFF);
                }
                //优化参数列表
                List<Object> argsList = new ArrayList<>(parts.subList(1, parts.size()));
                //清除空参数
                for(int i = argsList.size() - 1; i >= 0; i--) {
                    Object arg = argsList.get(i);
                    if(arg.equals("")) argsList.remove(i);
                }
                //检查参数个数是否足够
                if(argsList.size() < invoker.getArgsNum()) {
                    return RobotMultipartMessage.of(ConstantMessage.PARAMETER_NOT_ENOUGH);
                }
                Object[] args = argsList.toArray(new Object[0]);
                //调用
                reply = invoker.invoke(group, qq, args);
                break;
            }
            //判断是否指令有误（有前缀但没有找到命令）
            if(!noPrefix && !foundCommand) {
                reply = RobotMultipartMessage.of(wrongCommandMsg);
            }
        } catch(Throwable t) {
            reporter.sendExceptionToDevelopingGroup(t);
            //找到了命令但没有正确处理
            if(foundCommand) reply = RobotMultipartMessage.of(ConstantMessage.ERROR);
        }
        return reply;
    }
}
