package core.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import core.config.Configure;

public class UString {
    static final String PROP_KEY_LINE_SEPARATOR = "line.separator";
    final static public String COMMA = ",";
    final static public String Type_Integer = "Integer";
    final static public String Type_String = "String";
    final static public String Type_Short = "Short";
    final static public String Type_Long = "Long";
    final static protected String hostName;

    static {
        String hostNameTmp = null;
        try {
            long startTime = System.currentTimeMillis();
            hostNameTmp = InetAddress.getLocalHost().getHostName();
            long endTime = System.currentTimeMillis();
            System.out.println(String.format("getHostName hostName:%s, time(ms):%s", hostNameTmp, endTime - startTime));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        hostName = hostNameTmp;
    }

    /**
     * NULLチェック<br>
     * 文字列がnullまたは""であった場合、trueを返す<br>
     *
     * @return
     * @throws Exception
     */
    public static boolean isNull(Object o) {
        if (o == null) return true;
        String str = o.toString();
        if(str == null || str.length()==0 )return true;
        return false;
    }

    public static String padZeros(String src, int size){
        StringBuffer sb = new StringBuffer(src);
        for(int index = src.length(); index < size; index++)
            sb.insert(0, '0');

        return sb.toString();
    }

    public static String toString(int value, int size){
        return padZeros(Integer.toString(value), size);
    }

    private static final String SINGLE_QULT = "'";
    private static final String DOUBLE_QULT = "\"";
    public static StringBuilder wrapSingleQuot(String value){
        return new StringBuilder(SINGLE_QULT)
        .append(value).append(SINGLE_QULT);
    }
    public static StringBuilder wrapDoubleQuot(String value){
        return new StringBuilder(DOUBLE_QULT)
        .append(value).append(DOUBLE_QULT);
    }

    private static final String BRACKET_FRONT = "[";
    private static final String BRACKET_BACK = "]";
    private static final String PARENTHESIS_FRONT = "(";
    private static final String PARENTHESIS_BACK = ")";
    private static final String AT = " at ";
    private static final String DOT = ".";
    private static final String ELEMENT_SEPARATOR = " : ";
    private static final String ELEMENT_SEPARATOR_S = ":";

    public static StringBuilder wrapBracket(StringBuilder sb,String value){
        return sb.append(BRACKET_FRONT).append(value).append(BRACKET_BACK);
    }

    public static String getLineSeparator(){
        return System.getProperty(PROP_KEY_LINE_SEPARATOR);
    }

    public static StringBuilder appendLineSeparator(StringBuilder sb){
        return sb.append(System.getProperty(PROP_KEY_LINE_SEPARATOR));
    }

    public static void appendStackTrace(StringBuilder sb ,Throwable t, int maxDepth){
    	int depth = 0;
    	int skip = 0;

    	Throwable next = t;
    	if (maxDepth > 0) {
            while (next != null) {
            	depth++;
            	next = next.getCause();
            }
            skip = (depth > maxDepth)?(depth - maxDepth):(0);

        	next = t;
            for (int i = 0; i < skip; i++) {
            	if (next != null) next = next.getCause();
            }
    	}
        if (next != null) appendStackTrace(sb,next);
    }

    public static void appendStackTrace(StringBuilder sb ,Throwable t){
        appendLineSeparator(sb);
        sb.append(BRACKET_FRONT).append(t.getClass().getSimpleName())
        .append(BRACKET_BACK);
        if(t.getLocalizedMessage()!=null)
            sb.append(ELEMENT_SEPARATOR).append(t.getLocalizedMessage());
        appendLineSeparator(sb);
        for(StackTraceElement element:t.getStackTrace()){
            sb.append(AT);
            sb.append(element.getClassName()).append(DOT)
            .append(element.getMethodName());
            sb.append(PARENTHESIS_FRONT).append(element.getFileName());
            if(element.getLineNumber()>0){
                sb.append(ELEMENT_SEPARATOR_S).append(element.getLineNumber());
            }
            appendLineSeparator(sb.append(PARENTHESIS_BACK));
        }
        appendLineSeparator(sb);
        Throwable next = t.getCause();
        if(next!=null) appendStackTrace(sb,next);
    }

    public static String getEncodingString(byte[] bytes){
        try {
            return new String(bytes,Configure.getEncording());
        } catch (NullPointerException e) {
            throw new EncodingException(e);
        } catch (UnsupportedEncodingException e) {
            throw new EncodingException(e);
        }
    }

    public static String getEncodingString(byte[] bytes,String encord){
        try {
            return new String(bytes,encord);
        } catch (NullPointerException e) {
            throw new EncodingException(e);
        } catch (UnsupportedEncodingException e) {
            throw new EncodingException(e);
        }
    }
    public static class EncodingException extends RuntimeException{
        public EncodingException(Throwable e){
            super(e);
        }
    }

}
