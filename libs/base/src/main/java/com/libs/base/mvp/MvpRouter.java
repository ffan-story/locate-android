package com.libs.base.mvp;

import android.text.TextUtils;

import com.libs.utils.ExceptionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mengmeng on 16/5/24.
 */
public class MvpRouter extends IRouter {
    private Map<String,Method> mViewRouter = new HashMap<String,Method>();
    private Map<String,Method> mPresenterRouter = new HashMap<String,Method>();
    private Map<String,MvpMethord> mEventRouter = new HashMap<String,MvpMethord>();

    public MvpRouter(IView view, IPresentor presentor) {
        super(view, presentor);
    }

    public void register(Object obj){
        if(obj != null){
            Class objClass = obj.getClass();
            if(objClass != null){
                Method[] methords = objClass.getDeclaredMethods();
                if(methords != null && methords.length >0){
                    for (int i = 0; i < methords.length; i++) {
                        MvpAnnotation annotation = methords[i].getAnnotation(MvpAnnotation.class);
                        if(annotation != null){
                            String event = annotation.event();
                            String methordName = methords[i].getName();
                            if(!TextUtils.isEmpty(event) && !TextUtils.isEmpty(methordName)){
                                if(obj instanceof IView){
                                    registerViewRouter(event,methordName);
                                }else if (obj instanceof IPresentor){
                                    registerPresentRouter(event,methordName);
                                }else {
                                    ExceptionUtils.throwArgumentExeception("the paramter are not instanceof IView or IPresentor");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void registers(Object obj){
        if(obj != null){
            Class objClass = obj.getClass();
            if(objClass != null){
                Method[] methords = objClass.getDeclaredMethods();
                if(methords != null && methords.length >0){
                    for (int i = 0; i < methords.length; i++) {
                        MvpAnnotation annotation = methords[i].getAnnotation(MvpAnnotation.class);
                        if(annotation != null){
                            String event = annotation.event();
                            String methordName = methords[i].getName();
                            Class[] paramTypes = methords[i].getParameterTypes();
                            if(!TextUtils.isEmpty(event) && !TextUtils.isEmpty(methordName)){
                                MvpMethord mvpMethord = new MvpMethord();
                                mvpMethord.setEventName(event);
                                mvpMethord.setMethordName(methordName);
                                mvpMethord.setParameterType(paramTypes);
                                EventType eventType = null;
                                if(obj instanceof IView){
                                    eventType = EventType.VIEW;
                                }else if (obj instanceof IPresentor){
                                    eventType = EventType.PRESENTOR;
                                }else {
                                    ExceptionUtils.throwArgumentExeception("the paramter are not instanceof IView or IPresentor");
                                    return;
                                }
                                mvpMethord.setMethod(methords[i]);
                                mvpMethord.setEventType(eventType);
                                mEventRouter.put(event,mvpMethord);
                            }
                        }
                    }
                }
            }
        }
    }

    public void unRegister(Object obj){
        clear();
    }


    public void registerViewRouter(String event,String methord){
        Method[] methords = mIView.getClass().getDeclaredMethods();
        int i = 0;
        for (i = 0; i < methords.length; i++) {
            if(methords[i].getName().equals(methord)){
                mViewRouter.put(event,methords[i]);
                return;
            }
        }
        if(i >= methords.length){
            ExceptionUtils.throwArgumentExeception("no methord ........");
        }
    }


    public void registerPresentRouter(String event,String methord){
        Method[] methords = mPresentor.getClass().getDeclaredMethods();
        int i = 0;
        for (i = 0; i < methords.length; i++) {
            if(methords[i].getName().equals(methord)){
                mPresenterRouter.put(event,methords[i]);
                return;
            }
        }
        if(i >= methords.length){
            ExceptionUtils.throwArgumentExeception("no methord ........");
        }
    }

    public void clear(){
        if(mViewRouter != null && !mViewRouter.isEmpty()){
            mViewRouter.clear();
        }
        if(mPresenterRouter != null && !mPresenterRouter.isEmpty()){
            mPresenterRouter.clear();
        }
    }

    public void sendViewEvent(String event, Object... args){
        if(mViewRouter != null && !mViewRouter.isEmpty()){
            Method methord = mViewRouter.get(event);
            if(methord != null){
                try {
                    methord.setAccessible(true);
                    methord.invoke(mIView,args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendEvent(String event, Object... args){
        if(mEventRouter != null && !mEventRouter.isEmpty()){
            MvpMethord mvpMethord = mEventRouter.get(event);
            if(mvpMethord != null){
                Method methord = mvpMethord.getMethod();
                if(methord != null){
                    methord.setAccessible(true);
                    Class[] parameterClass = mvpMethord.getParameterType();
                    if(parameterClass.length == args.length){
                        int i = 0;
                        for (; i < parameterClass.length; i++) {
                            //判断post的事件的类型和接受的类型是否相同
                            if(!parameterClass[i].getClass().getName().equals(args[i].getClass().getName())){
                                break;
                            }
                        }
                        if(i >= parameterClass.length){
                            ExceptionUtils.throwArgumentExeception("the event "+event+" not register");
                        }
                        EventType eventType = mvpMethord.getEventType();
                        if(eventType == EventType.PRESENTOR){
                            invokePresentorMethord(methord,args);
                        }else if (eventType == EventType.VIEW){
                            invokeViewMethord(methord,args);
                        }

                    }
                }
            }
        }
    }

    private void invokePresentorMethord(Method methord,Object... args){
        if(methord != null){
            try {
                methord.setAccessible(true);
                methord.invoke(mPresentor,args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void invokeViewMethord(Method methord,Object... args){
        if(methord != null){
            try {
                methord.setAccessible(true);
                methord.invoke(mIView,args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPresentEvent(String event, Object... args){
        if(mPresenterRouter != null && !mPresenterRouter.isEmpty()){
            Method methord = mPresenterRouter.get(event);
            if(methord != null){
                try {
                    methord.setAccessible(true);
                    methord.invoke(mPresentor,args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
