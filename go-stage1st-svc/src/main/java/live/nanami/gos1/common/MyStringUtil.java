package live.nanami.gos1.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author arichi
 * @since 2024/12/11
 */
public class MyStringUtil {

    public static String pojo2Uml(String inputText) {
        // Define the regular expression pattern to match the input text
        String pattern = "(private|public|protected)\\s+((?:\\w+\\s*<[^>]+>)?\\w+)\\s+(\\w+);";

        // Create a Pattern object
        Pattern regex = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher matcher = regex.matcher(inputText);

        StringBuilder sb = new StringBuilder();

        // Iterate over the matches and convert the text format
        while (matcher.find()) {
            String accessModifier = matcher.group(1);
            String variableType = matcher.group(2);
            String variableName = matcher.group(3);
            String convertedLine = getAccessModifierSymbol(accessModifier) + " " + variableName + ":" + variableType + "\n";
            sb.append(convertedLine);
        }

        return sb.toString();
    }

    public static String camelToConstant(String str) {
        StringBuilder result = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == chars.length - 1) {
                result.append(chars[i]);
            }
            if (i != chars.length - 1) {
                result.append(chars[i]);
                if (Character.isUpperCase(chars[i + 1])) {
                    result.append("_");
                }
            }
        }
        return result.toString().toUpperCase();
    }

    public static String lowerFirstChar(String rawStr) {
        String rawStrWithoutFirstChar = rawStr.substring(1);
        char first = rawStr.charAt(0);
        if (Character.isUpperCase(first)) {
            first = Character.toLowerCase(first);
        }
        return first + rawStrWithoutFirstChar;
    }

    private static String getAccessModifierSymbol(String accessModifier) {
        switch (accessModifier) {
            case "private":
                return "-";
            case "public":
                return "+";
            case "protected":
                return "#";
            default:
                return "";
        }
    }

    public static String sayBye2aChar(String rawStr, char c) {
        return rawStr.replaceAll(c + "", "");
    }

    public static String ipv42Binary(String ipv4) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipv4);
            // 获取IP地址的字节数组
            byte[] addressBytes = inetAddress.getAddress();
            StringBuilder binaryString = new StringBuilder();
            for (byte b : addressBytes) {
                // 将每个字节转换为二进制字符串
                StringBuilder binary = new StringBuilder(Integer.toBinaryString(b & 255));
                while (binary.length() < 8) {
                    // 将二进制字符串补足8位
                    binary.insert(0, "0");
                }
                binaryString.append(binary);
                binaryString.append(".");
            }
            binaryString.deleteCharAt(binaryString.length() - 1);
            // 输出IPv4地址的二进制形式
            return binaryString.toString();
        } catch (UnknownHostException ignored) {
        }
        return "";
    }

    /**
     * 二进制格式的ipv4地址转换为十进制
     * @param binaryIpv4 eg: 11111111.11111111.11111000.00000000
     * @return eg:255.255.248.0
     */
    public static String binaryIpv42Dec(String binaryIpv4) {
        // 将二进制形式的IPv4地址按照"."分割成4个子串
        String[] binaryOctets = binaryIpv4.split("\\.");
        StringBuilder ipAddress = new StringBuilder();
        for (String binaryOctet : binaryOctets) {
            // 将每个子串转换为10进制整数
            int decimalValue = Integer.parseInt(binaryOctet, 2);
            // 将每个整数拼接起来
            ipAddress.append(decimalValue).append(".");
        }
        // 删除最后一个"."
        ipAddress.deleteCharAt(ipAddress.length() - 1);
        return ipAddress.toString();
    }

    /**
     * 八位格式的按位与
     * @param in1 eg:00011000
     * @param in2 eg:11110000
     * @return eg:00010000
     */
    public static String binaryAndInFormat8(String in1, String in2) {
        int a = Integer.parseInt(in1,2);
        int b = Integer.parseInt(in2,2);
        int c = a & b;
        String binary = Integer.toBinaryString(c);
        while (binary.length() < 8) {
            // 将二进制字符串补足8位
            binary = "0" + binary;
        }
        return binary;
    }

    public static boolean isValidCIDRAddress(String cidrAddress) {
        // IP地址的正则表达式
        String ipRegex = "(\\d{1,3}\\.){3}\\d{1,3}";
        // 子网掩码长度的正则表达式
        String subnetMaskRegex = "/([1-9]|[1-2]\\d|3[0-2])";
        // CIDR格式的正则表达式
        String cidrRegex = ipRegex + subnetMaskRegex;
        return cidrAddress.matches(cidrRegex);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(String str){
        return str == null || "".equals(str);
    }
}
