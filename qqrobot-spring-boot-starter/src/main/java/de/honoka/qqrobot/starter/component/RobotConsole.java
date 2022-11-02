package de.honoka.qqrobot.starter.component;

import de.honoka.sdk.util.system.ColorfulOutputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class RobotConsole {

    private ColorfulOutputStream newOut, newErr;

    private final StringBuilder lines = new StringBuilder();

    @Getter
    @Setter
    private int maxLineCount = 200;

    public RobotConsole() {
        init();
    }

    private void init() {
        changeSystemOut();
    }

    @SneakyThrows
    private void changeSystemOut() {
        newOut = new ColorfulOutputStream(System.out, Color.LIGHT_GRAY);
        newErr = new ColorfulOutputStream(System.err, new Color(
                255, 107, 103));
        newOut.setPrintMethod(bytes -> write(bytes, newOut));
        newErr.setPrintMethod(bytes -> write(bytes, newErr));
        PrintStream newOutPrintStream = new PrintStream(newOut,
                false, "UTF-8");
        PrintStream newErrPrintStream = new PrintStream(newErr,
                false, "UTF-8");
        System.setOut(newOutPrintStream);
        System.setErr(newErrPrintStream);
    }

    //两个输出流均使用此方法进行输出
    private synchronized void write(byte[] bytes, ColorfulOutputStream out) {
        String str = new String(bytes, StandardCharsets.UTF_8)
                .replace("\r", "");
        if(!str.contains("\n")) {
            writeLine(str, out);
        } else {
            //如果不使用split的重载方法，当分隔符位于末尾时，末尾的空串将丢失！
            String[] lines = str.split("\n", -1);
            for(int i = 0; i < lines.length; i++) {
                //不是最后一行
                if(i < lines.length - 1) {
                    writeLine(lines[i] + "\n", out);
                    this.lines.append("\n");
                } else {
                    writeLine(lines[i], out);
                }
            }
        }
        //检查保存的控制台输出是否超过最大行数
        int lineCount = StringUtils.countMatches(lines, "\n") + 1;
        if(lineCount > maxLineCount) {
            int newStartIndex = StringUtils.ordinalIndexOf(lines, "\n",
                    lineCount - maxLineCount) + 1;
            lines.delete(0, newStartIndex);
        }
    }

    private void writeLine(String line, ColorfulOutputStream out) {
        //按颜色输出
        String[] parts = line.split("\\u001B");
        for(int i = 0; i < parts.length; i++) {
            if(i == 0) {
                //第一部分直接按原色输出
                writeColorfulText(parts[i], out.getPrintColor());
            } else {
                //其他部分先进行处理，改变输出流颜色，并去除控制序列，再输出
                String part = out.changePrintColorByAnsiString(parts[i]);
                writeColorfulText(part, out.getPrintColor());
            }
        }
    }

    private void writeColorfulText(String str, Color color) {
        if(StringUtils.isEmpty(str)) return;
        String template = "<pre style=\"color: %s;\">%s</pre>";
        String colorStr = String.format("#%02X%02X%02X", color.getRed(),
                color.getGreen(), color.getBlue());
        str = str.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
        lines.append(String.format(template, colorStr, str));
    }

    public synchronized String getText() {
        return lines.toString();
    }
}
