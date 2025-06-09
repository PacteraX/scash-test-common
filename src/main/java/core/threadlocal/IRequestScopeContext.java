package core.threadlocal;

import core.ILoginUser;

/**
 * HttpRequest�Ɉˑ����Ȃ� request scope �� context
 * @author Ksf-Wada
 */
public interface IRequestScopeContext extends IThreadContext {
    String getContextType() ;
    Enum<?> getForward();
    String getScrollForward();
    String getSessionId();
    String getWindowId();
    String getSubSystemPackage();
    ILoginUser getLoginCtrlAnswer();
    ILoginUser getLoginUser();
    long getRequestId();
    String getRequestParams();
    String getLoggingIdInfo();

    boolean isDownLoad();
    void setDownLoad(boolean isDownLoad);
    void setContextType(String contextType);
    void setForward(Enum<?> forward);
    void setScrollForward(String scrollForward);
    void setSessionId(String sessionId);
    void setWindowId(String windowId);
    void setSubSystemPackage(String subSystemPackage);
    void setLoginUser(ILoginUser user);
    void setLoginCtrlAnswer(ILoginUser user);
    void setRequestId(long requestId);
    void setRequestParams(String requestParams);
    void setLoggingIdInfo(String loggingIdInfo);

    void setInvalidSessionIdReceived(boolean invalidSessionIdReceived);
    boolean isInvalidSessionIdReceived();
}
