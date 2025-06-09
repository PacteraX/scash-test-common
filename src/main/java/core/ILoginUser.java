package core;

import java.io.Serializable;

public interface ILoginUser extends Serializable {
    boolean isLogedIn();
    Integer getUserId();
}
