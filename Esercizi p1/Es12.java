public class Es12 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;

		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);

			switch (state) {
				case 0:
					if (ch == '_')
						state = 1;
					else if (ch >= 65 && ch <= 90 || ch >= 97 && ch <= 122)
						state = 2;
					else
						state = -1;
					break;

				case 1:
					if (ch >= 65 && ch <= 90 || ch >= 97 && ch <= 122 || ch >= 48 && ch <= 57)
						state = 2;
					else if (ch == '_')
						state = 1;
					else
						state = -1;
					break;

				case 2:
					if (ch >= 65 && ch <= 90 || ch >= 97 && ch <= 122 || ch >= 48 && ch <= 57 || ch == '_')
						state = 2;
					else
						state = -1;
					break;
			}

		}
		return state == 2;
	}

	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}