package com.wanda.logger.log;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.wanda.logger.base.DefaultConfig;
import com.wanda.logger.file.TxtFileRequest;
import com.wanda.logger.toolbox.LogService;
import com.wanda.logger.toolbox.LogUtil;
import com.wanda.logger.toolbox.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by mengmeng on 15/6/8.
 */
public class ILogerImpl implements ILog,Thread.UncaughtExceptionHandler {
    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 4;

    private Settings mSettring;

    private static String mHeadStr = "";
    private final String CRASH_LOG = "CRASH_LOG";
    private static final String DEFAULT_LOG_TAG = "LOGGER_TAG";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private LogService mLogSevice;
    private Context mContext;
    private static ILogerImpl mLoggerImpl = null;

    public static ILogerImpl getInstance(String tag,Context context){
        if(mLoggerImpl == null){
            String apptag = tag;
            if(TextUtils.isEmpty(apptag)) {
                apptag = DEFAULT_LOG_TAG;
            }
            mLoggerImpl = new ILogerImpl(apptag,context);
        }
        return mLoggerImpl;
    }

    private ILogerImpl(String tag, Context context){
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context;
        if(mLogSevice == null) {
            mLogSevice = new LogService();
        }
        init(tag);
    }

    public void updateConfig(){
        mLogSevice = new LogService();
    }

    public void printPhoneInfo(){
        String phone_info = LogUtil.buildSystemInfo(mContext);
        writeFile("PHONE_INFO",phone_info);
    }
    @Override
    public Settings init(String tag) {
        if(mSettring == null){
            mSettring = new Settings();
            mSettring.setTAG(tag);
        }
        return mSettring;
    }

    @Override
    public Settings getSettings() {
        return null;
    }

    public void d(String msg){
        if(!TextUtils.isEmpty(msg)){
            d("", msg);
        }
    }
    public void e(String msg){
        if(!TextUtils.isEmpty(msg)){
            e("", msg);
        }
    }
    public void w(String msg){
        if(!TextUtils.isEmpty(msg)){
            w("", msg);
        }
    }
    public void i(String msg){
        if(!TextUtils.isEmpty(msg)){
            i("", msg);
        }
    }
    @Override
    public void d(String tag,String message, Object... args) {
        String msg = createMessage(message,args);
        Log.d(tag, msg);
    }

    @Override
    public void e(String tag,String message, Object... args) {
        String msg = createMessage(message,args);
        Log.e(tag, msg);
    }

    @Override
    public void e(String tag,Throwable throwable, String message, Object... args) {
        String msg = createMessage(message,args);
        Log.e(tag, msg);
    }

    @Override
    public void w(String tag,String message, Object... args) {
        String msg = createMessage(message,args);
        Log.w(tag, msg);
    }

    @Override
    public void i(String tag,String message, Object... args) {
        String msg = createMessage(message,args);
        Log.i(tag, msg);
    }

    @Override
    public void v(String tag,String message, Object... args) {
        String msg = createMessage(message,args);
        Log.v(tag, msg);
    }

    @Override
    public void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d("","Empty/Null json content");
            return;
        }
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
            }
        } catch (JSONException e) {
            e(e.getCause().getMessage() + "\n" + json);
        }
    }

    @Override
    public void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            d("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e(e.getCause().getMessage() + "\n" + xml);
        }
    }

    public void writeFile(String tag, String msg){
        if(!TextUtils.isEmpty(msg)){
            String head = getDefaultHeader();
            StringBuffer sb = new StringBuffer();
            if(!TextUtils.isEmpty(tag)){
                sb.append(LogUtil.getWrapperStr(tag));
            }
            sb.append(head).append(msg).append("\n");
            Request fileRequest = new TxtFileRequest(new DefaultConfig());
            fileRequest.setLog(sb.toString());
            mLogSevice.sendLogMsg(fileRequest);
        }
    }

    public void writeFile(Request request){
        if(request != null){
            mLogSevice.sendLogMsg(request);
        }
    }
    public void writeFile(String msg){
        if(!TextUtils.isEmpty(msg)){
            StringBuffer sb = new StringBuffer();
            sb.append(msg).append("\n");
            Request fileRequest = new TxtFileRequest(new DefaultConfig());
            fileRequest.setLog(sb.toString());
            mLogSevice.sendLogMsg(fileRequest);
        }
    }

    private String createMessage(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
    }

    private String getMethordInfo(){
        StackTraceElement[] elements = new Throwable().getStackTrace();
        if(elements != null && elements.length <= 0){
            String className = elements[1].getFileName();
            String methodName = elements[1].getMethodName();
            int lineNumber = elements[1].getLineNumber();

            StringBuffer buffer = new StringBuffer();
            buffer.append("[");
            buffer.append(methodName);
            buffer.append(":");
            buffer.append(lineNumber);
            buffer.append("]");
            return buffer.toString();
        }
        return "";
    }

    public String getDefaultHeader() {
        if(TextUtils.isEmpty(mHeadStr)){
            mHeadStr = LogUtil.getWrapperStr(LogUtil.getNowTime());
//                    + getNowThreadName() + DIVIDER_LINE;
        }
        return mHeadStr;
    }

    private static String getNowThreadName() {
        return Thread.currentThread().getName();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            printWriter.append(ex.getMessage());
            ex.printStackTrace(printWriter);
            Log.getStackTraceString(ex);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            String msg = buildCrashLog(result.toString());
            Log.e("crash",msg);
            writeFile(CRASH_LOG,msg);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildCrashLog(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n#");
        sb.append(new Date().toString());
        sb.append("\n");
        // Add system and device info.
        sb.append(LogUtil.buildSystemInfo(mContext));
        sb.append("\n");
        sb.append("#-------AndroidRuntime-------");
        sb.append(msg);
        sb.append("\n");
        sb.append("#-------end-------");
        sb.append("\n");
        sb.append("#end\n");

        return sb.toString();
    }
}
