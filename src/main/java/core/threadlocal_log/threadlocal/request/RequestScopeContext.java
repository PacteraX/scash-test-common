package core.threadlocal_log.threadlocal.request;

import core.ILoginUser;
import core.threadlocal.IRequestScopeContext;

public class RequestScopeContext
    implements
        IRequestScopeContext {

    private long requestId;
    private String     contextType;
    private Enum<?>    forward;
    private String     scrollForward;
    private String     sessionId;
    private String     windowId;
    private String     subSystemPackage;
    private ILoginUser user;
    private ILoginUser loginCtrlAnswer;
    private boolean isDownLoad = false;
    private String requestParams;
    private String loggingIdInfo;

    private boolean invalidSessionIdReceived;

    // ----------- Implementing IRequestScopeContext interface --------- //

    @Override
    public String getContextType() {
        return this.contextType;
    }

    @Override
    public Enum<?> getForward() {
        return this.forward;
    }

    @Override
    public String getScrollForward() {
        return scrollForward;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    @Override
    public ILoginUser getLoginUser() {
        return this.user;
    }

    @Override
    public ILoginUser getLoginCtrlAnswer() {
        return this.loginCtrlAnswer;
    }

    @Override
    public String getSubSystemPackage() {
        return this.subSystemPackage;
    }

    public boolean isDownLoad() {
        return this.isDownLoad;
    }

    @Override
    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    @Override
    public void setForward(Enum<?> forward) {
        this.forward = forward;
    }

    @Override
    public void setScrollForward(String scrollForward) {
        this.scrollForward = scrollForward;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void setLoginUser(ILoginUser user) {
        this.user = user;
    }

    @Override
    public void setLoginCtrlAnswer(ILoginUser user) {
        this.loginCtrlAnswer = user;
    }

    @Override
    public void setSubSystemPackage(String subSystemPackage) {
        this.subSystemPackage = subSystemPackage;
    }

    public void setDownLoad(boolean isDownLoad) {
        this.isDownLoad = isDownLoad;
    }

    @Override
    public String getWindowId() {
        return windowId;
    }

    @Override
    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }

    @Override
    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    @Override
    public long getRequestId() {
        return requestId;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setLoggingIdInfo(String loggingIdInfo) {
        this.loggingIdInfo = loggingIdInfo;
    }

    public String getLoggingIdInfo() {
        return loggingIdInfo;
    }

    public void setInvalidSessionIdReceived(boolean invalidSessionIdReceived) {
        this.invalidSessionIdReceived = invalidSessionIdReceived;
    }

    public boolean isInvalidSessionIdReceived() {
        return invalidSessionIdReceived;
    }
}
