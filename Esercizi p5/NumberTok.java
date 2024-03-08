
public class NumberTok extends Token {
	public static final int tag = 256;
	int lexeme;

	public NumberTok(int v) {
		super(tag);
		lexeme = v;
	}

	public String toString() {
		return "<" + tag + ", " + lexeme + ">";
	}
}
