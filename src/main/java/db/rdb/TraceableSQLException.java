package db.rdb;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TraceableSQLException extends SQLException{
	private static final long serialVersionUID = 7880169374364055912L;
	private final List<Throwable> sqlThrowables;
	
	public TraceableSQLException(SQLException sqlThrowable){
		super(sqlThrowable);
		sqlThrowables = new ArrayList<Throwable>();
		do{
			sqlThrowables.add(0, sqlThrowable);
			sqlThrowable = sqlThrowable.getNextException();
		}while(sqlThrowable != null);
	}
	
	@Override
    public void printStackTrace(PrintStream s) {
		for(Throwable sqlThrowable : sqlThrowables)
			sqlThrowable.printStackTrace(s);
    }

	@Override
    public void printStackTrace(PrintWriter s) {
		for(Throwable sqlThrowable : sqlThrowables)
			sqlThrowable.printStackTrace(s);
    }
	
	@Override
    public String getLocalizedMessage() {
		Throwable realThrowable = sqlThrowables.get(0);
        return realThrowable.getMessage();
    }
	
	@Override
    public StackTraceElement[] getStackTrace() {
		List<StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();
		
		for(Throwable sqlThrowable : sqlThrowables){
			StackTraceElement[] subStackTrace = sqlThrowable.getStackTrace();
			for(StackTraceElement ste :  subStackTrace)
				stackTrace.add(ste);
		}
		
        return stackTrace.toArray(new StackTraceElement[0]);
    }
    
	@Override
    public String toString() {
		Throwable realThrowable = sqlThrowables.get(0);
        return realThrowable.toString();
    }
}
