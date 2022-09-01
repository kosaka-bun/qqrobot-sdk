package de.honoka.qqrobot.spring.boot.starter.component;

import de.honoka.qqrobot.spring.boot.starter.component.logger.RobotLogger;
import de.honoka.qqrobot.spring.boot.starter.component.util.RobotImageUtils;
import de.honoka.qqrobot.spring.boot.starter.property.RobotBasicProperties;
import de.honoka.sdk.util.code.ActionUtils;
import de.honoka.sdk.util.text.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.sobte.cqp.jcq.event.JcqApp.CC;

@Component
public class ExceptionReporter {

	@Resource
	private RobotBasicProperties basicProperties;

	@Resource
	private RobotLogger robotLogger;

	@Resource
	private RobotBeanHolder beanHolder;

	@Resource
	private RobotImageUtils robotImageUtils;
	
	/**
	 * 异常信息记录表，只记录第一行
	 */
	public List<String> exceptionList = new ArrayList<>();
	
	/**
	 * 将异常信息存储到数据库中
	 */
	protected void logException(Throwable t) {
		robotLogger.logException(t);
	}
	
	/**
	 * 将错误信息发送到开发群
	 */
	public void sendExceptionToDevelopingGroup(Throwable t) {
		t.printStackTrace();    //首先将信息写入控制台
		try {
			//只发送和记录最根本的错误信息
			while(t.getCause() != null) t = t.getCause();
			//将异常信息存入数据库
			final Throwable tCopy = t;
			ActionUtils.doIgnoreException(() -> logException(tCopy));
			//region 判断是否需要报告异常，若不需要，则跳出
			String exceptionText = ExceptionUtils.transfer(t);
			//配置为不报告
			if(!basicProperties.getReportException()) return;
			//将信息拆分成行
			String[] rows = exceptionText.split("\n");
			//如果该异常已经发生过则不予报告
			for(String s : exceptionList) {
				if(s.equals(rows[0])) return;
			}
			//endregion
			//报告
			exceptionList.add(rows[0]);
			String reply = "出现了问题，堆栈信息如下：\n";
			reply += CC.image(getExceptionTextImg(exceptionText).getAbsolutePath());
			beanHolder.getFramework().sendGroupMsg(basicProperties
					.getDevelopingGroup(), reply);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected File getExceptionTextImg(String exceptionText) {
		return robotImageUtils.textToImageFileBySize(exceptionText,
				1200);
	}
}
