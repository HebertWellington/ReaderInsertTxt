package db;

public class DbIntregrityException extends RuntimeException{
	
	private static final long serialVersionUDI = 1L;
	
	public DbIntregrityException(String msg) {
		super(msg);
	}

}
