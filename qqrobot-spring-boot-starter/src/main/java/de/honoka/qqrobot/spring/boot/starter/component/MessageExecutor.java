package de.honoka.qqrobot.spring.boot.starter.component;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import de.honoka.qqrobot.spring.boot.starter.annotation.RobotController;
import de.honoka.qqrobot.spring.boot.starter.command.CommandInvoker;
import de.honoka.qqrobot.spring.boot.starter.component.logger.RobotLogger;
import de.honoka.qqrobot.spring.boot.starter.component.session.RobotSession;
import de.honoka.qqrobot.spring.boot.starter.component.session.SessionManager;
import de.honoka.qqrobot.spring.boot.starter.property.RobotBasicProperties;
import de.honoka.sdk.util.code.ActionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 消息处理器，按相应的逻辑统一地处理各类消息
 */
@Component
public class MessageExecutor {

	/**
	 * 命令调用器
	 */
	private final List<CommandInvoker> invokers;
	
	/**
	 * 利用命令调用器列表加载菜单文本
	 */
	private void loadMenu() {
		StringBuilder menu = new StringBuilder("目前可以完成的功能有：\n");
		for(CommandInvoker invoker : invokers) {
			for(String name : invoker.getCommandName()) {
				if(invoker.isMustInvokeByAdmin())
					name = "*" + name;
				menu.append(name).append("、");
			}
		}
		attributes.menu = menu.substring(0, menu.length() - 1);
	}
	
	//参数中的List指的是将Spring容器中，所有的此类的实例，组装为List提供到此方法
	//如果某个要注入的成员在构造方法时就要使用，必须将其作为构造方法的参数
	public MessageExecutor(@RobotController List<Object> controllers,
                           RobotAttributes attributes,
                           RobotBasicProperties basicProperties) {
		this.attributes = attributes;
		this.basicProperties = basicProperties;
		//加载命令调用器
		invokers = CommandInvoker.getInvokers(controllers, basicProperties);
		//加载菜单
		loadMenu();
		//加载错误指令的提示信息
		attributes.wrongCommandMsg = String.format(attributes.wrongCommandMsg,
				basicProperties.getCommandPrefix());
	}
	
	private String executeMsg0(Long group, long qq, String msg) {
		String reply = null;
		boolean foundCommand = false;	//是否找到了对应的命令
		try {
			//判断是否由起始字符开始，由起始字符开始，则去除起始字符
			boolean noPrefix = true;
			if(msg.startsWith(basicProperties.getCommandPrefix())) {
				msg = msg.substring(basicProperties.getCommandPrefix().length());
				noPrefix = false;
			}
			//获取并检查命令名
			String[] parts = msg.split(" ");
			if(parts[0].equals("")) return null;	//命令名为空串，不处理
			//处理命令名，将字母全部转为小写
			parts[0] = parts[0].toLowerCase();
			//遍历所有命令调用器
			for(CommandInvoker invoker : invokers) {
				//若提供的命令没有前缀，而此调用器有前缀
				//（或提供的命令有前缀，而此调用器没有前缀），则忽略此调用器
				if(noPrefix != invoker.isNoPrefix()) continue;
				//处理命令名集合，将调用器中的所有命令名复制一份，并转为小写字母
				List<String> nameInInvoker = new ArrayList<>();
				invoker.getCommandName().forEach(name ->
						nameInInvoker.add(name.toLowerCase()));
				//此调用器的命令名不包含要调用的命令名
				if(!nameInInvoker.contains(parts[0])) continue;
				//包含，则可确认此消息是需要响应的命令，将调用此命令调用器，并跳出
				foundCommand = true;
				//响应命令前先检查总开关状态，若未开启，则回复提示信息，不响应命令
				if(!attributes.isEnabled) return RobotAttributes.offMsg;
				//优化参数列表
				List<String> argsList = new ArrayList<>(Arrays.asList(parts)
						.subList(1, parts.length));
				//清除空参数
				for(int i = argsList.size() - 1; i >= 0; i--) {
					String arg = argsList.get(i);
					if(arg.equals("")) argsList.remove(i);
				}
				//检查参数个数是否足够
				if(argsList.size() < invoker.getArgsNum())
					return RobotAttributes.parameterNotEnoughMsg;
				String[] args = argsList.toArray(new String[0]);
				//调用
				reply = invoker.invoke(group, qq, args);
				break;
			}
			//判断是否指令有误（有前缀但没有找到命令）
			if(!noPrefix && !foundCommand)
				reply = attributes.wrongCommandMsg;
		} catch (Throwable t) {
			reporter.sendExceptionToDevelopingGroup(t);
			//找到了命令但没有正确处理
			if(foundCommand) reply = RobotAttributes.errMsg;
		}
		return reply;
	}
	
	/**
	 * 消息统一处理方法，处理各类群消息（群、讨论组）与各类私聊消息（好友，临时会话）
	 */
	public String executeMsg(Long group, long qq, String msg) {
		//起始操作，优化要处理的信息
		//处理信息前先去掉信息左右两侧的空格
		msg = msg.trim();
		//进行简繁转换
		msg = ZhConverterUtil.toSimple(msg);
		//判断发送消息的qq是否在会话列表内
		RobotSession session = sessionManager.getCurrentSession(group, qq);
		//如果发送此消息的qq在会话列表内，则此qq发送的任何信息都需要被处理
		if(session != null) {
			//检查总开关状态，若未开启，则不将信息传递给会话
			if(!attributes.isEnabled) return null;
			/* reply是驱动会话进行的动力，waitingForReply方法会时刻监听reply的内容，
			 * 直到reply不为null时返回 */
			session.setReply(msg);  //记录此消息，传递给会话线程
			return null;	//截获此信息，不再进行下面的命令匹配
		}
		//若不处于会话当中，则进行命令匹配
		//处理消息
		String reply = executeMsg0(group, qq, msg);
		//结束操作，用于对消息进行一些记录或分析
		final String msgCopy = msg;	//内部类中需要局部变量未被更改才能调用
		//在新线程中，忽略异常地进行结束操作
		new Thread(() -> ActionUtils.doIgnoreException(() -> {
			//记录消息处理的相关信息
			robotLogger.logMsgExecution(group, qq, msgCopy, reply);
		})).start();
		return reply;
	}

	private final RobotBasicProperties basicProperties;

	private final RobotAttributes attributes;

	@Resource
	private RobotLogger robotLogger;
	
	@Resource
	private SessionManager sessionManager;
	
	@Resource
	private ExceptionReporter reporter;
}
